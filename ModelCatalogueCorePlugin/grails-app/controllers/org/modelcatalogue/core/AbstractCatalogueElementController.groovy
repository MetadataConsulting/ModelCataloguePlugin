package org.modelcatalogue.core

import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.Mappings
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.Relationships
import org.modelcatalogue.core.util.SimpleListWrapper
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletResponse

abstract class AbstractCatalogueElementController<T> extends AbstractRestfulController<T> {

    static responseFormats = ['json', 'xml', 'xlsx']
    static allowedMethods = [outgoing: "GET", incoming: "GET", addIncoming: "POST", addOutgoing: "POST", removeIncoming: "DELETE", removeOutgoing: "DELETE", mappings: "GET", removeMapping: "DELETE", addMapping: "POST"]

    def relationshipService
    def mappingService

	def uuid(String uuid){
		reportCapableRespond resource.findByModelCatalogueId(uuid)
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
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

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

        if (old.hasErrors()) {
            reportCapableRespond old.errors
            return
        }

        response.status = HttpServletResponse.SC_NO_CONTENT
        render "DELETED"
    }

    private addRelation(Long id, String type, boolean outgoing) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

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
            reportCapableRespond rel.errors
            return
        }

        def metadata = objectToBind.metadata

        if (metadata != null) {
            rel.setExt(metadata)
        }

        response.status = HttpServletResponse.SC_CREATED
        reportCapableRespond rel
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
        handleParams(max)

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

        reportCapableRespond new Relationships(
                owner: element,
                direction: direction,
                list: Lists.fromCriteria(params, "/${resourceName}/${params.id}/${direction.actionName}" + (typeParam ? "/${typeParam}" : ""), "relationships", direction.composeWhere(element, type))
        )
    }

    def searchIncoming(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.INCOMING)
    }

    def searchOutgoing(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.OUTGOING)
    }


    def searchRelationships(Integer max, String type) {
        searchWithinRelationshipsInternal(max, type, RelationshipDirection.BOTH)
    }

    private searchWithinRelationshipsInternal(Integer max, String type, RelationshipDirection direction){
        CatalogueElement element = queryForResource(params.id)

        if (!element) {
            notFound()
            return
        }

        RelationshipType relationshipType = RelationshipType.findByName(type)

        handleParams(max)
        def results =  modelCatalogueSearchService.search(element, relationshipType, direction, params)

        if(results.errors){
            reportCapableRespond results
            return
        }

        def total = (results.total)?results.total.intValue():0

        SimpleListWrapper<Relationship> elements = new SimpleListWrapper<Relationship>(
                base: "/${resourceName}/${params.id}/${direction.actionName}" + (type ? "/${type}" : "") + "/search?search=${params.search?.encodeAsURL() ?: ''}",
                total: total,
                items: results.searchResults,
        )
        reportCapableRespond new Relationships(owner: element, direction: direction, list: withLinks(elements))
    }


    def mappings(Integer max){
        handleParams(max)
        CatalogueElement element = queryForResource(params.id)
        if (!element) {
            notFound()
            return
        }

        reportCapableRespond new Mappings(list: Lists.fromCriteria(params, Mapping, "/${resourceName}/${params.id}/mapping", "mappings") {
            eq 'source', element
        })
    }

    def removeMapping() {
        addOrRemoveMapping(false)
    }

    def addMapping() {
        addOrRemoveMapping(true)
    }

    private addOrRemoveMapping(boolean add) {
        if (!modelCatalogueSecurityService.hasRole('CURATOR')) {
            notAuthorized()
            return
        }

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
                json {
                    mappingString = request.getJSON().mapping
                }
                xml {
                    mappingString = request.getXML().text()
                }
            }
            Mapping mapping = mappingService.map(element, destination, mappingString)
            if (mapping.hasErrors()) {
                reportCapableRespond mapping.errors
                return
            }
            response.status = HttpServletResponse.SC_CREATED
            reportCapableRespond mapping
            return
        }
        Mapping old = mappingService.unmap(element, destination)
        if (old) {
            response.status = HttpServletResponse.SC_NO_CONTENT
            render "DELETED"
        } else {
            notFound()
        }
    }

    protected getDefaultSort()  { actionName == 'index' ? 'name'  : null }
    protected getDefaultOrder() { actionName == 'index' ? 'asc'   : null }


}
