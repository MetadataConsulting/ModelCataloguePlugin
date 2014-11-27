package org.modelcatalogue.core.pages

import geb.Page
import geb.navigator.Navigator

abstract class ModelCataloguePage extends Page {

    private static final Map<String, Object> OPT = [required: false]

    static content = {

        viewTitle               { $("h2") }
        subviewTitle            { $("h3:not(.ng-hide)") }
        subviewStatus           { $("h3 small span.label") }

        showLoginButton(OPT)    { $(".navbar-form i.glyphicon.glyphicon-log-in") }
        showLogoutButton(OPT)   { $(".navbar-form i.glyphicon.glyphicon-log-out") }


        loginDialog(OPT)        { $("div.login-modal-prompt") }
        modalDialog(OPT)        { $("div.modal") }
        modalHeader(OPT)        { $("div.modal-header h4") }
        modalPrimaryButton(OPT) { modalDialog.find('button.btn-primary') }
        modalCloseButton(OPT)   { modalDialog.find('button.close') }

        username                { loginDialog.find("#username") }
        password                { loginDialog.find("#password") }
        loginButton             { loginDialog.find("button.btn-success") }

        confirmDialog(OPT)      { $('.modal.messages-modal-confirm') }
        confirmOk(OPT)          { $('.modal.messages-modal-confirm .btn-primary') }

        tableFooterAction(OPT)  { $('tr.inf-table-footer-action')}

        simpleObjectEditor(OPT) { $('table.soe-table')}


    }


    // keep the passwords simply stupid, they are only for dev/test or very first setup
    // sauce labs connector for some reason fails with the six in the input
    def loginAdmin() { loginUser("admin", "admin") }
    def loginViewer() { loginUser("viewer", "viewer") }
    def loginCurator() { loginUser("curator", "creator") }

    def loginUser(String user, String pwd) {
        if (!showLoginButton.displayed) {
            showLogoutButton.click()
        }

        showLoginButton.click()

        waitFor {
            loginDialog.displayed
        }

        username = user
        password = pwd
        loginButton.click()
    }

    /**
     * Selects the first item from catalogue element picker if any element is found.
     * returns true if the element was selected, false otherwise
     */
    boolean selectCepItemIfExists() {
        try {
            waitFor(3) {
                $('.cep-item').displayed
            }
            $('.cep-item').click()
            return true
        } catch (ignored) {
            return false
        }

    }

    Navigator actionButton(String id, String role = "item") {
        $('#role_' + role + '_' + id + 'Btn')
    }

    /**
     * @param row number of row starting 1
     * @param column number of column starting 1
     * @return given cell
     */
    Navigator infTableCell(Map attrs = [:], int row, int column) {
        $(attrs, 'div.inf-table-body tbody tr:nth-child(' + row +') td:nth-child(' + column + ')')
    }


    void toggleInfTableRow(int row) {
        $('div.inf-table-body tbody tr:nth-child(' + row +') a.inf-cell-expand').click()
    }

    int totalOf(String name) {
        Navigator totalSpan = tab(name).find('span.badge.tab-value-total')
        if (!totalSpan.displayed) {
            return 0
        }
        return totalSpan.text() as Integer
    }

    Navigator tab(String name) {
        $('li', 'data-tab-name': name)
    }

    void selectTab(String name) {
        tab(name).find('a').click()
    }

    boolean tabActive(String name) {
        tab(name).hasClass('active')
    }

    /**
     * Fills the metadata with the new values
     * @param newMetadata
     */
    void fillMetadata(Map newMetadata, Navigator parent = null) {
        if (!parent) {
            parent = simpleObjectEditor
        }

        while (parent.find('.soe-table-property-row').size() > 1) {
            parent.find('.soe-table-property-row:first-child .soe-table-property-actions .soe-remove-row').click()
        }

        newMetadata.each { key, value ->
            parent.find('.soe-table-property-row:last-child .soe-table-property-key input').value(key?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-value input').value(value?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-actions .soe-add-row').click()
        }
    }

}
