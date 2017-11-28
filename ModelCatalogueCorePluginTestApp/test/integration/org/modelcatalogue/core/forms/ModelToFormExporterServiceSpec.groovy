package org.modelcatalogue.core.forms

import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.crf.model.DataType
import org.modelcatalogue.crf.model.GridGroup
import org.modelcatalogue.crf.model.Group
import org.modelcatalogue.crf.model.Item
import org.modelcatalogue.crf.model.ResponseLayout
import org.modelcatalogue.crf.model.ResponseType
import org.modelcatalogue.crf.model.Section

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.crf.model.CaseReportForm
import org.springframework.validation.Errors
import spock.lang.Ignore

class ModelToFormExporterServiceSpec extends AbstractIntegrationSpec {

    public static final String TEST_FORM_REVISION_NOTES = "This is a test revision"
    public static final String TEST_FORM_VERSION_DESCRIPTION = "This is a test version"
    public static final String TEST_FORM_NAME = 'Form 1'
    public static final String TEST_FORM_VERSION = "v1"
    public static final String TEST_SECTION_NAME_1 = "Section 1"
    public static final String TEST_SECTION_LABEL_1 = "Section_1"
    public static final String TEST_SECTION_NAME_2 = "Section 2"
    public static final String TEST_SECTION_NAME_3 = "Section 3"
    public static final String TEST_SECTION_SUBTITLE_1 = "This is section one subtitle"
    public static final String TEST_SECTION_INSTRUCTIONS_1 = "These ase section one instructions"
    public static final String TEST_SECTION_PAGE_NUMBER_1 = "13"
    public static final String TEST_GRID_MODEL_NAME = "Test Grid"
    public static final String TEST_GRID_MODEL_LABEL = "Test_Grid"
    public static final String TEST_GRID_REPEAT_NUM = '10'
    public static final String TEST_GRID_REPEAT_MAX = '200'
    public static final String ITEM_FILE_NAME = "File Item"
    public static final String ITEM_FILE_NAME_NORMALIZED = "Form_1_Section_1_File_Name_Overriden"
    public static final String ITEM_FILE_NAME_OVERRIDEN = "File Name Overriden"
    public static final String ITEM_FILE_QUESTION = "Attachment"
    public static final String ITEM_FILE_QUTESTION_NUMBER = "10"
    public static final String ITEM_FILE_RESPONSE_LABEL = "file"
    public static final String ITEM_RADIO_NAME = "Radio Item"
    public static final String ITEM_RADIO_NAME_NORMALIZED = "Form_1_Section_1_Radio_Item"
    public static final String ITEM_SINGLE_SELECT_NAME = "Single Select Name"
    public static final String ITEM_SINGLE_SELECT_DEFAULT_VALUE = 'Please, selectRelation one!'
    public static final String ITEM_TEXT_NAME = "Stuff with Units"
    public static final String ITEM_TEXT_NAME_NORMALIZED = "Form_1_Section_1_Stuff_with_Units"


    DefaultCatalogueBuilder catalogueBuilder
    ModelToFormExporterService modelToFormExporterService

    def "there must be a top level model representing a form and at least one model representing the section"() {
        given:
        DataClass formModel = build {
            dataClass(name: TEST_FORM_NAME) {
                ext ModelToFormExporterService.EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext ModelToFormExporterService.EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext ModelToFormExporterService.EXT_FORM_VERSION, TEST_FORM_VERSION

                dataElement(name: 'Test Element') {
                    description "Test Element Description"
                }
            }
        }

        when:
        CaseReportForm form = modelToFormExporterService.convert(formModel)
        then:
        form
        form.name == TEST_FORM_NAME
        form.version == TEST_FORM_VERSION
        form.revisionNotes == TEST_FORM_REVISION_NOTES
        form.versionDescription == TEST_FORM_VERSION_DESCRIPTION
        form.sections.size() == 1

        when:
        Errors errors = modelToFormExporterService.validate(form)

        println errors

        then:
        errors.errorCount == 0
    }

    def "nested models represent sections"() {
        given:
        DataClass formModel = build {
            dataClass(name: TEST_FORM_NAME) {
                ext ModelToFormExporterService.EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext ModelToFormExporterService.EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext ModelToFormExporterService.EXT_FORM_VERSION, TEST_FORM_VERSION

                dataClass(name: TEST_SECTION_NAME_1) {
                    ext ModelToFormExporterService.EXT_SECTION_SUBTITLE, TEST_SECTION_SUBTITLE_1
                    ext ModelToFormExporterService.EXT_SECTION_INSTRUCTIONS, TEST_SECTION_INSTRUCTIONS_1
                    relationship {
                        ext ModelToFormExporterService.EXT_SECTION_PAGE_NUMBER, TEST_SECTION_PAGE_NUMBER_1
                    }
                }
                dataClass(name: TEST_SECTION_NAME_2)
                dataClass(name: TEST_SECTION_NAME_3)
            }
        }

        when:
        CaseReportForm form = modelToFormExporterService.convert(formModel)
        then:
        form
        form.sections.size() == 3

        when:
        Section section1 = form.section(TEST_SECTION_LABEL_1)

        then:
        section1.label == TEST_SECTION_LABEL_1
        section1.title == TEST_SECTION_NAME_1
        section1.subtitle == TEST_SECTION_SUBTITLE_1
        section1.instructions == TEST_SECTION_INSTRUCTIONS_1
        section1.pageNumber == TEST_SECTION_PAGE_NUMBER_1
    }

    def "grids are signaled with flag"() {
        given:
        DataClass formModel = build {
            dataClass(name: TEST_FORM_NAME) {
                ext ModelToFormExporterService.EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext ModelToFormExporterService.EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext ModelToFormExporterService.EXT_FORM_VERSION, TEST_FORM_VERSION

                dataClass(name: TEST_SECTION_NAME_1) {
                    ext ModelToFormExporterService.EXT_SECTION_SUBTITLE, TEST_SECTION_SUBTITLE_1
                    ext ModelToFormExporterService.EXT_SECTION_INSTRUCTIONS, TEST_SECTION_INSTRUCTIONS_1
                    ext ModelToFormExporterService.EXT_SECTION_PAGE_NUMBER, TEST_SECTION_PAGE_NUMBER_1

                    dataClass(name: TEST_GRID_MODEL_NAME) {
                        ext ModelToFormExporterService.EXT_GROUP_GRID, 'true'
                        ext ModelToFormExporterService.EXT_GROUP_REPEAT_NUM, TEST_GRID_REPEAT_NUM
                        ext ModelToFormExporterService.EXT_GROUP_REPEAT_MAX, TEST_GRID_REPEAT_MAX
                    }
                }
            }
        }

        when:
        CaseReportForm form = modelToFormExporterService.convert(formModel)
        Section section1 = form.section(TEST_SECTION_LABEL_1)

        then:
        section1

        when:
        Group gridGroup = section1.groups[TEST_GRID_MODEL_LABEL]

        then:
        gridGroup instanceof GridGroup
        gridGroup.header == TEST_GRID_MODEL_NAME
        gridGroup.repeatNum == TEST_GRID_REPEAT_NUM as Integer
        gridGroup.repeatMax == TEST_GRID_REPEAT_MAX as Integer
    }


    //@Requires({ !System.getenv('TRAVIS') })
    @Ignore
    def "various item types"() {
        given:
        DataClass formModel = build {
            dataClass(name: TEST_FORM_NAME) {
                ext ModelToFormExporterService.EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext ModelToFormExporterService.EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext ModelToFormExporterService.EXT_FORM_VERSION, TEST_FORM_VERSION

                dataClass(name: TEST_SECTION_NAME_1) {
                    ext ModelToFormExporterService.EXT_SECTION_SUBTITLE, TEST_SECTION_SUBTITLE_1
                    ext ModelToFormExporterService.EXT_SECTION_INSTRUCTIONS, TEST_SECTION_INSTRUCTIONS_1
                    ext ModelToFormExporterService.EXT_SECTION_PAGE_NUMBER, TEST_SECTION_PAGE_NUMBER_1

                    dataElement(name: ITEM_FILE_NAME) {
                        dataType(name: 'File')

                        relationship {
                            ext ModelToFormExporterService.EXT_NAME_LC, ITEM_FILE_NAME_OVERRIDEN
                            ext ModelToFormExporterService.EXT_ITEM_QUESTION, ITEM_FILE_QUESTION
                            ext ModelToFormExporterService.EXT_ITEM_QUESTION_NUMBER, ITEM_FILE_QUTESTION_NUMBER
                        }
                    }

                    dataElement(name: ITEM_RADIO_NAME) {
                        dataType name: 'Gender', enumerations: [F: 'Female', M: 'Male']

                        relationship {
                            ext ModelToFormExporterService.EXT_ITEM_RESPONSE_TYPE, ModelToFormExporterService.RESPONSE_TYPE_RADIO
                            ext ModelToFormExporterService.EXT_ITEM_LAYOUT, 'horizontal'
                            ext Metadata.MIN_OCCURS, '1'
                        }
                    }

                    dataElement(name: ITEM_SINGLE_SELECT_NAME) {
                        dataType(name: 'Multi Type', enumerations: [A: 'Alpha', B: 'Beta', O: 'Omega']) {
                            ext ModelToFormExporterService.EXT_ITEM_INSTRUCTIONS, '''
                                <span data-id="Form 12" data-type="Multi Type"> </span> Multi Type
                            '''
                        }

                        relationship {
                            ext ModelToFormExporterService.EXT_ITEM_DEFAULT_VALUE, ITEM_SINGLE_SELECT_DEFAULT_VALUE
                        }
                    }

                    dataElement(name: ITEM_TEXT_NAME) {
                        ext ModelToFormExporterService.EXT_ITEM_PHI, 'true'

                        dataType(name: 'double') {
                            ext ModelToFormExporterService.EXT_ITEM_LENGTH, '10'
                            ext ModelToFormExporterService.EXT_ITEM_DIGITS, '2'
                            measurementUnit(name: 'Nano Coins', symbol: 'NC')
                        }
                    }

                }
            }
        }

        when:
        CaseReportForm form = modelToFormExporterService.convert(formModel)
        Section section1 = form.section(TEST_SECTION_LABEL_1)

        then:
        section1

        when:
        println section1.items.keySet()

        Item fileItem = section1.items[ITEM_FILE_NAME_NORMALIZED]

        then:
        fileItem
        fileItem.responseType == ResponseType.FILE
        fileItem.dataType == DataType.FILE
        fileItem.questionNumber == ITEM_FILE_QUTESTION_NUMBER
        fileItem.leftItemText == ITEM_FILE_QUESTION
        fileItem.responseLabel == ITEM_FILE_RESPONSE_LABEL

        when:
        Item radioItem = section1.items[ITEM_RADIO_NAME_NORMALIZED]

        then:
        radioItem
        radioItem.responseType == ResponseType.RADIO
        radioItem.required == 1
        radioItem.responseOptions.size() == 2
        radioItem.responseOptions[0].value == 'F'
        radioItem.responseOptions[0].text == 'Female'
        radioItem.responseOptions[1].value == 'M'
        radioItem.responseOptions[1].text == 'Male'
        radioItem.responseLayout == ResponseLayout.HORIZONTAL

        when:
        Item singleSelectItem = section1.items["Form_1_Section_1_Single_Select_Name"]

        then:
        singleSelectItem

        singleSelectItem.rightItemText == '<span data-id="Form 12" data-type="Multi Type"> </span> Multi Type'
        singleSelectItem.defaultValue == ITEM_SINGLE_SELECT_DEFAULT_VALUE

        when:
        Item textItem = section1.items[ITEM_TEXT_NAME_NORMALIZED]

        then:
        textItem
        textItem.widthDecimal == '10(2)'
        textItem.dataType == DataType.REAL
        textItem.units == 'NC'
        textItem.phi == 1
    }

    // TODO: more tests

    DataClass build(@DelegatesTo(CatalogueBuilder) Closure builder) {
        catalogueBuilder.build(builder)
        catalogueBuilder.created.find{ it.instanceOf(DataClass) } as DataClass

    }

}
