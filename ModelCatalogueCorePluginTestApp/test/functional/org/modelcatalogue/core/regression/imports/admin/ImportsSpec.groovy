package org.modelcatalogue.core.regression.imports.admin

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.ExcelLoader
import org.modelcatalogue.core.dataimport.excel.HeadersMap
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.CatalogueAction
import org.modelcatalogue.core.geb.Common
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') })
@Ignore
@Stepwise
class ImportsSpec extends AbstractModelCatalogueGebSpec {

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

    def "upload excel file"() {
        click importExcel

        expect:
        check Common.modalDialog displayed

        when:
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
            parser.buildXmlFromWorkbook(HeadersMap.createForStandardExcelLoader(), new ByteArrayInputStream(downloadBytes("api/modelCatalogue/core/asset/${currentId}/download")))

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
        new File(ImportsSpec.getResource(name).toURI()).absolutePath
    }

}
