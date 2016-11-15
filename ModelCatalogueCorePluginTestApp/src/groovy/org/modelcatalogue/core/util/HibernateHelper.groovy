package org.modelcatalogue.core.util

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil
import org.hibernate.proxy.HibernateProxy

class HibernateHelper {

    /**
     * Gets the object class.
     *
     * Sadly this triggers initialization of the element as HibernateProxyHelper#getClassWithoutInitializingProxy(Object)
     * may return superclass in some situations.
     *
     * @param object which class we would like to obtain
     * @return class of the object specified
     */
    static <T> Class<T> getEntityClass(T object) {
        if (!object) {
            return Object as Class<T>
        }
        // still need to use the hibernate proxy helper
        return (Class<T>) ensureNoProxy(object).class
    }

    static <T> T ensureNoProxy(T published) {
        if (published instanceof HibernateProxy) {
            return GrailsHibernateUtil.unwrapProxy(published) as T
        }
        if (published.getClass().name.contains('_javassist_')) {
            return GrailsHibernateUtil.unwrapIfProxy(published)
        }
        return published
    }
}
