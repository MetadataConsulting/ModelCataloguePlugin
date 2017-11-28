package org.modelcatalogue.core.dataimport.excel.gmcGridReport

import groovy.util.logging.Log4j
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportHeaders as Headers
import org.modelcatalogue.core.dataexport.excel.gmcgridreport.GMCGridReportXlsxExporter

/**
 * The GMCGridReportExcelLoaderDCB using the defaultCatalogueBuilder has been
 * hard to get working properly, so I will try doing things a bit more directly.
 * Created by james on 23/08/2017.
 */
@Log4j
class GMCGridReportExcelLoaderDirect extends GMCGridReportExcelLoader {

    GMCGridReportExcelLoaderDirect() {
    }

    void updateFromWorkbookSheet(Workbook workbook, int index = 0) {
        Sheet sheet = workbook.getSheetAt(index)
        List<Map<String, String>> rowMaps = getRowMaps(sheet)
        Map<String, List<Map<String, String>>> modelMap = rowMaps.groupBy{it.get(Headers.sourceSystem)}

        // create new data elements if necessary? This shouldn't happen; all necessary placeholders should already exist.

        // move data elements from old systems
        log.info("Moving data elements between data sources")
        def moves = movesFromRowMaps(rowMaps)
        moves.each {it.justMove()}

        // update metadata for already existing data elements
        log.info("Updating metadata for placeholders (now moved)")
        modelMap.each{String name, List<Map<String,String>> rowMapsForModel ->
            DataModel dataSource = getDraftModelFromName(name)
            if (!dataSource) {
                log.info("No draft data source of name $name found")
            }
            else {
                rowMapsForModel.each {Map<String,String> rowMap ->
                    String placeholderName = rowMap.get(Headers.relatedTo)
                    if (!ignoreRelatedTo.contains(placeholderName)) {
                        DataElement placeholder = dataSource.dataElements.find {
                            it.name == placeholderName
                        }
                        if (!placeholder) {
                            log.info("No placeholder of name $placeholderName found in draft data model $dataSource.name")
                        }
                        else {
                            Headers.ntElementMetadataHeaders.eachWithIndex {
                                header, i ->
                                    String entry = rowMap[header]
                                    String key = Headers.ntElementMetadataKeys[i]
                                    placeholder.ext.setProperty(key,
                                        (entry == GMCGridReportXlsxExporter.oneSourceNoMetadataMessage) ?
                                            defaultGMCMetadataValue : entry)
                            }
                            placeholder.save(flush:true)
                        }
                    }

                }
            }
        }
    }
}
