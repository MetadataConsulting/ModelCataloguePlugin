package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionRunner
import org.modelcatalogue.core.actions.ActionService
import org.springframework.beans.factory.annotation.Autowired

class ActionMarshaller extends AbstractMarshaller {

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

        ret.putAll parameters: el?.ext


        ret
    }

}




