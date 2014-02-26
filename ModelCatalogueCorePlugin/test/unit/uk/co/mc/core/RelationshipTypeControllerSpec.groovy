package uk.co.mc.core

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.util.GrailsNameUtils
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.plugins.web.mimes.MimeTypesFactoryBean
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.json.JSONObject
import org.modelcatalogue.fixtures.FixturesLoader
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.mc.core.util.ResultRecorder
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.ElementsMarshaller
import uk.co.mc.core.util.marshalling.EnumeratedTypeMarshaller
import uk.co.mc.core.util.marshalling.RelationshipMarshallers
import uk.co.mc.core.util.marshalling.RelationshipTypeMarshaller
import uk.co.mc.core.util.marshalling.RelationshipsMarshaller

import javax.servlet.http.HttpServletResponse


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RelationshipTypeController)
@Mixin(ResultRecorder)
@Mock([RelationshipType, Model, ValueDomain, ConceptualDomain, DataType])
class RelationshipTypeControllerSpec extends AbstractRestfulControllerSpec {

    def setup() {
        fixturesLoader.load('relationshipTypes/RT_pubRelationship', 'relationshipTypes/RT_antonym')
        assert (loadItem1 = fixturesLoader.RT_pubRelationship.save(flush:true))

        assert (newInstance = new RelationshipType(name:"Antonym",
                sourceToDestination: "AntonymousWith",
                destinationToSource: "AntonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement))
        assert (badInstance = new RelationshipType(name: "", sourceClass: PublishedElement))
        assert (propertiesToEdit = [name: "changedName", sourceClass: PublishedElement, destinationClass: PublishedElement])
        assert (propertiesToCheck = ['name'])
        badXmlUpdateError = null

    }

    @Override
    Map<String, Object> getUniqueDummyConstructorArgs(int counter) {
        [name: "relationshipType${counter}", sourceToDestination: "${counter}superseded by", destinationToSource: "${counter}supersedes", sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "source.class == destination.class"]
    }

    @Override
    Class getResource() {
        RelationshipType
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerSpec")
    }

    @Override
    List<AbstractMarshallers> getMarshallers() {
        [new RelationshipTypeMarshaller()]
    }

}


