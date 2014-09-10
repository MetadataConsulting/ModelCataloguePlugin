package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionService
import org.modelcatalogue.core.actions.ActionState
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.util.ListWithTotalAndType
import org.springframework.beans.factory.annotation.Autowired

class BatchMarshaller extends AbstractMarshallers {

    @Autowired ActionService actionService

    BatchMarshaller() {
        super(Batch)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]

        def ret = [
                id: el.id,
                name: el.name,
                description: el.description,
                version: el.version,
                elementType: el.class.name,
                archived: el.archived,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                link:  "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id",

        ]

        for (ActionState state in ActionState.values()) {
            ret[state.name().toLowerCase()] = [count: actionService.list(el as Batch, state).total, itemType: Action.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/actions/${state.name().toLowerCase()}"]
        }

        ret['actions'] = [count: actionService.list(el as Batch).total, itemType: Action.name, link: "/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id/actions/"]

        ret
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            name el.name
            description el.description
        }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(el.archived, "archived", xml)
    }

}




