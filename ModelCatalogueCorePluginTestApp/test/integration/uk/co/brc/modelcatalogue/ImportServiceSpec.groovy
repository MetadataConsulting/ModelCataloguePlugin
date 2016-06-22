package uk.co.brc.modelcatalogue

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.util.test.TestDataHelper
import org.modelcatalogue.testapp.AbstractIntegrationSpec

class ImportServiceSpec extends AbstractIntegrationSpec {

    def importService

    def setup() {
        TestDataHelper.initFreshDb(sessionFactory, 'nhic.sql') {
            relationshipTypeService.clearCache()
            initCatalogue()
            importService.importData()
        }
    }


    def
    "import nhic spreadsheet"() {

        when:
        def models = DataClass.list()
        def dataTypes = DataType.list()
        def dataElements = DataElement.list()

        then:
        !models.isEmpty()
        !dataTypes.isEmpty()
        !dataElements.isEmpty()

        when:
        def core = models.find { it.name == "MAIN" }
        def patientIdentity = models.find { it.name == "PATIENT IDENTITY DETAILS" }
        def NHICConceptualDomain = DataModel.findByName("NHIC")
        def indicatorCode = dataTypes.find { it.name == "nhsNumberStatusIndicatorCode" }
        def dataElement = dataElements.find { it.name == "nhsNumberStatusIndicatorCode" }

        then:
        core.id
        patientIdentity.id
        indicatorCode.id
        NHICConceptualDomain.id
        dataElement.id
        patientIdentity.childOf.contains(core)
        core.parentOf.contains(patientIdentity)
        patientIdentity.dataModel == NHICConceptualDomain
        core.dataModel == NHICConceptualDomain

        when:
        HashMap<String, String> icodehash = new HashMap(
                '01': 'Number present and verified',
                '02': 'Number present but not traced',
                '03': 'Trace required',
                '04': 'Trace attempted - No match or multiple match found',
                '05': 'Trace needs to be resolved - (NHS Number or patient detail conflict)',
                '06': 'Trace in progress',
                '07': 'Number not present and trace not required',
                '08': 'Trace postponed (baby under six weeks old)'
        )
        def icodeEnumerations = new HashMap<String, String>(indicatorCode.enumerations)

        then:
        icodehash.entrySet().containsAll(icodeEnumerations.entrySet())


    }

}
