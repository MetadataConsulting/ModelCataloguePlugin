package org.modelcatalogue.core.reports

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.util.Metadata

class RegisterReportsService {

    ReportsRegistry reportsRegistry

    void register() {
        reportsRegistry.register {
            creates asset
            title { "Export All Elements of ${it.name} to Excel XSLX" }
            defaultName { "Export All Elements of ${it.name} to Excel XSLX" }
            type DataClass
            when { DataClass dataClass ->
                dataClass.countContains() > 0
            }
            link controller: 'dataArchitect', action: 'getSubModelElements', params: [format: 'xlsx', report: 'NHIC'], id: true
        }

        reportsRegistry.register {
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

//        reportsRegistry.register {
//            creates asset
//            title { "Grid Report Spreadsheet" }
//            defaultName { "${it.name} report as MS Excel Document Grid" }
//            depth 3
//            type DataModel
//            when { DataModel dataModel ->
//                dataModel.countDeclares() > 0
//            }
//            link controller: 'dataModel', action: 'gridSpreadsheet', id: true
//        }

        reportsRegistry.register {
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
            reportsRegistry.register {
                creates asset
                title { "GMC Grid Report – North Thames – ${name}" }
                defaultName { "${it.name} report as MS Excel Document Grid" }
                depth 7 // for Rare Diseases
                type DataModel
                when { DataModel dataModel ->
                    (dataModel.countDeclares() > 0) && (gelSourceModelNames.contains(dataModel.name))
                }
                link controller: 'northThames', action: 'northThamesSummaryReport', id: true, params: [organization: name]
            }
        }


        reportsRegistry.register {
            creates asset
            title { "Inventory Report Document" }
            defaultName { "${it.name} report as MS Word Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventoryDoc', id: true
        }

        reportsRegistry.register {
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

        reportsRegistry.register {
            creates asset
            title { "Inventory Report Spreadsheet" }
            defaultName { "${it.name} report as MS Excel Document" }
            depth 3
            type DataClass
            link controller: 'dataClass', action: 'inventorySpreadsheet', id: true
        }

//  needs more work
//        reportsRegistry.register {
//            creates asset
//            title { "Changelog Document" }
//            defaultName { "${it.name} changelog as MS Word Document" }
//            depth 3
//            includeMetadata true
//            type DataClass
//            link controller: 'dataClass', action: 'changelogDoc', id: true
//        }

        reportsRegistry.register {
            creates link
            type DataModel, DataClass, DataElement, DataType, MeasurementUnit
            title { "Export to Catalogue XML" }
            link { CatalogueElement element ->
                [url: element.getDefaultModelCatalogueId(false) + '?format=xml']
            }
        }

        reportsRegistry.register {
            creates link
            title { "Generate all ${it.name} files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_CANCER_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllCancerReports', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Generate all ${it.name} files" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true'
            }
            link controller: 'genomics', action: 'exportAllRareDiseaseReports', id: true
        }


        reportsRegistry.register {
            creates link
            title { "Rare Diseases Disorder List (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseDisorderListAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria Report (Word Doc)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityDoc', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Phenotypes and Clinical Tests Report (Word Doc)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasePhenotypesAndClinicalTestsDoc', id: true
        }


        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Phenotypes Split Docs" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseSplitDocs', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Disorder List Only (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseListAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Eligibility Criteria (JSON)" }
            defaultName { "${it.name} report as Json" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOEligibilityCriteriaAsJson', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases HPO And Clinical Tests (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseHPOAndClinicalTestsAsCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Disease Eligibility Criteria Report (CSV)" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseaseEligibilityCsv', id: true
        }

        reportsRegistry.register {
            creates link
            title { "Rare Diseases Static Website" }
            type DataModel
            when { DataModel dataModel ->
                dataModel.ext.get('Rare Disease Report Available') == 'true' || dataModel.ext.get("All Rare Disease Conditions Reports") == 'true' || dataModel.ext.get(Metadata.ALL_RD_REPORTS) == 'true' || dataModel.ext.get(Metadata.HPO_REPORT_AVAILABLE) == 'true'
            }
            link controller: 'genomics', action: 'exportRareDiseasesWebsite', id: true
        }



        reportsRegistry.register {
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
