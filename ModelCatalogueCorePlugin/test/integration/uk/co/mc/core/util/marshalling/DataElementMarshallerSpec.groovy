package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Specification
import uk.co.mc.core.DataElement
import uk.co.mc.core.Relationship
import uk.co.mc.core.RelationshipType

/**
 * Created by adammilward on 10/02/2014.
 */
class DataElementMarshallerSpec extends Specification{

    def setup(){

        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

    }

    def cleanup(){
        Relationship.list().each{ relationship ->
            Relationship.unlink(relationship.source, relationship.destination, relationship.relationshipType)
        }

        DataElement.list().each{ dataElement ->
            dataElement.delete()
        }

        RelationshipType.list().each{ relationshipType ->
            relationshipType.delete()
        }
    }

    def 'test json marshalling for incoming relationships'(){

        expect:

        DataElement.list().isEmpty()

        when:

        def de1 = new DataElement(id: 1, name: 'One', description: 'First data element', definition: 'First data element definition').save()
        def de2 = new DataElement(id: 2, name: 'Two', description: 'Second data element', definition: 'Second data element definition').save()
        def de3 = new DataElement(id: 3, name: 'Three', description: 'Third data element', definition: 'Third data element definition').save()

        def rt = new RelationshipType(name:'Synonym',
                sourceToDestination: 'SynonymousWith',
                destinationToSource: 'SynonymousWith',
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        def rel = Relationship.link(de1, de2, rt)
        def rel2 = Relationship.link(de1, de3, rt)

        then:

        def de1JSON = de1 as JSON

        de1JSON.toString() == "{\"id\":$de1.id,\"name\":\"$de1.name\",\"description\":\"$de1.description\",\"status\":{\"enumType\":\"uk.co.mc.core.PublishedElement\$Status\",\"name\":\"DRAFT\"},\"versionNumber\":0.1,\"incomingRelationships\":[],\"outgoingRelationships\":[{\"sourcePath\":\"/DataElement/$de3.id\",\"sourceName\":\"$de3.name\",\"destinationPath\":\"/DataElement/$de1.id\",\"destinationName\":\"$de1.name\",\"relationshipType\":{\"class\":\"uk.co.mc.core.RelationshipType\",\"id\":$rt.id,\"destinationClass\":\"uk.co.mc.core.DataElement\",\"destinationToSource\":\"SynonymousWith\",\"name\":\"Synonym\",\"rule\":null,\"sourceClass\":\"uk.co.mc.core.DataElement\",\"sourceToDestination\":\"SynonymousWith\"}},{\"sourcePath\":\"/DataElement/$de2.id\",\"sourceName\":\"$de2.name\",\"destinationPath\":\"/DataElement/$de1.id\",\"destinationName\":\"$de1.name\",\"relationshipType\":{\"class\":\"uk.co.mc.core.RelationshipType\",\"id\":$rt.id,\"destinationClass\":\"uk.co.mc.core.DataElement\",\"destinationToSource\":\"SynonymousWith\",\"name\":\"Synonym\",\"rule\":null,\"sourceClass\":\"uk.co.mc.core.DataElement\",\"sourceToDestination\":\"SynonymousWith\"}}]}"

    }

}
