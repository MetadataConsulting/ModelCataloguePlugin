package org.modelcatalogue.core.a

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.CatalogueContent
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.integration.excel.HeadersMap
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Stepwise

import static org.modelcatalogue.core.geb.Common.*

@Stepwise
class AssetWizardSpec extends AbstractModelCatalogueGebSpec {

    public static final CatalogueAction validateXsd = CatalogueAction.runFirst('item', 'catalogue-element', 'validate-xsd-schema')
    public static final String successInFooter = '.modal-footer .alert-success'
    public static final String dangerInFooter = '.modal-footer .alert-danger'
    public static final CatalogueAction importMc = CatalogueAction.runFirst('navigation-right', 'curator-menu', 'import-mc')
    public static final CatalogueAction refreshAsset = CatalogueAction.runFirst('item-detail', 'refresh-asset')
    public static final CatalogueContent dataModelsProperty = CatalogueContent.create('td', 'data-value-for': 'Data Models')
    public static final CatalogueAction downloadButton = CatalogueAction.runFirst('item-detail', 'download-asset')
    public static final CatalogueAction importExcel = CatalogueAction.runFirst('navigation-right', 'curator-menu', 'import-excel')
    public static final CatalogueAction export = CatalogueAction.runFirst('item', 'export')
    public static final String asset = 'asset'
    public static final String infiniteTableRow = '.inf-table tbody .inf-table-item-row'

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "go to login"() {
        when:
        loginAdmin()
        select "Test 1"
        selectInTree "Test 1"
        selectInTree "Assets"

        then:
        check rightSideTitle is 'Active Assets'
    }

    def "upload new asset"() {
        when:
        click create

        then:
        check modalDialog displayed

        when:
        fill name with 'Sample XSD'
        fill asset with file('example.xsd')

        click save

        then:
        check infiniteTableRow present once

        and:
        check modalDialog gone
    }

    def "Check the asset shows up with own detail page"(){
        expect:
        check { infTableCell(1, 2, text: "Sample XSD") } displayed

        click { infTableCell(1, 2).find('a:not(.inf-cell-expand)') }

        check rightSideTitle contains 'Sample XSD Test 1'
    }

    def "validate xml schema"() {
        when:
        click validateXsd

        then:
        check modalDialog displayed

        when:
        fill 'xml' with file('example.xml')

        then:
        check successInFooter displayed

        when:
        fill 'xml' with file('example-invalid.xml')

        then:
        check dangerInFooter displayed

        when:
        click modalCloseButton

        then:
        check modalDialog gone
    }


    def "upload mc file"() {
        click importMc

        expect:
        check modalDialog displayed

        when:
        fill asset with file('MET-523.mc')

        click '.modal-footer .btn-success'

        then:
        check modalDialog gone
        check 'h3' contains 'Import for MET-523.mc'

        when:
        waitUntilFinalized()

        then:
        check 'h3' contains 'Import for MET-523.mc MET-523 0.0.1'
    }

    def "upload excel file"() {
        click importExcel

        expect:
        check modalDialog displayed

        when:
        fill asset with file('MET-522.xlsx')
        click '.modal-footer .btn-success'

        then:
        check 'h3' contains 'Import for MET-522.xlsx'


        when:
        waitUntilFinalized()

        select 'MET-522'

        then:
        check rightSideTitle is 'MET-522 0.0.1'

        when:
        select('MET-522') / 'MET-522' / 'Data Classes' / 'MET-522.M1'

        then:
        check rightSideTitle is 'MET-522.M1 MET-522 0.0.1'
    }

    /**
     * This test is currently ignored as it is very unreliable. It's better to have reliable tests than cover every
     * bit of the application.
     */
    @Issue('MET-1050')
    @Ignore def "export to excel"() {
        when:
        withNewWindow({
            click export
            click { $('span', text: 'Export All Elements of MET-522.M1 to Excel XSLX').parent('a') }
            click modalPrimaryButton
        }, {
            check rightSideTitle contains 'Data Elements to Excel.xlsx'
            waitUntilFinalized()

            StringWriter sw = new StringWriter()
            CatalogueBuilder builder = new XmlCatalogueBuilder(sw)
            ExcelLoader parser = new ExcelLoader(builder)
            parser.importData(HeadersMap.create(), new ByteArrayInputStream(downloadBytes("api/modelCatalogue/core/asset/${currentId}/download")))

            assert sw.toString().count('<dataElement') == 5
        })

        then:
        noExceptionThrown()
    }

    void waitUntilFinalized() {
        int counter = 0
        while (!$(downloadButton.toSelector()).displayed && counter++ < 100) {
            // ! + gone does not implicitly require the element
            if ($(refreshAsset.toSelector()).displayed) {
                try {
                    click refreshAsset
                } catch (ignored) {}
            }
            Thread.sleep(1000)
        }
    }


    String file(String name) {
        new File(AssetWizardSpec.getResource(name).toURI()).absolutePath
    }

}
