package org.modelcatalogue.core

import grails.rest.RestfulController
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWrapper

class DataArchitectController {

    static responseFormats = ['json', 'xml', 'xlsx']

    def dataArchitectService, modelService

    def uninstantiatedDataElements(Integer max){
        setSafeMax(max)
        ListAndCount results

        try{
            results = dataArchitectService.uninstantiatedDataElements(params)
        }catch(Exception e){
            println(e)
            return
        }

        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/uninstantiatedDataElements", results.count)

        Elements elements =  new Elements(
                total: results.count,
                items: results.list,
                itemType: DataElement,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10
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

        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/metadataKeyCheck", results.count)

        Elements elements =  new Elements(
                total: results.count,
                items: results.list,
                itemType: DataElement,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10
        )

        respond elements
    }

    def getSubModelElements(){
        def dataElements = new ListAndCount(count: 0, list: [])
        if(params?.modelId){
            Model model = Model.get(params.modelId)
            def subModels = modelService.getSubModels(model)
            dataElements = modelService.getDataElementsFromModels(subModels.list)
        }

        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/getSubModelElements", dataElements.count)

        Elements elements =  new Elements(
                total: dataElements.count,
                items: dataElements.list,
                itemType: DataElement,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 10
        )

        respond elements

    }


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
