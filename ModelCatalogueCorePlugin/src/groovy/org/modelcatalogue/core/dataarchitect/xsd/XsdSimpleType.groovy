package org.modelcatalogue.core.dataarchitect.xsd

import groovy.xml.QName

/**
 * Created by sus_avi on 17/06/2014.
 */
class XsdSimpleType{
    String name
    String description
    XsdRestriction restriction
    XsdList list
    XsdUnion union
    QName namespace
}