package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.export.inventory.DataClassToDocxExporter
import org.modelcatalogue.core.export.inventory.DataClassToXlsxExporter
import org.modelcatalogue.core.publishing.changelog.ChangelogGenerator
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Relationships

class DataClassController extends AbstractCatalogueElementController<DataClass> {

    def dataClassService

    DataClassController() {
        super(DataClass, false)
    }

    @Override
    protected ListWithTotalAndType<DataClass> getAllEffectiveItems(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.getAllEffectiveItems(max)
        }
        return Lists.wrap(params, "/${resourceName}/", dataClassService.getTopLevelDataClasses(overridableDataModelFilter, params))
    }

    def referenceTypes(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        DataClass dataClass = queryForResource(params.id)
        if (!dataClass) {
            notFound()
            return
        }

        respond dataModelService.classified(Lists.fromCriteria(params, ReferenceType, "/${resourceName}/${params.id}/referenceType") {
            eq "dataClass", dataClass
            if (!all && !dataClass.attach().archived) {
                ne 'status', ElementStatus.DEPRECATED
                ne 'status', ElementStatus.UPDATED
                ne 'status', ElementStatus.REMOVED
            }
        })

    }

    def content(Integer max) {
        handleParams(max)

        params.sort = 'outgoingIndex'

        DataClass element = queryForResource(params.id)

        if (!element) {
            notFound()
            return
        }

        DataModelFilter filter = DataModelFilter.from(modelCatalogueSecurityService.currentUser)

        respond new Relationships(
                owner: element,
                direction: RelationshipDirection.OUTGOING,
                list: Lists.fromCriteria(params, Relationship, "/${resourceName}/${params.id}/content") {
                    join 'destination'
                    eq 'source', element
                    inList 'relationshipType', [RelationshipType.containmentType, RelationshipType.hierarchyType]

                    if (filter) {
                        or {
                            isNull 'dataModel'
                            and {
                                if (filter.excludes) {
                                    not {
                                        'in' 'dataModel.id', filter.excludes
                                    }
                                }
                                if (filter.includes) {
                                    'in'  'dataModel.id', filter.includes
                                }
                            }
                        }
                    }

                    sort('outgoingIndex')
                }
        )
    }

    def inventoryDoc(String name, Integer exportDepth) {
        DataClass model = DataClass.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                model.dataModel,
                name: name ? name : "${model.name} report as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )  { OutputStream out ->
            new DataClassToDocxExporter(DataClass.get(modelId), dataClassService, exportDepth).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def inventorySpreadsheet(String name, Integer exportDepth) {
        DataClass model = DataClass.get(params.id)

        Long modelId = model.id
        def assetId= assetService.storeReportAsAsset(
                model.dataModel,
                name: name ? name : "${model.name} report as MS Excel Document",
                originalFileName: "${model.name}-${model.status}-${model.version}.xlsx",
                contentType: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )  { OutputStream out ->
            new DataClassToXlsxExporter(DataClass.get(modelId), dataClassService, exportDepth).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def changelogDoc(String name, Integer exportDepth) {
        DataClass model = DataClass.get(params.id)

        Long modelId = model.id
        def assetId = assetService.storeReportAsAsset(
                model.dataModel,
                name: name ? name : "${model.name} changelog as MS Word Document",
                originalFileName: "${model.name}-${model.status}-${model.version}-changelog.docx",
                contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        ) { OutputStream out ->
            new ChangelogGenerator(auditService, dataClassService, exportDepth).generateChangelog(DataClass.get(modelId), out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }
}
