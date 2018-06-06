package org.modelcatalogue.core.geb

import geb.Page

class CreateRelationshipPage extends Page implements InputUtils {
    static at = { $('.modal-dialog').text().contains('Create Data Element') }

    static content = {
        chooseRelation(wait: true) { $("#type option", text: it) }
        metadataTab(wait: true) { $(".nav-stacked li", text: it) }
        destination(wait: true) { $("#element") }
        destinationDropdown(wait: true) { $(".cep-item") }
        minOccur(wait: true) { $("#minOccurs") }
        maxOccur(wait: true) { $("#maxOccurs") }
        createRelationship(wait: true) { $(".btn-primary") }
        metadataExpand(wait: true) { $(".fa-toggle-down") }
        pageHeading(wait: true) { $("h4.ng-binding") }
    }

    void setName(String value) {
        fillInput(nameInput, value)
    }

    void slectRelationDropdown(String value) {
        chooseRelation(value).click()
    }

    void slectMetadataTab(String value) {
        metadataTab(value).click()
    }


    void minOccurValue(String value) {
        fillInput(minOccur, value)
    }

    void maxOccurValue(String value) {
        fillInput(maxOccur, value)
    }

    void destinationalue(String value) {
        fillInput(destination, value)
        sleep(3_000)
    }

    void createRelationshipBttn() {
        createRelationship.click()
    }

    void destinationDropdownFirst() {
        destinationDropdown[0].click()
    }

    void metadataExpandBttn() {
        metadataExpand.click()
    }

    Boolean matchPageHeading(String value) {
        pageHeading.text().equals(value)
    }


}
