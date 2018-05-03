package org.modelcatalogue.core.geb

import geb.Page

class CreateRelationshipPage extends Page {
    static at = { $("select#type", 0).displayed }

    static content = {
        relationshipTypeLink { $('select#type') }
        relationshipTypeDropdown { $('select#type option', text: it) }
        searchMoreButton { $('span.search-for-more-icon') }
        metadataDropdown { $('label.expand-metadata span') }
        metadataSideNavigator(wait: true) { $('div.mc-metadata-editor div', 0).$('ul li') }
        metadataInputFeildsNavigator { $('div.mc-metadata-editor div', 1) }
        maxOccurance(wait: true) { $('input#maxOccurs') }
        createRelationshipButton { $('button.btn-primary', text: 'Create Relationship') }
    }

    void relationship() {
        relationshipTypeLink.click()
    }

    void selectRelationshipType(String value) {
        relationshipTypeDropdown(value).click()
    }

    void searchMore() {
        searchMoreButton.click()
    }

    void openMetadata() {
        metadataDropdown.click()
    }

    void openOccuranceNavigator() {
        metadataSideNavigator.$('a', text: "Occurrence").click()
    }

    void setMaxOccurance(Integer value) {
        maxOccurance.value(value)
    }

    void createRelationship() {
        createRelationshipButton.click()
        sleep(1000)
    }
}