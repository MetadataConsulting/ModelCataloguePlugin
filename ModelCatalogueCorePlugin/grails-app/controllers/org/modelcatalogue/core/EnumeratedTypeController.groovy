package org.modelcatalogue.core

class EnumeratedTypeController extends DataTypeController<EnumeratedType> {

    EnumeratedTypeController() {
        super(EnumeratedType)
    }

    @Override
    protected getIncludeFields() {
        def fields = super.includeFields
        fields.add('enumerations')
        fields
    }

}
