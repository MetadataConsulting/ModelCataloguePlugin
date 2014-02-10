package uk.co.mc.core

class Model extends ExtendibleElement  {

    static transients = ['contains', 'hasContextOf', 'parentOf', 'childOf']

    //CONTAINMENT

    List/*<DataElement>*/ getContains() {
        getOutgoingRelationsByType(RelationshipType.containmentType)
    }

    Relationship addToContains(DataElement element) {
        createLinkTo(element, RelationshipType.containmentType)
    }

    void removeFromContains(DataElement element) {
        removeLinkTo(element, RelationshipType.containmentType)
    }

    //CONTEXT


    List/*<ConceptualDomain>*/ getHasContextOf() {
        getIncomingRelationsByType(RelationshipType.contextType)
    }

    Relationship addToHasContextOf(ConceptualDomain conceptualDomain) {
        createLinkFrom(conceptualDomain, RelationshipType.contextType)
    }

    void removeFromHasContextOf(ConceptualDomain conceptualDomain) {
        removeLinkFrom(conceptualDomain, RelationshipType.contextType)
    }

    //HIERARCHY


    List/*<Model>*/ getParentOf() {
        getOutgoingRelationsByType(RelationshipType.hierarchyType)
    }

    Relationship addToParentOf(Model model) {
        createLinkTo(model, RelationshipType.hierarchyType)
    }

    void removeFromParentOf(Model model) {
        removeLinkTo(model, RelationshipType.hierarchyType)
    }

    List/*<Model>*/ getChildOf() {
        getIncomingRelationsByType(RelationshipType.hierarchyType)
    }

    Relationship addToChildOf(Model model) {
        createLinkFrom(model, RelationshipType.hierarchyType)
    }

    void removeFromChildOf(Model model) {
        removeLinkFrom(model, RelationshipType.hierarchyType)
    }





}
