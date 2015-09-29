package uk.co.brc.modelcatalogue

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.*

/**
 * Created by adammilward on 11/02/2014.
 */
class ImportServiceSpec extends IntegrationSpec {


    def
    "import nhic spreadsheet"() {

        when:
        def models = Model.list()
        def dataTypes = DataType.list()
        def dataElements = DataElement.list()
        def valueDomains = ValueDomain.list()

        then:
        !models.isEmpty()
        !dataTypes.isEmpty()
        !dataElements.isEmpty()
        !valueDomains.isEmpty()

        when:
        def core = models.find { it.name == "MAIN" }
        def patientIdentity = models.find { it.name == "PATIENT IDENTITY DETAILS" }
        def NHICConceptualDomain = Classification.findByName("NHIC")
        def indicatorCode = dataTypes.find { it.name == "NHS NUMBER STATUS INDICATOR CODE" }
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
        patientIdentity.classifications.contains(NHICConceptualDomain)
        core.classifications.contains(NHICConceptualDomain)
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

        dataElement.valueDomain
        valueDomain.dataElements as Set == [dataElement] as Set
        valueDomain.classifications as Set == [NHICConceptualDomain] as Set

    }

}
