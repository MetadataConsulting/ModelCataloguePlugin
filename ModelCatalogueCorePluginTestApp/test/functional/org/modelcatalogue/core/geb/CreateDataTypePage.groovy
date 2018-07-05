package org.modelcatalogue.core.geb

import geb.Page
import geb.module.RadioButtons

class CreateDataTypePage extends Page implements MetadataUtils {

    static at = { $('.modal-dialog h4', 0).text().contains('Create Data Type') }

    static content = {
        nameInput(wait: true) { $('input#name', 0) }
        modelCatalogueIdInput { $('input#modelCatalogueId', 0) }
        descriptionTexarea { $('textarea#description', 0) }
        subTypeRadioButtons { $('input', name: 'subtype').module(RadioButtons) }
        pickSimpleTypeRadio { $('input#pickSimpleType', type: 'radio', 0) }
        pickEnumeratedTypeRadio { $('input#pickEnumeratedType', type: 'radio', 0) }
        pickPrimitiveTypeRadio { $('input#pickPrimitiveType', type: 'radio', 0) }
        pickReferenceTypeRadio { $('input#pickReferenceType', type: 'radio', 0) }
        pickSubsetTypeRadio { $('input#pickSubsetType', type: 'radio', 0) }
        buttons { $('.modal-footer', 0).module(ModalFooterModule) }
    }

    void simple() {
        pickSimpleTypeRadio.click()
    }

    void enumerated() {
        pickEnumeratedTypeRadio.click()
    }

    void primitive() {
        pickPrimitiveTypeRadio.click()
    }

    void reference() {
        pickReferenceTypeRadio.click()
    }

    void subset() {
        pickSubsetTypeRadio.click()
    }

    void setName(String value) {
        fill(nameInput, value)
    }

    void setModelCatalogueId(String value) {
        fill(modelCatalogueIdInput, value)
    }

    void setDescription(String value) {
        fill(descriptionTexarea, value)
    }

    void fill(def element, String value) {
        for (char c : value.toCharArray()) {
            element << "${c}".toString()
        }
    }
}
