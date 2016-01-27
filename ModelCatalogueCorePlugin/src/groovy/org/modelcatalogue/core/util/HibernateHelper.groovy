package org.modelcatalogue.core.util

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
        // initialize anyway to prevent problem with subclasses
        object.toString() // this works the best, Hibernate.initialize() still left the entity untouched
        // still need to use the hibernate proxy helper
        return (Class<T>) object.class
    }

}
