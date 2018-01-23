package org.modelcatalogue.core

import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.asset.MicrosoftOfficeDocument
import org.modelcatalogue.core.catalogueelement.DataClassCatalogueElementService
import org.modelcatalogue.core.catalogueelement.ManageCatalogueElementService
import org.modelcatalogue.core.export.inventory.DataClassToDocxExporter
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWrapper
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Relationships

class DataClassController extends AbstractCatalogueElementController<DataClass> {

    PerformanceUtilService performanceUtilService
    DataClassGormService dataClassGormService
    DataClassCatalogueElementService dataClassCatalogueElementService
    AssetGormService assetGormService
    AssetMetadataService assetMetadataService

    DataClassController() {
        super(DataClass, false)
    }

    def referenceTypes(Integer max){
        handleParams(max)

        Boolean all = params.boolean('all')

        DataClass dataClass = findById(params.long('id'))
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

        DataClass element = findById(params.long('id'))

        if (!element) {
            notFound()
            return
        }

        DataModelFilter filter = overridableDataModelFilter

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

    def inventoryDoc(String name, Integer depth) {
        if (handleReadOnly()) {
            return
        }


        DataClass dataClass = dataClassGormService.findById(params.long('id'))

        Asset asset = saveAsset(assetMetadataService.assetReportForDataClass(dataClass, name, MicrosoftOfficeDocument.DOC), dataClass)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream outputStream ->
            new DataClassToDocxExporter(dataClass, dataClassService, depth, elementService).export(outputStream)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def inventorySpreadsheet(String name, Integer depth) {
        if (handleReadOnly()) {
            return
        }

        DataClass dataClass = dataClassGormService.findById(params.long('id'))

        Long dataClassId = dataClass.id

        Asset asset = saveAsset(assetMetadataService.assetReportForDataClass(dataClass, name, MicrosoftOfficeDocument.XLSX), dataClass)
        Long assetId = asset.id

        assetService.storeReportAsAsset(asset.id, asset.contentType)  { OutputStream out ->
            CatalogueElementToXlsxExporter.forDataClass(DataClass.get(dataClassId), dataClassService, grailsApplication, depth).export(out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    def changelogDoc(String name, Integer depth, Boolean includeMetadata) {
        if (handleReadOnly()) {
            return
        }

        DataClass dataClass = dataClassGormService.findById(params.long('id'))

        Long dataClassId = dataClass.id
        Asset asset = saveAsset(assetMetadataService.assetReportForDataClass(dataClass, name, MicrosoftOfficeDocument.DOC), dataClass)
        Long assetId = asset.id

        assetService.storeReportAsAsset(assetId, asset.contentType) { OutputStream out ->
            new ChangeLogDocxGenerator(auditService, dataClassService, performanceUtilService, elementService, depth, includeMetadata)
                .generateChangelog(DataClass.get(dataClassId), out)
        }

        response.setHeader("X-Asset-ID",assetId.toString())
        redirect controller: 'asset', id: assetId, action: 'show'
    }

    protected Asset saveAsset(Asset asset, DataClass dataClass) {
        asset.dataModel = dataClass.dataModel
        assetGormService.save(asset)
    }

    @Override
    protected ManageCatalogueElementService getManageCatalogueElementService() {
        dataClassCatalogueElementService
    }

    @Override
    protected ListWrapper<DataClass> getAllEffectiveItems(Integer max) {
        if (!params.boolean("toplevel")) {
            return super.getAllEffectiveItems(max)
        }
        return Lists.wrap(params, "/${resourceName}/", dataClassService.getTopLevelDataClasses(overridableDataModelFilter, params))
    }

    protected DataClass findById(long id) {
        dataClassGormService.findById(id)
    }
}
