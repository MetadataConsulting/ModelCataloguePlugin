package org.modelcatalogue.core

import grails.gorm.DetachedCriteria
import grails.plugin.springsecurity.SpringSecurityUtils
import org.modelcatalogue.core.asset.MicrosoftOfficeDocument
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.util.ParamArgs
import org.modelcatalogue.core.util.SearchParams
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeImpl
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeWrapper
import org.modelcatalogue.gel.export.GridReportXlsxExporter

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK
import com.google.common.collect.ImmutableSet
import grails.transaction.Transactional
import grails.util.GrailsNameUtils
import org.hibernate.SessionFactory
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.catalogueelement.DataModelCatalogueElementService
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.dataimport.excel.ExcelExporter
import org.modelcatalogue.core.events.DataModelFinalizedEvent
import org.modelcatalogue.core.events.DataModelWithErrorsEvent
import org.modelcatalogue.core.events.MetadataResponseEvent
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.export.inventory.DataModelToDocxExporter
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.policy.VerificationPhase
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import grails.plugin.springsecurity.acl.AclUtilService
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors
import grails.plugin.springsecurity.SpringSecurityService

class DataModelController<T extends CatalogueElement> extends AbstractCatalogueElementController<DataModel> {

    SessionFactory sessionFactory

    SpringSecurityService springSecurityService

    DataModelGormService dataModelGormService

    AclUtilService aclUtilService

    FavouriteService favouriteService

    DataModelCatalogueElementService dataModelCatalogueElementService

    AssetGormService assetGormService

    AssetMetadataService assetMetadataService

    DataModelController() {
        super(DataModel, false)
    }

    //can be used in the future
    static final String DOC_IMAGE_PATH = ""

    //custom template is better than the default inventory report - so using it
    static Closure customTemplate = {
        'document' font: [family: 'Calibri', size: 11], margin: [left: 20, right: 10]
        'paragraph.title' font: [color: '#1F497D', size: 32.pt, bold: true], margin: [top: 150.pt, bottom: 10.pt]
        'paragraph.subtitle' font: [color: '#1F497D', size: 36.pt], margin: [top: 0.pt]
        'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
        'heading1' font: [size: 18, bold: true]
        'heading2' font: [size: 18, bold: true]
        'heading3' font: [size: 16, bold: true]
        'heading4' font: [size: 16, bold: true]
        'heading5' font: [size: 15]
        'heading6' font: [size: 14]
        'table.row.cell.headerCell' font: [color: '#FFFFFF', size: 12.pt, bold: true], background: '#1F497D'
        'table.row.cell' font: [size: 10.pt]
        'paragraph.headerImage' height: 1.366.inches, width: 2.646.inches
    }

    @Override
    protected DataModel findById(long id) {
        dataModelGormService.findById(id)
    }

    def showAssetInAngular() {
        redirect(url: "/#/${params.id}/asset/${params.subResourceId}")
    }

    /**
     * Saves a resource
     * Overrides the base method - there is a slightly different security model
     */
    @Transactional
    def save() {
        if (handleReadOnly()) {
            return
        }
        DataModel instance = createResource()

        instance.validate()

        if (!params.skipPolicies) {
            validatePolicies(VerificationPhase.PROPERTY_CHECK, instance, objectToBind)
        }

        if (instance.hasErrors()) {
            if (!hasUniqueName() || getObjectToBind().size() > 1 || !getObjectToBind().containsKey('name')) {
                respond instance.errors
                return
            }

            Errors errors = instance.errors

            if (errors.getFieldError('name').any { it.code == 'unique' }) {
                T found = resource.findByName(getObjectToBind().name, [sort: 'versionNumber', order: 'desc'])
                if (found) {
                    if (!found.instanceOf(CatalogueElement)) {
                        respond found
                        return
                    }
                    if (found.status != ElementStatus.DRAFT) {
                        found = elementService.createDraftVersion(found, DraftContext.userFriendly())
                    }
                    respond found
                    return
                }
            }

            respond errors
            return
        }

        cleanRelations(instance)

        instance.save flush: true

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }


        bindRelations(instance, false)

        instance.save flush: true

        if (instance.hasErrors()) {
            respond instance.errors
            return
        }

        if (favoriteAfterUpdate && modelCatalogueSecurityService.userLoggedIn && instance) {
            favouriteService.favouriteModel(instance)
        }

        respond instance, [status: CREATED]
    }

    /**
     * Finalize a model
     * @param id cloned element id
     * @param destinationDataModelId destination data model id
     */
    @Transactional
    def finalizeElement() {
        if (handleReadOnly()) {
            return
        }

        Long dataModelId = params.long('id')
        String semanticVersion = params.semanticVersion
        String revisionNotes = params.revisionNotes

        MetadataResponseEvent responseEvent = dataModelCatalogueElementService.finalize(dataModelId, semanticVersion, revisionNotes)
        boolean handled = handleMetadataResponseEvent(responseEvent)
        if ( handled ) {
            return
        }
        if ( responseEvent instanceof DataModelWithErrorsEvent) {
            DataModel dataModel = (responseEvent as DataModelWithErrorsEvent).dataModel
            respond dataModel.errors, view: 'edit' // STATUS CODE 422
            return
        }
        if ( !(responseEvent instanceof DataModelFinalizedEvent) ) {
            log.warn("Got an unexpected event ${responseEvent.class.name}")
            notFound()
            return
        }

        DataModel instance = (responseEvent as DataModelFinalizedEvent).dataModel
        respond instance, [status: OK]
    }

    /**
     * Create a new version of a data model
     * @param id
     */
    @Transactional
    def newVersion() {
        if (handleReadOnly()) {
            return
        }

        T instance = findById(params.long('id'))
        if (instance == null) {
            notFound()
            return
        }

        String semanticVersion = params.semanticVersion ?: objectToBind.semanticVersion
        String preferDrafts = params.preferDrafts ?: objectToBind.preferDrafts

        Long id = instance.getId()

        executorService.submit {
            try {
                CatalogueElement element = resource.get(id)
                DraftContext context = DraftContext.userFriendly().withMonitor(BuildProgressMonitor.create("Create new version of $element", id))

                if (preferDrafts) {
                    preferDrafts.split(/\s*,\s*/).each {
                        context.preferDraftFor(Long.parseLong(it, 10))
                    }
                }

                if (element.instanceOf(DataModel)) {
                    elementService.createDraftVersion((DataModel) element, semanticVersion, context) as T
                } else {
                    elementService.createDraftVersion(element, context) as T
                }
            } catch (e) {
                log.error "Exception creating draft in the background", e
            }
        }

        respond instance, [status: OK]
    }

    def d3View() {
        def dataModelJson = [
            "name": "flare",
            "children": [
                [
                    "name": "analytics",
                    "children": [
                        [
                            "name": "cluster",
                            "children": [
                                ["name": "AgglomerativeCluster", "size": 3938],
                                ["name": "CommunityStructure", "size": 3812],
                                ["name": "HierarchicalCluster", "size": 6714],
                                ["name": "MergeEdge", "size": 743]
                            ]
                        ],
                        [
                            "name": "graph",
                            "children": [
                                ["name": "BetweennessCentrality", "size": 3534],
                                ["name": "LinkDistance", "size": 5731],
                                ["name": "MaxFlowMinCut", "size": 7840],
                                ["name": "ShortestPaths", "size": 5914],
                                ["name": "SpanningTree", "size": 3416]
                            ]
                        ],
                        [
                            "name": "optimization",
                            "children": [
                                ["name": "AspectRatioBanker", "size": 7074]
                            ]
                        ]
                    ]
                ],
                [
                    "name": "animate",
                    "children": [
                        ["name": "Easing", "size": 17010],
                        ["name": "FunctionSequence", "size": 5842],
                        [
                            "name": "interpolate",
                            "children": [
                                ["name": "ArrayInterpolator", "size": 1983],
                                ["name": "ColorInterpolator", "size": 2047],
                                ["name": "DateInterpolator", "size": 1375],
                                ["name": "Interpolator", "size": 8746],
                                ["name": "MatrixInterpolator", "size": 2202],
                                ["name": "NumberInterpolator", "size": 1382],
                                ["name": "ObjectInterpolator", "size": 1629],
                                ["name": "PointInterpolator", "size": 1675],
                                ["name": "RectangleInterpolator", "size": 2042]
                            ]
                        ],
                        ["name": "ISchedulable", "size": 1041],
                        ["name": "Parallel", "size": 5176],
                        ["name": "Pause", "size": 449],
                        ["name": "Scheduler", "size": 5593],
                        ["name": "Sequence", "size": 5534],
                        ["name": "Transition", "size": 9201],
                        ["name": "Transitioner", "size": 19975],
                        ["name": "TransitionEvent", "size": 1116],
                        ["name": "Tween", "size": 6006]
                    ]
                ],
                [
                    "name": "data",
                    "children": [
                        [
                            "name": "converters",
                            "children": [
                                ["name": "Converters", "size": 721],
                                ["name": "DelimitedTextConverter", "size": 4294],
                                ["name": "GraphMLConverter", "size": 9800],
                                ["name": "IDataConverter", "size": 1314],
                                ["name": "JSONConverter", "size": 2220]
                            ]
                        ],
                        ["name": "DataField", "size": 1759],
                        ["name": "DataSchema", "size": 2165],
                        ["name": "DataSet", "size": 586],
                        ["name": "DataSource", "size": 3331],
                        ["name": "DataTable", "size": 772],
                        ["name": "DataUtil", "size": 3322]
                    ]
                ],
                [
                    "name": "display",
                    "children": [
                        ["name": "DirtySprite", "size": 8833],
                        ["name": "LineSprite", "size": 1732],
                        ["name": "RectSprite", "size": 3623],
                        ["name": "TextSprite", "size": 10066]
                    ]
                ],
                [
                    "name": "flex",
                    "children": [
                        ["name": "FlareVis", "size": 4116]
                    ]
                ],
                [
                    "name": "physics",
                    "children": [
                        ["name": "DragForce", "size": 1082],
                        ["name": "GravityForce", "size": 1336],
                        ["name": "IForce", "size": 319],
                        ["name": "NBodyForce", "size": 10498],
                        ["name": "Particle", "size": 2822],
                        ["name": "Simulation", "size": 9983],
                        ["name": "Spring", "size": 2213],
                        ["name": "SpringForce", "size": 1681]
                    ]
                ],
                [
                    "name": "query",
                    "children": [
                        ["name": "AggregateExpression", "size": 1616],
                        ["name": "And", "size": 1027],
                        ["name": "Arithmetic", "size": 3891],
                        ["name": "Average", "size": 891],
                        ["name": "BinaryExpression", "size": 2893],
                        ["name": "Comparison", "size": 5103],
                        ["name": "CompositeExpression", "size": 3677],
                        ["name": "Count", "size": 781],
                        ["name": "DateUtil", "size": 4141],
                        ["name": "Distinct", "size": 933],
                        ["name": "Expression", "size": 5130],
                        ["name": "ExpressionIterator", "size": 3617],
                        ["name": "Fn", "size": 3240],
                        ["name": "If", "size": 2732],
                        ["name": "IsA", "size": 2039],
                        ["name": "Literal", "size": 1214],
                        ["name": "Match", "size": 3748],
                        ["name": "Maximum", "size": 843],
                        [
                            "name": "methods",
                            "children": [
                                ["name": "add", "size": 593],
                                ["name": "and", "size": 330],
                                ["name": "average", "size": 287],
                                ["name": "count", "size": 277],
                                ["name": "distinct", "size": 292],
                                ["name": "div", "size": 595],
                                ["name": "eq", "size": 594],
                                ["name": "fn", "size": 460],
                                ["name": "gt", "size": 603],
                                ["name": "gte", "size": 625],
                                ["name": "iff", "size": 748],
                                ["name": "isa", "size": 461],
                                ["name": "lt", "size": 597],
                                ["name": "lte", "size": 619],
                                ["name": "max", "size": 283],
                                ["name": "min", "size": 283],
                                ["name": "mod", "size": 591],
                                ["name": "mul", "size": 603],
                                ["name": "neq", "size": 599],
                                ["name": "not", "size": 386],
                                ["name": "or", "size": 323],
                                ["name": "orderby", "size": 307],
                                ["name": "range", "size": 772],
                                ["name": "select", "size": 296],
                                ["name": "stddev", "size": 363],
                                ["name": "sub", "size": 600],
                                ["name": "sum", "size": 280],
                                ["name": "update", "size": 307],
                                ["name": "variance", "size": 335],
                                ["name": "where", "size": 299],
                                ["name": "xor", "size": 354],
                                ["name": "_", "size": 264]
                            ]
                        ],
                        ["name": "Minimum", "size": 843],
                        ["name": "Not", "size": 1554],
                        ["name": "Or", "size": 970],
                        ["name": "Query", "size": 13896],
                        ["name": "Range", "size": 1594],
                        ["name": "StringUtil", "size": 4130],
                        ["name": "Sum", "size": 791],
                        ["name": "Variable", "size": 1124],
                        ["name": "Variance", "size": 1876],
                        ["name": "Xor", "size": 1101]
                    ]
                ],
                [
                    "name": "scale",
                    "children": [
                        ["name": "IScaleMap", "size": 2105],
                        ["name": "LinearScale", "size": 1316],
                        ["name": "LogScale", "size": 3151],
                        ["name": "OrdinalScale", "size": 3770],
                        ["name": "QuantileScale", "size": 2435],
                        ["name": "QuantitativeScale", "size": 4839],
                        ["name": "RootScale", "size": 1756],
                        ["name": "Scale", "size": 4268],
                        ["name": "ScaleType", "size": 1821],
                        ["name": "TimeScale", "size": 5833]
                    ]
                ],
                [
                    "name": "util",
                    "children": [
                        ["name": "Arrays", "size": 8258],
                        ["name": "Colors", "size": 10001],
                        ["name": "Dates", "size": 8217],
                        ["name": "Displays", "size": 12555],
                        ["name": "Filter", "size": 2324],
                        ["name": "Geometry", "size": 10993],
                        [
                            "name": "heap",
                            "children": [
                                ["name": "FibonacciHeap", "size": 9354],
                                ["name": "HeapNode", "size": 1233]
                            ]
                        ],
                        ["name": "IEvaluable", "size": 335],
                        ["name": "IPredicate", "size": 383],
                        ["name": "IValueProxy", "size": 874],
                        [
                            "name": "math",
                            "children": [
                                ["name": "DenseMatrix", "size": 3165],
                                ["name": "IMatrix", "size": 2815],
                                ["name": "SparseMatrix", "size": 3366]
                            ]
                        ],
                        ["name": "Maths", "size": 17705],
                        ["name": "Orientation", "size": 1486],
                        [
                            "name": "palette",
                            "children": [
                                ["name": "ColorPalette", "size": 6367],
                                ["name": "Palette", "size": 1229],
                                ["name": "ShapePalette", "size": 2059],
                                ["name": "SizePalette", "size": 2291]
                            ]
                        ],
                        ["name": "Property", "size": 5559],
                        ["name": "Shapes", "size": 19118],
                        ["name": "Sort", "size": 6887],
                        ["name": "Stats", "size": 6557],
                        ["name": "Strings", "size": 22026]
                    ]
                ],
                [
                    "name": "vis",
                    "children": [
                        [
                            "name": "axis",
                            "children": [
                                ["name": "Axes", "size": 1302],
                                ["name": "Axis", "size": 24593],
                                ["name": "AxisGridLine", "size": 652],
                                ["name": "AxisLabel", "size": 636],
                                ["name": "CartesianAxes", "size": 6703]
                            ]
                        ],
                        [
                            "name": "controls",
                            "children": [
                                ["name": "AnchorControl", "size": 2138],
                                ["name": "ClickControl", "size": 3824],
                                ["name": "Control", "size": 1353],
                                ["name": "ControlList", "size": 4665],
                                ["name": "DragControl", "size": 2649],
                                ["name": "ExpandControl", "size": 2832],
                                ["name": "HoverControl", "size": 4896],
                                ["name": "IControl", "size": 763],
                                ["name": "PanZoomControl", "size": 5222],
                                ["name": "SelectionControl", "size": 7862],
                                ["name": "TooltipControl", "size": 8435]
                            ]
                        ],
                        [
                            "name": "data",
                            "children": [
                                ["name": "Data", "size": 20544],
                                ["name": "DataList", "size": 19788],
                                ["name": "DataSprite", "size": 10349],
                                ["name": "EdgeSprite", "size": 3301],
                                ["name": "NodeSprite", "size": 19382],
                                [
                                    "name": "render",
                                    "children": [
                                        ["name": "ArrowType", "size": 698],
                                        ["name": "EdgeRenderer", "size": 5569],
                                        ["name": "IRenderer", "size": 353],
                                        ["name": "ShapeRenderer", "size": 2247]
                                    ]
                                ],
                                ["name": "ScaleBinding", "size": 11275],
                                ["name": "Tree", "size": 7147],
                                ["name": "TreeBuilder", "size": 9930]
                            ]
                        ],
                        [
                            "name": "events",
                            "children": [
                                ["name": "DataEvent", "size": 2313],
                                ["name": "SelectionEvent", "size": 1880],
                                ["name": "TooltipEvent", "size": 1701],
                                ["name": "VisualizationEvent", "size": 1117]
                            ]
                        ],
                        [
                            "name": "legend",
                            "children": [
                                ["name": "Legend", "size": 20859],
                                ["name": "LegendItem", "size": 4614],
                                ["name": "LegendRange", "size": 10530]
                            ]
                        ],
                        [
                            "name": "operator",
                            "children": [
                                [
                                    "name": "distortion",
                                    "children": [
                                        ["name": "BifocalDistortion", "size": 4461],
                                        ["name": "Distortion", "size": 6314],
                                        ["name": "FisheyeDistortion", "size": 3444]
                                    ]
                                ],
                                [
                                    "name": "encoder",
                                    "children": [
                                        ["name": "ColorEncoder", "size": 3179],
                                        ["name": "Encoder", "size": 4060],
                                        ["name": "PropertyEncoder", "size": 4138],
                                        ["name": "ShapeEncoder", "size": 1690],
                                        ["name": "SizeEncoder", "size": 1830]
                                    ]
                                ],
                                [
                                    "name": "filter",
                                    "children": [
                                        ["name": "FisheyeTreeFilter", "size": 5219],
                                        ["name": "GraphDistanceFilter", "size": 3165],
                                        ["name": "VisibilityFilter", "size": 3509]
                                    ]
                                ],
                                ["name": "IOperator", "size": 1286],
                                [
                                    "name": "label",
                                    "children": [
                                        ["name": "Labeler", "size": 9956],
                                        ["name": "RadialLabeler", "size": 3899],
                                        ["name": "StackedAreaLabeler", "size": 3202]
                                    ]
                                ],
                                [
                                    "name": "layout",
                                    "children": [
                                        ["name": "AxisLayout", "size": 6725],
                                        ["name": "BundledEdgeRouter", "size": 3727],
                                        ["name": "CircleLayout", "size": 9317],
                                        ["name": "CirclePackingLayout", "size": 12003],
                                        ["name": "DendrogramLayout", "size": 4853],
                                        ["name": "ForceDirectedLayout", "size": 8411],
                                        ["name": "IcicleTreeLayout", "size": 4864],
                                        ["name": "IndentedTreeLayout", "size": 3174],
                                        ["name": "Layout", "size": 7881],
                                        ["name": "NodeLinkTreeLayout", "size": 12870],
                                        ["name": "PieLayout", "size": 2728],
                                        ["name": "RadialTreeLayout", "size": 12348],
                                        ["name": "RandomLayout", "size": 870],
                                        ["name": "StackedAreaLayout", "size": 9121],
                                        ["name": "TreeMapLayout", "size": 9191]
                                    ]
                                ],
                                ["name": "Operator", "size": 2490],
                                ["name": "OperatorList", "size": 5248],
                                ["name": "OperatorSequence", "size": 4190],
                                ["name": "OperatorSwitch", "size": 2581],
                                ["name": "SortOperator", "size": 2023]
                            ]
                        ],
                        ["name": "Visualization", "size": 16540]
                    ]
                ]
            ]
        ]
        render(view: 'd3_data_model_view', model: [dataModelJson: dataModelJson])
    }

    /**
     * Check if a data model contains or imports another catalogue element
     * @param id of data model, other id of item to be checked
     */
    def containsOrImports() {

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        CatalogueElement other = CatalogueElement.get(params.other)
        if (!other) {
            notFound()
            return
        }

        if (!other.dataModel) {
            respond(success: false, contains: false, imports: false)
            return
        }

        if (dataModel == other.dataModel) {
            respond(success: true, contains: true, imports: false)
            return
        }

        DataModelFilter filter = DataModelFilter.includes(dataModel).withImports(dataModelGormService.findAll())


        if (filter.isIncluding(other.dataModel)) {
            respond(success: true, contains: false, imports: true)
            return
        }
        respond(success: false, contains: false, imports: false)
    }

    /**
     * Redindex a data model using the elasticsearch service.
     * @param id of data model
     */
    def reindex() {
        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        Long id = dataModel.id

        executorService.submit {
            DataModel model = dataModelGormService.findById(id)
            modelCatalogueSearchService.index(model.declares).subscribe {
                log.info "${model} reindexed"
            }
        }

        respond(success: true)
    }

    /**
     * Get the content of a data model
     * returns all the things that should be displayed in the data model ui
     * @param id of data model
     */

    //TODO: needs work, counts etc are confusing and have been removed from the ui but they should at some point be update properly

    def content() {
        long dataModelId = params.long('id')
        DataModel dataModel = dataModelGormService.findById(dataModelId)
        if (!dataModel) {
            notFound()
            return
        }

        DataModelFilter filter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
        Map<String, Integer> stats = dataModelService.getStatistics(filter)

        ListWithTotalAndType<DataClass> dataClasses = dataClassService.getTopLevelDataClasses(filter, [toplevel: true, status: dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''])

        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            List<Map> contentDescriptors = []

            contentDescriptors << createContentDescriptor(dataModel, 'Data Classes', DataClass, dataClasses.total)

            contentDescriptors << createDataElementsByTagDescriptor(dataModel)
            contentDescriptors << createContentDescriptor(dataModel, 'Data Types', DataType, Integer.MAX_VALUE)
            contentDescriptors << createContentDescriptor(dataModel, 'Measurement Units', MeasurementUnit, stats["totalMeasurementUnitCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Business Rules', ValidationRule, stats["totalValidationRuleCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Assets', Asset, stats["totalAssetCount"])
            contentDescriptors << createContentDescriptor(dataModel, 'Tags', Tag, stats["totalTagCount"])

            if (dataModel.status != ElementStatus.DEPRECATED) {
                Map deprecatedItems = createContentDescriptor(dataModel, 'Deprecated Items', CatalogueElement, stats["deprecatedCatalogueElementCount"])
                deprecatedItems.link = deprecatedItems.link.replace('status=active', 'status=deprecated')
                deprecatedItems.content.link = deprecatedItems.link
                deprecatedItems.status = 'DEPRECATED'
                contentDescriptors << deprecatedItems
            }

            contentDescriptors << createContentDescriptorForRelationship('Imported Data Models', 'imports', dataModel, RelationshipType.importType, RelationshipDirection.OUTGOING)

            if (params.boolean('root')) {
                contentDescriptors << createVersionsDescriptor(dataModel)
            }

            contentDescriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${dataModelId}/content", list)
    }

    /**
     * Get all the data models the import this data model
     * @param id of data model
     */

    def dependents() {
        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            notFound()
            return
        }

        respond dataModelService.findDependents(dataModel)
    }

    /**
     * Get the history or changes for a data model
     * @param id of data model
     */

    //TODO: this needs some work

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        dataModelCatalogueElementService
    }

    def history(Integer max) {
        String name = getResourceName()
        Class type = resource

        params.max = Math.min(max ?: 10, 100)
        long catalogueElementId = params.long('id')
        CatalogueElement element = findById(catalogueElementId)
        if (!element) {
            notFound()
            return
        }

        Long id = element.id

        if (!element.latestVersionId) {
            respond Lists.wrap(params, "/${name}/${catalogueElementId}/history", Lists.lazy(params, type, {
                [type.get(id)]
            }, { 1 }))
            return
        }

        Long latestVersionId = element.latestVersionId

        def customParams = [:]
        customParams.putAll params

        customParams.sort = 'versionNumber'
        customParams.order = 'desc'

        respond Lists.fromCriteria(customParams, type, "/${name}/${catalogueElementId}/history") {
            eq 'latestVersionId', latestVersionId
        }
    }

/**
 * Spreadsheet report
 * @param id of data model
 */
    def inventorySpreadsheet(String name, Integer depth) {

        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }
        Long dataModelId = dataModel.id

        Asset asset = saveAsset(assetMetadataService.assetReportForDataModel(dataModel, name, MicrosoftOfficeDocument.XLSX), dataModel)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream ->
            DataModel dataModelInstance = dataModelGormService.findById(dataModelId)
            CatalogueElementToXlsxExporter exporter = CatalogueElementToXlsxExporter.forDataModel(dataModelInstance, dataClassService, grailsApplication, depth)
            exporter.export(outputStream)
        }

        response.setHeader("X-Asset-ID", asset.id.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
        return
    }

    protected Asset saveAsset(Asset asset, DataModel dataModel) {
        asset.dataModel = dataModel
        assetGormService.save(asset)
    }

    /**
     * Grid Spreadsheet report
     * @param id of data model
     */
    def gridSpreadsheet(String name, Integer depth) {
        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))

        Long dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Asset asset = saveAsset(assetMetadataService.assetReportForDataModel(dataModel, name, MicrosoftOfficeDocument.XLSX), dataModel)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream ->
            GridReportXlsxExporter.create(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    /**
     * Export using "ExcelExporter" to file with suffix .mc.xlsx
     * @param name
     * @param depth
     * @return
     */
    def excelExporterSpreadsheet(String name, Integer depth) {

        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))

        Long dataModelId = dataModel.id

        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }

        Asset asset = saveAsset(assetMetadataService.assetReportForDataModel(dataModel, name, MicrosoftOfficeDocument.XLSX), dataModel)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream ->
            ExcelExporter.create(dataModelGormService.findById(dataModelId), dataClassService, grailsApplication, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    /**
     * Word Document Report
     * @param id of data model
     */
    def inventoryDoc(String name, Integer depth) {
        if (handleReadOnly()) {
            return
        }

        DataModel dataModel = dataModelGormService.findById(params.long('id'))
        if (!dataModel) {
            respond status: HttpStatus.NOT_FOUND
            return
        }
        Long modelId = dataModel.id

        Asset asset = saveAsset(assetMetadataService.assetReportForDataModel(dataModel, name, MicrosoftOfficeDocument.DOC), dataModel)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream ->
            new DataModelToDocxExporter(dataModelGormService.findById(modelId), dataClassService, elementService, customTemplate, DOC_IMAGE_PATH, depth).export(outputStream)
        }

        response.setHeader("X-Asset-ID", assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

//TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private static Map createDataElementsByTagDescriptor(DataModel dataModel) {
        String link = "/tag/forDataModel/${dataModel.getId()}?status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map ret = [:]
        ret.id = 'forDataModel'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = DataElement.name
        ret.name = 'Data Elements'
        ret.content = [count: DataModelService.allTags(dataModel).size() + 2, itemType: Tag.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(DataElement)
        ret.status = dataModel.status.toString()
        ret
    }

    //TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private static Map createContentDescriptor(DataModel dataModel, String name, Class clazz, long count) {
        String link = "/${GrailsNameUtils.getPropertyName(clazz)}?toplevel=true&dataModel=${dataModel.getId()}&status=${dataModel.status != ElementStatus.DEPRECATED ? 'active' : ''}"
        Map ret = [:]
        ret.id = 'all'
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = clazz.name
        ret.name = name
        ret.content = [count: count, itemType: clazz.name, link: link]
        ret.link = link
        ret.resource = GrailsNameUtils.getPropertyName(clazz)
        ret.status = dataModel.status.toString()
        ret
    }

    //TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private static Map createVersionsDescriptor(DataModel dataModel) {
        String link = "/dataModel/${dataModel.getId()}/history"
        Map ret = [:]
        ret.id = link
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = "${DataModel.name}.Versions"
        ret.name = 'Versions'
        ret.content = [count: dataModel.countVersions(), itemType: DataModel.name, link: link]
        ret.link = link
        ret.status = dataModel.status.toString()
        ret
    }

    //TODO: DOCUMENT
    /**
     * not sure what this does
     * @param id of data model
     */
    private
    static Map createContentDescriptorForRelationship(String name, String property, DataModel dataModel, RelationshipType relationshipType, RelationshipDirection direction) {
        String link = "/dataModel/${dataModel.getId()}/${direction.actionName}/${relationshipType.name}"
        Map ret = [:]
        ret.id = link
        ret.dataModels = [CatalogueElementMarshaller.minimalCatalogueElementJSON(dataModel)]
        ret.elementType = Relationships.name
        ret.name = name
        ret.content = [count: dataModel.countRelationshipsByDirectionAndType(direction, relationshipType), itemType: Relationship.name, link: link]
        ret.link = link
        ret.relationshipType = relationshipType
        ret.direction = direction.actionName
        ret.status = dataModel.status.toString()
        ret.element = dataModel
        ret.property = property

        ret
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    @Override
    protected boolean hasUniqueName() {
        true
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    @Override
    protected boolean isFavoriteAfterUpdate() {
        return true
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */

    protected bindRelations(DataModel instance, boolean newVersion, Object objectToBind) {

        if (!allowSaveAndEdit()) {
            unauthorized()
            return
        }
        if (handleReadOnly()) {
            return
        }

        if (objectToBind.declares != null) {
            for (domain in instance.declares.findAll { !(it.id in objectToBind.declares*.id) }) {
                domain.dataModel = null
                FriendlyErrors.failFriendlySave(domain)
            }
            for (domain in objectToBind.declares) {
                CatalogueElement catalogueElement = CatalogueElement.get(domain.id as Long)
                catalogueElement.dataModel = instance
                FriendlyErrors.failFriendlySave(catalogueElement)
            }
        }

        if (objectToBind.policies != null) {
            Set<DataModelPolicy> policies = objectToBind.policies.collect {
                DataModelPolicy.get(it.id)
            } as Set<DataModelPolicy>
            Set<DataModelPolicy> existing = instance.policies ?: Collections.emptySet()
            (policies - existing).each {
                instance.addToPolicies(it)
            }
            (existing - policies).each {
                instance.removeFromPolicies(it)
            }
            FriendlyErrors.failFriendlySave(instance)
        }

    }

    /**
     * not sure what this does - think it collects the fields used in the json?
     * @param id of data model
     */

    @Override
    protected getIncludeFields() {
        def fields = super.includeFields
        fields.removeAll(['declares'])
        fields
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */

    @Override
    protected DataModel createResource() {
        DataModel instance = resource.newInstance()
        bindData instance, getObjectToBind(), [include: includeFields]
        instance
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    protected String getHistoryOrderDirection() {
        'asc'
    }

    //TODO: why is this here?
    /**
     * not sure what this does
     * @param id of data model
     */
    protected String getHistorySortProperty() {
        'semanticVersion'
    }

    /**
     * override the abstract controller method so all effective items for a user is a list of data models based on the general role - rather than a specific data model role
     * i.e. we can view the basic info of data models that we aren't subscribed to
     * @param id of data model
     */

    @Override
    protected ListWrapper<DataModel> getAllEffectiveItems(Integer max) {
        ListWrapper<DataModel> items = findUnfilteredEffectiveItems(max)
        filterUnauthorized(items)
    }

    protected ListWrapper<DataModel> findUnfilteredEffectiveItems(Integer max) {
        //if you only want the active data models (draft and finalised)
        if (params.status?.toLowerCase() == 'active') {
            //if not you can only see finalised models
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'eq' 'status', ElementStatus.FINALIZED
            }), overridableDataModelFilter)
        }

        //if you want models with a specific status
        //check that you can access drafts i.e. you have a viewer role
        //then return the models by the status - providing you have the correct role
        if (params.status) {
            return dataModelService.classified(withAdditionalIndexCriteria(Lists.fromCriteria(params, resource, "/${resourceName}/") {
                'in' 'status', ElementService.getStatusFromParams(params)
            }), overridableDataModelFilter)
        }

        return dataModelService.classified(withAdditionalIndexCriteria(Lists.all(params, resource, "/${resourceName}/")), overridableDataModelFilter)
    }

    protected ListWrapper<DataModel> filterUnauthorized(ListWrapper<DataModel> items) {
        if ( items instanceof ListWithTotalAndTypeWrapper ) {
            ListWithTotalAndTypeWrapper listWithTotalAndTypeWrapperInstance = (ListWithTotalAndTypeWrapper) items
            DetachedCriteria<DataModel> criteria = listWithTotalAndTypeWrapperInstance.list.criteria
            Map<String, Object> params = listWithTotalAndTypeWrapperInstance.list.params
            ListWithTotalAndType<DataModel> listWithTotalAndType = instantiateListWithTotalAndTypeWithCriteria(criteria, params)
            return ListWithTotalAndTypeWrapper.create(listWithTotalAndTypeWrapperInstance.params, listWithTotalAndTypeWrapperInstance.base, listWithTotalAndType)
        }
        items
    }

    protected ListWithTotalAndType<DataModel> instantiateListWithTotalAndTypeWithCriteria(DetachedCriteria<DataModel> criteria, Map<String, Object> params) {
        List<DataModel> dataModelList = dataModelGormService.findAllByCriteria(criteria)
        if ( !dataModelList ) {
            return new ListWithTotalAndTypeImpl<DataModel>(DataModel, [], 0L)
        }
        int total = dataModelList.size()
        dataModelList = MaxOffsetSublistUtils.subList(SortParamsUtils.sort(dataModelList, params), params)
        new ListWithTotalAndTypeImpl<DataModel>(DataModel, dataModelList, total as Long)
    }
    @Override
    def search(Integer max) {
        String search = params.search
        if ( !search ) {
            respond errors: "No query string to search on"
            return
        }
        ParamArgs paramArgs = instantiateParamArgs(max)
        SearchParams searchParams = SearchParams.of(params, paramArgs)
        ListWithTotalAndType<T> results = modelCatalogueSearchService.search(searchParams)
       // ListWithTotalAndType<T> results = getAllEffectiveItems(max)

        respond Lists.wrap(params, "/${resourceName}/search?search=${URLEncoder.encode(search, 'UTF-8')}", results)
    }
}
