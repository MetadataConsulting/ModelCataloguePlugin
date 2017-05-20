package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.CSVService
import org.modelcatalogue.core.util.lists.ListWithTotal
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

class DataArchitectController extends AbstractRestfulController<CatalogueElement> {

    static responseFormats = ['json', 'xlsx']

    def dataArchitectService
    def executorService
    def dataClassService
    @Autowired CSVService csvService

    DataArchitectController() {
        super(CatalogueElement, false)
    }

    def index(){}


    def metadataKeyCheck(Integer max){
        handleParams(max)
        respond Lists.wrap(params, DataElement, "/dataArchitect/metadataKeyCheck", dataArchitectService.metadataKeyCheck(params))
    }

    def getSubModelElements(){
        Long id = params.long('dataClassId') ?: params.long('id')
        respond Lists.lazy(params, DataElement, "/dataArchitect/getSubModelElements") {
            if (id){
                DataClass model = DataClass.get(id)
                ListWithTotalAndType<DataClass> subModels = dataClassService.getInnerClasses(model)
                return dataClassService.getDataElementsFromClasses(subModels.items).items
            }
            return []
        }
    }

    def findRelationsByMetadataKeys(Integer max){
        handleParams(max)
        ListWithTotal results
        def keyOne = params.keyOne
        def keyTwo = params.keyTwo
        if(keyOne && keyTwo) {
            try {
                results = dataArchitectService.findRelationsByMetadataKeys(keyOne, keyTwo, params)
            } catch (Exception e) {
                println(e)
                return
            }

            //FIXME we need new method to do this and integrate it with the ui
            try {
                dataArchitectService.actionRelationshipList(results.items)
            } catch (Exception e) {
                println(e)
                return
            }

            respond Lists.wrap(params, Relationship, "/dataArchitect/findRelationsByMetadataKeys", results)

        }else{
            respond "please enter keys"
        }

    }

    def modelsFromCSV(){
        MultipartFile file = request.getFile('csv')

        if (!file) {
            respond status: HttpStatus.BAD_REQUEST
            return
        }

        List<Object> elements = []

        file.inputStream.withReader {
            elements = dataArchitectService.matchModelsWithCSVHeaders(csvService.readHeaders(it, params.separator ?: ';'))
        }

        respond elements
    }

    def elementsFromCSV(){
        MultipartFile file = request.getFile('csv')

        if (!file) {
            respond status: HttpStatus.BAD_REQUEST
            return
        }

        List<Object> elements = []

        file.inputStream.withReader {
            elements = dataArchitectService.matchDataElementsWithCSVHeaders(csvService.readHeaders(it, params.separator ?: ';'))
        }

        respond elements
    }

    def suggestionsNames() {
        respond dataArchitectService.suggestionsNames
    }

    def generateSuggestions() {
        String suggestion = params.suggestion
        executorService.execute {
            dataArchitectService.generateSuggestions(suggestion)
        }
        respond status: HttpStatus.OK

    }


    def deleteSuggestions() {
        executorService.execute {
            dataArchitectService.deleteSuggestions()
        }
        respond status: HttpStatus.OK

    }

}
