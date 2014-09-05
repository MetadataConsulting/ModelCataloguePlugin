package org.modelcatalogue.core

import grails.test.mixin.TestFor
import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.dataarchitect.XSDImportService
import org.modelcatalogue.core.dataarchitect.xsd.XsdLoader
import spock.lang.Shared

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(XSDImportService)
class XSDImportServiceSpec extends IntegrationSpec {


    def "ingest XML schema"(){

        setup:
        def filenameXsd = "test/unit/resources/SACT/XSD_Example.xsd"
        InputStream inputStream = new FileInputStream(filenameXsd)
        XsdLoader parserXSD = new XsdLoader(inputStream)
        def (elements, simpleDataTypes, complexDataTypes, groups, attributes, logErrorsSACT) = parserXSD.parse()

        when:
        service.createValueDomainsAndDataTypes(simpleDataTypes)
        service.createModels(complexDataTypes)

        def nhsDateModel = Model.findByName("TS.GB-en-NHS.Date")
        def tsModel = Model.findByName("TS")
        def any = Model.findByName("ANY")
        def qty = Model.findByName("QTY")
        def value1 = DataElement.findByNameAndDescription("value","TS.value")
        def value2 = DataElement.findByNameAndDescription("value","TS.GB-en-NHS.Date.value")
        def nullFlavor = DataElement.findByName("nullFlavor")
        def updateMode = DataElement.findByName("updateMode")
        def cs_NullFlavor = ValueDomain.findByName("cs_NullFlavor")
        def cs_UpdateMode = ValueDomain.findByName("cs_UpdateMode")
        def cs = ValueDomain.findByName("cs")
        def ts = ValueDomain.findByName("ts")
        def valueDomain = ValueDomain.findByNameIlike("value")
        def cs_UpdateModeType = EnumeratedType.findByName("cs_UpdateMode")
        def cs_NullFlavorType = EnumeratedType.findByName("cs_NullFlavor")

        then:

        nhsDateModel
        tsModel
        any
        qty
        value1
        value2
        nullFlavor
        updateMode
        cs_NullFlavor
        cs_UpdateMode
        cs
        ts
        valueDomain
        cs_UpdateModeType
        cs_NullFlavorType

        nhsDateModel.countRelations() == 4
        tsModel.countRelations() == 3
        any.countRelations() ==1

        nhsDateModel.contains.contains(value2)
        nhsDateModel.contains.contains(nullFlavor)
        nhsDateModel.contains.contains(updateMode)

        tsModel.contains.contains(value1)
        tsModel.contains.contains(nullFlavor)
        tsModel.contains.contains(updateMode)

        qty.contains.contains(nullFlavor)
        qty.contains.contains(updateMode)

        any.contains.contains(nullFlavor)
        any.contains.contains(updateMode)

        value1.valueDomain == ts
        value2.valueDomain == valueDomain
        nullFlavor.valueDomain == cs_NullFlavor
        updateMode.valueDomain == cs_UpdateMode

        cs.dataType.name == "xs:token"
        ts.dataType.name == "xs:string"
        cs_UpdateMode.dataType == cs_UpdateModeType
        cs_NullFlavor.dataType == cs_NullFlavorType

        nhsDateModel.isBasedOn.contains(tsModel)
        tsModel.isBasedOn.contains(qty)
        qty.isBasedOn.contains(any)

        cs_NullFlavor.isBasedOn.contains(cs)
        cs_UpdateMode.isBasedOn.contains(cs)
        valueDomain.isBasedOn.contains(ts)


    }


}
