package org.modelcatalogue.core.geb

import geb.Page

class FinalizeDataModelPage extends Page implements InputUtils {

    static at = { $('.modal-dialog').text()?.contains('Finalize Data Model') }

    static content = {
        versionNoteTextarea { $('textarea#revisionNotes') }
        finalizeButton { $('a#role_modal_modal-finalize-data-modalBtn', 0) }
    }

    void setVersionNote(String value) {
        fillInput(versionNoteTextarea, value)
    }

    void submit() {
        finalizeButton.click()
    }
}
