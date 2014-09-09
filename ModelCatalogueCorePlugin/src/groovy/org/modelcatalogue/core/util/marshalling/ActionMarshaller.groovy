package org.modelcatalogue.core.util.marshalling

import grails.converters.XML
import grails.util.GrailsNameUtils
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionRunner
import org.modelcatalogue.core.actions.ActionService
import org.modelcatalogue.core.actions.Batch
import org.springframework.beans.factory.annotation.Autowired

class ActionMarshaller extends AbstractMarshallers {

    @Autowired ActionService actionService

    ActionMarshaller() {
        super(Action)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        if (!el) return [:]



        def ret = [
                id: el.id,
                outcome: el.outcome,
                version: el.version,
                elementType: el.class.name,
                dateCreated: el.dateCreated,
                lastUpdated: el.lastUpdated,
                state: el.state?.toString(),
                result: el.result,
                dependsOn: el.dependsOn.collectEntries { [it.provider.id, it.role] } + [length: el.dependsOn.size()],
                dependencies: el.dependencies.collectEntries { [it.dependant.id, it.role] } + [length: el.dependencies.size()] ,
        ]

        if (el.batch) {
            ret.batch =  el.batch
            ret.link  =  "/batch/${el.batch.id}/actions/$el.id"
        }

        if (el.type) {
            ret.type = type
            ActionRunner runner = actionService.createRunner(el.type)

            runner.initWith(el.ext ?: [:])

            ret.description         = runner.description
            ret.message             = runner.message
            ret.naturalName         = runner.naturalName
            ret.requiredParameters  = runner.requiredParameters
        }

        ret.putAll parameters: el.ext


        ret
    }

    protected void buildXml(el, XML xml) {
        xml.build {
            if (el.type) {
                'type' el.type.name
            }
            outcome el.outcome
        }
        if (el.ext) {
            xml.build {
                parameters {
                    for (e in el.ext.entrySet()) {
                        parameter key: e.key, e.value
                    }
                }
            }
        }
    }

    protected void addXmlAttributes(el, XML xml) {
        addXmlAttribute(el.id, "id", xml)
        addXmlAttribute(el.version, "version", xml)
        addXmlAttribute("/${GrailsNameUtils.getPropertyName(el.getClass())}/$el.id", "link", xml)
        addXmlAttribute(el.class.name, "elementType", xml)
        addXmlAttribute(el.state?.toString(), "state", xml)
    }

}




