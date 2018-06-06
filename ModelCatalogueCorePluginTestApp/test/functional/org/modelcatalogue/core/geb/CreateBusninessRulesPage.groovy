package org.modelcatalogue.core.geb

import geb.Page
import geb.module.Checkbox

class CreateBusninessRulesPages extends Page implements InputUtils {

    static url = '/dataModel/create'

    static at = { $('.modal-dialog').text().contains('New Validation Rule') }

    static content = {
        name(wait: true, required: false) { $('input#name') }
        component(wait: true, required: false) { $('input#component') }
        focus(wait: true, required: false) { $('input#ruleFocus') }
        trigger(wait: true, required: false) { $('input#trigger') }
        rule(wait: true, required: false) { $('textarea#rule') }
        errorCondition(wait: true, required: false) { $('input#errorCondition') }
        issueRecord(wait: true, required: false) { $('input#issueRecord') }
        notification(wait: true, required: false) { $('input#notification') }
        target(wait: true, required: false) { $('input#notificationTarget') }
        ICON(wait: true, required: false) { $('div.modal-body>form>div:nth-child(5)>span>span') }
        SEARCH(wait: true, required: false) { $('input#value') }
        modelCatalogue(wait: true, required: false) { $('span.mc-name') }
        table(wait: true, required: false) { $('td.col-md-4') }
        businessRule(wait: true, required: false) { $('td.col-md-4>span>span>a') }
        deleteButton(wait: true, required: false) { $('a#delete-menu-item-link>span:nth-child(3)') }
        measurementUnitButton(wait: true, required: false) { $('a#role_modal_modal-save-elementBtn') }

    }

    void setName(String value) {
        fillInput(name, value)
    }

    void setComponent(String value) {
        fillInput(component, value)
    }

    void setFocus(String value) {
        fillInput(focus, value)
    }

    void setTrigger(String value) {
        fillInput(trigger, value)
    }

    void setRule(String value) {
        fillInput(rule, value)
    }

    void submit() {
        measurementUnitButton.click()
        sleep(2000)
    }

}

