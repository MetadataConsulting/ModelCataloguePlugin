package org.modelcatalogue.core.geb

import geb.Page

class DataModelPolicyEnumerationPage extends Page {

    static url = '#/catalogue/dataModelPolicy/3'

    static at = { title.contains("Enumeration Checks") }

    static content = {
        policy(wait: true) { $('td.pp-table-property-element-value') }
    }

    String policyText() {
        return policy.text()
    }

}
