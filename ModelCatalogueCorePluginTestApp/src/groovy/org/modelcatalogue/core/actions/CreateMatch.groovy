package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.ObjectError

/**
 * Action Runner to create new relations for matches between dataElements
 * Created by david on 21/05/2017.
 */
class CreateMatch extends AbstractActionRunner {

    @Autowired RelationshipService relationshipService

    static String description = """
        Creates relationship of type 'relation' from the catalogue element 'source' to 'destination'.

        The validity of 'source', 'destination' and 'relation' parameters are validated immediately.

        Parameters:
            source: the source of the relationship in format 'gorm://<entity class name>:<id>'
            destination: the destination of the relationship in format 'gorm://<entity class name>:<id>'
            type: the relationship type in format 'gorm://org.modelcatalogue.core.RelationshipType:<id>'
            matchScore: shows a metric on the degree of closeness of the two items
    """


    String getMessage() {
//
        def destination = decodeEntity(parameters?.destination)
        def source = decodeEntity(parameters?.source)
        def matchScore = parameters?.matchScore
        //If no matchscore then we assume this is being used by the exactMatch routine so insert 100
        if(!matchScore){
            matchScore = """100"""
        }
        def additionalMessage = parameters?.message



        String destClass = GrailsNameUtils.getPropertyName(destination.class)
        String destId = destination.id as String
        String destClassifiedName = CatalogueElementMarshaller.getClassifiedName(destination)

        String sourceClass = GrailsNameUtils.getPropertyName(source.class)
        String sourceId =       source.id
        String sourceClassName = CatalogueElementMarshaller.getClassifiedName(source)

        String type = decodeEntity(parameters.type)

        String srclink = """<a target="_blank" href='#/catalogue/${sourceClass}/${sourceId}'> '${sourceClassName}'</a>"""
        String destLink = """<a target="_blank" href='#/catalogue/${destClass}/${destId}'> '${destClassifiedName}'</a>"""

        String desc = """  <table class="table">
                      <tr>
                        <th>Data Model 1</th>
                        <th>Match</th>
                        <th>Data Model 2</th>
                      </tr>
                      <tr>
                        <td>${srclink}</td>
                        <td>${matchScore}</td>
                        <td>${destLink}</td>
                      </tr>
                    </table> 
                     """
        if(additionalMessage){
            desc = desc + """<p>$additionalMessage</p>"""
        }
        normalizeDescription(desc)
//        normalizeDescription """
//            Create new relationship '   <a href='#/catalogue/${GrailsNameUtils.getPropertyName(decodeEntity(parameters.source)?.class)}/${decodeEntity(parameters.source)?.id}'> ${GrailsNameUtils.getNaturalName(decodeEntity(parameters.source)?.class?.simpleName)} '${CatalogueElementMarshaller.getClassifiedName(decodeEntity(parameters.source))}'</a>  ${decodeEntity(parameters.type)?.sourceToDestination} <a href='#/catalogue/${GrailsNameUtils.getPropertyName(decodeEntity(parameters.destination)?.class)}/${decodeEntity(parameters.destination)?.id}'> ${GrailsNameUtils.getNaturalName(decodeEntity(parameters.destination)?.class?.simpleName)} '${CatalogueElementMarshaller.getClassifiedName(decodeEntity(parameters.destination))}'</a> with following parameters:
//
//
//            ${parameters.collect {  key, value ->
//            "${GrailsNameUtils.getNaturalName(key)}: ${decodeEntity(value)?.name}"}.join('\n\n')
//        }
//        """
    }

    @Override
    Map<String, String> validate(Map<String, String> params) {
        Map<String, String> ret = [:]

        Object source = decodeEntity(params.source)
        Object destination = decodeEntity(params.destination)
        Object type = decodeEntity(params.type)

        if (!source) {
            ret.source = 'Missing source'
        }

        if (!destination) {
            ret.destination = 'Missing destination'
        }

        if (!type) {
            ret.type = 'Missing type'
        }

        if (type) {
            if (!(type instanceof RelationshipType)) {
                ret.type = 'Type must be relationship type'
            } else {
                if (source && !(type.sourceClass.isAssignableFrom(source.class))) {
                    ret.source = 'Source must be ' + GrailsNameUtils.getNaturalName(type.sourceClass.simpleName).toLowerCase()
                }

                if (destination && !(type.destinationClass.isAssignableFrom(destination.class))) {
                    ret.destination = 'Destination must be ' + GrailsNameUtils.getNaturalName(type.destinationClass.simpleName).toLowerCase()
                }
            }
        } else {
            if (source && !(source instanceof CatalogueElement)) {
                ret.source = 'Source must be catalogue element'
            }

            if (destination && !(destination instanceof CatalogueElement)) {
                ret.destination = 'Destination must be catalogue element'
            }
        }

        ret.putAll super.validate(params)

        ret
    }

    @Override void run() {
        CatalogueElement theSource = getSource() as CatalogueElement
        CatalogueElement theDestination = getDestination() as CatalogueElement
        RelationshipType theType = getType() as RelationshipType
        Relationship relationship = link()
        if (!relationship.hasErrors()) {
            String match = parameters?.matchScore
            String matchOn = parameters?.matchOn
            if(match){
                relationship.addExtension("match", "$match")
                if(matchOn) relationship.addExtension("matchOn", "$matchOn")
                relationship.save()
            }

            out << "<a href='#/catalogue/${GrailsNameUtils.getPropertyName(theSource.class)}/${theSource.id}'>${GrailsNameUtils.getNaturalName(theSource.class.simpleName)} '$theSource.name'</a> now <a href='#/catalogue/${GrailsNameUtils.getPropertyName(theType.class)}/${theType.id}'>$theType.sourceToDestination</a> <a href='#/catalogue/${GrailsNameUtils.getPropertyName(theDestination.class)}/${theDestination.id}'>${GrailsNameUtils.getNaturalName(theDestination.class.simpleName)} '$theDestination.name'</a>"
            result = encodeEntity relationship
        } else {
            fail("Unable to create new relationship using parameters ${parameters}")
            for (ObjectError error in relationship.errors.allErrors) {
                out << "$error\n"
            }
        }
    }

    def getSource() {
        decodeEntity(parameters.source)
    }

    def getDestination() {
        decodeEntity(parameters.destination)
    }

    def getType() {
        decodeEntity(parameters.type)
    }

    def getMatchScore() {
        decodeEntity(parameters.matchScore)
    }

    /**
     * Creates new relationship.
     * @return new relationship
     */
    Relationship link() {
        if (source && destination && type) {
            relationshipService.link(source as CatalogueElement, destination as CatalogueElement, type as RelationshipType)
        } else {
            throw new IllegalStateException("The action wasn't initialized yet with the valid parameters")
        }
    }

    @Override
    List<String> getRequiredParameters() {
        ['source', 'destination', 'type']
    }






}
