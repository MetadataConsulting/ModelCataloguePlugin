package uk.co.mc.core

import grails.rest.RestfulController
import uk.co.mc.core.util.Elements
import uk.co.mc.core.util.Relationships

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
        def links = nextAndPreviousLinks("/${resourceName}/", total)
        respond new Elements(
                total: total,
                elements: list,
                previous: links.previous,
                next: links.next,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }

    def incoming(Integer max, String typeParam) {
        relationships(max, typeParam, "Destination", "incoming")
    }

    def outgoing(Integer max, String typeParam) {
        relationships(max, typeParam, "Source", "outgoing")
    }

    private relationships(Integer max, String typeParam, String sourceOrDestination, String incomingOrOutgoing) {
        params.max = Math.min(max ?: 10, 100)

        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        RelationshipType type = typeParam ? RelationshipType.findByName(typeParam) : null
        if (typeParam && !type) {
            notFound()
            return
        }

        int total = type ? Relationship."countBy${sourceOrDestination}AndRelationshipType"(element, type) : (element."${incomingOrOutgoing}Relationships".size() ?: 0)
        def list = type ? Relationship."findAllBy${sourceOrDestination}AndRelationshipType"(element, type, params) : Relationship."findAllBy${sourceOrDestination}"(element, params)
        def direction = sourceOrDestination == "Source" ? "sourceToDestination" : "destinationToSource"
        def links = nextAndPreviousLinks("/${resourceName}/${incomingOrOutgoing}/${params.id}" + (typeParam ? "/${typeParam}" : ""), total)

        respond new Relationships(
                relationships: list,
                previous: links.previous,
                next: links.next,
                direction: direction,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
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
