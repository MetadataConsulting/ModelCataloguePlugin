package org.modelcatalogue.core.geb

import geb.Page

class FinalizeDataModelPage extends Page implements InputUtils {
    static atCheckWaiting = true

    static at = { $("div.modal-header>h4").text()?.contains('Finalize Data Model') }

    static content = {
        versionNoteTextarea(wait: true) { $('textarea#revisionNotes') }
        finalizeButton(wait: true) { $('a#role_modal_modal-finalize-data-modalBtn', 0) }
        version(wait: true) { $('#semanticVersion') }
    }

    void setVersionNote(String value) {
        fillInput(versionNoteTextarea, value)
    }

    void setVersion(String value) {
        version.value(value)
    }

    void submit() {
        finalizeButton.click()
        sleep(3_000)
    }
}
