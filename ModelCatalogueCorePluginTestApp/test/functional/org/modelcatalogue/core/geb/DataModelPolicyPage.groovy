package org.modelcatalogue.core.geb

import geb.Page

class DataModelPolicyPage extends Page {

    static url = '/#/catalogue/dataModelPolicy'

    static at = { title.contains("Properties of") }

    static content = {
        policyTitle { $('h3 span') }
        policyText { $('table#properties-props tbody tr') }
    }

    Boolean policyTextIs(String value) {
        String text = policyText.$('td').text()
        (value == text)
    }

    Boolean titleIs(String value) {
        String title = policyTitle.text()
        (value == title)
    }

}
