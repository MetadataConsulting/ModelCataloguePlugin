package org.modelcatalogue.core.geb

import geb.Module

class MainNavigationModule extends Module {

    static content = {
        cogIcon(required: false) { $('span.fa-cog', 0) }
    }

    boolean isCogIconDisplayed() {
        cogIcon.isDisplayed()
    }
}
