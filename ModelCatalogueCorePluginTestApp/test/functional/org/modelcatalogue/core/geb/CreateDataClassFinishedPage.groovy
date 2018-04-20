package org.modelcatalogue.core.geb

import geb.Page
import geb.navigator.Navigator

class CreateDataClassFinishedPage extends Page implements InputUtils, MetadataUtils {
    static at = { $('.modal-dialog').text().contains('Data Class Wizard') }

    static content = {
        createAnotherButton(wait: true) { $('button.btn.btn-success', 1) }
        exitButton(wait: true) { $('button#exit-wizard') }
    }

    void createAnother() {
        createAnotherButton.click()
    }

    void exitWizard() {
        exitButton.click()
    }
}
