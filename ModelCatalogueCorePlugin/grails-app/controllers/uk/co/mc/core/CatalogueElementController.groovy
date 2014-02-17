package uk.co.mc.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils

abstract class CatalogueElementController<T> extends RestfulController<CatalogueElement>{

    static responseFormats = ['json', 'xml']

    CatalogueElementController(Class<T> resource) {
        super(resource)
    }

    @Override
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def total = resource.count()
        def list = listAllResources(params)
        def link = "/${resourceClassName}/?"
        if(params.max){ link +="max=${params.max}"}
        if(params.sort){ link += "&sort=${params.sort}"}
        if(params.order){ link += "&order=${params.order}"}
        def nextOffset
        def previousOffset
        if(params?.max && params.max<total){
            def offset = (params?.offset) ? params?.offset.toInteger() : 0
            def prev =  offset - params?.max
            def next = offset + params?.max
            if( next < total){nextOffset = "${link}&offset=${next}"}
            if( prev >= 0 ){previousOffset ="${link}&offset=${prev}"}
        }

        def model=  [
                success:    true,
                total:      total,
                size:       list.size(),
                list:       list,
                next: nextOffset,
                previous: previousOffset,
        ]
        respond model
    }
}
