package org.modelcatalogue.core.reports

import groovy.transform.Immutable
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.util.Metadata

@Immutable(copyWith=true)
class OrganizationDescription {
    String fullName, abbreviatedName, regexForDataModelName

    static OrganizationDescription createWithAbbrevNamePrefixAsRegex(String fullName, String abbreviatedName) {
        new OrganizationDescription(fullName, abbreviatedName, abbreviatedName + "(.*)")
    }
}
class RegisterReportDescriptorsService {

    ReportDescriptorRegistry reportDescriptorRegistry

    void register() {
        reportDescriptorRegistry.register {
            creates asset
            title { "Export All Elements of ${it.name} to Excel XSLX" }
            defaultName { "Export All Elements of ${it.name} to Excel XSLX" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.countContains() > 0
            }
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'NHIC'], id: true
        }

        reportDescriptorRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'inventorySpreadsheet', id: true
        }

        reportDescriptorRegistry.register {
            creates asset
            title { "Grid Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document Grid" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'gridSpreadsheet', id: true
        }

        reportDescriptorRegistry.register {
            creates asset
            title { "MC Excel Export" }
            defaultName { "${it.name} report as MS Excel Document Grid" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'excelExporterSpreadsheet', id: true
        }

        List<String> northThamesHospitalNames = ['GOSH', 'LNWH', 'MEH', 'UCLH'] // not sure if this should be defined here. Maybe it would be better in a source file, or perhaps a config file.
        List<String> gelSourceModelNames = ['Cancer Model', 'Rare Diseases']
        northThamesHospitalNames.each { name ->
            reportDescriptorRegistry.register {
                creates asset
                title { "GMC Grid Report – North Thames – ${name}" }
                defaultName { "${it.name} report as MS Excel Document Grid" }
                depth 7 // for Rare Diseases
                type DataModel
                when { DataModel dataModel ->
                    (dataModel.countDeclares() > 0) && (gelSourceModelNames.contains(dataModel.name))
                    // report only applies to Cancer Model and Rare Diseases!
                }
                link controller: 'northThames', action: 'northThamesGridHierarchyMappingSummaryReport', id: true, params: [organization: name]
            }

            reportDescriptorRegistry.register {
                creates asset
                title { "GMC Mapping Report – North Thames – ${name}" }
                defaultName { "${it.name} North Thames mapping report as MS Excel Document" }
                depth 7 // for Rare Diseases
                type DataModel
                when { DataModel dataModel ->
                    (dataModel.countDeclares() > 0) && (gelSourceModelNames.contains(dataModel.name))
                }
                link controller: 'northThames', action: 'northThamesMappingReport', id: true, params: [organization: name]
            }

        }


        List<OrganizationDescription> organizationDescriptions = [
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("Royal Free Hospital", "RFH"),
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("University College London Hospital", "UCLH"),
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("Great Ormond Street Hospital", "GOSH"),
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("St. Bartholomews", "BARTS"),
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("Royal National Orthopaedic Hospital", "RNOH"),
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("London North West Hospital", "LNWH"),
                OrganizationDescription.createWithAbbrevNamePrefixAsRegex("Moorfields Eye Hospital", "MEH"),
                new OrganizationDescription("North Thames Genomic Medical Centres", "NT", "LONDONPATHOLOGYCODES(.*)")
        ]

        for (OrganizationDescription organizationDescription : organizationDescriptions) {
            OrganizationDescription copiedOrganizationDescription = organizationDescription.copyWith() // copy so that each reportDescriptor has a different variable
            reportDescriptorRegistry.register {
                creates asset
                title { "${copiedOrganizationDescription.fullName} Mapping Report" }
                defaultName { "${copiedOrganizationDescription.abbreviatedName} Mapping Report Document" }
                depth 3
                type DataModel
                when { DataModel dataModel ->
                    dataModel.name.matches(copiedOrganizationDescription.regexForDataModelName)
                }
                link controller: 'northThames', action: 'northThamesMappingReport', id: true
            }

        }





        reportDescriptorRegistry.register {
            creates asset
            title { "Inventory Report Document" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventoryDoc', id: true
        }

        reportDescriptorRegistry.register {
            creates asset
            title { "Inventory Report Document" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataModel
            when { DataModel dataModel ->
                dataModel.countDeclares() > 0
            }
            link controller: 'dataModel', action: 'inventoryDoc', id: true
        }

        reportDescriptorRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventorySpreadsheet', id: true
        }

//  needs more work
//        reportDescriptorRegistry.register {
//            creates asset
//            title { "Changelog Document" }
//            defaultName { "${it.name} changelog as MS Word Document" }
//            depth 3
//            includeMetadata true
//            type DataClass
//            link controller: 'dataClass', action: 'changelogDoc', id: true
//        }

        reportDescriptorRegistry.register {
            creates link
            type DataModel, DataClass, DataElement, DataType, MeasurementUnit
            title { "Export to Catalogue XML" }
            link { CatalogueElement element ->
                [url: element.getDefaultModelCatalogueId(false) + '?format=xml']
            }
        }

        ///// Genomics England Reports

        reportDescriptorRegistry.register {
            creates link
            title { "Generate all ${it.name} Cancer Report files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_CANCER_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllCancerReports', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Generate all ${it.name} Rare Disease Report files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllRareDiseaseReports', id: true
        }


        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Disorder List (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseDisorderListAsCsv', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria Report (Word Doc)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Phenotypes and Clinical Tests Report (Word Doc)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc', id: true
        }


        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Phenotypes Split Docs" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseSplitDocs', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Disorder List Only (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseListAsJson', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOEligibilityCriteriaAsJson', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Disease Eligibility Criteria Report (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityCsv', id: true
        }

        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Static Website" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get('Rare Disease Report Available') == 'true' || dataModel.ext.get("All Rare Disease Conditions Reports") == 'true' || dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true' || dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasesWebsite', id: true
        }



        reportDescriptorRegistry.register {
            creates link
            title { "Rare Diseases Static Website" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get('Rare Disease Report Available') == 'true' || dataModel.ext.get("All Rare Disease Conditions Reports") == 'true' || dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true' || dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasesWebsite', id: true
        }
    }
}
