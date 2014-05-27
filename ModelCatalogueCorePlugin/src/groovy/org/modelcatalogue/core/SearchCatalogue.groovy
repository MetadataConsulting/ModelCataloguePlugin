package org.modelcatalogue.core

/**
 * Created by adammilward on 20/05/2014.
 */
public interface SearchCatalogue {

    def search(Class resource, Map params)
    def search(Map params)
    def index(Class resource)
    def index(Collection<Class> resource)
    def unindex(Object object)
    def unindex(Collection<Object> object)
    def refresh()

}