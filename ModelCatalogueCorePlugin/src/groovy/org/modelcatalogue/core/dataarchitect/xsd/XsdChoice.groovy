package org.modelcatalogue.core.dataarchitect.xsd

/**
 * Created by sus_avi on 17/06/2014.
 */


class XsdChoice{
    String description
    String minOccurs
    String maxOccurs
    ArrayList <XsdElement> elements
    ArrayList <XsdChoice> choiceElements
    ArrayList <XsdSequence> sequenceElements
    ArrayList <XsdGroup> groupElements
}