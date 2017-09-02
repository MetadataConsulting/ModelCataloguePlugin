package org.modelcatalogue.core.dataimport.excel.nt.uclh

import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.uclh.UCLHExcelLoader

/**
 * Created by davidmilward on 31/08/2017.
 */
class OpenEhrExcelLoader extends UCLHExcelLoader {


    @Override
    Pair<String, List<String>> buildXmlFromWorkbookSheet(Workbook workbook, int index=0, String owner='') {

        if (owner == '') {
            ownerSuffix = ''
        }
        else {
            ownerSuffix = '_' + owner
        }


        Writer stringWriter = new StringWriter()
        CatalogueBuilder catalogueBuilder = new XmlCatalogueBuilder(stringWriter, true)

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        Sheet sheet = workbook.getSheetAt(index);

        List<Map<String, String>> rowMaps = getRowMaps(sheet)

        String modelName = rowMaps[0]['Current Paper Document  or system name']+getOwnerSuffixWithRandom() // at the moment we are dealing with just one            UCLH data source, so there will be just one model

        List<String> modelNames = [modelName]

        catalogueBuilder.build {
            dataModel('name': modelName) {
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'
                rowMaps.each { Map<String, String> rowMap ->
                    dataElement(name: getNTElementName(rowMap)) {
                        openEhrHeaders.each {k, v ->
                            ext v, (rowMap[k] ?: 'unavailable')
                        }
                        //ext 'represents', "${getMCIdFromSpreadSheet(rowMap)}"
                    }
                }
            }
        }
        return Pair.of(stringWriter.toString(), modelNames)
    }


    @Override
    String getNTElementName(Map<String,String> rowMap){
        String alias = getElementFromGelName(rowMap) + "_ph"
        String name = rowMap['Data Item Unique Code']?:alias
        return name
    }

    @Override
    String getElementFromGelName(Map<String,String> rowMap){

        String sName = rowMap['GEL Dataset Name']?:"Name not provided" //we need to put this into a form to use on the db
        List<String> tokens = sName.tokenize('(') //grab bit before the bracket - Event Reference (14858.3)
        return tokens[0].trim()

    }

    @Override
    Long getMCIdFromSpreadSheet(Map<String,String> rowMap) {
        Long id = 0
        try{
            String sId = rowMap['GEL Dataset Identifier']?:'unavailable'
            if(sId){
                def identity = sId.split("\\.")
                id = identity[0] as Long
            }
        }catch(NumberFormatException ne){//
            //exception catches the case where the row has been
            //filled with data that is not in the expected format
            // - so return id = 0
        }
        return id
    }

    static Map<String, String> openEhrHeaders = ['Archetype Path Query Statement',	'GEL Dataset Identifier'].collectEntries {
        header -> [(header), WordUtils.capitalizeFully(header).replaceAll(/\?/,'')]
    }  // map from header keys to their capitalized forms used as metadata keys






}
