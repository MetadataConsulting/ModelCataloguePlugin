package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import spock.lang.Shared

/**
 * Test that TopLevelDataClassService methods work
 */
class TopLevelDataClassServiceIntegrationSpec extends AbstractIntegrationSpec {

    @Shared
    DataModel dataModel
    @Shared
    DataClass dataClassParent
    @Shared
    DataClass dataClassChild
    @Shared
    DataModelFilter dataModelFilter
    @Shared
    TopLevelDataClassService topLevelDataClassService

    def setupSpec() {
        dataModel = new DataModel(name: "DM1")
        dataModel.save(flush:true)

        dataClassParent = new DataClass(name: "DC1", dataModel: dataModel)
        dataClassParent.save(flush:true)

        dataClassChild = new DataClass(name: "DC2", dataModel: dataModel)
        dataClassChild.save(flush:true)

        dataModelFilter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
    }

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
