package org.modelcatalogue.core.forms

import groovy.xml.XmlUtil
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.mutable.MutableInt
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.modelcatalogue.crf.model.CaseReportForm
import org.modelcatalogue.crf.model.DataType as FormDataType
import org.modelcatalogue.crf.model.GenericItem
import org.modelcatalogue.crf.model.GridGroup
import org.modelcatalogue.crf.model.Item
import org.modelcatalogue.crf.model.ItemContainer
import org.modelcatalogue.crf.model.ResponseType
import org.modelcatalogue.crf.model.Section
import org.springframework.validation.DataBinder
import org.springframework.validation.Errors
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import javax.validation.Validation
import javax.validation.Validator

class ModelToFormExporterService {

    static transactional = false

    static final String EXT_FORM_NAME = "http://forms.modelcatalogue.org/form#name"
    static final String EXT_FORM_FORM = "http://forms.modelcatalogue.org/form#form"
    static final String EXT_FORM_CUSTOMIZER = "http://forms.modelcatalogue.org/form#customizer"
    static final String EXT_FORM_VERSION = "http://forms.modelcatalogue.org/form#version"
    static final String EXT_FORM_VERSION_DESCRIPTION = "http://forms.modelcatalogue.org/form#versionDescription"
    static final String EXT_FORM_REVISION_NOTES = "http://forms.modelcatalogue.org/form#revisionNotes"
    static final String EXT_FORM_ITEM_NAMES = "http://forms.modelcatalogue.org/form#itemNames"
    static final String EXT_SECTION_EXCLUDE = "http://forms.modelcatalogue.org/section#exclude"
    static final String EXT_SECTION_EXCLUDE_DATA_ELEMENTS = "http://forms.modelcatalogue.org/section#excludeDataElements"
    static final String EXT_SECTION_MERGE = "http://forms.modelcatalogue.org/section#merge"
    static final String EXT_SECTION_TITLE = "http://forms.modelcatalogue.org/section#title"
    static final String EXT_SECTION_LABEL = "http://forms.modelcatalogue.org/section#label"
    static final String EXT_SECTION_SUBTITLE = "http://forms.modelcatalogue.org/section#subtitle"
    static final String EXT_SECTION_INSTRUCTIONS = "http://forms.modelcatalogue.org/section#instructions"
    static final String EXT_SECTION_PAGE_NUMBER = "http://forms.modelcatalogue.org/section#pageNumber"
    static final String EXT_GROUP_GRID = "http://forms.modelcatalogue.org/group#grid"
    static final String EXT_GROUP_HEADER = "http://forms.modelcatalogue.org/group#header"
    static final String EXT_GROUP_REPEAT_NUM = "http://forms.modelcatalogue.org/group#repeatNum"
    static final String EXT_GROUP_REPEAT_MAX = "http://forms.modelcatalogue.org/group#repeatMax"
    static final String EXT_ITEM_EXCLUDE = "http://forms.modelcatalogue.org/item#exclude"
    static final String EXT_ITEM_NAME = "http://forms.modelcatalogue.org/item#name"
    static final String EXT_ITEM_RESPONSE_TYPE = "http://forms.modelcatalogue.org/item#responseType"
    static final String EXT_ITEM_PHI = "http://forms.modelcatalogue.org/item#phi"
    static final String EXT_ITEM_DESCRIPTION = "http://forms.modelcatalogue.org/item#description"
    static final String EXT_ITEM_QUESTION = "http://forms.modelcatalogue.org/item#question"
    static final String EXT_ITEM_UNITS = "http://forms.modelcatalogue.org/item#units"
    static final String EXT_ITEM_REQUIRED = "http://forms.modelcatalogue.org/item#required"
    static final String EXT_ITEM_INSTRUCTIONS = "http://forms.modelcatalogue.org/item#instructions"
    static final String EXT_ITEM_QUESTION_NUMBER = "http://forms.modelcatalogue.org/item#questionNumber"
    static final String EXT_ITEM_COLUMN_NUMBER = "http://forms.modelcatalogue.org/item#columnNumber"
    static final String EXT_ITEM_DATA_TYPE = "http://forms.modelcatalogue.org/item#dataType"
    static final String EXT_ITEM_LAYOUT = "http://forms.modelcatalogue.org/item#layout"
    static final String EXT_ITEM_DEFAULT_VALUE = "http://forms.modelcatalogue.org/item#defaultValue"
    static final String EXT_ITEM_DIGITS = "http://forms.modelcatalogue.org/item#digits"
    static final String EXT_ITEM_LENGTH = "http://forms.modelcatalogue.org/item#length"
    static final String EXT_ITEM_REGEXP = "http://forms.modelcatalogue.org/item#regexp"
    static final String EXT_ITEM_REGEXP_ERROR_MESSAGE = "http://forms.modelcatalogue.org/item#regexpErrorMessage"

    static final String RESPONSE_TYPE_FILE = "file"
    static final String RESPONSE_LABEL_FILE = "file"
    static final String RESPONSE_TYPE_TEXTAREA = "textarea"
    static final String RESPONSE_TYPE_MULTI_SELECT = "multiselect"
    static final String RESPONSE_TYPE_RADIO = "radio"
    static final String RESPONSE_TYPE_CHECKBOX = "checkbox"
    static final String RESPONSE_LAYOUT_HORIZONTAL = "horizontal"
    static final String EXT_NAME_LC = "name"
    static final String EXT_NAME_CAP = "Name"
    static final String ENUM_DEFAULT = "default"
    static final Set<String> DATA_TYPE_REAL_NAMES = ['number', 'decimal', 'float', 'double', 'real', 'xs:decimal',
                                                     'xs:double', 'xs:float']
    static final Set<String> DATA_TYPE_INTEGER_NAMES = ['int', 'integer', 'long', 'short', 'byte', 'xs:int',
                                                        'xs:integer', 'xs:long', 'xs:short', 'xs:byte',
                                                        'xs:nonNegativeInteger', 'xs:nonPositiveInteger',
                                                        'xs:negativeInteger', 'xs:positiveInteger', 'xs:unsignedLong',
                                                        'xs:unsignedInt', 'xs:unsignedShort', 'xs:unsignedByte']
    public static final ArrayList<String> DATA_TYPE_DATA_NAMES = ['date', 'xs:date']
    public static final ArrayList<String> DATA_TYPE_PDATE_NAMES = ['pdate', 'partialdate', 'xs:gYear', 'xs:gYearMonth']

    ElementService elementService

    private static class ItemIndex {
        private int index = 0
    }

    CaseReportForm convert(DataClass formModel) {
        Set<Long> processed = []
        String formName = formModel.ext[EXT_FORM_NAME] ?: formModel.name

        Map<String, String> nameOverrides = formModel.ext[EXT_FORM_ITEM_NAMES]?.split('\n')?.collectEntries {
            it.split('=') as List<String>
        } ?: [:]



        MutableInt itemNumber = new MutableInt(1)
        CaseReportForm form = CaseReportForm.build(formName) {
            def caseReportForm = delegate
            version formModel.ext[EXT_FORM_VERSION] ?: formModel.versionNumber.toString()
            versionDescription formModel.ext[EXT_FORM_VERSION_DESCRIPTION] ?: formModel.description ?: "Generated from ${alphaNumNoSpaces(formModel.name)}"
            revisionNotes formModel.ext[EXT_FORM_REVISION_NOTES] ?: "Generated from ${alphaNumNoSpaces(formModel.name)}"

            if (formModel.countParentOf() && formModel.ext[EXT_FORM_FORM] != 'true') {
                processed << formModel.getId()
                for (Relationship sectionRel in formModel.parentOfRelationships) {
                    handleSectionModel(itemNumber, processed, formName, caseReportForm, sectionRel, nameOverrides)
                }
            }

            // at least one section is mandatory (this may happen when data class has no childs or all childs are excluded)
            if (caseReportForm.sections.isEmpty()) {
                handleSectionModel(itemNumber, [] as Set<Long>, '', caseReportForm, new Relationship(destination: formModel), nameOverrides, formModel.ext[EXT_FORM_FORM] != 'true')
            }
        }

        String customizer = formModel.ext[EXT_FORM_CUSTOMIZER]

        if (customizer) {
            try {
                new SecuredRuleExecutor(
                    form: form,
                    include: { String mcid ->
                        CatalogueElement dataClass = elementService.findByModelCatalogueId(DataClass, mcid)
                        if (!dataClass) {
                            throw new IllegalArgumentException("No data class found for id: $mcid!")
                        }
                        handleSectionModel(itemNumber, processed, formName, form, new Relationship(destination: dataClass), nameOverrides)
                    },
                    add: { String sectionName, String mcid ->
                        Section section = form.sections[sectionName]
                        if (!section) {
                            throw new IllegalArgumentException("No such section: $section!")
                        }
                        CatalogueElement dataElement = elementService.findByModelCatalogueId(DataElement, mcid)
                        if (!dataElement) {
                            throw new IllegalArgumentException("No data element found for id: $mcid!")
                        }
                        generateItems([new Relationship(destination: dataElement)], sectionName, sectionName, nameOverrides, section, itemNumber, null, null)
                    }
                ).execute(customizer)
            } catch (Exception e) {
                throw new IllegalArgumentException("There were problems with the form customization script!", e)
            }
        }

        form.sections.each { String name, Section section ->
            section.sortItemsByQuestionNumber()
        }

        return form
    }

    private void handleSectionModel(MutableInt itemNumber, Set<Long> processed, String prefix, CaseReportForm form, Relationship sectionRel, Map<String, String> nameOverrides, boolean dataElementsOnly = false) {
        DataClass sectionModel = sectionRel.destination as DataClass

        if (sectionModel.getId() in processed) {
            return
        }

        processed << sectionModel.getId()

        String sectionModelName = fromDestination(sectionRel, EXT_NAME_CAP, fromDestination(sectionRel, EXT_NAME_LC, sectionModel.name))
        String sectionName = fromDestination(sectionRel, EXT_SECTION_LABEL, alphaNumNoSpaces(sectionModelName))
        log.info "Creating section $sectionName for model $sectionModel"

        if(fromDestination(sectionRel, EXT_SECTION_EXCLUDE) == 'true') {
            log.info "Section $sectionName is excluded from the processing"
            return
        }

        if (dataElementsOnly && sectionModel.countContains() || !dataElementsOnly) {
            form.section(sectionName) {
                title fromDestination(sectionRel, EXT_SECTION_TITLE, sectionModelName)
                subtitle fromDestination(sectionRel, EXT_SECTION_SUBTITLE)
                instructions fromDestination(sectionRel, EXT_SECTION_INSTRUCTIONS, sectionModel.description)
                pageNumber fromDestination(sectionRel, EXT_SECTION_PAGE_NUMBER)

                generateItems(itemNumber, prefix, delegate as ItemContainer, sectionRel, null, null, nameOverrides)

                if (dataElementsOnly) {
                    return
                }

                handleGroupOrVirtualSection(itemNumber, processed, prefix + " " + sectionName, delegate, sectionModel.parentOfRelationships, true, nameOverrides)
            }
        }
    }

    private void handleGroupOrVirtualSection(MutableInt itemNumber, Set<Long> processed, String prefix, Section section,
                                             List<Relationship> relationships, boolean nameAsHeader, Map<String, String> nameOverrides) {
        for (Relationship itemsWithHeaderOrGridRel in relationships) {
            DataClass itemsWithHeaderOrGrid = itemsWithHeaderOrGridRel.destination as DataClass

            if (itemsWithHeaderOrGrid.getId() in processed) {
                return
            }

            processed << itemsWithHeaderOrGridRel.getId()

            String itemsWithHeaderOrGridName = fromDestination(itemsWithHeaderOrGridRel, EXT_NAME_CAP, fromDestination(itemsWithHeaderOrGridRel, EXT_NAME_LC, itemsWithHeaderOrGrid.name))
            log.info "Creating group or section for model $itemsWithHeaderOrGrid"

            if(fromDestination(itemsWithHeaderOrGridRel, EXT_SECTION_EXCLUDE) == 'true') {
                log.info "Group or section for model $itemsWithHeaderOrGrid is excluded from the processing"
                continue
            }

            if (fromDestination(itemsWithHeaderOrGridRel, EXT_GROUP_GRID) == 'true') {

                section.grid(alphaNumNoSpaces(itemsWithHeaderOrGridName)) { GridGroup grid ->
                    header fromDestination(itemsWithHeaderOrGridRel, EXT_GROUP_HEADER, itemsWithHeaderOrGridName)

                    generateItems(itemNumber, prefix, grid, itemsWithHeaderOrGridRel, null, null, nameOverrides)

                    Integer repeatNum = safeInteger(fromDestination(itemsWithHeaderOrGridRel, EXT_GROUP_REPEAT_NUM), EXT_GROUP_REPEAT_NUM, itemsWithHeaderOrGridRel.destination)
                    if (repeatNum) {
                        rows repeatNum

                    }
                    Integer repeatMax = safeInteger(fromDestination(itemsWithHeaderOrGridRel, EXT_GROUP_REPEAT_MAX), EXT_GROUP_REPEAT_MAX, itemsWithHeaderOrGridRel.destination)
                    if (repeatMax) {
                        upto repeatMax
                    }
                }
            } else {
                if (fromDestination(itemsWithHeaderOrGridRel, EXT_SECTION_MERGE) != "true") {
                    if (nameAsHeader) {
                        generateItems(itemNumber, prefix, section, itemsWithHeaderOrGridRel, itemsWithHeaderOrGridName, null, nameOverrides)
                    } else {
                        generateItems(itemNumber, prefix, section, itemsWithHeaderOrGridRel, null, itemsWithHeaderOrGridName, nameOverrides)
                    }
                } else {
                    // if merge, do not include header
                    generateItems(itemNumber, prefix, section, itemsWithHeaderOrGridRel, null, null, nameOverrides)
                }
            }
            handleGroupOrVirtualSection(itemNumber, processed, prefix + " " + itemsWithHeaderOrGridName, section, itemsWithHeaderOrGrid.parentOfRelationships, false, nameOverrides)
        }
    }

    Errors validate(CaseReportForm form) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator()
        SpringValidatorAdapter validatorAdapter = new SpringValidatorAdapter(validator)

        DataBinder binder = new DataBinder(form, 'form')
        binder.addValidators(validatorAdapter)
        binder.validate()
        binder.bindingResult
    }

    static String alphaNumNoSpaces(String label) {
        label?.replaceAll(/[^\pL\pN_]/, '_')
    }

    private void generateItems(MutableInt itemNumber, String prefix, ItemContainer container, Relationship relationship,
                               String aHeader, String aSubheader, Map<String, String> nameOverrides) {
        DataClass model = relationship.destination as DataClass

        if(fromDestination(relationship, EXT_SECTION_EXCLUDE_DATA_ELEMENTS) == 'true') {
            log.info "Items for model $model are excluded from the processing"
            return
        }

        generateItems(model.containsRelationships, prefix, model.name, nameOverrides, container, itemNumber, aHeader, aSubheader)
    }

    private void generateItems(List<Relationship> relationships, String prefix, String modelName, Map<String, String> nameOverrides, ItemContainer container, MutableInt itemNumber, String aHeader, String aSubheader) {
        boolean first = true

        for (Relationship rel in relationships) {
            DataElement dataElement = rel.destination as DataElement
            DataType dataType = dataElement.dataType

            log.info "Generating items from data element $dataElement"

            if (fromDestination(rel, EXT_ITEM_EXCLUDE) == 'true') {
                log.info "Items for data element $dataElement are excluded from the processing"
                continue
            }

            List<CatalogueElement> candidates = [dataElement, dataType].grep()

            collectBases(candidates, dataElement)
            collectBases(candidates, dataType)

            // bit of heuristic
            String localName = fromDestination(rel, EXT_NAME_CAP, fromDestination(rel, EXT_NAME_LC, dataElement.name))
            String itemName = fromDestination(rel, EXT_ITEM_NAME, alphaNumNoSpaces("${prefix ? (prefix + '_') : ''}${modelName}_${localName}"))
            itemName = nameOverrides[itemName] ?: itemName
            String normalizedResponseType = normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE))
            if (candidates.any { it.name.toLowerCase() == 'file' } || normalizedResponseType == RESPONSE_TYPE_FILE) {
                container.file(itemName)
            } else if (dataType && dataType.instanceOf(EnumeratedType)) {
                // either value domain is marked as multiple or
                Map<String, Object> enumOptions = (dataType as EnumeratedType).enumerationsObject.iterator().collectEntries {
                    org.modelcatalogue.core.enumeration.Enumeration enumeration ->
                        [enumeration.value ?: enumeration.key, enumeration.key == ENUM_DEFAULT ? '' : enumeration.key]
                }
                if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) in
                    [RESPONSE_TYPE_CHECKBOX, RESPONSE_TYPE_MULTI_SELECT] || rel.ext[Metadata.MAX_OCCURS] && rel.ext[Metadata.MAX_OCCURS] != '1') {
                    // multi select or checkbox (default)
                    if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) == RESPONSE_TYPE_MULTI_SELECT) {
                        container.multiSelect(itemName) {
                            options enumOptions
                        }
                    } else {
                        container.checkbox(itemName) {
                            options enumOptions
                        }
                    }
                } else {
                    // single select (default) or radio
                    if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) == RESPONSE_TYPE_RADIO) {
                        container.radio(itemName) {
                            options enumOptions
                        }
                    } else {
                        container.singleSelect(itemName) {
                            def selectOptions = ["Please select...": '']
                            selectOptions.putAll(enumOptions)
                            options selectOptions
                        }
                    }
                }
            } else {
                if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) == RESPONSE_TYPE_TEXTAREA) {
                    container.textarea(itemName)
                } else {
                    container.text(itemName)
                }
            }

            GenericItem last = container.items.values().last()

            last.with {
                // TODO: is there any way to configure simple conditional display
                // TODO: validation
                String regexpDef = fromCandidates(rel, candidates, EXT_ITEM_REGEXP, dataType?.regexDef)
                if (regexpDef) {
                    String message = "Value must match provided regular expression"
                    if (regexpDef.size() + message.size() < 253) {
                        message = "$message: $regexpDef"
                    }
                    regexp regexpDef, fromCandidates(rel, candidates, EXT_ITEM_REGEXP_ERROR_MESSAGE, message)
                }
                description fromCandidates(rel, candidates, EXT_ITEM_DESCRIPTION, dataElement.description)
                question fromCandidates(rel, candidates, EXT_ITEM_QUESTION, localName)
                def qNumber = fromCandidates(rel, candidates, EXT_ITEM_QUESTION_NUMBER)

                questionNumber = (qNumber == null ? itemNumber.value++ : qNumber)
                if (!last.group) {
                    instructions generateItemElementId(dataElement, fromCandidates(rel, candidates, EXT_ITEM_INSTRUCTIONS),
                        fromCandidates(rel, candidates, "id"), prefix + questionNumber) + generateItemInstructions(dataElement,
                        fromCandidates(rel, candidates, EXT_ITEM_INSTRUCTIONS))
                }
                phi(fromCandidates(rel, candidates, EXT_ITEM_PHI) == 'true')
                required(fromCandidates(rel, candidates, EXT_ITEM_REQUIRED) == 'true' || rel.ext[Metadata.MIN_OCCURS] == '1')
                columnNumber = safeInteger(fromCandidates(rel, candidates, EXT_ITEM_COLUMN_NUMBER), EXT_ITEM_COLUMN_NUMBER, rel)
                units fromCandidates(rel, candidates, EXT_ITEM_UNITS, candidates.find {
                    it.instanceOf(PrimitiveType) && it.measurementUnit
                }?.measurementUnit?.symbol)

                if (first) {
                    if (!last.group) {
                        header aHeader
                        subheader aSubheader
                    }
                    first = false
                }
                if (last.responseType != ResponseType.FILE) {
                    Integer aLength = safeInteger(fromCandidates(rel, candidates, EXT_ITEM_LENGTH), EXT_ITEM_LENGTH, rel)
                    Integer aDigits = safeInteger(fromCandidates(rel, candidates, EXT_ITEM_DIGITS), EXT_ITEM_DIGITS, rel)

                    if (aLength) {
                        if (aDigits) {
                            digits aLength, aDigits
                        } else {
                            length aLength
                        }
                    } else if (aDigits) {
                        digits aDigits
                    }

                    last.dataType(guessDataType(candidates, fromCandidates(rel, candidates, EXT_ITEM_DATA_TYPE)))
                }

                if (last.responseType.supportingDefaultValue) {
                    value fromCandidates(rel, candidates, EXT_ITEM_DEFAULT_VALUE)
                }

                if (last.responseType in [ResponseType.CHECKBOX, ResponseType.RADIO]) {
                    if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_LAYOUT)) == RESPONSE_LAYOUT_HORIZONTAL) {
                        layout horizontal
                    }
                }
                if (last instanceof Item) {
                    if (last.responseType == ResponseType.FILE) {
                        last.setResponseLabel(RESPONSE_LABEL_FILE)
                    } else {
                        CatalogueElement labelSource = dataType ?: dataElement
                        last.setResponseLabel(alphaNumNoSpaces(labelSource.name + "_" + labelSource.versionNumber))
                    }
                }
            }
        }
    }

    /**
     * Generate an <span> element based on his datatype
     * @param element
     * @param overridenInstructions
     * @return
     */
    private static String generateItemElementId(DataElement element, String overridenInstructions, String spanID,
                                                String defaultSpanID) {
        if (overridenInstructions && overridenInstructions.contains("</span>") && overridenInstructions.contains("<span data-id")) {
            return overridenInstructions.subSequence(overridenInstructions.indexOf("<span data-id"), overridenInstructions.indexOf("</span>") + 7)
        } else {
            //add a logic here to colect span ID and his associated datatype
        }


        DataType dataType = element?.dataType
        def regex = dataType?.regexDef ?: ''
        def dataTypeName = transformDataType(dataType)
        def defaultID = (spanID == null ? defaultSpanID : spanID)

        String hidden = "";
        String typeStr = " data-type=\"";
        if ("hidden" == "key")//TODO add additional metadata
        {
            hidden = " data-hidden=\"true\"";
        }

        if (StringUtils.isNotEmpty(regex)) {
            regex = " data-regex=\"" + XmlUtil.escapeXml(dataType.regexDef) + "\"";
        }


        typeStr += dataTypeName + "\""
        return "<span data-id=\"" + defaultID + "\"" + typeStr + hidden + regex + "> </span>"
    }

    /**
     * Generate text description if was not already overriden.
     * @param element
     * @param overridenInstructions
     * @return text description with or without previous saved instructions. Keep compatibility between old and new format.
     */
    private static String generateItemInstructions(DataElement element, String overridenInstructions) {
        if (!overridenInstructions) {
            return element.description
        }
        if (!overridenInstructions && !overridenInstructions.contains("<span data-id")) {
            return element.description
        } else {
            return overridenInstructions.substring(overridenInstructions.indexOf("</span>") + 7)
        }
    }


    protected static transformDataType(DataType dataType) {
        if (dataType != null) {
            if (dataType instanceof EnumeratedType) {
                return dataType.name
            }

            def dataType2 = dataType.name.replace('xs:', '')

            def basicOnes = [
                "string", "boolean",
                "integer", "decimal",
                "float", "date",
                "pdate", "an10 date",
                "time", "datetime",
                "textarea", "file",
                "email", "phone",
                "NHSNumber"];

            if (dataType2.toLowerCase() == "nonnegativeinteger" || dataType2.toLowerCase() == "positiveinteger") {
                dataType2 = "integer"
            } else if (dataType2.toLowerCase() == "double") {
                dataType2 = "decimal"
            } else if (dataType2.toLowerCase() == "dateTime") {
                dataType2 = "datetime"
            } else if (dataType2.toLowerCase() == "base64binary") {
                dataType2 = "file"
            } else if (!basicOnes.contains(dataType2.toLowerCase())) {
                dataType2 = "string"
            }

            if (dataType2 == null) return "null"
            return dataType2.replaceAll(" ", "-").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("/", "-").toLowerCase()
        }
    }

    private static FormDataType guessDataType(List<CatalogueElement> candidates, String extDataType) {
        if (extDataType) {
            switch (normalizeResponseType(extDataType)) {
                case DATA_TYPE_REAL_NAMES: return FormDataType.REAL
                case DATA_TYPE_INTEGER_NAMES: return FormDataType.INT
                case DATA_TYPE_DATA_NAMES: return FormDataType.DATE
                case DATA_TYPE_PDATE_NAMES: return FormDataType.PDATE
                default: return FormDataType.ST
            }
        }
        for (CatalogueElement candidate in candidates) {
            switch (candidate.name) {
                case DATA_TYPE_REAL_NAMES: return FormDataType.REAL
                case DATA_TYPE_INTEGER_NAMES: return FormDataType.INT
                case DATA_TYPE_DATA_NAMES: return FormDataType.DATE
                case DATA_TYPE_PDATE_NAMES: return FormDataType.PDATE
            }
        }
        return FormDataType.ST

    }

    private static String fromDestination(Relationship rel, String extensionName, String defaultValue = null) {
        String value = rel.ext[extensionName]
        if (value) {
            return value
        }
        value = rel.destination.ext[extensionName]
        if (value) {
            return value
        }
        return defaultValue
    }

    private static String fromCandidates(Relationship rel, List<CatalogueElement> candidates, String extensionName,
                                         String defaultValue = null) {
        String value = rel.ext[extensionName]
        if (value) {
            return value
        }
        for (CatalogueElement candidate in candidates) {
            value = candidate.ext[extensionName]
            if (value) {
                return value
            }
        }
        return defaultValue
    }

    private static String normalizeResponseType(String responseType) {
        if (!responseType) {
            return ''
        }
        responseType.replaceAll('_', ' ').replaceAll(/\s+/, '').toLowerCase()
    }

    private static Integer safeInteger(String value, String key, Object element) {
        if (!value) {
            return null
        }
        if (value =~ /\d+/) {
            return value as Integer
        }
        throw new IllegalArgumentException("Value $value (extension $key of $element) is not a number!")
    }

    private static void collectBases(Collection<CatalogueElement> collector, CatalogueElement source) {
        if (source) {
            collector.addAll source.isBasedOn
            for (CatalogueElement el in source.isBasedOn) {
                collectBases(collector, el)
            }
        }
    }
}
