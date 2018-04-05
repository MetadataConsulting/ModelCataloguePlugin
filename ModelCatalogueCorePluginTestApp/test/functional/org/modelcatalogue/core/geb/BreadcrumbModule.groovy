package org.modelcatalogue.core.geb

import geb.Module

class BreadcrumbModule extends Module {

    static content = {
        breadcrumbItem { $('.breadcrumb-item') }
        breadcrumbLink { $('.breadcrumb-item a', text: it) }
    }

    void select(String name) {
        breadcrumbLink(name).click()
    }
}
