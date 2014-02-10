package uk.co.mc.core

class Model extends ExtendibleElement  {

    static transients = ['contains']

    List/*<DataElement>*/ getContains() {
        getOutgoingRelationsByType(RelationshipType.containmentType)
    }

    Relationship addToContains(DataElement element) {
        createLinkTo(element, RelationshipType.containmentType)
    }

    void removeFromContains(DataElement element) {
        removeLinkTo(element, RelationshipType.containmentType)
    }

}
