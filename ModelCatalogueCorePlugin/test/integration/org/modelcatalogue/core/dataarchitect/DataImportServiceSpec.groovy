package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model
import spock.lang.Shared

class DataImportServiceSpec extends AbstractIntegrationSpec {

    @Shared
    def dataImportService
    @Shared
    ImportRow validImportRow, validImportRow2, modelOnlyImportRow,modelOnlyImportRow2, invalidImportRow
    @Shared
    def fileName, fileName2, fileName3


    def "placeholder"(){}

    //TODO: refactor into unit test

//    def setupSpec() {
//        fileName= "test/integration/resources/DataTemplate.xls"
//        fileName2= "test/integration/resources/DataTemplateChangeDataItemName.xls"
//        fileName3= "test/integration/resources/DataTemplateChangeDataItems.xls"
//        dataImportService = new DataImport()
//        loadFixtures()
//        validImportRow = new ImportRow()
//        validImportRow2 = new ImportRow()
//        modelOnlyImportRow = new ImportRow()
//        modelOnlyImportRow2 = new ImportRow()
//        invalidImportRow = new ImportRow()
//
//        //model only import row
//        modelOnlyImportRow.dataElementName = ""
//        modelOnlyImportRow.dataElementCode = ""
//        modelOnlyImportRow.parentModelName = "testParentModelCode"
//        modelOnlyImportRow.parentModelCode = "MC_037e6962-3b6f-4ae4-a171-2570b64dfq10_1"
//        modelOnlyImportRow.containingModelName = "testJustModel"
//        modelOnlyImportRow.containingModelCode = "MC_037e6162-2b6f-4ae4-a171-2570b64daf10_1"
//        modelOnlyImportRow.dataType = ""
//        modelOnlyImportRow.dataElementDescription = ""
//        modelOnlyImportRow.measurementUnitName = ""
//        modelOnlyImportRow.conceptualDomainName = "formula one"
//        modelOnlyImportRow.conceptualDomainDescription = " the domain of formula one"
//
//        //model only import row
//        modelOnlyImportRow2.dataElementName = ""
//        modelOnlyImportRow2.dataElementCode = ""
//        modelOnlyImportRow2.parentModelName = ""
//        modelOnlyImportRow2.parentModelCode = ""
//        modelOnlyImportRow2.containingModelName = "testParentModelCode"
//        modelOnlyImportRow2.containingModelCode = "MC_037e6962-3b6f-4ae4-a171-2570b64dfq10_1"
//        modelOnlyImportRow2.dataType = ""
//        modelOnlyImportRow2.dataElementDescription = ""
//        modelOnlyImportRow2.measurementUnitName = ""
//        modelOnlyImportRow2.conceptualDomainName = "formula one"
//        modelOnlyImportRow2.conceptualDomainDescription = " the domain of formula one"
//
//        //row 1
//        validImportRow.dataElementName = "testDataItem"
//        validImportRow.dataElementCode = "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1"
//        validImportRow.parentModelName = "testParentModelCode"
//        validImportRow.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1"
//        validImportRow.containingModelName = "testModel"
//        validImportRow.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1"
//        validImportRow.dataType = "String"
//        validImportRow.dataElementDescription = "test description"
//        validImportRow.measurementUnitName = "Degrees of Fahrenheit"
//        validImportRow.conceptualDomainName = "formula one"
//        validImportRow.conceptualDomainDescription = " the domain of formula one"
//
//        //row 2 -same model as row 1 but different data element
//        validImportRow2.dataElementName = "testDataItem2"
//        validImportRow2.dataElementCode = "MC_037e6162-3b6f-4ae3-a171-2570b64dff10_1"
//        validImportRow2.parentModelName = "testParentModelCode"
//        validImportRow2.parentModelCode = "MC_037e6162-3b6f-4ae4-a171-2570b64dff10_1"
//        validImportRow2.containingModelName = "testModel"
//        validImportRow2.containingModelCode = "MC_037e6162-5b6f-4ae4-a171-2570b64dff10_1"
//        validImportRow2.dataType = "String"
//        validImportRow2.dataElementDescription = "test description 2"
//        validImportRow2.measurementUnitName = "Degrees Celsius"
//        validImportRow2.conceptualDomainName = "formula one"
//        validImportRow2.conceptualDomainDescription = " the domain of formula one"
//    }
//
//
//
//
//
//    void "Test load spreadsheet twice"()
//    {
//        when:"loading the dataElements"
//        def inputStream = new FileInputStream(fileName)
//        ExcelLoader parser = new ExcelLoader(inputStream)
//        def (headers, rows) = parser.parse()
//
//        HeadersMap headersMap = new HeadersMap()
//        headersMap.dataElementCode = "Data Item Unique Code"
//        headersMap.dataElementName = "Data Item Name"
//        headersMap.dataElementDescription = "Data Item Description"
//        headersMap.dataType = "Data type"
//        headersMap.parentModelName = "Parent Model"
//        headersMap.parentModelCode = "Parent Model Unique Code"
//        headersMap.containingModelName = "Model"
//        headersMap.containingModelCode = "Model Unique Code"
//        headersMap.measurementUnitName = "Measurement Unit"
//        headersMap.metadata = "Metadata"
//        def importer = dataImportService.importData(headers, rows, "NHIC : TRA",  "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", headersMap)
//        dataImportService.resolveAll(importer)
//        DataElement de1 = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_1")
//        DataElement de2 = DataElement.findByModelCatalogueId("MC_065e6162-3b6f-4ae2-a171-2470b64dff10_1")
//        DataElement de3 = DataElement.findByModelCatalogueId("MC_067e6162-3b4f-4ae2-a171-2470b64dff10_1")
//        DataElement de4 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff10_1")
//        DataElement de5 = DataElement.findByModelCatalogueId("MC_067e6162-1b6f-4ae2-a171-2470b64dff10_1")
//        DataElement de6 = DataElement.findByModelCatalogueId("MC_067e6189-3b6f-4ae2-a171-2470b64dff10_1")
//        DataElement de7 = DataElement.findByModelCatalogueId("MC_067e6232-3b6f-4ae2-a171-2470b64dff10_1")
//        DataElement de8 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_1")
//        DataElement de9 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae9-a171-2470b64dff10_1")
//        DataElement de10 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-2ae2-a171-2470b64dff10_1")
//        DataElement de11 = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-9ae2-a181-2470b64dff10_1")
//        Model admissions = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff00_1")
//        Model unit = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff19_1")
//        Model demographics = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_1")
//
//
//        then:"the dataElement should have name"
//        de1
//        de2
//        de3
//        de4
//        de5
//        de6
//        de7
//        de8
//        de9
//        de10
//        de11
//        admissions
//        demographics
//        unit
//        admissions.parentOf.contains(demographics)
//        admissions.parentOf.contains(unit)
//        demographics.contains.contains(de1)
//        demographics.contains.contains(de1)
//        demographics.contains.contains(de2)
//        demographics.contains.contains(de3)
//        demographics.contains.contains(de4)
//        demographics.contains.contains(de5)
//        unit.contains.contains(de6)
//        unit.contains.contains(de7)
//        unit.contains.contains(de8)
//        unit.contains.contains(de9)
//        unit.contains.contains(de10)
//        unit.contains.contains(de11)
//
//        when: "I load the spreadsheet in again with changes to the first data element name"
//
//        def inputStream2 = new FileInputStream(fileName2)
//        ExcelLoader parser2 = new ExcelLoader(inputStream2)
//        (headers, rows) = parser2.parse()
//        def importer2 = dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", headersMap)
//        dataImportService.resolveAll(importer2)
////        dataImportService.ingestImportQueue(importer2)
//        def de1Old = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_1")
//        def demographicsOld = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_1")
//
//        then: "the data element and corresponding model have been updated"
//
//        de1.refresh()
//        demographics.refresh()
//        demographicsOld.refresh()
//        de1.modelCatalogueId=="MC_037e6162-3b6f-4ae2-a171-2470b64dff10_2"
//        de1.supersedes.contains(de1Old)
//        de2
//        de3
//        de4
//        de5
//        de6
//        de7
//        de8
//        de9
//        de10
//        de11
//        admissions
//        demographics
//        unit
//        admissions.parentOf.contains(demographicsOld)
//        admissions.parentOf.contains(unit)
//        demographics.contains.contains(de1)
//        demographics.contains.contains(de1)
//        demographics.contains.contains(de2)
//        demographics.contains.contains(de3)
//        demographics.contains.contains(de4)
//        demographics.contains.contains(de5)
//        demographics.supersedes.contains(demographicsOld)
//        !demographicsOld.contains.contains(de1)
//        demographicsOld.contains.contains(de1Old)
//        !unit.supersedes
//        unit.contains.contains(de6)
//        unit.contains.contains(de7)
//        unit.contains.contains(de8)
//        unit.contains.contains(de9)
//        unit.contains.contains(de10)
//        unit.contains.contains(de11)
//
//
//        when: "I load the spreadsheet in again with changes to the first data element name"
//
//        def inputStream3 = new FileInputStream(fileName3)
//        ExcelLoader parser3 = new ExcelLoader(inputStream3)
//        (headers, rows) = parser3.parse()
//        def importer3 = dataImportService.importData(headers, rows, "NHIC : TRA", "NHIC : TRA", "NHIC TRA conceptual domain for renal transplantation", headersMap)
//        dataImportService.resolveAll(importer3)
////        dataImportService.ingestImportQueue(importer3)
//        de1Old = DataElement.findByModelCatalogueId("MC_037e6162-3b6f-4ae2-a171-2470b64dff10_2")
//        demographicsOld = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b63dff01_2")
//        def deTimeOld = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_1")
//        def deTime = DataElement.findByModelCatalogueId("MC_067e6162-3b6f-4ae7-a171-2470b64dff10_2")
//        Model unitOld = Model.findByModelCatalogueId("MC_067e6162-3b6f-4ae2-a171-2470b64dff19_1")
//
//        then: "the data element and corresponding model have been updated"
//
//        de1.refresh()
//        demographics.refresh()
//        de1.modelCatalogueId=="MC_037e6162-3b6f-4ae2-a171-2470b64dff10_3"
//        de1.supersedes.contains(de1Old)
//        de2
//        de3
//        de4
//        de5
//        de6
//        de7
//        de8
//        de9
//        de10
//        de11
//        deTime
//        deTimeOld
//        admissions
//        demographics
//        unit
//        admissions.parentOf.contains(demographicsOld)
//        admissions.parentOf.contains(unitOld)
//        demographics.contains.contains(de1)
//        demographics.contains.contains(de1)
//        demographics.contains.contains(de2)
//        demographics.contains.contains(de3)
//        demographics.contains.contains(de4)
//        demographics.contains.contains(de5)
//        demographics.supersedes.contains(demographicsOld)
//        unit.contains.contains(de6)
//        unit.contains.contains(de7)
//        unit.contains.contains(de8)
//        unit.contains.contains(de9)
//        unit.contains.contains(de10)
//        unit.contains.contains(de11)
//
//    }


//    void "Test loading NHIC spreadsheet"(){
//        when:"loading the dataElements"
//        def inputStream = new FileInputStream("test/integration/resources/CAN_CUH.xlsx")
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
//        DataElement nhsNumberIndicator = DataElement.findByModelCatalogueId("MC_acc7732d-edc4-4d00-9a11-00e79035df8e_1")
//        DataElement organisationCode = DataElement.findByModelCatalogueId("MC_cf53034f-ebe5-4e83-bed0-d7b14e6d5ea9_1")
//        DataElement siteCode = DataElement.findByModelCatalogueId("MC_512421cb-3803-4d46-a066-216e4fb0c84a_1")
//        DataElement procedureDAte = DataElement.findByModelCatalogueId("MC_f36bd267-4f9e-4be2-a848-6239181deddf_1")
//        Model core = Model.findByModelCatalogueId("MC_3fc0bbcc-7107-4045-aa6f-286e1ae6f679_1")
//        Model patientIdentity = Model.findByModelCatalogueId("MC_39b45bed-f3f6-4ed1-a7d6-3f44f2433b0e_1")
//        Model imaging = Model.findByModelCatalogueId("MC_3b270190-3ce7-4abd-98fe-e041f2608c1a_1")
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
//        when:"loading the dataElements"
//        def inputStream2 = new FileInputStream("test/integration/resources/CAN_CUH_with_change.xlsx")
//        ExcelLoader parser2 = new ExcelLoader(inputStream2)
//        (headers, rows) = parser2.parse()
//
//        dataImportService.importData(headers, rows, "NHIC : CAN", "NHIC CAN conceptual domain for cancer", ["NHIC Datasets", "CAN", "CAN_CUH", "Round 1"], headersMap)
//        DataElement nhsNumberIndicatorOld = DataElement.findByModelCatalogueId("MC_acc7732d-edc4-4d00-9a11-00e79035df8e_1")
//        nhsNumberIndicator.refresh()
//        Model patientIdentityOld = Model.findByModelCatalogueId("MC_39b45bed-f3f6-4ed1-a7d6-3f44f2433b0e_1")
//        patientIdentity.refresh()
//        imaging.refresh()
//
//        then:"the dataElement should have name"
//        nhsNumberIndicator
//        nhsNumberIndicator.supersedes.contains(nhsNumberIndicatorOld)
//        organisationCode
//        siteCode
//        procedureDAte
//        core
//        !imaging.supersedes
//        patientIdentity.supersedes.contains(patientIdentityOld)
//        core.parentOf.contains(imaging)
//        core.parentOf.contains(patientIdentity)
//        imaging.contains.contains(procedureDAte)
//        imaging.contains.contains(siteCode)
//        patientIdentity.contains.contains(organisationCode)
//        patientIdentity.contains.contains(nhsNumberIndicator)
//
//    }



}