package org.modelcatalogue.core.a

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.AbstractModelCatalogueGebSpec
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.core.pages.AssetListPage
import org.modelcatalogue.integration.excel.HeadersMap
import spock.lang.Stepwise

@Stepwise
class AssetWizardSpec extends AbstractModelCatalogueGebSpec {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "go to login"() {
        go "#/"
        loginAdmin()

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
        $('#asset').value(file('example.xsd'))

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
        actionButton('catalogue-element').click()

        then:
        waitFor {
            actionButton('validate-xsd-schema')
        }

        when:
        actionButton('validate-xsd-schema').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#xml').value(file('example.xml'))

        then:
        waitFor(60) {
            $('.modal-footer .alert-success').displayed
        }

        when:
        $('#xml').value(file('example-invalid.xml'))

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


    def "upload mc file"() {
        when:
        go "#/catalogue/asset/all"

        then:
        at AssetListPage

        waitFor {
            actionButton('new-import', 'list').displayed
        }

        when:
        actionButton('new-import', 'list').click()

        then:
        waitFor {
            actionButton('import-mc').displayed
        }

        when:
        actionButton('import-mc').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#asset').value(file('MET-523.mc'))

        then:
        waitFor {
            !modalSuccessButton.disabled
        }

        when:
        modalSuccessButton.click()

        then:
        waitFor(60) {
            subviewTitle.displayed
        }
        waitFor(60) {
            subviewTitle.text().startsWith('Import for MET-523.mc')
        }

        when:
        10.times {
            actionButton('refresh-asset').click()
            try {
                waitFor {
                    subviewTitle.text() == 'Import for MET-523.mc FINALIZED'
                }
            } catch (ignored) {}
        }

        then:
        waitFor {
            $('td', 'data-value-for': 'Data Models').text() == 'MET-523'
        }

        when:
        noStale({$('td', 'data-value-for': 'Data Models').find('a')}) {
            it.click()
        }

        then:
        waitFor {
            subviewTitle.text() == 'MET-523 DRAFT'
        }
        totalOf('declares') == 28
    }

    def "upload excel file"() {
        when:
        go "#/catalogue/asset/all"

        then:
        at AssetListPage

        waitFor {
            actionButton('new-import', 'list').displayed
        }

        when:
        actionButton('new-import', 'list').click()

        then:
        waitFor {
            actionButton('import-excel').displayed
        }

        when:
        actionButton('import-excel').click()

        then:
        waitFor {
            modalDialog.displayed
        }

        when:
        $('#asset').value(file('MET-522.xlsx'))

        then:
        waitFor {
            !modalSuccessButton.disabled
        }

        when:
        modalSuccessButton.click()

        then:
        waitFor(60) {
            subviewTitle.displayed
        }
        waitFor(60) {
            subviewTitle.text().startsWith('Import for MET-522.xlsx')
        }

        when:
        waitUntilFinalized('Import for MET-522.xlsx')

        goToDetailUsingSearch('MET-522')

        then:
        waitFor(60) {
            subviewTitle.text() == 'MET-522 DRAFT'
        }
        totalOf('declares') == 27

        when:
        goToDetailUsingSearch('MET-522.M1', 'MET-522')

        then:
        waitFor(60) {
            subviewTitle.text() == 'MET-522.M1 DRAFT'
        }

        when:
        actionButton('export').click()

        noStale({ $('span', text: 'Export All Elements of MET-522.M1 to Excel XSLX').parent('a') }) { exportAll ->
            if (exportAll.displayed) {
                exportAll.click()
            }
        }

        withWindow({
            title != 'MET-522.M1 (MET-522)'
        }) {
            waitFor {
                subviewTitle.text().startsWith 'Data Elements to Excel.xlsx'
            }
            waitUntilFinalized('Data Elements to Excel.xlsx')

            StringWriter sw = new StringWriter()
            CatalogueBuilder builder = new XmlCatalogueBuilder(sw)
            ExcelLoader parser = new ExcelLoader(builder)
            parser.importData(HeadersMap.create(), new ByteArrayInputStream(downloadBytes("api/modelCatalogue/core/asset/${currentId}/download")))

            sw.toString().count('<dataElement') == 15
        }

        then:

        true
    }

    void waitUntilFinalized(String expectedName) {
        10.times {
            if (subviewTitle.text() == "${expectedName} PENDING") {
                actionButton('refresh-asset').click()
                try {
                    waitFor(10) {
                        subviewTitle.text() == "${expectedName} FINALIZED"
                    }
                } catch (e) {
                    if (it == 9) throw new RuntimeException("Waiting for element finalization. Expected '${expectedName} FINALIZED' got '${subviewTitle.text()}", e)
                }
            }
        }
    }


    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }

}