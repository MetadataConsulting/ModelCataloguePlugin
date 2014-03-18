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

    static transients = ['isContextFor', 'includes']

    static elasticGormSearchable = {
        name boost:5
        incomingRelationships component: true
        outgoingRelationships component: true
        except = ['isContextFor', 'includes']
    }

    static searchable = false

    //CONTEXT

    List/*<Model>*/ getIsContextFor() {
        getOutgoingRelationsByType(RelationshipType.contextType)
    }

    Relationship addToIsContextFor(Model model) {
        createLinkTo(model, RelationshipType.contextType)
    }

    void removeFromIsContextFor(Model model) {
        removeLinkTo(model, RelationshipType.contextType)
    }

    //INCLUSION

    List/*<ValueDomain>*/ getIncludes() {
        getOutgoingRelationsByType(RelationshipType.inclusionType)
    }

    Relationship addToIncludes(ValueDomain valueDomain) {
        createLinkTo(valueDomain, RelationshipType.inclusionType)
    }

    void removeFromIncludes(ValueDomain valueDomain) {
        removeLinkTo(valueDomain, RelationshipType.inclusionType)
    }


    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConceptualDomain)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        ConceptualDomain cd = (ConceptualDomain) obj;
        return new EqualsBuilder()
                .append(name, cd.name)
                .append(id, cd.id)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(id)
                .toHashCode();
    }


}
