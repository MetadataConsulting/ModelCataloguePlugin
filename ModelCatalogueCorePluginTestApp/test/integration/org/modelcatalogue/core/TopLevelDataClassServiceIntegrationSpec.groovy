package org.modelcatalogue.core

/**
 * Test that TopLevelDataClassService methods work
 */
class TopLevelDataClassServiceIntegrationSpec extends AbstractIntegrationSpec {

    DataModel dataModel
    DataClass dataClassParent
    DataClass dataClassChild


    def "dataClassChild and dataClassParent both initially top-level"() {
        // TODO: Create and add dataClasses to dataModel. Check that getTopLevelDataClasses returns the two.
    }

    def "dataClassChild no longer top-level when hierarchy relationship added"() {
        // TODO: Add relationship from dataClassParent to dataClassChild. Check that getTopLevelDataClasses just returns the parent.
    }

    def "dataClassChild top-level again when hierarchy relationship removed"() {
        // TODO: Remove relationship from dataClassParent to dataClassChild.
    }

}
