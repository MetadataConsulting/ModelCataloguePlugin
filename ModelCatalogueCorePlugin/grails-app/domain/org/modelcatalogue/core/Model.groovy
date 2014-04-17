package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class Model extends ExtendibleElement  {


    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        modelCatalogueId boost:10
        name boost:5
        extensions component:true
        incomingRelationships component: true
        outgoingRelationships component: true
        except = ['ext', 'contains', 'hasContextOf', 'parentOf', 'childOf']
    }

    static relationships = [
            incoming: [context: 'hasContextOf', hierarchy: 'childOf'],
            outgoing: [containment: 'contains', hierarchy: 'parentOf']
    ]

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, version: ${version}, status: ${status}]"
    }

}
