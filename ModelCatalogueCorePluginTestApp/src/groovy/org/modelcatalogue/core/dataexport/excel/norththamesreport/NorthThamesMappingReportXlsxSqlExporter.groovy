package org.modelcatalogue.core.dataexport.excel.norththamesreport

import grails.util.Holders
import groovy.util.logging.Log
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.hibernate.SessionFactory
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataElementService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.export.inventory.ModelCatalogueStyles
import org.modelcatalogue.spreadsheet.builder.api.SheetDefinition
import org.modelcatalogue.spreadsheet.builder.api.SpreadsheetBuilder
import org.modelcatalogue.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import org.springframework.context.ApplicationContext

import static org.modelcatalogue.core.export.inventory.ModelCatalogueStyles.H1

@Log
class NorthThamesMappingReportXlsxSqlExporter {
    static Map<String, Map<String, String>> ntSitesMap = [
        LONDONPATHOLOGYCODES: [siteName: 'RFH', lpcModelName: 'LONDONPATHOLOGYCODES', localModelName: 'WinPath', loincModelName: 'LOINC', gelModelName: 'Rare Diseases'],
        RFH_LONDONPATHOLOGYCODES: [siteName: 'RFH', lpcModelName: 'RFH_LONDONPATHOLOGYCODES', localModelName: 'RFH_WinPath', loincModelName: 'LOINC', gelModelName: 'Rare Diseases'],
        RFH_LONDONPATHOLOGYCODES_GEL_SUBSET: [siteName: 'RFH', lpcModelName: 'RFH_LONDONPATHOLOGYCODES_GEL_SUBSET', localModelName: 'RFH_WinPath', loincModelName: 'LOINC', gelModelName: 'Rare Diseases'],
        GOSH_OMNILAB: [siteName: 'GOSH', lpcModelName: 'GOSH_OMNILAB', localModelName: '', loincModelName: 'LOINC', gelModelName: '']
    ]
    static Map<String, String> defaultSiteMap = [siteName: 'RFH', lpcModelName: 'RFH_LONDONPATHOLOGYCODES', localModelName: 'RFH_WinPath', loincModelName: 'LOINC', gelModelName: 'RFH_LONDONPATHOLOGYCODES_GEL_SUBSET']

    /**
     * Map of data source systems
     */
    final DataModel sourceModel
//    final GrailsApplication grailsApplication
    ApplicationContext context = Holders.getApplicationContext()
    SessionFactory sessionFactory = (SessionFactory) context.getBean('sessionFactory')
    protected List<String> excelHeaders = [
        'LOCAL SITE',
        'LOCAL CODESET 1', 'LOCAL CODE 1', 'LOCAL CODE 1 NAME',
        'LOCAL CODESET 2', 'LOCAL CODE 2', 'LOCAL CODE 2 DESCRIPTION',
        'LOINC CODE', 'LOINC CODE DESCRIPTION', 'LOINC SYSTEM(SPECIMEN)',
        'GEL CODE', 'GEL CODE DESCRIPTION', 'OPENEHR QUERY', 'REF RANGE'
    ]
    enum cols {lpc_model, lpc_code, lpc_name, local_model, local_code, local_name, loinc_code, loinc_name, loinc_system, gel_code, gel_name, gel_openehr, ref_range}
    /**
     * The report is triggered from a DataModel (element), and is on the
     * location of data elements specified by that DataModel in the given 'organization'.
     * @param element
     * @param dataClassService
     * @param grailsApplication
     * @return
     */
    static NorthThamesMappingReportXlsxSqlExporter create(DataModel sourceModel, DataClassService dataClassService, DataElementService dataElementService, GrailsApplication grailsApplication, Boolean mySQL) {
        return new NorthThamesMappingReportXlsxSqlExporter(sourceModel)
    }

    static NorthThamesMappingReportXlsxSqlExporter create(DataModel sourceModel) {
        return new NorthThamesMappingReportXlsxSqlExporter(sourceModel)
    }

    NorthThamesMappingReportXlsxSqlExporter(DataModel sourceModel) {
        this.sourceModel = sourceModel
//        this.grailsApplication = grailsApplication
    }

    private List getMappedDataElements(Map<String, String> siteMap){
        String query = '''
SELECT DISTINCT
	IFNULL(lpc_dm.`name`, '') AS `lpc_model`,
	IFNULL(lpc.`code`, '') AS `lpc_code`,
	IFNULL(lpc.`name`, '') AS `lpc_name`,
	IF(wpath.`code` IS NULL, '', wpath_dm.`name`) AS `local_model`,
	IFNULL(wpath.`code`, '') AS `local_code`,
	IFNULL(wpath.`name`, '') AS `local_name`,
	IFNULL(loinc.`code`, '') AS `loinc_code`,
	IFNULL(loinc.`name`, '') AS `loinc_name`,
	IFNULL(loinc.system, '') AS `loinc_system`,
	IFNULL(gel.`code`, '') AS `gel_code`,
	IFNULL(gel.`name`, '') AS `gel_name`,
	IFNULL(gel.openehr, '') AS `gel_openehr`,
	IFNULL(lpc.ref_range, '') AS `ref_range`
FROM
	(catalogue_element AS lpc_dm, catalogue_element AS wpath_dm, catalogue_element AS loinc_dm, catalogue_element AS gel_dm)
	JOIN (
		SELECT 
			ce.id, r.source_id, ce.model_catalogue_id AS `code`, ce.`name`, ce.data_model_id, ev2.extension_value AS ref_range
		FROM catalogue_element AS ce
			JOIN data_element AS de USING (id)
			LEFT JOIN extension_value AS ev2 ON ev2.element_id = ce.id AND ev2.`name`  = 'Ref Range'
			LEFT JOIN relationship AS r ON r.source_id = ce.id AND r.relationship_type_id = 7
	) AS lpc ON lpc.data_model_id = lpc_dm.id
	LEFT JOIN (
		SELECT 
			r.source_id, ev.extension_value AS `code`, ce.`name`, ce.data_model_id
		FROM
			catalogue_element AS ce
			JOIN extension_value AS ev ON ev.element_id = ce.id AND ev.`name` = 'WinPath TFC'
			JOIN relationship AS r ON r.destination_id = ce.id AND r.relationship_type_id = 7
	) AS wpath ON wpath.source_id = lpc.id AND wpath.data_model_id = wpath_dm.id
	LEFT JOIN (
		SELECT 
			r.source_id, IFNULL(ce.model_catalogue_id, IFNULL(ce.latest_version_id, IFNULL(ce.id, ''))) AS `code`, ce.`name`, ce.data_model_id, ev.extension_value AS system
		FROM
			catalogue_element AS ce
			LEFT JOIN extension_value AS ev ON ev.element_id = ce.id AND ev.name = 'SYSTEM'
			JOIN relationship AS r ON r.destination_id = ce.id AND r.relationship_type_id = 7
	) AS loinc ON loinc.source_id = lpc.id AND loinc.data_model_id = loinc_dm.id
	LEFT JOIN (
		SELECT 
			r.source_id, IFNULL(ce.model_catalogue_id, IFNULL(ce.latest_version_id, IFNULL(ce.id, ''))) AS `code`, ce.`name`, ce.data_model_id, ev.extension_value AS openehr
		FROM
			catalogue_element AS ce
			JOIN relationship AS r ON r.destination_id = ce.id AND r.relationship_type_id = 7
			LEFT JOIN relationship AS r2 ON r2.source_id = ce.id AND r2.relationship_type_id = 7
			LEFT JOIN extension_value AS ev ON ev.element_id = r2.destination_id AND ev.`name` = 'Archetype Path Query Statement'
	) AS gel ON gel.source_id = lpc.id AND gel.data_model_id = gel_dm.id
WHERE
	lpc_dm.id = (SELECT id FROM catalogue_element JOIN data_model USING (id) WHERE `name` = :lpcModel ORDER BY version_number DESC LIMIT 1)
	AND wpath_dm.id = IFNULL((SELECT id FROM catalogue_element JOIN data_model USING (id) WHERE `name` = :localModel ORDER BY version_number DESC LIMIT 1), (SELECT id FROM data_element ORDER BY id LIMIT 1))
	AND loinc_dm.id = IFNULL((SELECT id FROM catalogue_element JOIN data_model USING (id) WHERE `name` = :loincModel ORDER BY version_number DESC LIMIT 1), (SELECT id FROM data_element ORDER BY id LIMIT 1))
	AND gel_dm.id = IFNULL((SELECT id FROM catalogue_element JOIN data_model USING (id) WHERE `name` = :gelModel ORDER BY version_number DESC LIMIT 1), (SELECT id FROM data_element ORDER BY id LIMIT 1))
ORDER BY
    `lpc_name`, `local_model`, `loinc_code`, `gel_code`
'''
        final session = sessionFactory.currentSession
        final sqlQuery = session.createSQLQuery(query)
        sqlQuery.with {
            setString('lpcModel', siteMap.lpcModelName)
            setString('localModel', siteMap.localModelName)
            setString('loincModel', siteMap.loincModelName)
            setString('gelModel', siteMap.gelModelName)
        }
        return sqlQuery.list()
    }
//    private void setPrefixes(Map<String, String> siteMap) {
//        int lpcSiteIx = siteMap.lpcModelName.indexOf('_')
//        if (lpcSiteIx > 0) {
//            String siteName = siteMap.lpcModelName.substring(0, lpcSiteIx)
//            if (siteMap.localModelName.indexOf('_') <= 0) {
//                siteMap.localModelName = siteName + '_' + siteMap.localModelName
//            }
//        }
//    }
    private Map<String, String> setModelNames(DataModel sourceModel) {
        if (sourceModel) {
            return ntSitesMap.get(sourceModel.name)?: defaultSiteMap
        } else {
            return null
        }
    }
    void export(OutputStream outputStream) {
        SpreadsheetBuilder builder = new PoiSpreadsheetBuilder()
        Map<String, String> siteMap = setModelNames(sourceModel)
        log.info("siteName:${siteMap.siteName} sourceModel:${sourceModel.name} lpcModel:${siteMap.lpcModelName} localModel:${siteMap.localModelName} loincModel:${siteMap.loincModelName} gelModel:${siteMap.gelModelName}")
        List mappedDataElements = getMappedDataElements(siteMap)
//        log.info(mappedDataElements.toString())

        builder.build(outputStream) {
            apply ModelCatalogueStyles
            style ('data') {
                wrap text
                border(left) {
                    color black
                    style medium
                }
            }
            sheet("Mapped Elements") { SheetDefinition sheetDefinition ->
                row {
                    style H1
                    for (String header in excelHeaders) {
                        cell {
                            value header
                            width auto
                        }
                    }
                }
                for (de in mappedDataElements) {
//                    log.info('=================')
//                    log.info(de.toString())
                    printMapping(siteMap.siteName, de, sheetDefinition)
                }
            }
        }
    }

    void printMapping(String siteName, deRow, SheetDefinition sheet){
        //print a row with all the mappings form the source models to the mapped models
        //get the mapped items from the source data element
//        log.info(deRow)
        sheet.with { SheetDefinition sheetDefinition ->
            row {
                style 'data'
                cell {
                    value siteName
                    width auto
                }
                cell {
                    value deRow[cols.lpc_model.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.lpc_code.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.lpc_name.ordinal()]
                    width auto
                }

                //mapped items local
                cell {
                    value deRow[cols.local_model.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.local_code.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.local_name.ordinal()]
                    width auto
                }

                //mapped loinc items
                cell {
                    value  deRow[cols.loinc_code.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.loinc_name.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.loinc_system.ordinal()]
                    width auto
                }

                //mapped gel items
                cell {
                    value deRow[cols.gel_code.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.gel_name.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.gel_openehr.ordinal()]
                    width auto
                }
                cell {
                    value deRow[cols.ref_range.ordinal()]
                    width auto
                }
            }
        }
    }
}
