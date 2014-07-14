package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.SimpleListWrapper

class DataArchitectController<T> extends AbstractRestfulController<T>{

    static responseFormats = ['json', 'xml', 'xlsx']

    def dataArchitectService, modelService

    DataArchitectController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    DataArchitectController(Class<T> resource) {
        super(resource, false)
    }

    def index(){}

    def uninstantiatedDataElements(Integer max){
        setSafeMax(max)
        ListAndCount results

        try{
            results = dataArchitectService.uninstantiatedDataElements(params)
        }catch(Exception e){
            println(e)
            return
        }

        def baseLink = "/dataArchitect/uninstantiatedDataElements"
        def total = (results.count)?results.count.intValue():0

        Elements elements =  new Elements(
                base: baseLink,
                total: total,
                items: results.list
        )

        respondWithLinks elements
    }


    def metadataKeyCheck(Integer max){
        setSafeMax(max)
        ListAndCount results

        try{
            results = dataArchitectService.metadataKeyCheck(params)
        }catch(Exception e){
            println(e)
            return
        }


        def baseLink = "/dataArchitect/metadataKeyCheck"
        def links = SimpleListWrapper.nextAndPreviousLinks(params, baseLink, results.count)

        Elements elements =  new Elements(
                base: baseLink,
                total: results.count,
                items: results.list
        )

        respondWithLinks elements
    }

    def getSubModelElements(){

        def results = new ListAndCount(count: 0, list: [])
        if (params.modelId || params.id){
            Long id = params.long('modelId') ?: params.long('id')
            Model model = Model.get(id)
            def subModels = modelService.getSubModels(model)
            results = modelService.getDataElementsFromModels(subModels.list)
        }

        def baseLink = "/dataArchitect/getSubModelElements"

        respondWithLinks DataElement, new Elements(
                base: baseLink,
                total: results.count,
                items: results.list
        )
    }

    def findRelationsByMetadataKeys(Integer max){
        setSafeMax(max)
        def results
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
                    total: results.count,
                    items: results.list,
            )

            respondWithLinks elements

        }else{
            respond "please enter keys"
        }

    }


}
