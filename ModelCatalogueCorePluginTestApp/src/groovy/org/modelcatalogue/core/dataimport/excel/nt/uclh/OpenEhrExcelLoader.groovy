package org.modelcatalogue.core.dataimport.excel.nt.uclh

import grails.util.Holders
import org.apache.commons.lang.WordUtils
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.elasticsearch.ElasticSearchService
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.PublishingContext

/**
 * Created by davidmilward on 31/08/2017.
 */
class OpenEhrExcelLoader extends ExcelLoader {

    String dataModelName = 'Open EHR Mapping Model'
    def modelCatalogueSearchService = Holders.applicationContext.getBean("elasticSearchService")
    def elementService = Holders.applicationContext.getBean("elementService")

    static Map<String, String> openEhrHeaders = ['Archetype Path Query Statement', 'GEL Dataset Name',	'GEL Dataset Identifier',  'Description'].collectEntries {
        header -> [(header), WordUtils.capitalizeFully(header).replaceAll(/\?/,'')]
    }  // map from header keys to their capitalized forms used as metadata keys

   void loadModel(Workbook workbook,  String dataModelName='Open EHR') {

        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }

        //see if an open EHR model already exists, if not create one
        DataModel openEHRModel =  DataModel.findByName(dataModelName)
        DataModel targetDataModel

        if(!openEHRModel){
            openEHRModel = new DataModel(name: dataModelName).save()
        }else{
            //if one exists, check to see if it's a draft
            // but if it's finalised create a new version
            if(openEHRModel.status == ElementStatus.FINALIZED){
                DraftContext context = DraftContext.userFriendly()
                openEHRModel = elementService.createDraftVersion(openEHRModel, PublishingContext.nextPatchVersion(openEHRModel.semanticVersion), context)
            }
        }



        //we convert the data in the spreadsheet to the workLists format
        Map<String, List<Map<String, String>>> workbookMap = [:]
        int sheetno = workbook.size()
        for (int i = 0; i < sheetno ; i++){
            Sheet sheet = workbook.getSheetAt(i)
            String sheetName = sheet.getSheetName()
            //if this is the first sheet get the data model info
            if(sheetName=="Version Control"){
                Iterator<Row> rowIt = sheet.rowIterator()
                Row row = rowIt.next()
                List<String> dataModelInfo = getRowData(row)
                targetDataModel = elementService.findByModelCatalogueId(DataModel, dataModelInfo[2])
            }else {
                List<Map<String, String>> rowMapList = importSheet(sheet)
                workbookMap[sheetName] = rowMapList
            }
        }

        //Iterate through the modelMaps to build new DataModel
        workbookMap.each { String sectionName, List<Map<String, String>> rowMapsForModel ->

            //see which section / class within the open ehr dataset the element can be found
            DataClass section = DataClass.findByNameAndDataModel(sectionName, openEHRModel)

            if(!section){
                //create new section
                section = new DataClass(name: sectionName, dataModel: openEHRModel).save()
            }

            //Iterate through each row to build a new DataElement or find an existing one
            rowMapsForModel.each{ Map<String, String> rowMap ->

                String dataElementName = sectionName + "." + rowMap.get("GEL Dataset Name")

                //see if the element exists in the current open ehr model
                DataElement openEHRDataElement = DataElement.findByNameAndDataModel(dataElementName, openEHRModel)

                if(!openEHRDataElement){
                    //create a new data element if it doesn't already exist
                    openEHRDataElement = new DataElement(name: dataElementName, description: rowMap.get('Description'), dataModel: openEHRModel).save()
                }

                //add archetype path query statement metadata to the data element
                openEHRDataElement.ext.put("Archetype Path Query Statement", rowMap.get("Archetype Path Query Statement"))
                openEHRDataElement.save()

                //add the data element to the normalised class i.e. ehr class / table / template
                section.addToContains(openEHRDataElement)


                //split the GEL ID in the spreadsheet
                if(rowMap.get("GEL Dataset Identifier") && targetDataModel) {
                    def splitID = rowMap.get("GEL Dataset Identifier").split("\\.")
                    def dataElementToLinkTo
                    //find data element based on model catalogue id from spreadsheet
                    if (splitID.size() > 0) {
                        //FIXME: don't hard code in the data model ID i.e. gel rare disease model - this should be passed in as a parameter
                        dataElementToLinkTo = modelCatalogueSearchService.search(DataElement, [search: splitID[0], dataModel: targetDataModel.id])?.items[0]
                    }

                    //create a relationship between the open ehr model and the
                    if (dataElementToLinkTo) {
                        openEHRDataElement.addToRelatedTo(dataElementToLinkTo)
                    }
                }

            }
        }

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
