package org.modelcatalogue.core.dataarchitect.xsd

import groovy.xml.QName

/**
 * Created by sus_avi on 17/06/2014.
 */

class XsdElement {
    String name
    String description
    String type
    String section
    String minOccurs
    String maxOccurs
    XsdSimpleType simpleType
    XsdComplexType complexType
    QName namespace
}