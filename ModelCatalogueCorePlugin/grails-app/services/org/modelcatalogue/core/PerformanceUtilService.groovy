package org.modelcatalogue.core

import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import org.hibernate.SessionFactory

class PerformanceUtilService {

    static transactional = false

    SessionFactory sessionFactory
    def propertyInstanceMap = DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }
}
