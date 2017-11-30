package org.modelcatalogue.core.dataimport.excel.nt.uclh

import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel

/**
 * Created by davidmilward on 31/08/2017.
 */
class OpenEhrExcelLoader extends UCLHExcelLoader {

    OpenEhrExcelLoader(boolean test = false){
        super(test)
    }

    @Override
    Pair<String, List<String>> buildXmlFromWorkbookSheet(Workbook workbook, int index=0, String owner='') {

        if (owner == '') {
            ownerAndGELModelSuffix = ''
        }
        else {
            ownerAndGELModelSuffix = '_' + owner
        }


        Writer stringWriter = new StringWriter()
        CatalogueBuilder catalogueBuilder = new XmlCatalogueBuilder(stringWriter, true)

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        Sheet sheet = workbook.getSheetAt(index);

        List<Map<String, String>> rowMaps = getRowMaps(sheet)
        String suffix = getOwnerSuffixWithRandom()
        if(bTest){
            suffix = getOwnerSuffix()
        }

        String modelName = rowMaps[0]['Current Paper Document  or system name']+  suffix // at the moment we are dealing with just one            UCLH data source, so there will be just one model

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
        String name = alias?:rowMap['GEL Dataset Identifier']
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

    List<String>  loadModel(Workbook workbook,  String owner='') {
        List<String> modelNames = []
        if (owner == '') {
            ownerAndGELModelSuffix = ''
        }
        else {
            ownerAndGELModelSuffix = '_openEHR_' + owner
        }

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        //We convert the data in the spreadsheet to the workLists format
        Map<String,List<Map<String, String>>> workbookMap = [:]
        int sheetno = workbook.size()
        for (int i = 1; i < sheetno ; i++){
            Sheet sheet = workbook.getSheetAt(i)
            String sheetName = sheet.getSheetName()
            List<Map<String, String>> rowMapList =importSheet(sheet)
            workbookMap[sheetName] = rowMapList
        }

        //Iterate through the modelMaps to build new DataModel
        workbookMap.each { String name, List<Map<String, String>> rowMapsForModel ->

            DataModel newModel = getDataModel(name + ownerAndGELModelSuffix)
            modelNames << name

            //Iterate through each row to build an new DataElement
            rowMapsForModel.each{ Map<String, String> rowMap ->
                String ntname = getNTElementName(rowMap)
                String ntdescription = 'OpenEhr Element Description'
                DataElement newElement = new DataElement(name: ntname, description:  ntdescription , DataModel: newModel ).save(flush:true, failOnError: true)
                newElement.setDataModel(newModel)
                //Add in metadata
                openEhrHeaders.each { k, v ->
                    newElement.addExtension(v, rowMap[k])
                }
                Long ref = getMCIdFromSpreadSheet(rowMap)
                //Add metadata for adding in relationship to reference model
                newElement.addExtension("represents", ref as String)
            }
        }

        return modelNames
    }
    /**
     * importSheet
     * For each sheet we iterate through the rows one at a time
     * For each row we store the information in a map
     * The map relates the header (for each column) with the data (for each column)
     * So for each row we get a map of (column-name -> row-value) - this is rowMap
     * The maps are then stored in a list of rowmaps -> rowMapList
     *
     * @param sheet
     */
    List<Map<String, String>> importSheet(Sheet sheet){

        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)
        List<Map<String, String>> rowMapList = []

        while (rowIt.hasNext()) {
            row = rowIt.next()
            if(!isRowEmpty(row)){
                Map<String, String> rowMap = createRowMap(row, headers)
                rowMapList << rowMap
            }
        }
        return rowMapList
    }
}
