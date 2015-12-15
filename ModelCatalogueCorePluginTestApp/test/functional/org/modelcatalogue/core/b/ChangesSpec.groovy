package org.modelcatalogue.core.b

import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.pages.GlobalChangesPage
import spock.lang.Stepwise

@Stepwise
class ChangesSpec extends AbstractModelCatalogueGebSpec {

    def "go to login"() {
        loginAdmin()

        fastAction 'New Data Type'

        fill 'name' with "Data Type Change Test"
        fill 'dataModel' with "Data Model Change Test"

        click CatalogueAction.runLast('modal', 'modal-save-element')

        expect:
        check 'div.modal' gone

        when:
        go "#/catalogue/change/all"

        then:
        at GlobalChangesPage

        check 'h3' is 'Change List'
    }

    def "check the unit shows up with own detail page"(){
        when:
        check { infTableCell(1, 4).find('a span.fa.fa-fw.fa-link') } displayed

        click { infTableCell(1, 4).find('a span.fa.fa-fw.fa-link').parent('a') }

        then:
        check "li[data-tab-name='changes']" displayed
        check ".pp-table-property-element-value", 'data-value-for': 'Undone' is 'false'

        when:
        click CatalogueAction.runLast('item', 'undo-change')

        then:
        check confirmDialog displayed

        when:
        click confirmOk

        then:
        check ".pp-table-property-element-value", 'data-value-for': 'Undone' is 'true'
    }

    def "users have activity feed"() {
        go "#/catalogue/user/all"

        expect:
        check 'h3' is 'User List'
        check { infTableCell(1, 2).find('a', text: 'admin') } displayed

        when:
        click { infTableCell(1, 2).find('a', text: 'admin') }

        then:
        check 'h3' is 'admin FINALIZED'

        check { tab('activity') } displayed
        check { tab('history') } displayed

        when:
        selectTab 'history'

        then: "the history tab contains treeview item which does not resize"
        check { $('#history-tab .catalogue-element-treeview-list-container.no-resize') }
        check { $('span.catalogue-element-treeview-name', text: startsWith("admin")) }

        when:
        selectInTree 'admin'

        then:
        check { $('span.catalogue-element-treeview-name', text: "admin (User) [1] created") }
    }

    def "classifications have activity feed"() {
        when:
        select 'XMLSchema'

        then:
        check { tab('activity') }
    }

}