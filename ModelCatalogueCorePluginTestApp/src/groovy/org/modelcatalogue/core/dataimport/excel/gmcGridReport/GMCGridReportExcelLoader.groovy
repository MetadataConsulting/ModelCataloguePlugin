package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportHeaders as Headers

/**
 * Class for loading GMC (Genomic Medical Centre) Grid Reports.
 *
 * Should load excel files of the format exported by what is now called GMCGridReportXlsxExporter.
 * Loading such a file consists of updating the GMC models, and changing the link relationships appropriately.
 * Updating would involve finding the differences between what is said on file
 * (placeholder metadata may change;
 * a placeholder may move from one GMC model to another)
 * and the current GMC models.
 *
 * Assumes that the placeholder data elements and the data sources already exist with exactly
 * the names in the columns 'Related To' (placeholder), 'Source System' (data source).
 *
 * If file x is loaded in, the exporter should produce the same x except with Previously In Source System column set to the new Source System column.
 *
 * Created by james on 24/08/2017.
 */
abstract class GMCGridReportExcelLoader extends ExcelLoader {
    abstract void updateFromWorkbookSheet(Workbook workbook, int index=0)

    static DataModel getDraftModelFromName(String name) {
        return DataModel.executeQuery(
            'from DataModel dm where dm.name=:name and status=:status',
            [name:name, status: ElementStatus.DRAFT]
        )[0]
    }

    static final List<String> ignoreRelatedTo = [GMCGridReportXlsxExporter.noSourceMessage,
                                                 GMCGridReportXlsxExporter.multipleSourcesMessage]

    static String defaultGMCMetadataValue = ''


    static String beforeDot(String s) {
        s.find(/(.*)\./){match, firstSection -> firstSection}
    }
    static boolean ignoreRow(Map<String, String> rowMap) {
        return ignoreRelatedTo.contains(rowMap.get(Headers.relatedTo))
    }
    static List<Move> movesFromRowMaps(List<Map<String, String>> rowMaps) {
        List<Move> moves = []
        for (Map<String, String> rowMap: rowMaps) {
            if (!ignoreRow(rowMap) &&
                rowMap.get(Headers.sourceSystem) !=
                rowMap.get(Headers.previouslyInSourceSystem)) { // if no change, these would be the same
                moves << new Move(
                    gelDataElementMCID: (beforeDot(rowMap.get(Headers.id))),
                    // this may not be the MCID! The exporter tries to write the MCID at first but otherwise does the latestVersionId...
                    gelDataElementName: rowMap.get(Headers.dataElement),
                    placeholderName: rowMap.get(Headers.relatedTo),
                    movedFrom: getDraftModelFromName(rowMap.get(Headers.previouslyInSourceSystem)),
                    movedTo: getDraftModelFromName(rowMap.get(Headers.sourceSystem)))
            }
        }
        return moves
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
}
