package org.modelcatalogue.core.geb

import geb.Page

class DataModelPolicyPage extends Page {

    static url = '#/catalogue/dataModelPolicy/3'

    static at = { title.contains("(Data Model Policy)")}

    static content = {
        policy(wait: true) { $('td.pp-table-property-element-value') }
    }

    String policyText() {
        return policy.text()
    }

}
