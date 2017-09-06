package org.modelcatalogue.core.dataimport.excel.uclh

import groovy.util.logging.Log
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder

import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.apache.commons.lang.WordUtils
import org.apache.commons.lang3.tuple.Pair
import org.modelcatalogue.core.dataimport.excel.gmcGridReport.GMCGridReportXlsxExporter

/**
 * Created by david on 04/08/2017.
 */
@Log
class UCLHExcelLoader extends ExcelLoader{
    ElementService elementService
    DataModelService dataModelService

    String ownerAndGELModelSuffix = ''
    String randomSuffix = ''
    UCLHExcelLoader(boolean test = false) {
        if (test) {
            randomSuffix = '_' + ((new Random()).nextInt(200) + 1) as String
        }
    }

    String getOwnerSuffixWithRandom(){
        return ownerAndGELModelSuffix+randomSuffix
    }


    Long getMCIdFromSpreadSheet(Map<String,String> rowMap) {
        Long id = 0
        try{
            String sId = rowMap['DE ID']?:rowMap['Idno']
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


    String getNTElementName(Map<String,String> rowMap){
        String alias = getElementFromGelName(rowMap) + "_ph"
        String name = rowMap['Data Item Unique Code']?:alias
        return name
    }


    String getElementFromGelName(Map<String,String> rowMap){

        String sName = rowMap['Name']?:rowMap['Data Element Name']?:"Name not provided" //we need to put this into a form to use on the db
        List<String> tokens = sName.tokenize('(') //grab bit before the bracket - Event Reference (14858.3)
        return tokens[0].trim()

    }
    static Map<String, String> metadataHeaders = ['Semantic Matching',	'Known issue',	'Immediate solution', 'Immediate solution Owner',
                                                  'Long term solution',	'Long term solution owner',	'Data Item Unique Code',
                                                  'Related To',	'Part of standard data set',
                                                  'Data Completeness','Estimated quality',
                                                  'Timely?', 'Comments'].collectEntries {
        header -> [(header), WordUtils.capitalizeFully(header).replaceAll(/\?/,'')]
    }  // map from header keys to their capitalized forms used as metadata keys





    List<String>  loadModel(Workbook workbook, String sheetName, String owner='') {

        if (owner == '') {
            ownerSuffix = ''
        }
        else {
            ownerSuffix = '_' + owner
        }

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        //Sheet sheet = workbook.getSheetAt(index);
        Sheet sheet = workbook.getSheet(sheetName)
        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)
        List<Map<String, String>> rowMaps = []

        while (rowIt.hasNext()) {
            row = rowIt.next()
            if(!isRowEmpty(row)){
                Map<String, String> rowMap = createRowMap(row, headers)
                rowMaps << rowMap
            }
        }

        Map<String, List<Map<String, String>>> modelMaps = rowMaps.groupBy{
            String modelName = it.get('Collected from')
            if(modelName){
                it.get('Collected from')+getOwnerSuffixWithRandom()
            }
        }

        List<String> modelNames = modelMaps.keySet() as List<String>

        String defaultMetadataValue = ''
        Closure resultClosure =    {
            modelMaps.each{ String modelName, List<Map<String,String>> modelRowMaps ->
                dataModel('name': modelName) {
                    ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'
                    modelRowMaps.each { Map<String, String> rowMap ->
                        String dename = getNTElementName(rowMap)
                        if(!dename.equalsIgnoreCase('blankcell_ph')) {
                            dataElement(name: dename) {
                                metadataHeaders.each { k, v ->
                                    ext v, (rowMap[k] ?: defaultMetadataValue)
                                }
                                ext 'represents', "${getMCIdFromSpreadSheet(rowMap)}"
                            }
                        }
                    }
                }
            }
        } as Closure
        return Pair.of(resultClosure, modelNames)
    }*/


    List<String>  loadModel(Workbook workbook, String sheetName, String ownerAndGELModel ='') {
        String organization = ''
        if (ownerAndGELModel == '') {
            ownerAndGELModelSuffix = ''
        }
        else {
            ownerAndGELModelSuffix = '_' + ownerAndGELModel
            organization = ownerAndGELModel.split(/_/)[0]
        }

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
        //Sheet sheet = workbook.getSheetAt(index);
        Sheet sheet = workbook.getSheet(sheetName)
        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)
        List<Map<String, String>> rowMaps = []

        while (rowIt.hasNext()) {
            row = rowIt.next()
            if(!isRowEmpty(row)){
                Map<String, String> rowMap = createRowMap(row, headers)
                rowMaps << rowMap
            }
        }
        //Set up a Map of new Models in the spreadsheet
        Map<String, List<Map<String, String>>> modelMaps = rowMaps.groupBy{
            String modelName = it.get('Collected from') ?: it.get('Primary source') ?: it.get('Secondary source')
            if(modelName){
                modelName+getOwnerSuffixWithRandom()
            }
        }
        //Store the list of model names for future usage
        List<String> modelNames = modelMaps.keySet() as List<String>
        Map<String, String> metadataHeaders = ['Semantic Matching',	'Known issue',	'Immediate solution', 'Immediate solution Owner',
                                               'Long term solution',	'Long term solution owner',	'Data Item', 'Unique Code',
                                               'Related To',	'Part of standard data set',
                                               'Data Completeness','Estimated quality',
                                               'Timely?', 'Comments'].collectEntries {
            header -> [(header), WordUtils.capitalizeFully(header).replaceAll(/\?/,'')]
        } // map from header keys to their capitalized forms used as metadata keys


        Date start = new Date()
        log.info("Start import to mc" + ownerAndGELModelSuffix )
        timed("Start import to mc for $ownerAndGELModelSuffix", {
        //Iterate through the modelMaps to build new DataModel
        modelMaps.each { String name, List<Map<String, String>> rowMapsForModel ->

            timed(
            "Start import of model $name",
            {
            DataModel newModel = getDataModel(name)
            newModel.addExtension(GMCGridReportXlsxExporter.organizationMetadataKey, organization)
            //Iterate through each row to build an new DataElement
            rowMapsForModel.each{ Map<String, String> rowMap ->
                String ntname = getNTElementName(rowMap)
                String ntdescription = rowMap['Description'] ?: rowMap['DE Description']

                timed(
                "Start import of element $ntname",
                {
                DataElement newElement = new DataElement(name: ntname, description:  ntdescription , DataModel: newModel ).save(flush:true, failOnError: true)

                newElement.setDataModel(newModel)
                //Add in metadata
                metadataHeaders.each { k, v ->
                    newElement.addExtension(v, rowMap[k])
                }
                Long ref = getMCIdFromSpreadSheet(rowMap)
                //Add metadata for adding in relationship to reference model
                newElement.addExtension("represents", ref as String)
                return newElement.name
                },
                {String newElementName ->
                    "Complete element import, element = $newElementName"
                })

            }
            return newModel.name
            },
            {String newModelName ->
                "Complete model import, model = $newModelName"
            })


        }
        return ''
        },
        {String unused ->
            "Completed import to mc for $ownerAndGELModelSuffix"
        })
        return modelNames
    }
    void timed(String startMessage, Closure<String> block, Closure<String> endMessageFromResultOfBlock) {
        Date start = new Date()
        log.info(startMessage)
        String resultString = block.call()
        Date stop = new Date()
        TimeDuration duration = TimeCategory.minus(stop, start)
        log.info(endMessageFromResultOfBlock(resultString) + " duration: $duration")
    }

    protected DataModel getDataModel(String dmName){
        DataModel newModel = DataModel.executeQuery(
            'from DataModel dm where dm.name=:name and status=:status',
            [name:dmName, status: ElementStatus.DRAFT]
        )[0]
        if((newModel == null )||( newModel.name == null)){
            newModel = new DataModel(name: dmName).save(flush:true, failOnError: true)
        }
        return newModel
    }



    //String defaultGMCMetadataValue = GMCGridReportExcelLoader.defaultGMCMetadataValue



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

        String modelName = rowMaps[0]['Current Paper Document  or system name']+getOwnerSuffixWithRandom() // at the moment we are dealing with just one            UCLH data source, so there will be just one model

        List<String> modelNames = [modelName]

        catalogueBuilder.build {
            dataModel('name': modelName) {
                ext 'http://www.modelcatalogue.org/metadata/#organization', 'UCL'
                rowMaps.each { Map<String, String> rowMap ->
                    dataElement(name: getNTElementName(rowMap)) {
                        metadataHeaders.each {k, v ->
                            ext v, (rowMap[k] ?: defaultGMCMetadataValue)
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
                Date startAddRelate = new Date()
                log.info("Start relate: dest:" + destinationDataElement.name )
                String mcID = destinationDataElement.ext.get('represents')
                DataElement sourceDataElement = DataElement.findAllByModelCatalogueIdAndStatus(mcID, ElementStatus.FINALIZED)[0]
                if(sourceDataElement) {
                    Relationship rs1 = sourceDataElement.createLinkTo(destinationDataElement, RelationshipType.relatedToType, ignoreRules: true, skipUniqueChecking: true)
                    Date stopAddRelate = new Date()
                    TimeDuration tdAddRelate = TimeCategory.minus( stopAddRelate, startAddRelate )
                    log.info("Complete relate dest: " + destinationDataElement.name + ",source:" + sourceDataElement.name + "duration:" + tdAddRelate )
                }else{
                    Date stopAddRelate = new Date()
                    TimeDuration tdAddRelate = TimeCategory.minus( stopAddRelate, startAddRelate )
                    log.info("Complete relate dest: " + destinationDataElement.name + ",source: NOT AVAILABLE"  + "duration:" + tdAddRelate )

                }


            }
        }
    }
}
