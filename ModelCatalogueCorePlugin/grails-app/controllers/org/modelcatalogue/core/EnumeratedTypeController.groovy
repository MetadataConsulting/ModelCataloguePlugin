package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.modelcatalogue.core.util.Lists

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

    def content() {
        EnumeratedType type = EnumeratedType.get(params.id)
        if (!type) {
            notFound()
            return
        }

        ListWithTotalAndType<Map> list = Lists.lazy(params, Map) {
            List<Map> descriptors = []

            for (Map.Entry<String, String> entry in type.enumerations) {
                descriptors << createDescriptor(type, entry)
            }

            descriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    private static Map createDescriptor(EnumeratedType type, Map.Entry<String, String> enumeratedValue) {
        String link = "/${GrailsNameUtils.getPropertyName(EnumeratedType)}/$type.id"
        Map ret = [:]
        ret.id = type.getId()
        ret.elementType = "${EnumeratedType.name}.EnumeratedValue"
        ret.name = "${enumeratedValue.getKey() ?: ''}: ${enumeratedValue.getValue() ?: ''}"
        ret.link = link
        ret.status = type.status.toString()
        ret
    }

}
