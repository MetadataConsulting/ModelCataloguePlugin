package uk.co.mc.core

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

class ConceptualDomain extends CatalogueElement  {

    static transients = ['isContextFor']

    List/*<Model>*/ getIsContextFor() {
        getOutgoingRelationsByType(RelationshipType.contextType)
    }

    Relationship addToIsContextFor(Model model) {
        createLinkTo(model, RelationshipType.contextType)
    }

    void removeFromIsContextFor(Model model) {
        removeLinkTo(model, RelationshipType.contextType)
    }


}
