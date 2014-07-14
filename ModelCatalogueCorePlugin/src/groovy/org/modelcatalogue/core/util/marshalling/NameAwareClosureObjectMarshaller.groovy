package org.modelcatalogue.core.util.marshalling

import org.codehaus.groovy.grails.web.converters.Converter
import org.codehaus.groovy.grails.web.converters.marshaller.ClosureObjectMarshaller
import org.codehaus.groovy.grails.web.converters.marshaller.NameAwareMarshaller

/**
 * Created by ladin on 14.07.14.
 */
class NameAwareClosureObjectMarshaller<T extends Converter> extends ClosureObjectMarshaller<T> implements NameAwareMarshaller {

    final Closure elementNameGetter

    NameAwareClosureObjectMarshaller(Closure<String> elementNameGetter, Class<?> clazz, Closure closure) {
        super(clazz, closure)
        this.elementNameGetter = elementNameGetter
    }

    @Override
    String getElementName(Object o) {
        return elementNameGetter(o)
    }
}
