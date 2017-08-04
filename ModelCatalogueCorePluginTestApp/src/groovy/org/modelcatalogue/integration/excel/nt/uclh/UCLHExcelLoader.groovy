package org.modelcatalogue.integration.excel.nt.uclh

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.integration.excel.ExcelLoader

/**
 * Created by david on 04/08/2017.
 */
class UCLHExcelLoader extends ExcelLoader{


    String getNTElementName(Map<String,String> rowMap){
        String alias = getElementFromGelName(rowMap) + "_ph"
        String name = rowMap['Data Item Unique Code']?:alias
        return name
    }

    String getElementFromGelName(Map<String,String> rowMap){

        String sName = rowMap['Name']  //we need to put this into a form to use on the db
        List<String> tokens = sName.tokenize('(') //grab bit before the bracket - Event Reference (14858.3)
        return tokens[0].trim()

    }

    void buildXmlFromWorkbookSheet(Workbook workbook, CatalogueBuilder catalogueBuilder, int index=0) {

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        Sheet sheet = workbook.getSheetAt(index);

        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)

        List<Map<String, String>> rowMaps = []
        while (rowIt.hasNext()) {
            row = rowIt.next()
            Map<String, String> rowMap = createRowMap(row, headers)

            /*def canBeInserted = false;
            rowMap.eachWithIndex { def entry, int i ->
                if (entry != null && entry != "")
                    canBeInserted = true;
            }
            if (canBeInserted)*/
            rowMaps << rowMap
        }
        catalogueBuilder.build {
            dataModel('name': rowMaps[0]['Current Paper Document  or system name']) {
                rowMaps.each { Map<String, String> rowMap ->
                    dataElement(name: getNTElementName(rowMap))
                }
            }

        }
    }


}
