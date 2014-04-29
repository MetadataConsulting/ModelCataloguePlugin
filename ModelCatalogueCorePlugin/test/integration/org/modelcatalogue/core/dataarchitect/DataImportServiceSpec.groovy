package org.modelcatalogue.core.dataarchitect

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model

class DataImportServiceSpec extends IntegrationSpec {

    def fileName= "test/integration/resources/DataTemplate.xls"
    def fileName2= "test/integration/resources/DataTemplateChangeDataItemName.xls"
    def fileName3= "test/integration/resources/DataTemplateChangeDataItems.xls"
    def dataImportService, initCatalogueService

    def setup(){
        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultMeasurementUnits()
        initCatalogueService.initDefaultDataTypes()
    }

    void "Test load spreadsheet twice"()
    {
        when:"loading the dataElements"
        def inputStream = new FileInputStream(fileName)
        ExcelLoader parser = new ExcelLoader(inputStream)
        def (headers, rows) = parser.parse()

        HeadersMap headersMap = new HeadersMap()
        headersMap.dataElementCodeRow = "Data Item Unique Code"
        headersMap.dataElementNameRow = "Data Item Name"
        headersMap.dataElementDescriptionRow = "Data Item Description"
        headersMap.dataTypeRow = "Data type"
        headersMap.parentModelNameRow = "Parent Model"
        headersMap.parentModelCodeRow = "Parent Model Unique Code"
        headersMap.containingModelNameRow = "Model"
        headersMap.containingModelCodeRow = "Model Unique Code"
        headersMap.measurementUnitNameRow = "Measurement Unit"
        headersMap.metadataRow = "Metadata"

        dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1"], headersMap)
        DataElement de1 = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de2 = DataElement.findByModelCatalogueId("MC_065e6162-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de3 = DataElement.findByModelCatalogueId("MC_067e6162-3b4f-4ae2-a171-2470b64dff10_1")
        DataElement de4 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de5 = DataElement.findByModelCatalogueId("MC_067e6162-1b6f-4ae2-a171-2470b64dff10_1")
        DataElement de6 = DataElement.findByModelCatalogueId("MC_067e6189-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de7 = DataElement.findByModelCatalogueId("MC_067e6232-3b6f-4ae2-a171-2470b64dff10_1")
        DataElement de8 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_1")
        DataElement de9 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae9-a171-2470b64dff10_1")
        DataElement de10 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-2ae2-a171-2470b64dff10_1")
        DataElement de11 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-9ae2-a181-2470b64dff10_1")
        Model admissions = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff00_1")
        Model unit = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff19_1")
        Model demographics = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_1")


        then:"the dataElement should have name"
        de1
        de2
        de3
        de4
        de5
        de6
        de7
        de8
        de9
        de10
        de11
        admissions
        demographics
        unit
        admissions.parentOf.contains(demographics)
        admissions.parentOf.contains(unit)
        demographics.contains.contains(de1)
        demographics.contains.contains(de1)
        demographics.contains.contains(de2)
        demographics.contains.contains(de3)
        demographics.contains.contains(de4)
        demographics.contains.contains(de5)
        unit.contains.contains(de6)
        unit.contains.contains(de7)
        unit.contains.contains(de8)
        unit.contains.contains(de9)
        unit.contains.contains(de10)
        unit.contains.contains(de11)

        when: "I load the spreadsheet in again with changes to the first data element name"

        def inputStream2 = new FileInputStream(fileName2)
        ExcelLoader parser2 = new ExcelLoader(inputStream2)
        (headers, rows) = parser2.parse()
        dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1"], headersMap)
        def de1Old = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_1")
        def demographicsOld = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_1")

        then: "the data element and corresponding model have been updated"

        de1.refresh()
        demographics.refresh()
        de1.modelCatalogueId=="MC_037e6162-3b6f-4ae2-a171-2470b64dff10_2"
        de1.supersedes.contains(de1Old)
        de2
        de3
        de4
        de5
        de6
        de7
        de8
        de9
        de10
        de11
        admissions
        demographics
        unit
        admissions.parentOf.contains(demographics)
        admissions.parentOf.contains(unit)
        demographics.contains.contains(de1)
        demographics.contains.contains(de1)
        demographics.contains.contains(de2)
        demographics.contains.contains(de3)
        demographics.contains.contains(de4)
        demographics.contains.contains(de5)
        demographics.supersedes.contains(demographicsOld)
        !demographicsOld.contains.contains(de1)
        demographicsOld.contains.contains(de1Old)
        unit.contains.contains(de6)
        unit.contains.contains(de7)
        unit.contains.contains(de8)
        unit.contains.contains(de9)
        unit.contains.contains(de10)
        unit.contains.contains(de11)


        when: "I load the spreadsheet in again with changes to the first data element name"

        def inputStream3 = new FileInputStream(fileName3)
        ExcelLoader parser3 = new ExcelLoader(inputStream3)
        (headers, rows) = parser3.parse()
        dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", ["NHIC Datasets", "TRA", "TRA_OUH", "Round 1"], headersMap)
        de1Old = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_2")
        demographicsOld = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_2")
        def deTimeOld = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_1")
        def deTime = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_2")

        then: "the data element and corresponding model have been updated"

        de1.refresh()
        demographics.refresh()
        de1.modelCatalogueId=="MC_037e6162-3b6f-4ae2-a171-2470b64dff10_3"
        de1.supersedes.contains(de1Old)
        de2
        de3
        de4
        de5
        de6
        de7
        de8
        de9
        de10
        de11
        deTime
        deTimeOld
        admissions
        demographics
        unit
        admissions.parentOf.contains(demographics)
        admissions.parentOf.contains(unit)
        demographics.contains.contains(de1)
        demographics.contains.contains(de1)
        demographics.contains.contains(de2)
        demographics.contains.contains(de3)
        demographics.contains.contains(de4)
        demographics.contains.contains(de5)
        demographics.supersedes.contains(demographicsOld)
        unit.contains.contains(de6)
        unit.contains.contains(de7)
        unit.contains.contains(de8)
        unit.contains.contains(de9)
        unit.contains.contains(de10)
        unit.contains.contains(de11)

    }


//    void "Test loading NHIC spreadsheet"(){
//        when:"loading the dataElements"
//        def inputStream = new FileInputStream("test/integration/resources/DataElementTest.xls")
//        ExcelLoader parser = new ExcelLoader(inputStream)
//        def (headers, rows) = parser.parse()
//
//        HeadersMap headersMap = new HeadersMap()
//        headersMap.dataElementCodeRow = "Data Item Unique Code"
//        headersMap.dataElementNameRow = "Data Item Name"
//        headersMap.dataElementDescriptionRow = "Data Item Description"
//        headersMap.dataTypeRow = "Data type"
//        headersMap.parentModelNameRow = "Parent Model"
//        headersMap.parentModelCodeRow = "Parent Model Unique Code"
//        headersMap.containingModelNameRow = "Model"
//        headersMap.containingModelCodeRow = "Model Unique Code"
//        headersMap.measurementUnitNameRow = "Measurement Unit"
//        headersMap.metadataRow = "Metadata"
//
//        dataImportService.importData(headers, rows, "NHIC : CAN", "NHIC CAN conceptual domain for cancer", ["NHIC Datasets", "CAN", "CAN_CUH", "Round 1"], headersMap)
//        DataElement nhsNumberIndicator = DataElement.findByModelCatalogueId("MC_40_1")
//        DataElement organisationCode = DataElement.findByModelCatalogueId("MC_43_1")
//        DataElement siteCode = DataElement.findByModelCatalogueId("MC_78_1")
//        DataElement procedureDAte = DataElement.findByModelCatalogueId("MC_79_1")
//        Model core = Model.findByModelCatalogueId("MC_35_1")
//        Model patientIdentity = Model.findByModelCatalogueId("MC_36_1")
//        Model imaging = Model.findByModelCatalogueId("MC_77_1")
//
//
//        then:"the dataElement should have name"
//        nhsNumberIndicator
//        organisationCode
//        siteCode
//        procedureDAte
//        core
//        imaging
//        patientIdentity
//        core.parentOf.contains(imaging)
//        core.parentOf.contains(patientIdentity)
//        imaging.contains.contains(procedureDAte)
//        imaging.contains.contains(siteCode)
//        patientIdentity.contains.contains(organisationCode)
//        patientIdentity.contains.contains(nhsNumberIndicator)
//
//
//
//    }

}