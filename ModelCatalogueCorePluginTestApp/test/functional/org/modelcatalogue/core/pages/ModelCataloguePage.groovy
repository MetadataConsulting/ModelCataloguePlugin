package org.modelcatalogue.core.pages

import geb.Page
import geb.navigator.Navigator
import geb.waiting.WaitTimeoutException
import org.openqa.selenium.StaleElementReferenceException

abstract class ModelCataloguePage extends Page {

    private static final Map<String, Object> OPT = [required: false]

    static content = {

        viewTitle(OPT)          { $("h2") }
        subviewTitle(OPT)       { $("h3:not(.ng-hide)") }
        subviewStatus(OPT)      { $("h3 small span.label") }

        showLoginButton(OPT)    { $(".navbar-form i.glyphicon.glyphicon-log-in") }
        showLogoutButton(OPT)   { $(".navbar-form i.glyphicon.glyphicon-log-out") }


        loginDialog(OPT)        { $("div.login-modal-prompt") }
        modalDialog(OPT)        { $("div.modal") }
        modalHeader(OPT)        { $("div.modal-header h4") }
        modalPrimaryButton(OPT) { $("div.modal").find('button.btn-primary') }
        modalSuccessButton(OPT) { $("div.modal").find('button.btn-success') }
        modalCloseButton(OPT)   { $("div.modal").find('button.close') }

        username                { $("div.modal").find("#username") }
        password                { $("div.modal").find("#password") }
        loginButton             { $("div.modal").find("button.btn-success") }

        confirmDialog(OPT)      { $('.modal.messages-modal-confirm') }
        confirmOk(OPT)          { $('.modal.messages-modal-confirm .btn-primary') }

        tableFooterAction(OPT)  { $('tr.inf-table-footer-action')}

        simpleObjectEditor(OPT) { $('table.soe-table')}


    }
}
