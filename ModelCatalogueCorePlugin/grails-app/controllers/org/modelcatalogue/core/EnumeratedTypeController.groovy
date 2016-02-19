package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.util.OrderedMap
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

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

    @Override
    protected bindRelations(DataType instance, boolean newVersion, Object objectToBind) {
        super.bindRelations(instance, newVersion, objectToBind)

        if (objectToBind.baseEnumeration) {
            EnumeratedType baseEnum = EnumeratedType.get(objectToBind.baseEnumeration.id)
            Map<String, String> selectedEnumerations = OrderedMap.fromJsonMap(objectToBind.selectedEnumerations)
            instance.addToIsBasedOn baseEnum, metadata: [(EnumeratedType.SUBSET_METADATA_KEY): selectedEnumerations.keySet().collect { it.replace(/,/, /\\,/) } .join(',')]
        }

    }

    private Map createDescriptor(EnumeratedType type, Map.Entry<String, String> enumeratedValue) {
        String link = "/${GrailsNameUtils.getPropertyName(EnumeratedType)}/$type.id"
        Map ret = [:]
        ret.id = type.getId()
        ret.elementType = "${EnumeratedType.name}.EnumeratedValue"
        ret.name = "${enumeratedValue.getKey() ?: ''}: ${enumeratedValue.getValue() ?: ''}"
        ret.link = "$link#${enumeratedValue.getKey()}"
        ret.status = type.status.toString()
        ret.dataModels = relationshipService.getDataModelsInfo(type)
        ret
    }

}
