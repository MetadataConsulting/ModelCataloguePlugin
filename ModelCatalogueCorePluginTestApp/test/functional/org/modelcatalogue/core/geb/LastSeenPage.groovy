package org.modelcatalogue.core.geb

import geb.Page

class LastSeenPage extends Page {

    static url = '/lastSeen/index'

    static at = { title == 'User Activity' }
}
