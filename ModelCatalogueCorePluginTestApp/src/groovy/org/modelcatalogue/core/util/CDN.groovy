package org.modelcatalogue.core.util

import grails.util.BuildScope
import grails.util.Environment

class CDN {

    static boolean isPreferred() {
        if (System.getProperty('mc.offline') == 'true') {
            return false
        }
        return Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM] || BuildScope.current == BuildScope.WAR
    }

}
