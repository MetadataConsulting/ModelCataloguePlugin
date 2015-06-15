package org.modelcatalogue.core.util.builder

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain

class CatalogueBuilderContext {

    private static Set<Class> SUPPORTED_AS_CONTEXT  = [CatalogueElement, Classification, ValueDomain, DataType, Model, MeasurementUnit, DataElement]

    private List<Map<Class, ContextItem>> contexts = []

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
    public <T extends CatalogueElement> WithOptionalOrClause withContextElement(Class<T> contextElementType, @DelegatesTo(CatalogueBuilder) @ClosureParams(value=FromString, options = ['org.modelcatalogue.core.util.builder.CatalogueElementProxy<T>', 'org.modelcatalogue.core.util.builder.CatalogueElementProxy<T>,Closure']) Closure closure) {
        ContextItem<T> contextElement = getContextElement(contextElementType) as ContextItem<T>
        if (contextElement) {
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure.delegate = builder
            if (closure.maximumNumberOfParameters == 2) {
                closure(contextElement.element, contextElement.relationshipConfiguration)
                // relationship configuration can only be used one
                contextElement.relationshipConfiguration = null
            } else {
                closure(contextElement.element)
            }

            return WithOptionalOrClause.NOOP
        }
        new DefaultWithOptionalOrClause(builder)
    }

    void configureCurrentRelationship(@DelegatesTo(RelationshipProxyConfiguration) Closure relationshipExtensionsConfiguration) {
        ContextItem item = getContextElement(CatalogueElement, 1)
        if (item) {
            if (item.relationshipConfiguration) {
                item.relationshipConfiguration = item.relationshipConfiguration << relationshipExtensionsConfiguration
            } else {
                item.relationshipConfiguration = relationshipExtensionsConfiguration
            }
        }
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
        ContextItem<T> item = new ContextItem<T>(element: contextElement)
        for (Class type in SUPPORTED_AS_CONTEXT) {
            if (type.isAssignableFrom(contextElement.domain)) {
                contexts.last()[type] = item
            }
        }
        contextElement
    }

    private <T extends CatalogueElement> ContextItem<T> getContextElement(Class<T> contextElementType = CatalogueElement, int skip = 0) {
        int skipped = 0
        for (Map<Class, ContextItem> context in contexts.reverse()) {
            if (skipped++ < skip) {
                continue
            }
            ContextItem<T> result = context[contextElementType] as ContextItem<T>
            if (result) {
                return result
            }
        }
        return null
    }

}

class ContextItem<T extends CatalogueElement> {
    CatalogueElementProxy<T> element
    Closure relationshipConfiguration
}

