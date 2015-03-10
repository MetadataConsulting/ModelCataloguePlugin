package org.modelcatalogue.core

import geb.spock.GebReportingSpec
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.core.pages.AssetListPage
import spock.lang.Stepwise

@Stepwise
class AssetWizardSpec extends GebReportingSpec {

    private final String SAMPLE_XSD_URL = "https://gist.githubusercontent.com/musketyr/fdafc05a3383758b6475/raw/b4e21b12613f70fd7733428e7bbb8434faec4925/example.xsd"
    private final String SAMPLE_VALID_XML_URL = "https://gist.githubusercontent.com/musketyr/fdafc05a3383758b6475/raw/d8369d85bb6b09e06706a96973697acc1010c439/example.xml"
    private final String SAMPLE_INVALID_XML_URL = "https://gist.githubusercontent.com/musketyr/fdafc05a3383758b6475/raw/b8d405978d8c42ed6c2f80a080bbf56f03aaf7ae/example-invalid.xml"

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "go to login"() {
        when:
        go "#/catalogue/asset/all"

        then:
        at AssetListPage
        waitFor(120) {
            viewTitle.displayed
        }
        waitFor {
            viewTitle.text().trim() == 'Asset List'
        }

        when:
        loginAdmin()

        then:
        waitFor {
            actionButton('create-catalogue-element', 'list').displayed
        }

    }

    def "upload new asset"() {
        when:
        actionButton('create-catalogue-element', 'list').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#name').value('Sample XSD')
        $('#asset').value(download('sample.xsd', SAMPLE_XSD_URL).absolutePath)

        modalDialog.find("button.btn-success").click()

        then:
        waitFor {
            $('.inf-table tbody .inf-table-item-row').size() == 1
        }
    }

    def "Check the asset shows up with own detail page"(){
        when:
        waitFor {
            infTableCell(1, 2, text: "Sample XSD").displayed
        }

        then:

        infTableCell(1, 2).find('a:not(.inf-cell-expand)').click()

        waitFor(60) {
            subviewTitle.displayed
        }

        subviewTitle.text().trim() == 'Sample XSD DRAFT'

    }

    def "validate xml schema"() {
        when:
        actionButton('validate-xsd-schema').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#xml').value(download('valid.xml', SAMPLE_VALID_XML_URL).absolutePath)

        then:
        waitFor(60) {
            $('.modal-footer .alert-success').displayed
        }

        when:
        $('#xml').value(download('invalid.xml', SAMPLE_INVALID_XML_URL).absolutePath)

        then:
        waitFor(60) {
            $('.modal-footer .alert-danger').displayed
        }

        when:
        modalCloseButton.click()

        then:
        waitFor {
            !modalDialog
        }
    }


    private File download(String name, String url) {
        File sampleXsd = tmp.newFile(name)
        def out = new BufferedOutputStream(new FileOutputStream(sampleXsd))
        out << new URL(url).openStream()
        out.close()
        sampleXsd
    }

}