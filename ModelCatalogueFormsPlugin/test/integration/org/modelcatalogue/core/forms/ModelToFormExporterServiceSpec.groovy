package org.modelcatalogue.core.forms

import org.modelcatalogue.crf.model.GridGroup
import org.modelcatalogue.crf.model.Group
import org.modelcatalogue.crf.model.Section

import static org.modelcatalogue.core.forms.ModelToFormExporterService.*

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.crf.model.CaseReportForm
import org.springframework.validation.Errors

class ModelToFormExporterServiceSpec extends IntegrationSpec {

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


    CatalogueBuilder catalogueBuilder
    ModelToFormExporterService modelToFormExporterService

    def "there must be a top level model representing a form and at least one model representing the section"(){
        given:
        Model formModel = build {
            model(name: TEST_FORM_NAME) {
                ext EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext EXT_FORM_VERSION, TEST_FORM_VERSION

                dataElement(name: 'Test Element')
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

    def "nested models represent sections"(){
        given:
        Model formModel = build {
            model(name: TEST_FORM_NAME) {
                ext EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext EXT_FORM_VERSION, TEST_FORM_VERSION

                model(name: TEST_SECTION_NAME_1) {
                    ext EXT_SECTION_SUBTITLE, TEST_SECTION_SUBTITLE_1
                    ext EXT_SECTION_INSTRUCTIONS, TEST_SECTION_INSTRUCTIONS_1
                    relationship {
                        ext EXT_SECTION_PAGE_NUMBER, TEST_SECTION_PAGE_NUMBER_1
                    }
                }
                model(name: TEST_SECTION_NAME_2)
                model(name: TEST_SECTION_NAME_3)
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

    def "grids are signaled with flag"(){
        given:
        Model formModel = build {
            model(name: TEST_FORM_NAME) {
                ext EXT_FORM_REVISION_NOTES, TEST_FORM_REVISION_NOTES
                ext EXT_FORM_VERSION_DESCRIPTION, TEST_FORM_VERSION_DESCRIPTION
                ext EXT_FORM_VERSION, TEST_FORM_VERSION

                model(name: TEST_SECTION_NAME_1) {
                    ext EXT_SECTION_SUBTITLE, TEST_SECTION_SUBTITLE_1
                    ext EXT_SECTION_INSTRUCTIONS, TEST_SECTION_INSTRUCTIONS_1
                    ext EXT_SECTION_PAGE_NUMBER, TEST_SECTION_PAGE_NUMBER_1

                    model(name: TEST_GRID_MODEL_NAME) {
                        ext EXT_GROUP_GRID, 'true'
                        ext EXT_GROUP_REPEAT_NUM, TEST_GRID_REPEAT_NUM
                        ext EXT_GROUP_REPEAT_MAX, TEST_GRID_REPEAT_MAX
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

    // TODO: more tests

    Model build(@DelegatesTo(CatalogueBuilder) Closure builder) {
        catalogueBuilder.build(builder).find{ it.instanceOf(Model) } as Model
    }

}
