package org.modelcatalogue.core.dataarchitect.xsd

/**
 * Created by sus_avi on 17/06/2014.
 */
class XsdRestriction{
    String base
    String minLength
    String maxLength
    String length
    String minInclusive
    String maxInclusive
    String minExclusive
    String maxExclusive
    String enumeration
    ArrayList<XsdPattern> patterns
    ArrayList <XsdAttribute> attributes
    XsdSequence sequence
}