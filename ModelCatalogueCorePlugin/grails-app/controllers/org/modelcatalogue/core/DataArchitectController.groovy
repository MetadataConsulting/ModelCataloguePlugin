package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.CSVService
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWithTotal
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

        ListWithTotal results = new ListAndCount(count: 0, list: [])
        if (params.modelId || params.id){
            Long id = params.long('modelId') ?: params.long('id')
            Model model = Model.get(id)
            def subModels = modelService.getSubModels(model)
            results = modelService.getDataElementsFromModels(subModels.list)
        }

        def baseLink = "/dataArchitect/getSubModelElements"

        respondWithLinks DataElement, new Elements(
                base: baseLink,
                total: results.total,
                items: results.items
        )
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
                    elements << header
                }
            }

        }

        respond elements
    }


}
