package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.CSVService
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

class DataArchitectController<T> extends AbstractRestfulController<T>{

    static responseFormats = ['json', 'xml', 'xlsx']

    def dataArchitectService
    def modelService
    @Autowired CSVService csvService

    DataArchitectController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    DataArchitectController(Class<T> resource) {
        super(resource, false)
    }

    def index(){}

    def uninstantiatedDataElements(Integer max){
        handleParams(max)
        ListWithTotal results

        try{
            results = dataArchitectService.uninstantiatedDataElements(params)
        }catch(Exception e){
            println(e)
            return
        }

        def baseLink = "/dataArchitect/uninstantiatedDataElements"
        def total = (results.total)?results.total.intValue():0

        Elements elements =  new Elements(
                base: baseLink,
                total: total,
                items: results.items
        )

        respondWithLinks elements
    }


    def metadataKeyCheck(Integer max){
        handleParams(max)
        ListWithTotal results

        try{
            results = dataArchitectService.metadataKeyCheck(params)
        }catch(Exception e){
            println(e)
            return
        }


        def baseLink = "/dataArchitect/metadataKeyCheck"

        Elements elements =  new Elements(
                base: baseLink,
                total: results.total,
                items: results.items
        )

        respondWithLinks elements
    }

    def getSubModelElements(){
        Long id = params.long('modelId') ?: params.long('id')
        reportCapableRespond Lists.lazy(params, Model, "/dataArchitect/getSubModelElements", "elements")  {
            if (id){
                Model model = Model.get(id)
                ListWithTotalAndType<Model> subModels = modelService.getSubModels(model)
                return modelService.getDataElementsFromModels(subModels.items).items
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
                dataArchitectService.actionRelationshipList(results.list)
            } catch (Exception e) {
                println(e)
                return
            }

            def baseLink = "/dataArchitect/findRelationsByMetadataKeys"
            Elements elements =  new Elements(
                    base: baseLink,
                    total: results.total,
                    items: results.list,
            )

            respondWithLinks elements

        }else{
            reportCapableRespond "please enter keys"
        }

    }

    def elementsFromCSV(){
        MultipartFile file = request.getFile('csv')

        if (!file) {
            respond status: HttpStatus.BAD_REQUEST
            return
        }

        List<Object> elements = []

        file.inputStream.withReader {
            String[] headers = csvService.readHeaders(it, params.separator ?: ';')

            for (String header in headers) {
                def element = DataElement.findByNameIlikeAndStatus(header, PublishedElementStatus.FINALIZED)
                if (!element) {
                    element = DataElement.findByModelCatalogueId(header)
                }
                if (!element) {
                    if (header.contains('_')) {
                        element = DataElement.findByNameIlikeAndStatus(header.replace('_', ' '), PublishedElementStatus.FINALIZED)
                    } else {
                        element = DataElement.findByNameIlikeAndStatus(header.replace(' ', '_'), PublishedElementStatus.FINALIZED)
                    }
                }
                if (!element) {
                    element = DataElement.findByNameIlikeAndStatus(header, PublishedElementStatus.DRAFT)
                }
                if (!element) {
                    if (header.contains('_')) {
                        element = DataElement.findByNameIlikeAndStatus(header.replace('_', ' '), PublishedElementStatus.DRAFT)
                    } else {
                        element = DataElement.findByNameIlikeAndStatus(header.replace(' ', '_'), PublishedElementStatus.DRAFT)
                    }
                }
                if (element) {
                    elements << element
                } else {
                    def searchResult = modelCatalogueSearchService.search(DataElement, [search: header])
                    // only if we have single hit
                    if (searchResult.total == 1) {
                        elements << searchResult.searchResults[0]
                    } else {
                        elements << header
                    }
                }
            }

        }

        respond elements
    }


}
