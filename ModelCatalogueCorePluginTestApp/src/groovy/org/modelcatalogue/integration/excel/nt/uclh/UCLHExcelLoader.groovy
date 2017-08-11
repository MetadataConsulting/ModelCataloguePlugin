package org.modelcatalogue.integration.excel.nt.uclh

import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.tuple.Pair
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder

import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.integration.excel.ExcelLoader
import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.tuple.Pair

/**
 * Created by david on 04/08/2017.
 */
class UCLHExcelLoader extends ExcelLoader{
    ElementService elementService
    DataModelService dataModelService

    String ownerSuffix = ''
    String randomSuffix = ''
    UCLHExcelLoader(boolean test = false) {
        if (test) {
            randomSuffix = '_' + ((new Random()).nextInt(200) + 1) as String
        }
    }

    String getOwnerSuffixWithRandom(){
        return ownerSuffix+randomSuffix
    }
    String getNTElementName(Map<String,String> rowMap){
        String alias = getElementFromGelName(rowMap) + "_ph"
        String name = rowMap['Data Item Unique Code']?:alias
        return name
    }
     origin/nt-reports

    Long getMCIdFromSpreadSheet(Map<String,String> rowMap) {
        Long id = 0
        try{
            String sId = rowMap['Idno']
            def identity = sId.split("\\.")
            id = identity[0] as Long
        }catch(NumberFormatException ne){//
            //exception catches the case where the row has been
            //filled with data that is not in the expected format
            // - so return id = 0
        }
        return id
    }


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


    @Override
    Pair<String, List<String>> buildXmlFromWorkbookSheet(Workbook workbook, int index=0, String owner='') {

        if (owner == '') {
            ownerSuffix = ''
        }
        else {
            ownerSuffix = '_' + owner
        }

        origin/nt-reports
        Writer stringWriter = new StringWriter()
        CatalogueBuilder catalogueBuilder = new XmlCatalogueBuilder(stringWriter, true)

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


            rowMaps << rowMap
        }

        String modelName = rowMaps[0]['Current Paper Document  or system name']+getOwnerSuffixWithRandom() // at the moment we are dealing with just one            UCLH data source, so there will be just one model
        origin/nt-reports
        List<String> modelNames = [modelName]

        Map<String, String> metadataHeaders = ['Semantic Matching',	'Known issue',	'Immediate solution', 'Immediate solution Owner',
                                               'Long term solution',	'Long term solution owner',	'Data Item', 'Unique Code',
                                               'Related To',	'Part of standard data set',
                                               'Data Completeness','Estimated quality',
                                               'Timely?', 'Comments'].collectEntries {
            header -> [(header), WordUtils.capitalizeFully(header).replaceAll(/\?/,'')]
        } // map from header keys to their capitalized forms used as metadata keys
        String defaultMetadataValue = ''

        catalogueBuilder.build {
            dataModel('name': modelName) {
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'
                rowMaps.each { Map<String, String> rowMap ->
                    dataElement(name: getNTElementName(rowMap)) {
                        metadataHeaders.each {k, v ->
                            ext v, (rowMap[k] ?: defaultMetadataValue)
                        }
                        ext 'represents', "${getMCIdFromSpreadSheet(rowMap)}"
                        //id('mcID1000')
                    }
                }
            }
        }
        return Pair.of(stringWriter.toString(), modelNames)
    }

    @Override
    void addRelationshipsToModels(DataModel sourceDataModel, List<String> destinationModelNames){
        for (String destinationModelName: destinationModelNames) {
            DataModel dataModel = DataModel.findByName(destinationModelName)
            List<DataElement> importedUCLHelements = dataModel.getDataElements()
            for (DataElement destinationDataElement: importedUCLHelements) {
                String mcID = destinationDataElement.ext.get('represents')
                DataElement sourceDataElement = DataElement.findByModelCatalogueIdAndDataModel(mcID, sourceDataModel) // and sourceDataModel?
                if(sourceDataElement) sourceDataElement.addToRelatedTo(destinationDataElement)
            }
        }
    }


}
