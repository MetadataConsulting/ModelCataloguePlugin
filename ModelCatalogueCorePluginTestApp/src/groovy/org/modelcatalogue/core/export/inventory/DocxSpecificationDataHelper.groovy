package org.modelcatalogue.core.export.inventory

import com.craigburke.document.core.builder.DocumentBuilder
import com.google.common.collect.SetMultimap
import com.google.common.collect.TreeMultimap
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.marshalling.CatalogueElementMarshaller

/**
 * Prints data specification using the DocumentBuilder library
 */
@Log4j
class DocxSpecificationDataHelper {

    final Set<ValidationRule> rules = new HashSet<>()

    private static final Map<String, Object> HEADER_CELL = [background: '#F2F2F2']
    private static final Map<String, Object> HEADER_CELL_TEXT = [font: [color: '#29BDCA', size: 12, bold: true, family: 'Times New Roman']]
    private static final Map<String, Object> CELL_TEXT = [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final def TITLE_COLUMN_CELL = [font: [bold: true]]
    private static final Map<String, Object> ENUM_HEADER_CELL_TEXT =  [font: [size: 12, bold: true]]
    private static final Map<String, Object> DOMAIN_NAME =  [font: [color: '#29BDCA', size: 14, bold: true]]
    private static final Map<String, Object> DOMAIN_CLASSIFICATION_NAME =  [font: [color: '#999999', size: 12, bold: true]]
    static final Map<String, Object> DEPRECATED_ENUM_CELL_TEXT = [font: [color: '#999999', italic: true]]



    private static final <T extends CatalogueElement> Comparator<T> compareByName(Class<T> type) {
        [compare: { T a, T b ->
            a?.name <=> b?.name
        }] as Comparator<T>
    }

    private DocumentBuilder builder
    int depth = 3
    final ElementService elementService



    /**
     * The main method for the DocxSpecificationDataHelper program.
     *
     * @param args Not used
     */

    DocxSpecificationDataHelper(DocumentBuilder builder, Integer depth, ElementService elementService) {
        this.builder = builder
        this.depth = depth
        this.elementService = elementService
    }

    final SetMultimap<DataType, DataClass> usedDataTypes = TreeMultimap.create(compareByName(DataType), compareByName(DataClass))



    /**
     * Print the data class information in the word document
     * - recurses through the data class and prints all child classes and data elements
     * @param args Not used
     */

    def printClass(DataClass dataClass, boolean recurse, int level, String minOccurs = "", String maxOccurs = "", String parentName = "", Boolean parentChoice = false) {
        if ((recurse && level > depth) || level > 50 ) { //stop potential runaway?
            return
        }

        log.debug "Exporting data class $dataClass to Word Document level=$level"

        builder.with {



            if(minOccurs||maxOccurs) { // if min occurs and max occurs then add multiplicity information as part of heading

                "heading${Math.min(level, 6)}" dataClass.name + " [$minOccurs..$maxOccurs]", ref: dataClass.id

                paragraph {
                    text getMultiplicityText(minOccurs, maxOccurs, parentName, dataClass.name), font: [italic: true]
                }

            }else{ // otherwise do not include multiplicity information as part of heading

                "heading${Math.min(level, 6)}" dataClass.name , ref: dataClass.id

            }


            if (dataClass.description) { //print description if there is one
                paragraph {
                    text dataClass.description
                }
            }



            if (!dataClass.countContains() && !dataClass.countParentOf()) { // if the data class is empty then add description to say it's empty
                paragraph {
                    text "${dataClass.name} data class does not have any inner data classes or data elements yet.", font: [italic: true]
                }
            }else { //else if the data class has content

                if (dataClass.ext.get("http://xsd.modelcatalogue.org/section#type") == "choice") {
                    // evaluate if the data class is a choice i.e. you must choose one of the data elements and only one, or one of the child classes
                    parentChoice = true
                    // this is set to determine if the child classes are choices i.e. one or the other of the children
                    paragraph { //display human readable text to identify that the child elements are choice elements
                        text "A choice of one of the following must be submitted together with each $dataClass.name report:", font: [italic: true]
                    }
                }

                if (dataClass.countContains()) { // if the class has data elements then print them out in a table format
                    table(padding: 1, border: [size: 1, color: '#D2D2D2'], columns: [2, 3, 2, 2, 3]) {
                        //print headings
                        row {
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Name'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Description'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Multiplicity'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Data Type'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Related To'
                            }
                        }
                        //iterate through the data elements - using contains relationships to print out metadata as well.
                        dataClass.containsRelationships.eachWithIndex { Relationship dataElementRelationship, idx ->
                            DataElement dataElement = dataElementRelationship.destination
                            if (dataElement.dataType) {
                                usedDataTypes.put dataElement.dataType, dataClass
                            }

                            //evaluate if the parent class is a choice
                            if (dataClass.ext.get("http://xsd.modelcatalogue.org/section#type") == "choice" && idx != 0) {
                                row {
                                    cell(colspan: 5, align: 'center') {
                                        text "Or in the case of, ", font: [bold: true]
                                    }

                                }

                            }
                            //print out rows for each data element
                            row {
                                cell { //print name
                                    text CELL_TEXT_FIRST, "${dataElement.name} (${(dataElement.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-id")) ? dataElement.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-id") + "@" + dataElement.getDataModelSemanticVersion() : dataElement.getCombinedVersion()}  )"
                                }
                                cell { //print description
                                    text CELL_TEXT, dataElement.description ?: ''
                                }
                                cell { //print multiplicity x..x
                                    text CELL_TEXT, getMultiplicity(dataElementRelationship)
                                }
                                cell { //print data types and link to data type in the back of the document
                                    if (dataElement.dataType) {
                                        Map<String, Object> attrs = [url: "#${dataElement.dataType.id}", font: [bold: true]]
                                        attrs.putAll(CELL_TEXT)
                                        link attrs, dataElement.dataType.name
                                        if (dataElement.dataType?.instanceOf(EnumeratedType)) {
                                            text '\n\n'
                                            if (dataElement.dataType.enumerations.size() <= 10) {
                                                Enumerations enumerations = dataElement.dataType.enumerationsObject
                                                for (Enumeration entry in enumerations) {
                                                    if (entry.deprecated) {
                                                        text "${entry.key ?: ''}", font: [italic: true, bold: true, color: '#999999']
                                                        text ":"
                                                        text "${entry.value ?: ''}", font: [italic: true, color: '#999999']
                                                        text "\n"
                                                    } else {
                                                        text "${entry.key ?: ''}", font: [bold: true]
                                                        text ":"
                                                        text "${entry.value ?: ''}"
                                                        text "\n"
                                                    }

                                                }
                                            }else{
                                                text "enumeration >10, please click link above to view full list."
                                            }

                                        }
                                    }
                                }
                                cell { //print data elements that are related to this data element
                                    for (CatalogueElement relatedTo in dataElement.relatedTo) {
                                        text CELL_TEXT, getRelatedTo(relatedTo)
                                        lineBreak()
                                    }

                                }
                            }
                        }
                    }
                }
            }



            if (dataClass.countContextFor()) {  //print out the business rules associated with the class

                paragraph {
                    text "Rules relating to $dataClass.name", font: [italic: true]
                }

                table(border: [size: 1, color: '#D2D2D2']) {
                        dataClass.contextFor.each { ValidationRule vr ->
                                row {
                                    cell {
                                        Map<String, Object> attrs = [url: "#${vr.id}", font: [bold: true]]
                                        link attrs, vr?.name
                                    }
                                    cell {
                                        text vr?.description
                                    }
                                }
                                if (!(vr in rules)) rules << vr
                        }
                }
            }


                if (recurse && dataClass.countParentOf()) { //recurse through the data classes and print child classes
                    dataClass.parentOfRelationships.eachWithIndex { Relationship dataClassRelationship, idx ->
                        DataClass child = dataClassRelationship.destination

                        if(parentChoice && idx!=0){ //if the child classes are part of a choice class and they aren't the first child class print human readable text to describe the choice
                            paragraph(align: 'center'){
                                text "Or in the case of,", font: [bold: true]
                            }
                        }
                        printClass(child, true, level + 1, dataClassRelationship.ext.get("Min Occurs")?:"0", dataClassRelationship.ext.get("Max Occurs")?:"*", dataClass.name, parentChoice)
                    }
                }
        }
    }


    //print data elements that are related to this data element
    private static String getRelatedTo(CatalogueElement element) {
        if (!element.dataModel) {
            return "${element.name}"
        }
        "${element.name} (${element.ext['Data Item No'] ? "${element.ext['Data Item No']} from " : ''}${element.dataModel.name})"
    }

    //print multiplicity of data element / class i.e. [1..1]
    private static String getMultiplicity(Relationship relationship) {
        "${relationship.ext['Min Occurs'] ?: 0}..${relationship.ext['Max Occurs'] ?: 'unbounded'}"
    }


    //print data types
    def printTypes(){

        builder.with {

            pageBreak()
            heading1 'Data Types'

            if(!usedDataTypes.size()){
                paragraph {
                    text "Model has no data type yet.", font: [italic: true]
                }
            }else {

                for (DataType dataType in usedDataTypes.keySet()) {

                    log.debug "Exporting data type $dataType to Word Document"

                    Map<String, Object> attrs = [ref: "${dataType.id}", style: 'heading2']
                    attrs.putAll(DOMAIN_NAME)

                    paragraph attrs, dataType.name

                    if (dataType.dataModel) {
                        paragraph {
                            text DOMAIN_CLASSIFICATION_NAME, "(${dataType.dataModel.name})"
                        }
                    }

                    if (dataType.description) {
                        paragraph {
                            text dataType.description
                        }
                    }
                    if (hasExtraInformation(dataType)) {
                        table(columns: [1, 4], border: [size: 0], font: [color: '#5C5C5C']) {
                            if (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) {
                                row {
                                    cell 'Unit of Measure'
                                    cell {
                                        text dataType.measurementUnit.name
                                        if (dataType.measurementUnit.description) {
                                            text ' ('
                                            text dataType.measurementUnit.description
                                            ')'
                                        }
                                    }
                                }
                            }

                            if (dataType.instanceOf(ReferenceType) && dataType.dataClass) {
                                row {
                                    cell 'Data Class'
                                    cell {
                                        text dataType.dataClass.name
                                        if (dataType.dataClass.description) {
                                            text ' ('
                                            text dataType.dataClass.description
                                            ')'
                                        }
                                    }
                                }
                            }

                            if (dataType.regexDef) {
                                row {
                                    cell 'Regular Expression'
                                    cell dataType.regexDef
                                }
                            } else if (dataType.rule) {
                                row {
                                    cell 'Rule'
                                    cell dataType.rule
                                }
                            }

                            getBaseRules(dataType).each { parent ->
                                if (parent.regexDef) {
                                    row {
                                        cell "Regular Expression based on\n ${CatalogueElementMarshaller.getClassifiedName(parent)} "
                                        cell parent.regexDef
                                    }
                                } else if (parent.rule) {
                                    row {
                                        cell "Rule based on\n ${CatalogueElementMarshaller.getClassifiedName(parent)}"
                                        cell parent.rule
                                    }
                                } else {
                                    row {
                                        cell "Based On"
                                        cell CatalogueElementMarshaller.getClassifiedName(parent)
                                    }
                                }
                            }

                        }

                        if (dataType?.instanceOf(EnumeratedType)) {

                            table(border: [size: 1, color: '#D2D2D2']) {
                                row(background: '#F2F2F2') {
                                    cell ENUM_HEADER_CELL_TEXT, 'Code'
                                    cell ENUM_HEADER_CELL_TEXT, 'Description'
                                }
                                Enumerations enumerations = dataType.enumerationsObject
                                for (Enumeration entry in enumerations) {
                                    if (entry.deprecated) {
                                        row(DEPRECATED_ENUM_CELL_TEXT) {
                                            cell entry.key
                                            cell entry.value
                                        }
                                    } else {
                                        row {
                                            cell entry.key
                                            cell entry.value
                                        }
                                    }

                                }
                            }
                        }
                    }


                    paragraph style: 'heading4', margin: [bottom: 0], font: [size: 11, bold: true, color: '#999999'], "Usages"
                    for (DataClass backref in usedDataTypes.get(dataType)) {
                        paragraph(margin: [top: 0, bottom: 0]) {
                            link url: "#${backref.id}", style: 'heading4', font: [size: 9, color: '#29BDCA'], backref.name
                        }

                    }

                }
            }

        }

    }


    //print the business rules at the end of the document
    def printRules() {
        builder.with {
            rules.each { vr ->
                Map<String, Object> attrs = [ref: "${vr.id}", style: 'heading2']


                heading3 attrs, vr?.name

                table(border: [size: 1, color: '#D2D2D2']) {
                    row {
                        cell "Description"
                        cell TITLE_COLUMN_CELL, vr.description
                    }
                    row {
                        cell "Classes Applied Within"
                        cell(TITLE_COLUMN_CELL){
                            vr.appliedWithin.each{ DataClass cls ->
                                text cls?.name
                                lineBreak()
                            }
                        }
                    }
                    row {
                        cell "Data Elements Involved"
                        cell(TITLE_COLUMN_CELL){
                            vr.involves.each{ DataElement de ->
                                text de?.name
                                lineBreak()
                            }
                        }
                    }
                    row {
                        cell "Component"
                        cell TITLE_COLUMN_CELL, vr?.component
                    }
                    row {
                        cell "Rule Focus"
                        cell TITLE_COLUMN_CELL, vr?.ruleFocus
                    }
                    row {
                        cell "Trigger"
                        cell TITLE_COLUMN_CELL, vr?.trigger
                    }
                    row {
                        cell "Rule"
                        cell TITLE_COLUMN_CELL, vr?.rule
                    }
                    row {
                        cell "Error Condition"
                        cell TITLE_COLUMN_CELL, vr?.errorCondition
                    }
                    row {
                        cell "Issue Record"
                        cell TITLE_COLUMN_CELL, vr?.issueRecord
                    }
                    row {
                        cell "Notification"
                        cell TITLE_COLUMN_CELL, vr?.notification
                    }
                    row {
                        cell "Notification Target"
                        cell TITLE_COLUMN_CELL, vr?.notificationTarget
                    }
                    row {
                        cell "Last Updated"
                        cell TITLE_COLUMN_CELL, vr?.lastUpdated.format("yyyy-MM-dd")
                    }
                    row {
                        cell "Version Created"
                        cell TITLE_COLUMN_CELL, vr?.versionCreated.format("yyyy-MM-dd")
                    }
                    row {
                        cell "Status"
                        cell TITLE_COLUMN_CELL, vr?.status.toString()
                    }
                }
            }
        }
    }

    private boolean hasExtraInformation(DataType dataType) {
        (dataType.instanceOf(PrimitiveType) && dataType.measurementUnit) || dataType.instanceOf(EnumeratedType) || (dataType.instanceOf(ReferenceType) && dataType.dataClass) || dataType.rule || dataType.isBasedOn
    }

    private Set getBaseRules(DataType dataType, Set basedOn = []){
        elementService.getTypeHierarchy([:], dataType).items.each{ DataType type ->
            basedOn.add(type)
            basedOn.addAll(getBaseRules(type, basedOn))
        }
        basedOn
    }


    /**
     * This method gets the text that appears within a section of the document to describe the mulitplicity of the section to non-technical
     * users i.e. if the multiplicity is 1..1 then you must include the section within the context of the parent
     *
     * @param args Not used
     */


    def getMultiplicityText(String minOccurs, String maxOccurs, String parentName, String childName){

        String multiplicityText = ""


        // if max occurs and min occurs aren't integers then do not add any text because it doesn't mean anything
        if(!maxOccurs.isInteger() || !minOccurs.isInteger()){
            return multiplicityText
        }


        switch (minOccurs) {

            case "0": // if there is min occurs and it is 0 then the section is optional and evaluate max occurs
                switch(maxOccurs){
                    case "0": //0..0 error
                        multiplicityText = "error: please check constraints in data model - cannot have 0..0"
                        break
                    case "1": //0..1 text
                        multiplicityText = "One report containing $childName can be submitted together with each $parentName report."
                        break
                    case "*": //0..* text
                        multiplicityText = "Multiple reports containing $childName can be submitted together with each $parentName report."
                        break
                    default: //0..? text
                        multiplicityText = "Up to $maxOccurs reports containing $childName can be submitted together with each $parentName report."
                        break;
                }
                break;

            case "1": // if there is min occurs is 1 then the section is mandatory and evaluate max occurs

                switch(maxOccurs){

                    case "0": // 1..0 error
                        multiplicityText = "error: please check constraints in data model - cannot have 1..0"
                        break
                    case "1": // 1..1 text
                        multiplicityText = "One report containing $childName must be submitted with each $parentName report."
                        break
                    case "*": // 1..* text
                        multiplicityText = "One or more reports containing $childName must be submitted together with each $parentName report."
                        break
                    default: // 1..? text
                        multiplicityText = "One report containing $childName must be submitted together with each $parentName report. Up to $maxOccurs reports may be submitted."
                        break;
                }
                break;


            default: // if there is min occurs is an integer and not 0 or 1 then evaluate the max occurs

                switch(maxOccurs){

                    case "0": // error
                        multiplicityText = "error: please check constraints"
                        break

                    case "1": // error
                        multiplicityText = "error: please check constraints"
                        break

                    case "*": // ?..* text
                        multiplicityText = "$minOccurs or more reports containing $childName must be submitted together with each $parentName report."
                        break

                    default:  //?..? text i.e 5..17
                        multiplicityText = "$minOccurs reports containing $childName must be submitted together with each $parentName report. Up to $maxOccurs reports may be submitted."
                        break
                }

                break;
        }

        //return human readable non-technical multiplicity text
        multiplicityText
    }



    private static String getSameAs(CatalogueElement element) {
        if (!element.dataModel) {
            return "${element.name}"
        }
        "${element.name} (${element.ext['Data Item No'] ? "${element.ext['Data Item No']} from " : ''}${element.dataModel.name})"
    }


}
