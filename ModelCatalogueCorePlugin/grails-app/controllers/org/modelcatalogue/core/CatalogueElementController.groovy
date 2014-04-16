package org.modelcatalogue.core

import org.modelcatalogue.core.util.Mappings
import org.modelcatalogue.core.util.Relationships

import javax.servlet.http.HttpServletResponse

abstract class CatalogueElementController<T> extends AbstractRestfulController<T> {

    static responseFormats = ['json', 'xml', 'xlsx']
    static allowedMethods = [outgoing: "GET", incoming: "GET", addIncoming: "POST", addOutgoing: "POST", removeIncoming: "DELETE", removeOutgoing: "DELETE", mappings: "GET", removeMapping: "DELETE", addMapping: "POST"]

    def relationshipService
    def mappingService

    CatalogueElementController(Class<T> resource, boolean readOnly = false) {
        super(resource, readOnly)
    }

    def incoming(Integer max, String type) {
        relationships(max, type, "Destination", "incoming")
    }

    def outgoing(Integer max, String type) {
        relationships(max, type, "Source", "outgoing")
    }

    def addOutgoing(Long id, String type) {
        addRelation(id, type, true)
    }


    def addIncoming(Long id, String type) {
        addRelation(id, type, false)
    }

    def removeOutgoing(Long id, String type) {
        removeRelation(id, type, true)
    }


    def removeIncoming(Long id, String type) {
        removeRelation(id, type, false)
    }

    private removeRelation(Long id, String type, boolean outgoing) {
        def otherSide = parseOtherSide()

        CatalogueElement source = resource.get(id)
        if (!source) {
            notFound()
            return
        }
        RelationshipType relationshipType = RelationshipType.findByName(otherSide.type ? otherSide.type.name : type)
        if (!relationshipType) {
            notFound()
            return

        }
        Class otherSideType
        try {
            otherSideType = Class.forName (otherSide.relation ? otherSide.relation.elementType : otherSide.elementType)
        } catch (ClassNotFoundException ignored) {
            notFound()
            return
        }

        CatalogueElement destination = otherSideType.get(otherSide.relation ? otherSide.relation.id : otherSide.id )
        if (!destination) {
            notFound()
            return
        }
        Relationship old = outgoing ?  relationshipService.unlink(source, destination, relationshipType) :  relationshipService.unlink(destination, source, relationshipType)
        if (!old) {
            notFound()
            return
        }
        response.status = HttpServletResponse.SC_NO_CONTENT
        render "DELETED"
    }

    private addRelation(Long id, String type, boolean outgoing) {
        def otherSide = parseOtherSide()

        CatalogueElement source = resource.get(id)
        if (!source) {
            notFound()
            return
        }
        RelationshipType relationshipType = RelationshipType.findByName(type)
        if (!relationshipType) {
            notFound()
            return

        }
        Class otherSideType
        try {
            otherSideType = Class.forName otherSide.elementType
        } catch (ClassNotFoundException ignored) {
            notFound()
            return
        }

        CatalogueElement destination = otherSideType.get(otherSide.id)
        if (!destination) {
            notFound()
            return
        }
        Relationship rel = outgoing ?  relationshipService.link(source, destination, relationshipType) :  relationshipService.link(destination, source, relationshipType)

        if (rel.hasErrors()) {
            respond rel.errors
            return
        }

        response.status = HttpServletResponse.SC_CREATED
        respond rel
    }

    protected parseOtherSide() {
        def otherSide = [:]

        withFormat {
            json {
                otherSide = request.getJSON()
            }
            xml {
                def xml = request.getXML()
                otherSide.id = xml.@id.text() as Long
                otherSide.elementType = xml.@elementType.text() as String
            }
        }
        otherSide
    }

    private relationships(Integer max, String typeParam, String sourceOrDestination, String incomingOrOutgoing) {
        setSafeMax(max)

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
        def links = nextAndPreviousLinks("/${resourceName}/${params.id}/${incomingOrOutgoing}" + (typeParam ? "/${typeParam}" : ""), total)

        respond new Relationships(
                items: list,
                previous: links.previous,
                next: links.next,
                direction: direction,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }


    def mappings(Integer max){
        setSafeMax(max)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        int total = element.outgoingMappings.size()
        def list = Mapping.findAllBySource(element, params)
        def links = nextAndPreviousLinks("/${resourceName}/${params.id}/mapping", total)

        respond new Mappings(
                items: list,
                previous: links.previous,
                next: links.next,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }

    def removeMapping() {
        addOrRemoveMapping(false)
    }

    def addMapping() {
        addOrRemoveMapping(true)
    }

    private addOrRemoveMapping(boolean add) {
        if (!params.destination || !params.id) {
            notFound()
            return
        }
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        CatalogueElement destination = queryForResource(params.destination)
        if (!destination) {
            notFound()
            return
        }
        if (add) {
            String mappingString = null
            withFormat {
                xml {
                    mappingString = request.getXML().text()
                }
                json {
                    mappingString = request.getJSON().mapping
                }
            }
            Mapping mapping = mappingService.map(element, destination, mappingString)
            if (mapping.hasErrors()) {
                respond mapping.errors
                return
            }
            response.status = HttpServletResponse.SC_CREATED
            respond mapping
            return
        }
        Mapping old = mappingService.unmap(element, destination)
        if (old) {
            response.status = HttpServletResponse.SC_NO_CONTENT
        } else {
            notFound()
        }
    }

}
