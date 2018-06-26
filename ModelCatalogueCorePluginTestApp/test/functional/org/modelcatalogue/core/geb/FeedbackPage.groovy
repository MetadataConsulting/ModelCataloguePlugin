package org.modelcatalogue.core.geb

import geb.Page

class FeedbackPage extends Page {

    static url = '#/catalogue/feedback/all'

    static at = { title == 'Feedbacks' }

    static content = {
        nav { $('#topmenu', 0).module(NavModule) }
    }
}
