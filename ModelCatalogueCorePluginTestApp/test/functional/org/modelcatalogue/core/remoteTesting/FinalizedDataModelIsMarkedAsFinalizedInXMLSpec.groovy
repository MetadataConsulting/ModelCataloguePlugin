package org.modelcatalogue.core.remoteTesting

import geb.spock.GebSpec
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.HomePage
import org.modelcatalogue.core.geb.LoginModalPage
import org.modelcatalogue.core.geb.LoginPage
import spock.lang.Issue
import org.modelcatalogue.core.geb.CatalogueAction
import spock.lang.Narrative
import spock.lang.Title
import groovy.io.FileType
import spock.lang.IgnoreIf

@Issue('https://metadata.atlassian.net/browse/MET-1561')
@Title('Examine that finalized data model is marked as finalized in the XML')
@Narrative('''
- Login to Model Catalogue
- Select a Finalized Model
- Navigate to the top menu and click on the Export button
- Scroll down and click on the Export to catalogue XML
- Open the downloaded file and verify that the status is marked as finalized
''')

@IgnoreIf({ !System.getProperty("downloadFilepath") })
class FinalizedDataModelIsMarkedAsFinalizedInXMLSpec extends GebSpec {

    def 'Examine that finalized data model is marked as finalized in the XML'() {

        given:
        String dataModelName = "Cancer Model"

        when: 'login as a curator'
        LoginPage loginPage = to LoginPage
        loginPage.login('curator', 'curator')

        then: 'you get redirected to Dashboard page'
        at DashboardPage

        when: 'Select a Finalized Model'
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select(dataModelName)

        then:
        at DataModelPage

        when: 'Examine that finalized data model is marked as finalized in the XML'
        DataModelPage dataModelPage = browser.page DataModelPage

        then:
        dataModelPage.isExportVisible()

        when:
        List<String> files = []
        String downloadPath = System.getProperty("downloadFilepath")
        File file = new File(downloadPath)

        then:
        file.exists()

        and:
        file.isDirectory()

        when:
        String path = downloadPath + File.separator + "Cancer_Model.mc.xml"
        File dataModelFile = new File(path)

        then:
        !dataModelFile.exists()

        when:
        dataModelPage.export()
        dataModelPage.exportXml()
        sleep(2_000)
        dataModelFile = new File(path)

        then:
        dataModelFile.exists()

        and:
        dataModelFile.text.contains(dataModelName)

        and:
        dataModelFile.text.toLowerCase().contains('status="FINALIZED"'.toLowerCase())

        cleanup:
        dataModelFile.delete()
    }
}