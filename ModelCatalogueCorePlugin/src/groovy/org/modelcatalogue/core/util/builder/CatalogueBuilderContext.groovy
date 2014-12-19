package org.modelcatalogue.core.util.builder

import com.google.common.escape.CharEscaperBuilder
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain

/**
 * Created by ladin on 19.12.14.
 */
class CatalogueBuilderContext {

    private static Set<Class> SUPPORTED_AS_CONTEXT  = [CatalogueElement, Classification, ValueDomain, DataType, Model, MeasurementUnit, DataElement]

    private List<Map<Class, CatalogueElementProxy>> contexts = []

    private final CatalogueBuilder builder

    CatalogueBuilderContext(CatalogueBuilder builder) {
        this.builder = builder
    }

    public void clear() {
        contexts.clear()
    }

    public <T extends CatalogueElement, A extends CatalogueElementProxy<T>>  void withNewContext(A contextElement, @DelegatesTo(CatalogueBuilder) Closure c) {
        pushContext()
        setContextElement(contextElement)
        builder.with c
        popContext()
    }

    /**
     * Executes closure with context element of given type if present.
     * @param contextElementType
     * @param closure
     */
    public <T extends CatalogueElement, A extends CatalogueElementProxy<T>> WithOptionalOrClause withContextElement(Class<T> contextElementType, @DelegatesTo(CatalogueBuilder) @ClosureParams(value=FromString, options = ['A']) Closure closure) {
        A contextElement = getContextElement(contextElementType) as A
        if (contextElement) {
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.delegate = builder
            closure(contextElement)
            return WithOptionalOrClause.NOOP
        }
        new DefaultWithOptionalOrClause(builder)
    }

    private void pushContext() {
        contexts.push([:])
    }

    private void popContext() {
        contexts.pop()
    }

    private <T extends CatalogueElement, A extends CatalogueElementProxy<T>> A setContextElement(A contextElement) {
        if (!contextElement) {
            return contextElement
        }
        for (Class type in SUPPORTED_AS_CONTEXT) {
            if (type.isAssignableFrom(contextElement.domain)) {
                contexts.last()[type] = contextElement
            }
        }
        contextElement
    }

    private <T extends CatalogueElement, A extends CatalogueElementProxy<T>> A getContextElement(Class<T> contextElementType = CatalogueElement) {
        for (Map<Class, CatalogueElementProxy> context in contexts.reverse()) {
            A result = context[contextElementType] as A
            if (result) {
                return result
            }
        }
        return null
    }

}
