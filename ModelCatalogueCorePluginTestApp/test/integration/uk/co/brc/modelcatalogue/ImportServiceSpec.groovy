package uk.co.brc.modelcatalogue

import org.modelcatalogue.core.*
import spock.lang.Specification

/**
 * Created by adammilward on 11/02/2014.
 */
class ImportServiceSpec extends Specification {

    def importService
    def initCatalogueService


    def
    "import nhic spreadsheet"() {

        when:
        initCatalogueService.initDefaultRelationshipTypes()
        importService.importData()

        then:
        def models = Model.list()
        !models.isEmpty()
        def dataTypes = DataType.list()
        !dataTypes.isEmpty()
        def dataElements = DataElement.list()
        !dataElements.isEmpty()
        def valueDomains = ValueDomain.list()
        !valueDomains.isEmpty()

        when:
        def core = models.find { it.name == "MAIN" }
        def patientIdentity = models.find { it.name == "PATIENT IDENTITY DETAILS" }
        def NHICConceptualDomain = ConceptualDomain.findByName("NHIC")
        def indicatorCode = dataTypes.find { it.name == "NHS_NUMBER_STATUS_INDICATOR_CODE" }
        def valueDomain = valueDomains.find { it.name == "NHS_NUMBER_STATUS_INDICATOR_CODE" }
        def dataElement = dataElements.find { it.name == "NHS NUMBER STATUS INDICATOR CODE" }

        then:
        core.id
        patientIdentity.id
        indicatorCode.id
        NHICConceptualDomain.id
        valueDomain.id
        dataElement.id
        patientIdentity.childOf.contains(core)
        core.parentOf.contains(patientIdentity)
        patientIdentity.hasContextOf.contains(NHICConceptualDomain)
        core.hasContextOf.contains(NHICConceptualDomain)
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
        assert icodehash.entrySet().containsAll(icodeEnumerations.entrySet())

        valueDomain.conceptualDomains as Set == [NHICConceptualDomain] as Set
        valueDomain.dataElements as Set == [dataElement] as Set

    }

}
