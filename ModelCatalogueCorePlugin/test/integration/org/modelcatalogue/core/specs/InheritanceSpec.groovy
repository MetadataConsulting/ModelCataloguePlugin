package org.modelcatalogue.core.specs

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.InitCatalogueService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.Inheritance
import spock.lang.Ignore

class InheritanceSpec extends IntegrationSpec  {

    public static final String DUMMY_DATA_CLASS_NAME = 'Dummy'
    public static final String TEST_PARENT_DATA_CLASS_NAME = 'Test Parent Class'
    public static final String TEST_DATA_ELEMENT_1_NAME = 'Test Data Element 1'
    public static final String TEST_DATA_ELEMENT_2_NAME = 'Test Data Element 2'
    public static final String TEST_DATA_ELEMENT_3_NAME = 'Test Data Element 3'
    public static final String TEST_DATA_ELEMENT_4_NAME = 'Test Data Element 4'
    public static final String METADATA_KEY_1 = 'one'
    public static final String METADATA_KEY_2 = 'two'
    public static final String METADATA_KEY_3 = 'three'
    public static final String METADATA_KEY_4 = 'four'
    public static final String METADATA_KEY_5 = 'five'
    public static final String METADATA_VALUE_1 = '1'
    public static final String METADATA_VALUE_2 = '2'
    public static final String METADATA_VALUE_3 = '3'
    public static final String METADATA_VALUE_4 = '4'
    public static final String METADATA_VALUE_5 = '5'
    public static final String METADATA_VALUE_5_ALT = 'V'
    public static final String TEST_CHILD_DATA_CLASS_NAME = 'Test Child Class'
    public static final String TEST_PARENT_VALUE_DOMAIN_NAME = 'Test Parent Value Domain'
    public static final String TEST_CHILD_VALUE_DOMAIN_NAME = 'Test Child Value Domain'
    public static final String TEST_DATA_TYPE_1_NAME = 'Test Data Type 1'
    public static final String TEST_DATA_TYPE_2_NAME = 'Test Data Type 2'
    public static final String TEST_DATA_MODEL_1_NAME = 'Test Data Model 1'
    public static final String TEST_DATA_MODEL_2_NAME = 'Test Data Model 2'

    InitCatalogueService initCatalogueService
    ElementService elementService
    CatalogueBuilder catalogueBuilder

    DataClass parentClass
    DataClass childClass
    DataClass dummyClass
    DataElement dataElement1
    DataElement dataElement2
    DataElement dataElement3
    DataElement dataElement4
    DataElement parentDataElement
    DataElement childDataElement
    DataType dataType1
    DataType dataType2
    DataModel dataModel1
    DataModel dataModel2

    def setup() {
        initCatalogueService.initDefaultRelationshipTypes()
        catalogueBuilder.build {
            dataModel name: TEST_DATA_MODEL_1_NAME, {
                dataClass name: DUMMY_DATA_CLASS_NAME
                dataClass name: TEST_PARENT_DATA_CLASS_NAME, {
                    dataElement name: TEST_DATA_ELEMENT_1_NAME
                    dataElement name: TEST_DATA_ELEMENT_2_NAME
                    dataElement name: TEST_DATA_ELEMENT_3_NAME
                    ext METADATA_KEY_1, METADATA_VALUE_1
                    ext METADATA_KEY_2, METADATA_VALUE_2
                    ext METADATA_KEY_3, METADATA_VALUE_3
                    rel 'synonym' to dataClass called DUMMY_DATA_CLASS_NAME
                }
                dataElement name: TEST_PARENT_VALUE_DOMAIN_NAME, {
                    dataType name: TEST_DATA_TYPE_1_NAME
                }
                dataElement name: TEST_CHILD_VALUE_DOMAIN_NAME
                dataType name: TEST_DATA_TYPE_2_NAME
            }

            dataModel name: TEST_DATA_MODEL_2_NAME, {
                dataClass name: TEST_CHILD_DATA_CLASS_NAME, {
                    dataElement name: TEST_DATA_ELEMENT_4_NAME
                    ext METADATA_KEY_4, METADATA_VALUE_4
                }
            }
        }

        parentClass = DataClass.findByName(TEST_PARENT_DATA_CLASS_NAME)
        childClass = DataClass.findByName(TEST_CHILD_DATA_CLASS_NAME)
        dummyClass = DataClass.findByName(DUMMY_DATA_CLASS_NAME)
        dataElement1 = DataElement.findByName(TEST_DATA_ELEMENT_1_NAME)
        dataElement2 = DataElement.findByName(TEST_DATA_ELEMENT_2_NAME)
        dataElement3 = DataElement.findByName(TEST_DATA_ELEMENT_3_NAME)
        dataElement4 = DataElement.findByName(TEST_DATA_ELEMENT_4_NAME)
        parentDataElement = DataElement.findByName(TEST_PARENT_VALUE_DOMAIN_NAME)
        childDataElement = DataElement.findByName(TEST_CHILD_VALUE_DOMAIN_NAME)
        dataType1 = DataType.findByName(TEST_DATA_TYPE_1_NAME)
        dataType2 = DataType.findByName(TEST_DATA_TYPE_2_NAME)
        dataModel1 = DataModel.findByName(TEST_DATA_MODEL_1_NAME)
        dataModel2 = DataModel.findByName(TEST_DATA_MODEL_2_NAME)

        assertNothingInherited()
    }

    def "with children works"(){
        addBasedOn()

        List<CatalogueElement> children = []
        Inheritance.withChildren(parentClass) {
            children << it
        }

        expect:
        children == [childClass]
    }

    def "with all children works"(){
        addBasedOn()

        List<CatalogueElement> children = []
        Inheritance.withAllChildren(parentClass) {
            children << it
        }

        expect:
        children == [childClass]
    }

    def "with parents works"(){
        addBasedOn()

        List<CatalogueElement> parents = []
        Inheritance.withParents(childClass) {
            parents << it
        }

        expect:
        parents == [parentClass]
    }

    def "with all parents works"(){
        addBasedOn()

        List<CatalogueElement> parents = []
        Inheritance.withAllParents(childClass) {
            parents << it
        }

        expect:
        parents == [parentClass]
    }

    def "Inherit relationships"() {
        addBasedOn()
        expect: "version specific relationships are inherited"
        parentClass.countContains() == 3
        childClass.countContains() == 4

        and: "semantic links aren't"
        parentClass.countIsSynonymFor() == 1
        childClass.countIsSynonymFor() == 0

        when: "we remove relationships from parent"
        parentClass.removeFromContains dataElement1
        parentClass.removeFromContains dataElement2

        then: "they are removed from the parent"
        !(dataElement1 in parentClass.contains)
        !(dataElement2 in parentClass.contains)

        and: "they are removed from child as well"
        !(dataElement1 in childClass.contains)
        !(dataElement2 in childClass.contains)

        when: "we add relationships to the parent"
        Relationship rp1 = parentClass.addToContains dataElement1, metadata: [(METADATA_KEY_5): METADATA_VALUE_5]
        Relationship rp2 = parentClass.addToContains dataElement2
        Relationship rc1 = childClass.containsRelationships.find { it.destination == dataElement1 } as Relationship
        Relationship rc2 = childClass.containsRelationships.find { it.destination == dataElement2 } as Relationship

        then: "they are added to the parent"
        rp1
        rp2
        dataElement1 in parentClass.contains
        dataElement2 in parentClass.contains
        rp1.ext[METADATA_KEY_5] == METADATA_VALUE_5

        and: "they are added to child as well"
        rc1
        rc1.inherited
        rc2
        rc2.inherited
        dataElement1 in childClass.contains
        dataElement2 in childClass.contains

        when: "metadata in the child relationship are overridden"
        rc1.ext[METADATA_KEY_5] = METADATA_VALUE_5_ALT

        and: "the relation is removed from the parent"
        parentClass.removeFromContains dataElement1

        then: "the relation is persisted in the child as it was already customized"
        dataElement1 in childClass.contains

        when: "metadata are added to the parent relationship"
        rp2.ext[METADATA_KEY_5] = METADATA_VALUE_5

        then: "the metadata are added to child relationships as well"
        rc2.ext[METADATA_KEY_5] == METADATA_VALUE_5

        when: "we try to remove relationship from child"
        Relationship rc3 = childClass.removeFromContains dataElement3

        then: "error is returned"
        rc3.errors.errorCount > 0

        and: "the children still contain the data element"
        dataElement3 in childClass.contains

        when: "we try to add relationship which is already inherited"
        childClass.addToContains dataElement3

        then: "error is returned"
        thrown(IllegalArgumentException)




        when:
        removeBasedOn()

        then:
        !(dataElement2 in childClass.contains)
        !(dataElement3 in childClass.contains)
    }

    def "Inheriting relationships does not steal the relationships from finalized item"() {
        elementService.finalizeElement(parentClass)
        addBasedOn()
        expect: "version specific relationships are inherited"
        parentClass.countContains() == 3
        childClass.countContains() == 4
    }

    def "inherit metadata"() {
        addBasedOn()
        expect: "metadata are inherited"
        parentClass.ext.size() == 3
        childClass.ext.size() == 4

        when: "we remove extension from parent"
        parentClass.ext.remove(METADATA_KEY_1)

        then: "it is removed from the child as well"
        !childClass.ext.get(METADATA_KEY_1)

        when: "we add extension to the parent"
        parentClass.ext[METADATA_KEY_5] = METADATA_VALUE_5

        then: "it is added to child as well"
        childClass.ext[METADATA_KEY_5] == METADATA_VALUE_5

        when: "extension in the child is overridden"
        childClass.ext[METADATA_KEY_5] = METADATA_VALUE_5_ALT

        and: "the extension is removed from the parent"
        parentClass.ext.remove(METADATA_KEY_5)

        then: "the extension is persisted in the child as it was already customized"
        childClass.ext[METADATA_KEY_5] == METADATA_VALUE_5_ALT

        when:
        removeBasedOn()

        then:
        !childClass.ext[METADATA_KEY_1]
        !childClass.ext[METADATA_KEY_2]
        !childClass.ext[METADATA_KEY_3]
    }

    def "inherit associations"() {
        addBasedOn()
        expect: "associations are inherited"
        parentDataElement.dataType == dataType1
        parentDataElement.save(failOnError: true, flush: true)
        childDataElement.dataType == dataType1
        childDataElement.save(failOnError: true, flush: true)

        when: "we remove associations from parent"
        parentDataElement.dataType = null
        parentDataElement.save(failOnError: true, flush: true)

        then: "it is removed from the child as well"
        childDataElement.dataType == null

        when: "we add association to the parent"
        parentDataElement.dataType = dataType2
        parentDataElement.save(failOnError: true, flush: true)

        then: "it is added to child as well"
        childDataElement.dataType == dataType2

        when: "association in the child is overridden"
        childDataElement.dataType = dataType1
        childDataElement.save(failOnError: true, flush: true)

        then: "it doesn't affect the parent"
        parentDataElement.dataType == dataType2

        when: "the association is removed from the parent"
        parentDataElement.dataType = null
        parentDataElement.save(failOnError: true, flush: true)

        then: "the association is persisted in the child as it was already customized"
        childDataElement.dataType == dataType1

        when: "the association is assigned in the parent but also exist in child"
        parentDataElement.dataType = dataType2
        parentDataElement.save(failOnError: true, flush: true)

        then: "only parent is assigned"
        parentDataElement.dataType == dataType2
        childDataElement.dataType == dataType1

        when: "the association is removed from the child"
        childDataElement.dataType = null
        childDataElement.save(failOnError: true, flush: true)

        then: "the association is reset to the one from parent"
        childDataElement.dataType == dataType2

        when: "the element no longer inherits form the parent"
        removeBasedOn()

        then: "the association is set to null"
        childDataElement.dataType == null

    }

    def "handle data models"() {
        expect: "both data classes belongs to right models"
        dataModel1 == parentClass.dataModel
        dataModel2 == childClass.dataModel

        when:
        addBasedOn()

        then:
        dataModel1 != childClass.dataModel
    }


    @Ignore
    def "handle multiple inheritance"() {
        expect: "to be implemented"
        false
    }

    private void addBasedOn() {
        childClass.addToIsBasedOn parentClass
        childDataElement.addToIsBasedOn parentDataElement
    }

    private void removeBasedOn() {
        childClass.removeFromIsBasedOn parentClass
        childDataElement.removeFromIsBasedOn parentDataElement
    }

    private void assertNothingInherited() {
        assert parentClass
        assert parentClass.countIsSynonymFor() == 1
        assert parentClass.countContains() == 3
        assert parentClass.ext.size() == 3

        assert childClass
        assert childClass.countContains() == 1
        assert childClass.extensions.size() == 1

        assert dummyClass

        assert dataElement1
        assert dataElement2
        assert dataElement3
        assert dataElement4

        assert parentDataElement
        assert childDataElement
        assert dataType1
        assert dataType2

        assert dataModel1
        assert dataModel2

        assert parentDataElement.dataType == dataType1
        assert childDataElement.dataType == null
    }
}
