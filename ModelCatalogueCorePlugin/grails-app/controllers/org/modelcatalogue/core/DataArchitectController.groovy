package org.modelcatalogue.core

import grails.rest.RestfulController
import org.modelcatalogue.core.util.Elements
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWrapper

class DataArchitectController {

    static responseFormats = ['json', 'xml', 'xlsx']

    def dataArchitectService
    def relationshipImporterService

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

        def total = (results.totalCount)?results.totalCount:0
        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/uninstantiatedDataElements", total)

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

        def total = (results.totalCount)?results.totalCount:0
        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/metadataKeyCheck", total)

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

    def createCOSDSynonymRelationships(Integer max){
//        setSafeMax(max)
//        ListAndCount results

        def rows=[]
        try{
            rows = dataArchitectService.createCOSDSynonymRelationships(params)
        }catch(Exception e){
            println(e)
            return
        }
        def headers = ["Source ModelCatalogueId","Relationship", "Destination ModelCatalogueId"]
        try {
            def errors = relationshipImporterService.importRelationships(headers, rows)
        }
        catch(Exception ex)
        {
            //log.error("Exception in handling excel file: "+ ex.message)
            log.error("Exception in handling excel file")
            flash.message ="Error while creating relationships`.";
        }

//        def total = (results)?results.count:0
//        def links = ListWrapper.nextAndPreviousLinks(params, "/dataArchitect/createCOSDSynonymRelationships", total)

//        Elements elements =  new Elements(
//                total: results.count,
//                items: results.list,
//                itemType: DataElement,
//                previous: links.previous,
//                next: links.next,
//                offset: params.int('offset') ?: 0,
//                page: params.int('max') ?: 10
//        )
//
//        respond elements
    }
//    def createCOSDSynonymRelationships(Integer max){
//
//        def rows
//
//        try{
//            rows = dataArchitectService.createCOSDSynonymRelationships(params)
//        }catch(Exception e){
//            println(e)
//            return
//        }
//        // Llamar al servicio to make the relationships
//        def headers = ["Source ModelCatalogueId","Relationship", "Destination ModelCatalogueId"]
//        try {
//            def errors = relationshipImporterService.importRelationships(headers, rows)
//            flash.message = "DataElements have been created.\n with {$errors.size()} errors ( ${errors.toString()} )"
//        }
//        catch(Exception ex)
//        {
//            //log.error("Exception in handling excel file: "+ ex.message)
//            log.error("Exception in handling excel file")
//            flash.message ="Error in importing the excel file.";
//        }
//
//    }
//

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
