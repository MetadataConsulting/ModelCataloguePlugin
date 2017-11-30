package org.modelcatalogue.core.dataarchitect

/**
 * Based on work by James Welch
 */
class StarUMLDiagram {
    //test
    Object json;
    // The top-level JSON object

    HashMap<String, Object> allClasses, topLevelClasses, allDataTypes, allEnumerations;

    StarUMLDiagram(json) {

        // We'll also add each class to a list of classes
        this.json = json
        allClasses = new HashMap<String, Object>()
        allDataTypes = new HashMap<String, Object>()
        allEnumerations = new HashMap<String, Object>()
        getAllClasses(json)
        getAllDataTypes(json)
        getAllEnumerations(json)
        getTopLevelClasses()
    }

    // assumes json is populated.
    void getAllClasses(element) {
        if (element._type.equals("UMLClass")) {
            println("Adding class: " + element.name)
            allClasses.put(element._id, element)
        } else {
            element.ownedElements.each {
                child -> getAllClasses(child)
            }
        }
    }

    void getAllDataTypes(element) {
        if (element._type.equals("UMLDataType")) {
            println("Adding datatype: " + element.name)
            allDataTypes.put(element._id, element)
        } else {
            element.ownedElements.each {
                child -> getAllDataTypes(child)
            }
        }
    }

    void getAllEnumerations(element) {
        if (element._type.equals("UMLEnumeration")) {
            println("Adding enumeration: " + element.name)
            allEnumerations.put(element._id, element)
        } else {
            element.ownedElements.each {
                child -> getAllEnumerations(child)
            }
        }
    }

    // assumes allClasses is populated
    void getTopLevelClasses() {
        // First we'll assume anything might be a top-level element
        topLevelClasses = new HashMap<String, Object>(allClasses)
        allClasses.each {
            id, elem ->
                if (elem.ownedElements) {
                    elem.ownedElements.each {
                        ownedElem ->
                            if (ownedElem._type.equals("UMLAssociation")) {
                                if (ownedElem.end2.aggregation.equals("composite")) {
                                    topLevelClasses.remove(id)
                                }
                            }
                            if (ownedElem._type.equals("UMLGeneralization")) {
                                assert (ownedElem.source.$ref.equals(elem._id))
                                topLevelClasses.remove(elem._id)
                            }
                    }
                }
        }
        topLevelClasses.each {
            id, elem ->
                println("Top Level Element: " + elem.name)
        }
    }
}
