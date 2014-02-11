package uk.co.mc.core.util.marshalling

import grails.converters.JSON
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import spock.lang.Shared
import spock.lang.Specification
import uk.co.mc.core.DataElement
import uk.co.mc.core.Relationship
import uk.co.mc.core.RelationshipType

/**
 * Created by adammilward on 10/02/2014.
 */
class DataElementMarshallerSpec extends Specification{

    @Shared
    def de1, de2, de3, rel, rel2, rt

    def setupSpec(){

        def springContext = WebApplicationContextUtils.getWebApplicationContext( ServletContextHolder.servletContext )

        //register custom json Marshallers
        springContext.getBean('customObjectMarshallers').register()

    }

    /*def cleanupSpec(){
        Relationship.unlink(de1, de2, rt)
        Relationship.unlink(de1, de3, rt)
        de1.delete()
        de2.delete()
        de3.delete()
        rt.delete()
    }*/

    def 'test json marshalling for incoming relationships'(){

        when:

        de1 = new DataElement(id: 1, name: 'One', description: 'First data element', definition: 'First data element definition').save()
        de2 = new DataElement(id: 2, name: 'Two', description: 'Second data element', definition: 'Second data element definition').save()
        de3 = new DataElement(id: 3, name: 'Three', description: 'Third data element', definition: 'Third data element definition').save()

        rt = new RelationshipType(name:'Antonym',
                sourceToDestination: 'AntonymousWith',
                destinationToSource: 'AntonymousWith',
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        rel = Relationship.link(de1, de2, rt)
        rel2 = Relationship.link(de1, de3, rt)

        then:

        def de1JSON = de1 as JSON

        de1JSON.toString() == "{\"id\":$de1.id,\"name\":\"$de1.name\",\"description\":\"$de1.description\",\"status\":{\"enumType\":\"uk.co.mc.core.PublishedElement\$Status\",\"name\":\"DRAFT\"},\"versionNumber\":0.1,\"incomingRelationships\":[],\"outgoingRelationships\":[{\"sourcePath\":\"/DataElement/$de3.id\",\"sourceName\":\"$de3.name\",\"destinationPath\":\"/DataElement/$de1.id\",\"destinationName\":\"$de1.name\",\"relationshipType\":{\"class\":\"uk.co.mc.core.RelationshipType\",\"id\":$rt.id,\"destinationClass\":\"uk.co.mc.core.DataElement\",\"destinationToSource\":\"AntonymousWith\",\"name\":\"Antonym\",\"rule\":null,\"sourceClass\":\"uk.co.mc.core.DataElement\",\"sourceToDestination\":\"AntonymousWith\"}},{\"sourcePath\":\"/DataElement/$de2.id\",\"sourceName\":\"$de2.name\",\"destinationPath\":\"/DataElement/$de1.id\",\"destinationName\":\"$de1.name\",\"relationshipType\":{\"class\":\"uk.co.mc.core.RelationshipType\",\"id\":$rt.id,\"destinationClass\":\"uk.co.mc.core.DataElement\",\"destinationToSource\":\"AntonymousWith\",\"name\":\"Antonym\",\"rule\":null,\"sourceClass\":\"uk.co.mc.core.DataElement\",\"sourceToDestination\":\"AntonymousWith\"}}]}"

    }

}
