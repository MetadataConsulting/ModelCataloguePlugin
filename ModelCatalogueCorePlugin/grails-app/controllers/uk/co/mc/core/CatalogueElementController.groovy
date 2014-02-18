package uk.co.mc.core

import grails.rest.RestfulController

abstract class CatalogueElementController<T> extends RestfulController<T> {

    static responseFormats = ['json', 'xml']

    CatalogueElementController(Class<T> resource) {
        super(resource)
    }

    @Override
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def total = countResources()
        def list = listAllResources(params)
        def model = [
                success: true,
                total: total,
                size: list.size(),
                list: list
        ]
        model.putAll nextAndPreviousLinks("/${resourceName}/", total)
        respond model
    }

    def incoming(Serializable id, Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def element = queryForResource(id)
        if (!element) {
            notFound()
            return
        }
        int total = Relationship.countByDestination(element)
        def list = Relationship.findAllByDestination(element, params)
        def model = [
                success: true,
                total: total,
                size: list.size(),
                list: list
        ]
        model.putAll nextAndPreviousLinks("/${resourceName}/incoming/${id}", total)
        respond model
    }

    def outgoing(Serializable id, Integer max) {
        params.max = Math.min(max ?: 10, 100)
        def element = queryForResource(id)
        if (!element) {
            notFound()
            return
        }
        int total = Relationship.countBySource(element)
        def list = Relationship.findAllBySource(element, params)
        def model = [
                success: true,
                total: total,
                size: list.size(),
                list: list
        ]
        model.putAll nextAndPreviousLinks("/${resourceName}/outgoing/${id}", total)
        respond model
    }


    private Map<String, String> nextAndPreviousLinks(String baseLink, Integer total) {
        def link = "${baseLink}?"
        if (params.max) {
            link += "max=${params.max}"
        }
        if (params.sort) {
            link += "&sort=${params.sort}"
        }
        if (params.order) {
            link += "&order=${params.order}"
        }
        def nextLink = ""
        def previousLink = ""
        if (params?.max && params.max < total) {
            def offset = (params?.offset) ? params?.offset?.toInteger() : 0
            def prev = offset - params?.max
            def next = offset + params?.max
            if (next < total) {
                nextLink = "${link}&offset=${next}"
            }
            if (prev >= 0) {
                previousLink = "${link}&offset=${prev}"
            }
        }
        [
                next: nextLink,
                previous: previousLink
        ]
    }
}
