package org.modelcatalogue.core.importexport

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.CreateDataModelPage
import org.modelcatalogue.core.geb.CreateDataTypePage
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.DataTypesPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.ImportModelCatalogueXMLPage
import org.modelcatalogue.core.geb.AssetPage
import spock.lang.Issue
import spock.lang.Ignore
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Title
import groovy.util.*
import groovy.xml.XmlUtil

@Issue('https://metadata.atlassian.net/browse/MET-1461')
@Title('Import Xml and Excel data')
@Narrative('''
- Login to model catalogue as curator/curator
- Create a new Data Model by selecting the create new data model button (black plus sign) in the top left hand corner | Redirected to Create New Data Model page 
- Populate form with Data Model Name, Catalogue ID and description . Press save button to create data model. | Data Mode is created. User is directed to Data Model page 
- Select Data Types from the tree navigation on the left | Taken to data types page. Active Data Types is displayed as title. 
- Press the green plus button to create new data type | Data Type Wizard pop-up appears
- Populate name, catalogue ID and description. Then select the enumerated option out of the radio button options at the bottom of the form | the form extends to give Value and description options for creation of enumerations
- Populate Value and description for the enumerated data type. Press the plus sign to add more enumerations.  
- Click save | Data type is saved
- Go to main page of data model
- Navigate to the top menu bar, click on the' Export' menu button and select Export to catalogue XML  | Export of XML begins
- XML file is downloading | XML downloaded
- Open the XML file with a suitable text-editor. Make a few change to XML by adding a new enumerated option for the enumerated data type.
- Save the xml file and go back to the data model.
- navigate to Import button (top- left-  next to create button)
- Click on the Import button and Select to import catalogue XML 
- Upload the change file, type the name and save
- Once file has finished uploading  file name should appear in blue in the history tab below loading box.
- Select the white 'show more' button ( with plus sign on it) to the left of the file name 
- Select option to download asset, download and open in text editor 
- Verify that the edit you made to the enumerated type is present in this version of the catalogue xml
- Repeat steps3-14  more than once, each time verifying that edits are present. 
- Check that an error message is not displayed       
''')
@Stepwise
class ImportXmlAndExcelDataSpec extends GebSpec {

    @Shared
    String dataModelName = UUID.randomUUID().toString()
    @Shared
    String dataTypeName = "NEW_DATATYPE"
    @Shared
    String downloadLocation = System.getProperty('downloadFilepath')

    def "Login as curator"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then:
        at DashboardPage
    }

    def "create new data model"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        dashboardPage.nav.createDataModel()

        then:
        at CreateDataModelPage

        when:
        CreateDataModelPage createDataModelPage = to CreateDataModelPage
        createDataModelPage.name = dataModelName
        createDataModelPage.submit()

        then:
        at DataModelPage
    }

    def "create new data type"() {
        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Data Types')
        then:
        at DataTypesPage

        when:
        DataTypesPage dataTypesPage = browser.page DataTypesPage
        dataTypesPage.createDataTypeFromNavigation()
        then:
        at CreateDataTypePage

        when:
        CreateDataTypePage createDataTypePage = browser.page CreateDataTypePage
        createDataTypePage.name = dataTypeName
        createDataTypePage.enumerated()
        createDataTypePage.fillMetadata(one: 1, two: 2)
        createDataTypePage.buttons.save()

        then:
        at DataTypesPage
    }

    def "export"() {
        when:
        DashboardPage dashboardPage = to DashboardPage
        Thread.sleep(2_000)
        dashboardPage.search(dataModelName)
        dashboardPage.select(dataModelName)
        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = at DataModelPage
        dataModelPage.export()
        sleep(2_000)
        dataModelPage.exportCatalogXml()
        then:
        at DataModelPage
    }

    def "update the xml file and import"() {
        when:
        String filelocation = "file://${downloadLocation}/${dataModelName}.mc.xml"
        URL url = new URL(filelocation)
        then:
        File f = new File(url.toURI())
        then:
        waitFor(10) { f.exists() }

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.importClick()
        dataModelPage.importCatalogXml()
        // Switch to new window opened
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        then:
        at ImportModelCatalogueXMLPage

        when:
        updateFile()
        then:
        true

        when:
        ImportModelCatalogueXMLPage importModelCatalogueXMLPage = browser.page ImportModelCatalogueXMLPage
        importModelCatalogueXMLPage.upload(f.absolutePath)
        then:
        waitFor(10) { at AssetPage }

    }

    def "download uploaded xml file"() {
        when:
        AssetPage assetPage = browser.page AssetPage
        assetPage.showMore()
        assetPage.downloadAsset()
        then:
        at AssetPage
    }


    def updateFile() {
        def xmlFromFile = new File("${downloadLocation}/${dataModelName}.mc.xml")
        def xml = new XmlSlurper().parseText(xmlFromFile.getText())
        def toadd = "<tag0:enumeration id='3' value='three'>3</tag0:enumeration>"
        def fragmentToAdd = new XmlSlurper(false, false).parseText(toadd)
        xml.dataModel.dataType.enumerations.appendNode(fragmentToAdd)

        XmlUtil xmlUtil = new XmlUtil()
        xmlUtil.serialize(xml, new FileWriter(xmlFromFile))
    }
}
