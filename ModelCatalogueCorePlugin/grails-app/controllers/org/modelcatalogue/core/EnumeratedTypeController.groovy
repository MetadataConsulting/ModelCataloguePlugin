package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

import static org.springframework.http.HttpStatus.OK

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

            for (Enumeration entry in type.enumerationsObject) {
                descriptors << createDescriptor(type, entry)
            }

            descriptors
        }

        respond Lists.wrap(params, "/${resourceName}/${params.id}/content", list)
    }

    def setDeprecated() {
        def jsonPayload = request.JSON
        def enumeratedType = EnumeratedType.get(params.id)

        if (!enumeratedType) {
            notFound()
            return
        }

        def enumerations = enumeratedType.enumerationsObject
        println enumerations.toJsonString()
        enumeratedType.setEnumerations(enumerations.withDeprecatedEnumeration(jsonPayload.enumerationId, jsonPayload.deprecated))

        enumeratedType.save(failOnError: true)

        respond enumeratedType, [status: OK]
    }

    @Override
    protected bindRelations(DataType instance, boolean newVersion, Object objectToBind) {
        super.bindRelations(instance, newVersion, objectToBind)

        if (objectToBind.baseEnumeration) {
            EnumeratedType baseEnum = EnumeratedType.get(objectToBind.baseEnumeration.id)
            Enumerations selectedEnumerations = Enumerations.from(objectToBind.selectedEnumerations)
            instance.addToIsBasedOn baseEnum, metadata: [(EnumeratedType.SUBSET_METADATA_KEY): selectedEnumerations.iterator().collect { it.id } .join(',')]
        }

    }

    private Map createDescriptor(EnumeratedType type, Enumeration enumeratedValue) {
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
