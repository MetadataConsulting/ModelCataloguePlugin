package org.modelcatalogue.core

import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWrapper

class DataArchitectController {

    static responseFormats = ['json', 'xml', 'xlsx']

    def dataArchitectService, modelService

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
        def links = ListWrapper.nextAndPreviousLinks(params, baseLink, results.count)

        Elements elements =  new Elements(
                base: baseLink,
                total: results.count,
                items: results.list,
                itemType: DataElement,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10,
                sort: params.sort,
                order: params.order
        )

        respond elements
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
        def links = ListWrapper.nextAndPreviousLinks(params, baseLink, results.count)

        Elements elements =  new Elements(
                base: baseLink,
                total: results.count,
                items: results.list,
                itemType: DataElement,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10,
                sort: params.sort,
                order: params.order
        )

        respond elements
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
        def links = ListWrapper.nextAndPreviousLinks(params, baseLink, results.count)

        Elements elements =  new Elements(
                base: baseLink,
                total: results.count,
                items: results.list,
                itemType: DataElement,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10,
                sort: params.sort,
                order: params.order
        )

        respond elements
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
            def links = ListWrapper.nextAndPreviousLinks(params, baseLink, results.count)

            Elements elements =  new Elements(
                    base: baseLink,
                    total: results.count,
                    items: results.list,
                    itemType: Relationship,
                    previous: links.previous,
                    next: links.next,
                    offset: params.int('offset') ?: 0,
                    page: params.int('max') ?: 10,
                    sort: params.sort,
                    order: params.order
            )

            respond elements

        }else{
            respond "please enter keys"
        }

    }


//    def actionRelationships(){
//
//        def relations = params.relatedElements
//
//        try {
//            def errors = dataArchitectService.createRelationshipByType(relations, "relatedTo")
//        }
//        catch (Exception ex) {
//            //log.error("Exception in handling excel file: "+ ex.message)
//            log.error("Exception in handling excel file")
//            flash.message = "Error while creating relationships`.";
//        }
//
//    }

    protected setSafeMax(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 10000)
            }
            xml {
                params.max = Math.min(max ?: 10, 10000)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }

    }

}
