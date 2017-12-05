package org.modelcatalogue.core.dataimport.excel

import grails.util.Holders
import groovy.util.logging.Log
import org.apache.poi.ss.usermodel.*
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.PublishingContext
import org.springframework.context.ApplicationContext

/**
 * This used to be a class for one purpose ("importData", now called "buildXmlFromStandardWorkbookSheet"), but now we have made it a parent class of
 * future NT Excel Loaders, so that they can access similar methods.
 * This may not be the best way
 */
@Log
class ConfigExcelLoader extends ExcelLoader {
//    AuditService auditService
    String dataModelName
    Map<String, String> headersMap

//    ConfigExcelLoader(String dataModelName/*, AuditService auditService = null*/) {
//        this.dataModelName = dataModelName
////        this.auditService = auditService
//    }
//    ConfigExcelLoader(String dataModelName, AuditService auditService, InputStream xmlInput) {
//        this(dataModelName, auditService)
//        this.headersMap = this.parseXml(xmlInput)
//    }
//    ConfigExcelLoader(String dataModelName, AuditService auditService, Reader xmlReader) {
//        this(dataModelName, auditService)
//        this.headersMap = this.parseXml(xmlReader)
//    }
    ConfigExcelLoader(String dataModelName, InputStream xmlInput) {
        this.dataModelName = dataModelName
        this.headersMap = this.parseXml(xmlInput)
    }
//    ConfigExcelLoader(String dataModelName, Reader xmlReader) {
//        this(dataModelName)
//        this.headersMap = this.parseXml(xmlReader)
//    }
    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlInput
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, String> parseXml(groovy.util.slurpersupport.GPathResult xml) {
        if (xml.name() == 'headersMap') {
            Map<String, String> hdrMap = [:]
            List<String> metadataKeys = []
            for (groovy.util.slurpersupport.Node n : xml.childNodes()) {
//            System.out.println(n.name() + ": '" + n.text() + "',")
                if (n.name == 'metadata') {
                    metadataKeys += n.text()
                } else if (n.text()) {
                    hdrMap[n.name()] = n.text()
                }
            }
            hdrMap['metadata'] = metadataKeys
            return this.headersMap = hdrMap
        } else {
            return null
        }
    }
    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlInput
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, String> parseXml(InputStream xmlInput) {
        return parseXml(new XmlSlurper().parse(xmlInput))
    }

    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlReader
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, String> parseXml(Reader xmlReader) {
        return parseXml(new XmlSlurper().parse(xmlReader))
    }

    static protected Map<String, String> createRowMap(Row row, List<String> headers) {
        Map<String, String> rowMap = new LinkedHashMap<>()
        // Important that it's LinkedHashMap, for order to be kept, to get the last section which is metadata!
        for (Cell cell : row) {
            rowMap = updateRowMap(rowMap, cell, headers)
        }
        return rowMap
    }
    ApplicationContext context = Holders.getApplicationContext()
    SessionFactory sessionFactory = (SessionFactory) context.getBean('sessionFactory')
    Session session = sessionFactory.getCurrentSession()
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    /**
     * getCatalogueElementDtoFromRow
     * @param Cell cell
     * @param List rowData
     * @return CatalogueElementDto
     */
    static protected Map<String, String> updateRowMap(Map<String,String> rowMap, Cell cell,  List<String> headers) {
        def colIndex = cell.getColumnIndex()
        rowMap[headers[colIndex]] = valueHelper(cell)
        rowMap
    }

    static List<String> getRowData(Row row) {
        def data = []
        for (Cell cell : row) {
            getValue(cell, data)
        }
        data
    }

    List<Map<String, String>> getRowMaps(Sheet sheet, headersMap) {
        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)
        log.info("Headers are ${headers as String}")
        List<Map<String, String>> rowMaps = []
        int counter = 0
        int batchSize = 1000
        while (rowIt.hasNext()) {
            println("processing row" + counter)
            if(++counter % batchSize == 0 ){
                processRowMaps(rowMaps, headersMap)
                rowMaps.clear()
            }
            row = rowIt.next()
            Map<String, String> rowMap = createRowMap(row, headers)
            rowMaps << rowMap
        }
        return rowMaps
    }

    static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c)
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false
        }
        return true
    }

    static void getValue(Cell cell, List<String> data) {
        def colIndex = cell.getColumnIndex()
        data[colIndex] = valueHelper(cell)
    }

    static String valueHelper(Cell cell){
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString().trim();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
        }
        return ""
    }
    static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]

    /**
     * "Standard" refers to an old way of importing excel files...
     * This thing with headersMap is done in a particular way to generically handle a few excel formats
     * regardless of the order of the headers.. and handle legacy "Classifications/Models" instead of "Data Models/Data Classes"
     * in future we will prefer to use a list of headers which exactly matches the headers in the file
     * @param headersMap
     * @param workbook
     * @param index
     */
    String buildModelFromStandardWorkbookSheet(Map<String, String> headersMap, Workbook workbook, int index=0) {
        buildModelFromWorkbookSheet(headersMap, workbook, index)
    }
    /**
     * "Standard" refers to an old way of importing excel files...
     * This thing with headersMap is done in a particular way to generically handle a few excel formats
     * regardless of the order of the headers.. and handle legacy "Classifications/Models" instead of "Data Models/Data Classes"
     * in future we will prefer to use a list of headers which exactly matches the headers in the file
     * @param headersMap
     * @param workbook
     * @param index
     */
    String buildModelFromStandardWorkbookSheet(Workbook workbook, int index=0) {
        buildModelFromWorkbookSheet(null, workbook, index)
    }
    void cleanGORM() {
        //flush a batch of updates and release memory:
        try{
            session.flush()
        }catch(Exception e){
            log.error(session)
            log.error(" error: " + e.message)
            throw e
        }
        session.clear()
        propertyInstanceMap.get().clear()
        println("cleaning up GORM")
    }
    def processRowMaps(List<Map<String, String>> rowMaps, Map<String, Object> headersMap, String dataModelName = this.dataModelName){
        List<String> metadataKeys = headersMap['metadata']
        String regEx = headersMap['classSeparator'] ?: "\\."
        int count = 0
        int batchSize = 50

        rowMaps.each { Map<String, String> rowMap  ->
            println("creating row" + count)

            //take the class name and split to see if there is a hierarchy
            //TODO: (this can be done further up)
            String dataClassNames = tryHeader(ConfigHeadersMap.containingDataClassName, headersMap, rowMap)
            String[] classNames = dataClassNames.split(regEx)

            //see if an open EHR model already exists, if not create one
            //could consider changing this - if there are multiple versions - should make sure we use the latest one.
            DataModel dataModel
            List<DataModel> dataModels =  DataModel.findAllByName(dataModelName, [sort: 'versionNumber', order: 'desc'])
            if(dataModels) dataModel = dataModels.first()

            if (!dataModel){
                log.info("Creating new DataModel: ${dataModelName}")
                dataModel = new DataModel(name: dataModelName).save()
            } else {
                log.info("Found Data Model: ${dataModelName}")
                //if one exists, check to see if it's a draft
                // but if it's finalised create a new version
                if(dataModel.status != ElementStatus.DRAFT){
                    DraftContext context = DraftContext.userFriendly()
                    dataModel = elementService.createDraftVersion(dataModel, PublishingContext.nextPatchVersion(dataModel.semanticVersion), context)
                }
            }

            //if "class" separated by . (regEx)
            //create class hierarchy if applicable,
            //if not then populate the parent data class with the appropriate data element

            DataClass parentDataClass

            classNames.each{ String className ->

                //TODO:
                //see if a model catalogue id exists for the class in case name changed / description changed etc.
                //then update it

                //see if there is a data class with this name - if so make sure you get the right version i.e. highest version number
                // it will be the latest one - only one of the same name per class and the data model is version specific
                 DataClass dataClass = DataClass.findByNameAndDataModel(className, dataModel)

                //if the data class doesn't already exist in the model then create it
                if(!dataClass) dataClass = new DataClass(name: className, dataModel: dataModel).save()
                if(parentDataClass) dataClass.addToChildOf(parentDataClass)

                //last one will be the one that contains the data element
                parentDataClass = dataClass
            }
            String deCode = tryHeader(ConfigHeadersMap.dataElementCode, headersMap, rowMap)
            String deName = tryHeader(ConfigHeadersMap.dataElementName, headersMap, rowMap)
            //see if a data element exists with this model catalogue id
            DataElement de = DataElement.findByModelCatalogueIdAndDataModel(deCode, dataModel)
            //if not see if a data element exists in this model with the same name
            if(!de) de = DataElement.findByNameAndDataModel(deName, dataModel)

            //TODO:
            //then update the data element
            // with params of data type info etc.

            //import the measurement unit for the data type (to be used in the creation of data type if applicable)
            String muCode = tryHeader(ConfigHeadersMap.measurementUnitCode, headersMap, rowMap)
            MeasurementUnit mu

            if (muCode){
                mu = MeasurementUnit.findByModelCatalogueIdAndDataModel(muCode, dataModel)

                //TODO:
                //then update it if it's different
            }else{
                //see if a datatype with this name already exists in this model
                String muName = tryHeader(ConfigHeadersMap.measurementUnitName, headersMap, rowMap)
                mu = MeasurementUnit.findByNameAndDataModel(muName, dataModel)

                //if no mu then create one
                if(!mu) mu  = new MeasurementUnit(dataModel: dataModel, name: muName).save()
            }

            String dtCode = tryHeader(ConfigHeadersMap.dataTypeCode, headersMap, rowMap)
            DataType dt

            //see if a datatype with the model catalogue id already exists in this model
            if(dtCode){
                dt = DataType.findByModelCatalogueIdAndDataModel(dtCode, dataModel)

                //TODO:
                //then update it

            }else{
                String dtName = tryHeader(ConfigHeadersMap.dataTypeName, headersMap, rowMap)
                //see if a datatype with this name already exists in this model
                dt = DataType.findByNameAndDataModel(dtName, dataModel)

                //if no dt then create one
                if(!dt) dt  = new PrimitiveType(dataModel: dataModel, name: dtName, measurementUnit: mu).save()
            }

            //if no de then create a new one
            if(!de) de = new DataElement(name: deName, dataModel: dataModel, dataType: dt).save()

            //add metadata to data element
            for (String key: metadataKeys) {
                def keyValue =  rowMap.get(key)
                if (keyValue) {
                    de.ext.put(key, keyValue.take(2000).toString())
                }
            }

            //the last parent data class i.e. the bottom of the hierarchy will be the container for the data element
            de.addToContainedIn(parentDataClass)

            if ( ++count % batchSize == 0 ) {
                cleanGORM()
            }
        }
        cleanGORM()
    }

    /**
     * This thing with headersMap is done in a particular way to generically handle a few excel formats;
     * regardless of the order of the headers.. and handle legacy "Classifications/Models" instead of "Data Models/Data Classes";
     * in future we will prefer to use a list of headers which exactly matches the headers in the file;
     * The headersMap maps internal header names to actual header names used in the spreadsheet. There is a default setting.
     * @param headersMap
     * @param workbook
     * @param index
     */
    String buildModelFromWorkbookSheet(Map<String, String> headersMap, Workbook workbook, int index = 0){
        // use default headersMap if headersMap is null
        // headersMap maps internal names of headers to what are hopefully the headers used in the actual spreadsheet.
        if (headersMap == null) {
            headersMap = this.headersMap
        }
        log.info("Using headersMap ${headersMap as String}")
        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
//        if (auditService) {
//            auditService.mute {
//                Sheet sheet = workbook.getSheetAt(index);
//                List<Map<String,String>> rowMaps = getRowMaps(sheet, headersMap)
//                //Iterate through the modelMaps to build new DataModel
//                processRowMaps(rowMaps, headersMap)
//            }
//        } else {
            Sheet sheet = workbook.getSheetAt(index);
            List<Map<String,String>> rowMaps = getRowMaps(sheet, headersMap)
            //Iterate through the modelMaps to build new DataModel
            processRowMaps(rowMaps, headersMap)
//        }

    }
    /**
     * This thing with headersMap is done in a particular way to generically handle a few excel formats;
     * regardless of the order of the headers.. and handle legacy "Classifications/Models" instead of "Data Models/Data Classes";
     * in future we will prefer to use a list of headers which exactly matches the headers in the file;
     * The headersMap maps internal header names to actual header names used in the spreadsheet. There is a default setting.
     * @param headersMap
     * @param workbook
     * @param index
     */
    String buildModel(Workbook workbook){
        // use default headersMap if headersMap is null
        // headersMap maps internal names of headers to what are hopefully the headers used in the actual spreadsheet.
        if (headersMap == null) {
            headersMap = this.headersMap
        }
        log.info("Using headersMap ${headersMap as String}")
        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }
//        if (auditService) {
//            auditService.mute {
//                Sheet sheet = workbook.getSheetAt(index);
//                List<Map<String,String>> rowMaps = getRowMaps(sheet, headersMap)
//                //Iterate through the modelMaps to build new DataModel
//                processRowMaps(rowMaps, headersMap)
//            }
//        } else {
        Sheet sheet = workbook.getSheetAt(0);
        List<Map<String,String>> rowMaps = getRowMaps(sheet, headersMap)
        //Iterate through the modelMaps to build new DataModel
        processRowMaps(rowMaps, headersMap)
//        }

    }
    String outOfBoundsWith(Closure<String> c, String s = '') {
        try {
            return c()
        } catch (ArrayIndexOutOfBoundsException a) {
            return s
        }
    }
    String tryHeader(String internalHeaderName, Map<String, Object> headersMap, Map<String, String> rowMap) {
        // headersMap maps internal names of headers to what are hopefully the headers used in the actual spreadsheet.
        String entry = rowMap.get(headersMap.get(internalHeaderName))
        if (entry) {
            return entry
        } else {
            /*log.info("Trying to use internalHeaderName '$internalHeaderName', which headersMap corresponds to " +
                "header ${headersMap.get(internalHeaderName)}, from rowMap ${rowMap as String}, nothing found.")*/
            return null
        }
    }

    static void importDataTypesFromExcelExporterFormat(CatalogueBuilder catalogueBuilder,
                                                       String dataTypeName, String dataTypeCode, String dataTypeEnumerations, String dataTypeRule, String measurementUnitName, String measurementUnitCode){
        String[] lines = dataTypeEnumerations?.split("\\r?\\n");
        Map<String,String> enumerations = dataTypeEnumerations ? parseMapStringLines(lines) : [:]
        if (enumerations) {
            catalogueBuilder.dataType(name: dataTypeName, enumerations: enumerations, rule: dataTypeRule, id: dataTypeCode) //TODO: get dataModel, which used to be dataTypeClassification, from data type code.
        } else if (measurementUnitName) {
            catalogueBuilder.dataType(name: dataTypeName, rule: dataTypeRule, id: dataTypeCode) {
                measurementUnit(name: measurementUnitName, id: measurementUnitCode)
            }
        } else {
            catalogueBuilder.dataType(name: dataTypeName, rule: dataTypeRule, id: dataTypeCode)
        }
    }

    /**
     *
     * @param dataElementName data element/item name
     * @param dataTypeNameOrEnum - Column F - content of - either blank or an enumeration or a named datatype.
     * @return
     */
    static importDataTypes(CatalogueBuilder catalogueBuilder, String dataElementName, dataTypeNameOrEnum, String dataTypeCode, String dataTypeClassification, String measurementUnitName, String measurementUnitSymbol) {
        if (!dataTypeNameOrEnum) {
            if (measurementUnitName) {
                return catalogueBuilder.dataType(id: dataTypeCode, dataModel: dataTypeClassification, name: 'String') {
                    measurementUnit name: measurementUnitName, symbol: measurementUnitSymbol
                }
            }
            return catalogueBuilder.dataType(id: dataTypeCode, dataModel: dataTypeClassification, name: 'String')
        }
        //default data type to return is the string data type
        String[] lines = dataTypeNameOrEnum.split("\\r?\\n");
        if (!(lines.size() > 0 && lines != null)) {
            if (measurementUnitName) {
                return catalogueBuilder.dataType(name: "String", dataModel: dataTypeClassification, id: dataTypeCode) {
                    measurementUnit name: measurementUnitName, symbol: measurementUnitSymbol
                }
            }
            return catalogueBuilder.dataType(name: "String", dataModel: dataTypeClassification, id: dataTypeCode)
        }

        def enumerations = lines.size() == 1 ? [:] : parseMapStringLines(lines)

        if(!enumerations){
            if (measurementUnitName) {
                return catalogueBuilder.dataType(name: dataTypeNameOrEnum, dataModel: dataTypeClassification, id: dataTypeCode) {
                    measurementUnit name: measurementUnitName, symbol: measurementUnitSymbol
                }
            }
            return catalogueBuilder.dataType(name: dataTypeNameOrEnum, dataModel: dataTypeClassification, id: dataTypeCode)
        }

        return catalogueBuilder.dataType(name: dataElementName, enumerations: enumerations, dataModel: dataTypeClassification, id: dataTypeCode)
    }

    static Map<String,String> parseMapString(String mapString) {
        return parseMapStringLines(mapString.split(/\r?\n/))
    }

    /**
     For colon-separated key-value lines which are usually enumerations or metadata.
     */
    static Map<String,String> parseMapStringLines(String[] lines){
        Map map = new HashMap()

        lines.each { enumeratedValues ->

            String[] EV = enumeratedValues.split(":")

            if (EV?.size() == 2) {
                String key = EV[0]
                String value = EV[1]

                if (value.size() > 244) {
                    value = value[0..244]
                }

                key = key.trim()
                value = value.trim()


                map.put(key, value)
            }
        }
        return map
    }
}
