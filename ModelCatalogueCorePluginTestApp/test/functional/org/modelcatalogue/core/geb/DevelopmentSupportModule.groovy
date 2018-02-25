package org.modelcatalogue.core.geb

import geb.Module

class DevelopmentSupportModule extends Module {

    static content = {
        linkImages { $('a > img') }
    }

    List<String> getLogoLinks() {
        linkImages.collect { it.parent('a').getAttribute('href') }
    }

    List<String> getLogoLinksAlt() {
        linkImages.collect { it.getAttribute('alt') }
    }
}
