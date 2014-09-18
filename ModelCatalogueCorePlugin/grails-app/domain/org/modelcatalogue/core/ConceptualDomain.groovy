package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/*
* Conceptual Domains include value domains
* i.e. a conceptual domain of a particular library could include a number of different
* value domains each of which has an enumerated lists such as: subjects:[politics, history, science],
* shelves[1a, 1b, 1c, 2a],
*
* Conceptual Domains also provide a context for models(concepts)
* i.e University Libraries and Public Libraries
* These different Conceptual Domains provide context for models i.e. the same book model can exists in the context of
* two conceptual domains:
* <xs:complexType name="book" conceptualDomain="UniversityLibrary"><xs:element name="title" /></xs:complexType>
* <xs:complexType name="book" conceptualDomain="PublicLibrary"><xs:element name="title" /></xs:complexType>
*
* */

class ConceptualDomain extends CatalogueElement {

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    String namespace

    static constraints = {
        namespace nullable: true, unique: true
    }

    static searchable = {
        name boost:5
        except = ['incomingRelationships', 'outgoingRelationships', 'valueDomains']
    }

    static relationships = [
            outgoing: [context: 'isContextFor']
    ]

    static hasMany = [valueDomains: ValueDomain]


    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

}
