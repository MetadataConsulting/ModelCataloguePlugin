package org.modelcatalogue.core.dataarchitect.xsd

import groovy.xml.QName

/**
 * Created by sus_avi on 14/07/2014.
 */
class XsdAttribute {
    String name
    String defaultValue
    String fixed
    String form
    String id
    String ref
    String type
    String use
    String description
    XsdSimpleType simpleType
    String section
    QName namespace
}
