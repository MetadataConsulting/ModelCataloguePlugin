package org.modelcatalogue.core

import com.google.common.collect.ImmutableSet
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.ExtensionValueGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.lists.ListWithTotalAndType

/**
 * Test that TopLevelDataClassService methods work
 */
class TopLevelDataClassServiceIntegrationSpec extends AbstractIntegrationSpec {

    TopLevelDataClassService topLevelDataClassService

    DataModelGormService dataModelGormService

    DataClassGormService dataClassGormService

    ExtensionValueGormService extensionValueGormService

    RelationshipGormService relationshipGormService

    def "dataClassChild and dataClassParent both initially top-level"() {
        when:
        DataModel dataModel = dataModelGormService.saveWithName("DM1")

        then:
        dataModelGormService.count() == old(dataModelGormService.count()) + 1

        when:
        DataClass dataClassParent = dataClassGormService.saveWithNameAndDataModel("DC1", dataModel)

        then:
        dataClassGormService.count() == old(dataClassGormService.count()) + 1
        extensionValueGormService.count() == old(extensionValueGormService.count())

        when:
        DataClass dataClassChild = dataClassGormService.saveWithNameAndDataModel("DC2", dataModel)

        then:
        dataClassGormService.count() == old(dataClassGormService.count()) + 1
        extensionValueGormService.count() == old(extensionValueGormService.count())

        when: "two unrelated dataClasses in model"
        DataModelFilter dataModelFilter = DataModelFilter.create(ImmutableSet.<DataModel> of(dataModel), ImmutableSet.<DataModel> of())
        ListWithTotalAndType<DataClass> topLevelDataClasses = topLevelDataClassService.getTopLevelDataClasses(dataModelFilter, [:])

        then: "2 top level dataClasses"
        topLevelDataClasses.total as int == 2

        when: "hierarchy relationship between dataClasses is added"
        dataClassParent.addToParentOf(dataClassChild)
        topLevelDataClasses = topLevelDataClassService.getTopLevelDataClasses(dataModelFilter, [:])

        then: "child is no longer top-level"
        topLevelDataClasses.getItems() == [dataClassParent]

        when: "hierarchy relationship is removed"
        dataClassParent.removeFromParentOf(dataClassChild)
        topLevelDataClasses = topLevelDataClassService.getTopLevelDataClasses(dataModelFilter, [:])

        then: "child is top-level again"
        topLevelDataClasses.total == 2
    }
}
