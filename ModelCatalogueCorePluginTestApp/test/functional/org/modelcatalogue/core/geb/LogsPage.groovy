package org.modelcatalogue.core.geb

import geb.Page

class LogsPage extends Page {

    static url = 'logs/index'

    static at = { title == 'Logs' }

    static content = {
        nav { $('#topmenu', 0).module(NavModule) }
    }
}
