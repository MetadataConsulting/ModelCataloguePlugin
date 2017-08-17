package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import groovy.transform.Immutable
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.gmcGridReport.GMCGridReportHeaders as Headers


/**
 * Class for loading GMC (Genomic Medical Centre) Grid Reports.
 *
 * Should load excel files of the format exported by what is now called GMCGridReportXlsxExporter.
 * This format should eventually become the one format used by all 13 GMCs.
 * The UCLHGridReport links a source data model such as the Cancer Model with GMC models representing data sources.
 * Loading such a file consists of creating or updating the GMC models, and changing the link relationships appropriately.
 * Updating would involve finding the differences between what is said on file
 * (placeholder metadata may change;
 * a placeholder may move from one GMC model to another)
 * and the current GMC models.
 *
 * These GMC models should have draft status, such that they can be easily updated. But if they are finalized,
 * updating would involve creating a new version.
 *
 * TODO: step 1: load excel into an in-memory programmatic/object representation Rep.
 * TODO: step 1.5: determine if the GMC models are to be created or updated
 *
 * TODO: step 2C(reate): create GMC models from Rep.
 *
 * TODO: step 2U(pdate): find differences between the representation and the GMC models.
 * TODO: step 3U: create new versions if necessary
 * TODO: step 4U: apply changes to placeholder metadata
 * TODO: step 5U: move placeholders
 *
 * Obviously the best representation of models is the domain class... of Data Models.
 * This could be created by DefaultCatalogueBuilder. The thing is
 * we don't want to save them.
 *
 * External bits we will need:
 * A loader that returns DataModels (not saved)
 * Diff function,
 * Status checking,
 * New version creating,
 * updating methods.
 *
 * Or maybe the DefaultCatalogueBuilder just updates things automatically!? Wouldn't that be great.
 *
 * Well, the DefaultCatalogueBuilder will update changed metadata automatically if the data element stays in place.
 * But it will create a new version of a model if a new data element appears there (as it would if we moved a data element).
 * It does not delete anything.
 *
 * We will need to specify in the spreadsheet that this data element was previously found in this source system,
 * but now it is in that source system, so that we can delete the data element from where it was previously,
 * and also create a new linking relationship to the new element in the new (draft) model.
 *
 * Created by james on 15/08/2017.
 */
class GMCGridReportExcelLoader extends ExcelLoader {
    ElementService elementService = null
    DataModelService dataModelService = null

    GMCGridReportExcelLoader(ElementService elementService, DataModelService dataModelService) {
        this.elementService = elementService
        this.dataModelService = dataModelService
    }
    void updateFromWorkbook(Workbook workbook, int index=0) {
        Patch patch = getPatchFromWorkbook(workbook, index)
        patch.applyInstructionsAndMoves()
    }
    List<String> ignoreRelatedTo = [GMCGridReportXlsxExporter.noSourceMessage,
                                    GMCGridReportXlsxExporter.multipleSourcesMessage]
    Patch getPatchFromWorkbook(Workbook workbook, int index=0) {
        Sheet sheet = workbook.getSheetAt(index)
        List<Map<String, String>> rowMaps = getRowMaps(sheet)
        Map<String, List<Map<String, String>>> modelMaps = rowMaps.groupBy{it.get(Headers.sourceSystem)}
        Closure instructions = {
            modelMaps.each {String modelName, modelRowMaps ->
                dataModel('name': modelName){
                    // ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL' // do we need this here? does metadata get carried forward in an update?
                    modelRowMaps.each {Map<String, String> rowMap ->
                        String placeholderName = rowMap.get(Headers.relatedTo)
                        if (!ignoreRelatedTo.contains(placeholderName)) {
                            dataElement(name: placeholderName){
                                GMCGridReportXlsxExporter.ntElementMetadataHeaders.each {
                                    header -> ext header, (rowMap[header])
                                }
                                // ext 'represents' "${getMCIdFromSpreadsheet(rowMap)}" // do we need this here?
                            }
                        }
                    }
                }

            }
        }
        List<Move> moves = []
        for (Map<String, String> rowMap: rowMaps) {
            if (rowMap.get(Headers.sourceSystem) !=
                rowMap.get(Headers.previouslyInSourceSystem)) { // if no change, these would be the same
                moves << new Move(
                    gelDataElementMCID: rowMap.get(Headers.id) as Long,
                    // this may not be the MCID! It tries to be at first but it could also be latestVersionId...
                    gelDataElementName: rowMap.get(Headers.dataElement),
                    placeholderName: rowMap.get(Headers.relatedTo),
                    movedFrom: getDraftModelFromName(rowMap.get(Headers.previouslyInSourceSystem)),
                    movedTo: getDraftModelFromName(rowMap.get(Headers.sourceSystem)))
            }
        }
        return new Patch(
            instructions: instructions,
            moves: moves)

    }


    DataModel getDraftModelFromName(String name) {
        return DataModel.executeQuery(
            'from DataModel dm where dm.name=:name and status=:status',
            [name:name, status: ElementStatus.DRAFT]
        )[0]
    }

    @Immutable(copyWith=true)
    class Patch {
        Closure instructions // instructions to DefaultCatalogueBuilder
        List<Move> moves
        void applyInstructionsAndMoves() {
            DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
            defaultCatalogueBuilder.build instructions

            for (Move move: moves) {
                move.changeRelationshipsAndDeleteOld()
            }
        }
    }

    @Immutable
    class Move {
        Long gelDataElementMCID
        String gelDataElementName
        String placeholderName
        DataModel movedFrom
        DataModel movedTo
        void changeRelationshipsAndDeleteOld() {
            DataElement gelDataElement = DataElement.executeQuery(
                'from DataElement d where d.modelCatalogueId=:mcID and d.name=:name',
                [mcID: gelDataElementMCID, name: gelDataElementName]
            )[0]
            //    DataElement.findByModelCatalogueId(gelDataElementMCID)

            DataElement oldPlaceholder = DataElement.executeQuery(
                'from DataElement d where d.name=:name and d.dataModel=:dataModel',
                [name: placeholderName, dataModel: movedFrom]
            )[0]
            Relationship oldRelationship = Relationship.executeQuery(
                'from Relationship r where r.source=:source and r.destination=:destination',
                [source: gelDataElement, destination: oldPlaceholder]
            )[0]

            DataElement newPlaceholder = DataElement.executeQuery(
                'from DataElement d where d.name=:name and d.dataModel=:dataModel',
                [name: placeholderName, dataModel: movedTo]
            )[0]
            oldRelationship.setDestination(newPlaceholder)
            oldRelationship.save(flush:true)
            oldPlaceholder.delete(flush:true)


        }
    }
}
