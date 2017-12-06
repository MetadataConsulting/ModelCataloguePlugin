package org.modelcatalogue.core

import static org.springframework.http.HttpStatus.OK
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.persistence.EnumeratedTypeGormService
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

class EnumeratedTypeController extends DataTypeController<EnumeratedType> {

    EnumeratedTypeGormService enumeratedTypeGormService

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
        long enumeratedTypeId = params.long('id')
        EnumeratedType type = enumeratedTypeGormService.findById(enumeratedTypeId)
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

        respond Lists.wrap(params, "/${resourceName}/${enumeratedTypeId}/content", list)
    }

    def setDeprecated() {
        if (handleReadOnly()) {
            return
        }

        def jsonPayload = request.JSON
        long enumeratedTypeId = params.long('id')
        EnumeratedType enumeratedType = enumeratedTypeGormService.findById(enumeratedTypeId)

        if (!enumeratedType) {
            notFound()
            return
        }

        def enumerations = enumeratedType.enumerationsObject
        println enumerations.toJsonString()
        enumeratedType.setEnumerations(enumerations.withDeprecatedEnumeration(jsonPayload.enumerationId, jsonPayload.deprecated))

        if (enumeratedType.save(flush: true))  {
            return respond(enumeratedType, [status: OK])
        }
        respond enumeratedType.errors
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
        ret.status = enumeratedValue.deprecated ? ElementStatus.DEPRECATED.toString() : type.status.toString()
        ret.dataModels = relationshipService.getDataModelsInfo(type)
        ret
    }

}
