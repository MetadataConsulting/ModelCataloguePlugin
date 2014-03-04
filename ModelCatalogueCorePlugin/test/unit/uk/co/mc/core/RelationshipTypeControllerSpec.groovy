package uk.co.mc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.util.GrailsNameUtils
import uk.co.mc.core.util.marshalling.AbstractMarshallers
import uk.co.mc.core.util.marshalling.RelationshipTypeMarshaller

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(RelationshipTypeController)
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
        //assert (propertiesToCheck = ['name'])
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


    def xmlCustomPropertyCheck(xml, item){
        checkProperty(xml.name, item.name, "name")
        return true
    }

    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        checkProperty(xml.name, inputItem.name, "name")
        return true
    }


    def customJsonPropertyCheck(item, json){
        checkProperty(json.name , item.name, "name")
        return true
    }


    def customJsonPropertyCheck(inputItem, json, outputItem){
        checkProperty(json.id , outputItem.id, "id")
        return true
    }

}


