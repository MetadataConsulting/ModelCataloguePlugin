package org.modelcatalogue.core.geb

import geb.Page

class UniqueOfKindPolicyPage extends Page {

    static at = {
        title.startsWith('Properties of Unique of Kind')
    }

    static url = '/#/catalogue/dataModelPolicy/1'

    static content = {
        policyText { $('#properties-props tr td') }
        editPolicyButton { $('a#role_item-detail_edit-catalogue-elementBtn') }
    }

    boolean allPropertiesAreUnique() {
        String text = policyText.text()
        String[] arr = text.split("\n")
        for (String s : arr) {
            if (!s.contains("unique")) {
                return false
            }
        }
        true
    }

    void edit() {
        editPolicyButton.click()
    }
}
