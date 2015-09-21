package org.modelcatalogue.core.gel


import grails.test.spock.IntegrationSpec

import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.RelationshipService;
import org.modelcatalogue.core.RelationshipType;
import org.modelcatalogue.core.RelationshipDefinition
import org.modelcatalogue.core.util.test.TestDataHelper
import spock.lang.Shared

class GelXmlServiceIntegrationSpec extends IntegrationSpec {
    
    Model parent1
    Model parent2
    Model child1
    Model child2
    Model grandChild
    GelXmlService gelXmlService
    RelationshipService relationshipService
    InitCatalogueService initCatalogueService
    DataElement de1
    DataElement de2
    DataElement de3

    def setup(){
        initCatalogueService.initDefaultRelationshipTypes()
        parent1 = new Model(name: 'book').save(failOnError: true)
        parent2 = new Model(name: 'chapter1').save(failOnError: true)
        child1 = new Model(name: 'chapter2').save(failOnError: true)
        child2 = new Model(name: 'mTest1').save(failOnError: true)
        grandChild = new Model(name: 'mTest2').save(failOnError: true)
        parent1.addToParentOf child1
        parent2.addToParentOf child2
        child1.addToParentOf grandChild
        de1 = DataElement.findByName("DE_author1")
        de2 = DataElement.findByName("AUTHOR")
        de3 = DataElement.findByName("auth")
        
        
        def ext=['Min Occurs':1,'Max Occurs':1]
        
        def rel=relationshipService.link(parent1,child1,RelationshipType.hierarchyType )
        rel.ext=ext;
        rel=relationshipService.link(child1,grandChild,RelationshipType.hierarchyType )
        rel.ext=ext;
        
        RelationshipDefinition.create(parent1, child1, RelationshipType.hierarchyType)
        
        parent1.ext.putAt(GelXmlService.XSD_SCHEMA_NAME,  "Xsd-Schema-Name")
        parent1.ext.putAt(GelXmlService.XSD_SCHEMA_VERSION,  "1.0.0")
        parent1.ext.putAt(GelXmlService.XSD_SCHEMA_VERSION_DESCRIPTION,  "Simple description")
    }



    def  "test printXsdSchema"(){
        when :
        def result=gelXmlService.printXSDModel(parent1)
        then :
        assert result!=null
        assert result.contains("<xs:complexType name='${child1.name.toLowerCase()}'")
        assert result.contains("<xs:complexType name='${grandChild.name.toLowerCase()}'")

        assert result.contains("<xs:element name='${parent1.name.toLowerCase()}'")
        assert result.contains("<xs:element name='${child1.name.toLowerCase()}'")
        assert result.contains("<xs:element name='${grandChild.name.toLowerCase()}'")
        
        when :
        result=null
        try{
            parent1.ext=[:]
            result=gelXmlService.printXSDModel(parent1)
        } catch(e){
           e.printStackTrace()
           e.printStackTrace()
        }
        then :
        assert result==null
    }


    def  "test print xml Shredder"(){
        when :
        def result=gelXmlService.printXmlModelShredder(parent1)
        then :
        assert result!=null
    }
}
