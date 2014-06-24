package org.modelcatalogue.core

import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.Mappings
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.Relationships

import javax.servlet.http.HttpServletResponse

abstract class AbstractCatalogueElementController<T> extends AbstractRestfulController<T> {

    static responseFormats = ['json', 'xml', 'xlsx']
    static allowedMethods = [outgoing: "GET", incoming: "GET", addIncoming: "POST", addOutgoing: "POST", removeIncoming: "DELETE", removeOutgoing: "DELETE", mappings: "GET", removeMapping: "DELETE", addMapping: "POST"]

    def relationshipService
    def mappingService

	def uuid(String uuid){
		respond resource.findByModelCatalogueId(uuid)
	}

    AbstractCatalogueElementController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    AbstractCatalogueElementController(Class<T> resource) {
        super(resource, false)
    }

    def incoming(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.INCOMING)
    }

    def outgoing(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.OUTGOING)
    }

    def relationships(Integer max, String type) {
        relationshipsInternal(max, type, RelationshipDirection.BOTH)
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

        def metadata = request.metadata

        if (metadata != null) {
            rel.setExt(metadata)
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

    private relationshipsInternal(Integer max, String typeParam, RelationshipDirection direction) {
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

        ListAndCount listAndCount = relationshipService.getRelationships(params, direction, element, type)
        respondWithLinks new Relationships(
                base: "/${resourceName}/${params.id}/${direction.actionName}" + (typeParam ? "/${typeParam}" : ""),
                owner: element,
                items: listAndCount.list,
                direction: direction,
                total: listAndCount.count,
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

        respondWithLinks new Mappings(
                base: "/${resourceName}/${params.id}/mapping",
                items: list,
                total: total
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

    protected getDefaultSort()  { actionName == 'index' ? 'name'  : null }
    protected getDefaultOrder() { actionName == 'index' ? 'asc'   : null }

}
