package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

/**
 * Test that TopLevelDataClassService methods work
 */
class TopLevelDataClassServiceIntegrationSpec extends AbstractIntegrationSpec {

    DataModel dataModel = new DataModel(name: "DM1")
    DataClass dataClassParent = new DataClass(name: "DC1", dataModel: dataModel)
    DataClass dataClassChild = new DataClass(name: "DC2", dataModel: dataModel)
    DataModelFilter dataModelFilter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
    TopLevelDataClassService topLevelDataClassService


    def "dataClassChild and dataClassParent both initially top-level"() {

        when: "two unrelated dataClasses in model"
        ListWithTotalAndType<DataClass> topLevelDataClasses = topLevelDataClassService.getTopLevelDataClasses(dataModelFilter, [:])

        then: "2 top level dataClasses"
        topLevelDataClasses.total == 2

    }

    def "dataClassChild no longer top-level when hierarchy relationship added"() {

        when: "hierarchy relationship between dataClasses is added"
        dataClassParent.addToParentOf(dataClassChild)
        ListWithTotalAndType<DataClass> topLevelDataClasses = topLevelDataClassService.getTopLevelDataClasses(dataModelFilter, [:])

        then: "child is no longer top-level"
        topLevelDataClasses.getItems() == [dataClassParent]

    }

    def "dataClassChild top-level again when hierarchy relationship removed"() {

        when: "hierarchy relationship is removed"
        dataClassParent.removeFromParentOf(dataClassChild)
        ListWithTotalAndType<DataClass> topLevelDataClasses = topLevelDataClassService.getTopLevelDataClasses(dataModelFilter, [:])

        then: "child is top-level again"
        topLevelDataClasses.total == 2

    }

}
