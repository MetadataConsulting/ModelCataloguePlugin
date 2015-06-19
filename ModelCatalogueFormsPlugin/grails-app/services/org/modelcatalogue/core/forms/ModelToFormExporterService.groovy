package org.modelcatalogue.core.forms

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.crf.model.CaseReportForm
import org.modelcatalogue.crf.model.GenericItem
import org.modelcatalogue.crf.model.ItemContainer
import org.modelcatalogue.crf.model.ResponseType
import org.modelcatalogue.crf.model.DataType as FormDataType
import org.modelcatalogue.crf.model.Section
import org.springframework.validation.DataBinder
import org.springframework.validation.Errors
import org.springframework.validation.beanvalidation.SpringValidatorAdapter

import javax.validation.Validation
import javax.validation.Validator

class ModelToFormExporterService {

    static transactional = false

    static final String EXT_FORM_NAME = "http://forms.modelcatalogue.org/form#name"
    static final String EXT_FORM_VERSION = "http://forms.modelcatalogue.org/form#version"
    static final String EXT_FORM_VERSION_DESCRIPTION = "http://forms.modelcatalogue.org/form#versionDescription"
    static final String EXT_FORM_REVISION_NOTES = "http://forms.modelcatalogue.org/form#revisionNotes"
    static final String EXT_SECTION_TITLE = "http://forms.modelcatalogue.org/section#title"
    static final String EXT_SECTION_SUBTITLE = "http://forms.modelcatalogue.org/section#subtitle"
    static final String EXT_SECTION_INSTRUCTIONS = "http://forms.modelcatalogue.org/section#instructions"
    static final String EXT_SECTION_PAGE_NUMBER = "http://forms.modelcatalogue.org/section#pageNumber"
    static final String EXT_GROUP_GRID = "http://forms.modelcatalogue.org/group#grid"
    static final String EXT_GROUP_HEADER = "http://forms.modelcatalogue.org/group#header"
    static final String EXT_GROUP_REPEAT_NUM = "http://forms.modelcatalogue.org/group#repeatNum"
    static final String EXT_GROUP_REPEAT_MAX = "http://forms.modelcatalogue.org/group#repeatMax"
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
    static final String RESPONSE_TYPE_TEXTAREA = "textarea"
    static final String RESPONSE_TYPE_MULTI_SELECT = "multiselect"
    static final String RESPONSE_TYPE_RADIO = "radio"
    static final String RESPONSE_TYPE_CHECKBOX = "checkbox"
    static final String RESPONSE_LAYOUT_HORIZONTAL = "horizontal"
    static final String EXT_MAX_OCCURS = "Max Occurs"
    static final String EXT_MIN_OCCURS = "Min Occurs"
    static final String EXT_NAME_LC = "Name"
    static final String EXT_NAME_CAP = "Name"
    static final Set<String> DATA_TYPE_REAL_NAMES = ['number', 'decimal', 'float', 'double', 'real', 'xs:decimal', 'xs:double', 'xs:float']
    static final Set<String> DATA_TYPE_INTEGER_NAMES = ['int', 'integer', 'long', 'short', 'byte', 'xs:int', 'xs:integer', 'xs:long', 'xs:short', 'xs:byte','xs:nonNegativeInteger', 'xs:nonPositiveInteger', 'xs:negativeInteger', 'xs:positiveInteger', 'xs:unsignedLong', 'xs:unsignedInt', 'xs:unsignedShort', 'xs:unsignedByte']
    public static final ArrayList<String> DATA_TYPE_DATA_NAMES = ['date', 'xs:date']
    public static final ArrayList<String> DATA_TYPE_PDATE_NAMES = ['pdate', 'partialdate', 'xs:gYear', 'xs:gYearMonth']


    CaseReportForm convert(Model formModel) {
        Set<Long> processed = []
        String formName = formModel.ext[EXT_FORM_NAME] ?:formModel.name
        CaseReportForm.build(formName) {
            version formModel.ext[EXT_FORM_VERSION] ?: formModel.versionNumber.toString()
            versionDescription formModel.ext[EXT_FORM_VERSION_DESCRIPTION] ?: formModel.description ?: "Generated from ${alphaNumNoSpaces(formModel.name)}"
            revisionNotes formModel.ext[EXT_FORM_REVISION_NOTES] ?: "Generated from ${alphaNumNoSpaces(formModel.name)}"

            if (formModel.countParentOf()) {
                processed << formModel.getId()
                for (Relationship sectionRel in formModel.parentOfRelationships) {
                    handleSectionModel(processed, formName, delegate as CaseReportForm, sectionRel)
                }
            } else {
                handleSectionModel(processed, '', delegate as CaseReportForm, new Relationship(destination: formModel), true)
            }
        }

    }

    private void handleSectionModel(Set<Long> processed, String prefix, CaseReportForm form, Relationship sectionRel, boolean dataElementsOnly = false) {
        Model sectionModel = sectionRel.destination as Model

        if (sectionModel.getId() in processed) {
            return
        }

        processed << sectionModel.getId()

        String sectionName = fromDestination(sectionRel, EXT_NAME_CAP, fromDestination(sectionRel, EXT_NAME_LC, sectionModel.name))

        log.info "Creating section $sectionName for model $sectionModel"

        if (dataElementsOnly && sectionModel.countContains() || !dataElementsOnly) {
            form.section(alphaNumNoSpaces(sectionName)) {
                title fromDestination(sectionRel, EXT_SECTION_TITLE, sectionName)
                subtitle fromDestination(sectionRel, EXT_SECTION_SUBTITLE)
                instructions fromDestination(sectionRel, EXT_SECTION_INSTRUCTIONS, sectionModel.description)
                pageNumber fromDestination(sectionRel, EXT_SECTION_PAGE_NUMBER)

                generateItems(processed, prefix, delegate as ItemContainer, sectionModel, null, null)

                if (dataElementsOnly) {
                    return
                }

                handleGroupOrVirtualSection(processed, prefix, delegate, sectionModel.parentOfRelationships, true)
            }
        }
    }

    private void handleGroupOrVirtualSection(Set<Long> processed, String prefix, Section section, List<Relationship> relationships, boolean nameAsHeader) {


        for (Relationship itemsWithHeaderOrGridRel in relationships) {
            Model itemsWithHeaderOrGrid = itemsWithHeaderOrGridRel.destination as Model

            if (itemsWithHeaderOrGridRel.getId() in processed) {
                return
            }

            processed << itemsWithHeaderOrGridRel.getId()


            log.info "Creating group or section for model $itemsWithHeaderOrGrid"
            String itemsWithHeaderOrGridName = fromDestination(itemsWithHeaderOrGridRel, EXT_NAME_CAP, fromDestination(itemsWithHeaderOrGridRel, EXT_NAME_LC, itemsWithHeaderOrGrid.name))
            if (fromDestination(itemsWithHeaderOrGridRel, EXT_GROUP_GRID) == 'true') {

                section.grid(alphaNumNoSpaces(itemsWithHeaderOrGridName)) {
                    header fromDestination(itemsWithHeaderOrGridRel, EXT_GROUP_HEADER, itemsWithHeaderOrGridName)

                    generateItems(processed, prefix, section, itemsWithHeaderOrGrid)

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
                if (nameAsHeader) {
                    generateItems(processed, prefix, section, itemsWithHeaderOrGrid, itemsWithHeaderOrGridName)
                } else {
                    generateItems(processed, prefix, section, itemsWithHeaderOrGrid, null, itemsWithHeaderOrGridName)
                }
            }
            handleGroupOrVirtualSection(processed, prefix, section, itemsWithHeaderOrGrid.parentOfRelationships, false)
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

    private void generateItems(Set<Long> processed, String prefix, ItemContainer container, Model model, String aHeader = null, String aSubheader = null) {
        container.with {

            boolean first = true

            for (Relationship rel in model.containsRelationships) {
                DataElement dataElement = rel.destination as DataElement
                ValueDomain valueDomain = dataElement.valueDomain
                DataType dataType = valueDomain?.dataType

                if (dataElement.getId() in processed) {
                    return
                }

                processed << dataElement.getId()

                log.info "Generating items from data element $dataElement"

                List<CatalogueElement> candidates = [dataElement, valueDomain, dataType].grep()

                collectBases(candidates, dataElement)
                collectBases(candidates, valueDomain)
                collectBases(candidates, dataType)


                // bit of heuristic
                String localName = fromDestination(rel, EXT_NAME_CAP, fromDestination(rel, EXT_NAME_LC, dataElement.name))
                String itemName = alphaNumNoSpaces("${prefix ? (prefix + '_') : ''}${model.name}_${localName}")
                if (candidates.any { it.name.toLowerCase() == 'file' } || candidates.any { normalizeResponseType(it.ext[EXT_ITEM_RESPONSE_TYPE]) == RESPONSE_TYPE_FILE }) {
                    file(itemName)
                } else if (dataType && dataType.instanceOf(EnumeratedType)) {
                    // either value domain is marked as multiple or
                    Map<String, Object> enumOptions = (dataType as EnumeratedType).enumerations.collectEntries { key, value -> [value ?: key, key]}
                    if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) in [RESPONSE_TYPE_CHECKBOX, RESPONSE_TYPE_MULTI_SELECT] || valueDomain?.multiple || rel.ext[EXT_MAX_OCCURS] && rel.ext[EXT_MAX_OCCURS] != '1') {
                        // multi select or checkbox (default)
                        if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) == RESPONSE_TYPE_MULTI_SELECT) {
                            multiSelect(itemName) {
                                options enumOptions
                            }
                        } else {
                            checkbox(itemName) {
                                options enumOptions
                            }
                        }
                    } else {
                        // single select (default) or radio
                        if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) == RESPONSE_TYPE_RADIO) {
                            radio(itemName) {
                                options enumOptions
                            }
                        } else {
                            singleSelect(itemName) {
                                options enumOptions
                            }
                        }
                    }
                } else {
                    if (normalizeResponseType(fromCandidates(rel, candidates, EXT_ITEM_RESPONSE_TYPE)) == RESPONSE_TYPE_TEXTAREA) {
                        textarea(itemName)
                    } else {
                        text(itemName)
                    }
                }

                GenericItem last = container.items.values().last()

                last.with {
                    // TODO: is there any way to configure simple conditional display
                    // TODO: validation
                    String regexpDef = fromCandidates(rel, candidates, EXT_ITEM_REGEXP, valueDomain?.regexDef)
                    if (regexpDef) {
                        regexp regexpDef, fromCandidates(rel, candidates, EXT_ITEM_REGEXP_ERROR_MESSAGE, "Value must match /$regexpDef/")
                    }
                    description fromCandidates(rel, candidates, EXT_ITEM_DESCRIPTION, dataElement.description)
                    question fromCandidates(rel, candidates, EXT_ITEM_QUESTION, localName)
                    questionNumber fromCandidates(rel, candidates, EXT_ITEM_QUESTION_NUMBER)
                    instructions fromCandidates(rel, candidates, EXT_ITEM_INSTRUCTIONS)
                    phi(fromCandidates(rel, candidates, EXT_ITEM_PHI) == 'true')
                    required(fromCandidates(rel, candidates, EXT_ITEM_REQUIRED) == 'true' || rel.ext[EXT_MIN_OCCURS] == '1')
                    columnNumber = safeInteger(fromCandidates(rel, candidates, EXT_ITEM_COLUMN_NUMBER), EXT_ITEM_COLUMN_NUMBER, rel)
                    units fromCandidates(rel, candidates, EXT_ITEM_UNITS, candidates.find {
                        it.instanceOf(ValueDomain) && it.unitOfMeasure
                    }?.unitOfMeasure?.symbol)

                    if (first) {
                        header aHeader
                        subheader aSubheader
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
                }
            }

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

    private static String fromCandidates(Relationship rel, List<CatalogueElement> candidates, String extensionName, String defaultValue = null) {
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
