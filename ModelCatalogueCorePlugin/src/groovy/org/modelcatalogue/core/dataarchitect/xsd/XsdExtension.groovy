package org.modelcatalogue.core.dataarchitect.xsd

/**
 * Created by sus_avi on 14/07/2014.
 */
class XsdExtension {

    String name
    String description
    String base
    String id
    XsdRestriction restriction
    XsdChoice choice
    XsdSequence sequence
    XsdGroup group
    ArrayList<XsdAttribute> attributes
}
