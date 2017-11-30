package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import groovy.transform.Immutable
import groovy.util.logging.Log4j
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportHeaders as Headers
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter as Exporter

/**
 *  Implementation using DefaultCatalogueBuilder. (Doesn't really work.)
 *
 * The DefaultCatalogueBuilder will update changed metadata automatically if the data element stays in place.
 * But it will create a new version of a model (with copies of the data elements) if a new data element appears there (as it would if we moved a data element).
 * It also does not delete anything.
 *
 * If a data element moves from one source system to another, we will need to specify the previous system in the spreadsheet so that we can delete the data element from where it was previously,
 * and also create a new linking relationship to the new element in the new (draft) model.
 *
 * Created by james on 15/08/2017.
 */
@Log4j
class GMCGridReportExcelLoaderDCB extends GMCGridReportExcelLoader {
    DataModelService dataModelService = null
    ElementService elementService = null
    DefaultCatalogueBuilder defaultCatalogueBuilder = null

    GMCGridReportExcelLoaderDCB(DataModelService dataModelService, ElementService elementService) {
        this.dataModelService = dataModelService
        this.elementService = elementService
        this.defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
    }
    void updateFromWorkbookSheet(Workbook workbook, int index=0) {
        Patch patch = getPatchFromWorkbook(workbook, index)
        patch.applyInstructionsAndMoves()
    }
    Patch getPatchFromWorkbook(Workbook workbook, int index=0) {
        Sheet sheet = workbook.getSheetAt(index)
        List<Map<String, String>> rowMaps = getRowMaps(sheet)
        Map<String, List<Map<String, String>>> modelMap = rowMaps.groupBy{it.get(Headers.sourceSystem)}
        Closure instructions = {
            /**
             * create models
             */
            modelMap.each {String modelName,
                           List<Map<String,String>> rowMapsForModel ->
                dataModel('name': modelName){
                    // ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL' // do we need this here? does metadata get carried forward in an update?
                    /**
                     * create data elements within models
                     */
                    rowMapsForModel.each {Map<String, String> rowMap ->
                        String placeholderName = rowMap.get(Headers.relatedTo)
                        if (!ignoreRow(rowMap)) { //only write the related to placeholder if the value is not one of the messages saying either no source or multiple sources
                            dataElement(name: placeholderName){
                                Headers.ntElementMetadataHeaders.eachWithIndex {
                                    header, i ->
                                        String entry = rowMap[header]
                                        String key = Headers.ntElementMetadataKeys[i]
                                        ext key, (entry == Exporter.oneSourceNoMetadataMessage) ?
                                            defaultGMCMetadataValue :
                                            entry
                                }
                                // ext 'represents' "${getMCIdFromSpreadsheet(rowMap)}" // do we need this here?
                            }
                        }
                    }
                }

            }
        }

        return new Patch(
            instructions: instructions,
            rowMaps: rowMaps)

    }

    static class Move {
        String gelDataElementMCID
        String gelDataElementName
        String placeholderName
        DataModel movedFrom
        DataModel movedTo
        void deleteOldAndRelateToNew() {
            DataElement gelDataElement = DataElement.executeQuery(
                'from DataElement d where d.modelCatalogueId=:mcID and d.name=:name',
                [mcID: gelDataElementMCID, name: gelDataElementName]
            )[0]
            //    DataElement.findByModelCatalogueId(gelDataElementMCID)

            DataElement oldPlaceholder = DataElement.executeQuery(
                'from DataElement d where d.name=:name and d.dataModel=:dataModel',
                [name: placeholderName, dataModel: movedFrom]
            )[0]
            oldPlaceholder.deleteRelationships()
            oldPlaceholder.delete(flush:true)

            DataElement newPlaceholder = DataElement.executeQuery(
                'from DataElement d where d.name=:name and d.dataModel=:dataModel',
                [name: placeholderName, dataModel: movedTo]
            )[0]
            gelDataElement.addToRelatedTo(newPlaceholder)
        }

        void justMove() {
            DataElement placeholder = DataElement.findByNameAndDataModel(placeholderName, movedFrom)
            placeholder.dataModel = movedTo
            placeholder.save(flush:true)
        }
    }



    @Immutable
    class Patch {
        Closure instructions // instructions to DefaultCatalogueBuilder
        List<Map<String, String>> rowMaps

        void applyInstructionsAndMoves() {

            Set<String> dataSourceNames = new HashSet<String>(rowMaps.collectMany {
                rowMap ->
                    String name = rowMap.get(Headers.previouslyInSourceSystem)
                    return name ? [name] : []

            })
            /**
             * must be collected before builder builds!
             */
            Map<String, String> previousSemanticVersions = dataSourceNames.collectEntries { name ->
                DataModel dataSource = getDraftModelFromName(name)
                if (!dataSource) {
                    log.error "no data source of name $name"
                }
                return [(name), dataSource.semanticVersion]
            }


            defaultCatalogueBuilder.build instructions

            Map<String, DataModel> currentDataSources = dataSourceNames.collectEntries { name ->
                DataModel dataSource = getDraftModelFromName(name)
                return [(name), dataSource]
            } // must be collected after builder builds!

            /**
             * Data sources with new versions take over their predecessor's relationships
             */
            currentDataSources.each {name, dataSource ->
                if (dataSource.semanticVersion != previousSemanticVersions.get(name)) {
                    takeOverPredecessorRelationships(dataSource)
                }
            }

            /** MUST create & execute moves AFTER build instructions have been carried out
             * and AFTER changed data sources have taken over predecessor's relationships
             * to find the right draft models */
            List<Move> moves = movesFromRowMaps(rowMaps)
            for (Move move: moves) {
                move.deleteOldAndRelateToNew()
            }

        }
    }
    void takeOverPredecessorRelationships(DataModel dataSource) {

        for (DataElement placeholder in dataSource.dataElements) {

            DataElement placeholderPredecessor = placeholder.getIncomingRelationsByType(RelationshipType.getSupersessionType())[0]

            if (placeholderPredecessor) { // may not have a predecessor

                List<Relationship> ppIncomingRelatedTo = placeholderPredecessor.getIncomingRelationsByType(RelationshipType.getRelatedToType())
                List<Relationship> ppOutgoingRelatedTo = placeholderPredecessor.getOutgoingRelationsByType(RelationshipType.getRelatedToType())

                if (ppIncomingRelatedTo.size() != 1) { // predecessor may not have exactly one ...
                    log.info "placeholder predecessor ${placeholderPredecessor} has not exactly one incoming 'related to' relationship, $placeholder has not taken over relationship to gel source model"
                    return
                }

                else {
                    DataElement originalElement = (DataElement) ppIncomingRelatedTo[0].source
                    ppIncomingRelatedTo.each{relationship -> relationship.delete(flush:true)}
                    ppOutgoingRelatedTo.each{relationship -> relationship.delete(flush:true)}
                    originalElement.addToRelatedTo(placeholder)
                }
            }
        }
    }
}
