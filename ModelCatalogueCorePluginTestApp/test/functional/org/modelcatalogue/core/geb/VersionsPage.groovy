package org.modelcatalogue.core.geb

import geb.Page

class VersionsPage extends Page {

    static url = '/#'

    static at = { title.startsWith('History of') }

    @Override
    String convertToPath(Object[] args) {
        args ? "/${args[0]}/versions/all" : ''
    }

    static content = {
        rows { $('#history-tab tbody tr') }
    }

    boolean  rowsContainText(String text) {
        for ( int i = 0; i < rows.size(); i++ ) {
            if ( rows[i].text().contains(text) ) {
                return true
            }
        }
        false
    }

}
