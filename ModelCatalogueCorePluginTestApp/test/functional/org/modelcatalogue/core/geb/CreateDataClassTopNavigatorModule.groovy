package org.modelcatalogue.core.geb

import geb.Module

class CreateDataClassTopNavigatorModule extends Module {

    static content = {
        finishButton(wait: true) { $('#step-finish', 0) }
        metadataButton(wait: true) { $('#step-metadata', 0) }
        parentsButton(wait: true) { $('#step-parents', 0) }
        childrenButton(wait: true) { $('#step-children', 0) }
        elementsButton(wait: true) { $('#step-elements', 0) }
    }

    void finish() {
        finishButton.click()
        sleep(5000)
    }

    void elements() {
        elementsButton.click()
    }

    void childrens() {
        childrenButton.click()
    }

    void parentz() {
        parentsButton.click()
    }

    void metadata() {
        metadataButton.click()
    }

}
