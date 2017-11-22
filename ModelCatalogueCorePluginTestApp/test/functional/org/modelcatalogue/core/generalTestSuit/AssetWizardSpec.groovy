package org.modelcatalogue.core.generalTestSuit

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.gebUtils.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.gebUtils.CatalogueAction
import org.modelcatalogue.core.gebUtils.Common
import org.modelcatalogue.integration.excel.ExcelLoader
import org.modelcatalogue.integration.excel.HeadersMap
import spock.lang.Ignore
import spock.lang.IgnoreIf
import spock.lang.Issue
import spock.lang.Stepwise



@IgnoreIf({ !System.getProperty('geb.env') })

@Stepwise
class AssetWizardSpec extends AbstractModelCatalogueGebSpec {

    public static final CatalogueAction validateXsd = CatalogueAction.runFirst('item', 'catalogue-element', 'validate-xsd-schema')
    public static final String successInFooter = '.modal-footer .alert-success'
    public static final String dangerInFooter = '.modal-footer .alert-danger'
    public static final CatalogueAction importMc = CatalogueAction.runFirst('navigation-right', 'curator-menu', 'import-mc')
    public static final CatalogueAction refreshAsset = CatalogueAction.runFirst('item-detail', 'refresh-asset')
    public static final CatalogueAction downloadButton = CatalogueAction.runFirst('item-detail', 'download-asset')
    public static final CatalogueAction importExcel = CatalogueAction.runFirst('navigation-right', 'curator-menu', 'import-excel')
    public static final CatalogueAction export = CatalogueAction.runFirst('item', 'export')
    public static final String asset = 'asset'
    public static final String infiniteTableRow = '.inf-table tbody .inf-table-item-row'

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "go to login"() {
        when:
        loginAdmin()
        select "Test 1" select "Assets"

        then:
        check Common.rightSideTitle is 'Active Assets'
    }

    def "upload new asset"() {
        when:
        click Common.create

        then:
        check Common.modalDialog displayed

        when:
        fill Common.nameLabel with 'Sample XSD'
        fill asset with file('example.xsd')

        click Common.save

        then:
        check infiniteTableRow displayed

        and:
        check Common.modalDialog gone
    }

    def "Check the asset shows up with own detail page"(){
        expect:
        check { infTableCell(1, 2, text: "Sample XSD") } displayed

        click { infTableCell(1, 2).find('a:not(.inf-cell-expand)') }

        check Common.rightSideTitle contains 'Sample XSD'
    }

    def "validate xml schema"() {
        when:
        click validateXsd

        then:
        check Common.modalDialog displayed

        when:
        fill 'xml' with file('example.xml')

        then:
        check successInFooter displayed

        when:
        fill 'xml' with file('example-invalid.xml')

        then:
        check dangerInFooter displayed

        when:
        click Common.modalCloseButton

        then:
        check Common.modalDialog gone
    }


    def "upload mc file"() {
        click importMc

        expect:
        check Common.modalDialog displayed

        when:
        fill asset with file('MET-523.mc')

        click '.modal-footer .btn-success'

        then:
        check Common.modalDialog gone
        check 'h3' contains 'Import for MET-523.mc'

        when:
        waitUntilFinalized()

        then:
        check 'h3' contains 'Import for MET-523.mc'
    }
    @Ignore
    def "upload excel file"() {
        click importExcel

        expect:
        check Common.modalDialog displayed

        when:
        Thread.sleep(2000l)
        fill asset with file('MET-522.xlsx')
        click '.modal-footer .btn-success'

        then:
        check 'h3' contains 'Import for MET-522.xlsx'


        when:
        waitUntilFinalized()

        click 'h3 a.label.label-warning'

        then:
        check Common.rightSideTitle contains 'MET-522'

        when:
        Thread.sleep(2000)
        select 'MET-522' open 'Data Classes' select 'MET-522.M1'

        then:
        check Common.rightSideTitle contains 'MET-522.M1'
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
            click Common.modalPrimaryButton
        }, {
            check Common.rightSideTitle contains 'Data Elements to Excel.xlsx'
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
        while (!$(downloadButton.toSelector()).first().displayed && counter++ < 100) {
            // ! + gone does not implicitly require the element
            if ($(refreshAsset.toSelector()).first().displayed) {
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
