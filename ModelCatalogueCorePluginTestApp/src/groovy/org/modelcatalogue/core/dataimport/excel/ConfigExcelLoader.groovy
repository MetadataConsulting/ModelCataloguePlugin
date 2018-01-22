package org.modelcatalogue.core.dataimport.excel

import grails.util.Holders
import groovy.util.logging.Log
import org.apache.poi.ss.usermodel.*
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
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
    final String DEFAULT_MU_NAME = null
    final Integer MAX_METADATA_LEN = 2000
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
            for (groovy.util.slurpersupport.Node n in xml.childNodes()) {
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
            if(++counter % batchSize == 0 ) {
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
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false
            }
        }
        return true
    }

    static void getValue(Cell cell, List<String> data) {
        def colIndex = cell.getColumnIndex()
        data[colIndex] = valueHelper(cell)
    }

    static String valueHelper(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString().trim()
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue()
                }
                return cell.getNumericCellValue()
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue()
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula()
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
    /**
     * flushes a batch of updates and releases memory
     */
    void cleanGORM() {
        try{
            session.flush()
        }catch(Exception e) {
            log.error(session)
            log.error(" error: " + e.message)
            throw e
        }
        session.clear()
        propertyInstanceMap.get().clear()
        log.info("cleaned up GORM")
    }
    /**
     *
     * @param params
     * @param code
     * @param name
     * @param symbol
     * @return
     */
    Map<String, Object> paramsAddCodeNameDesc(Map<String, Object> params, String code, String name, String desc = null) {
        if (code) {
            params['modelCatalogueId'] = code
        }
        if (name) {
            params['name'] = name
        }
        if (desc) {
            params['description'] = desc
        }
        return params
    }
    /**
     *
     * @param key
     * @param oldValue
     * @param newValue
     * @param params
     * @return
     */
    Map<String, String> update(String key, String oldValue, String newValue, Map<String, String> params = null) {
        if (oldValue != newValue) {
            if (params == null) {
                params = [key: newValue]
            } else {
                params[key] = newValue
            }
        }
        return params
    }

    void addAsChildTo(CatalogueElement child, CatalogueElement parent) {
        Set<Relationship> incoming = child.getIncomingRelationships()
        for (Relationship rel in incoming) {
            if (rel.getSource() == parent && rel.getDestination() == child && rel.getRelationshipType().getId() == RelationshipType.hierarchyType.getId()) {
                return // is already a child - will usually be the case
            }
        }
        child.addToChildOf(parent)
    }
    /**
     *
     * @param dataModelName
     * @return
     */
    DataModel processDataModel(String dataModelName) {
        //see if an open EHR model already exists, if not create one
        //could consider changing this - if there are multiple versions - should make sure we use the latest one.
        DataModel dataModel
        List<DataModel> dataModels =  DataModel.findAllByName(dataModelName, [sort: 'versionNumber', order: 'desc'])
        if(dataModels) {
            dataModel = dataModels.first()
        }

        if (!dataModel) {
            log.info("Creating new DataModel: ${dataModelName}")
            dataModel = new DataModel(name: dataModelName).save()
        } else {
            log.info("Found Data Model: ${dataModelName}")
            //if one exists, check to see if it's a draft
            // but if it's finalised create a new version
            if(dataModel.status != ElementStatus.DRAFT) {
                DraftContext context = DraftContext.userFriendly()
                dataModel = elementService.createDraftVersion(dataModel, PublishingContext.nextPatchVersion(dataModel.semanticVersion), context)
            }
        }
        return dataModel
    }
    /**
     *
     * @param dataModel
     * @param headersMap
     * @param rowMap
     * @return
     */
    DataClass processDataClass(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap) {
        String regEx = headersMap['classSeparator'] ?: "\\."
        //take the class name and split to see if there is a hierarchy
        String dcCode = tryHeader(ConfigHeadersMap.containingDataClassCode, headersMap, rowMap)
        String dcNames = tryHeader(ConfigHeadersMap.containingDataClassName, headersMap, rowMap)
        if(dcNames == null){
            return null  //Best go no further and return a null object
        }
        String dcDescription = tryHeader(ConfigHeadersMap.containingDataClassDescription, headersMap, rowMap)
        String[] dcNameList = dcNames.split(regEx)
        Integer maxDcNameIx = dcNameList.length - 1
        Integer dcNameIx = 0
        DataClass dc, parentDC
        String className = dcNameList[dcNameIx]

        //if "class" separated by . (regEx) create class hierarchy if applicable,
        //if not then populate the parent data class with the appropriate data element
        while (dcNameIx < maxDcNameIx) { // we are just checking names at this point (above the leaf)
            if (!(dc = DataClass.findByNameAndDataModel(className, dataModel))) {
                dc = new DataClass(name: className, dataModel: dataModel).save() // any cat id or description will not apply here
            }
            // maybe check if the parent link is already in the incomingRelationships before calling addToChildOf?
            if (parentDC)
                addAsChildTo(dc, parentDC)

            //last one will be the one that contains the data element
            parentDC = dc
            className = dcNameList[++dcNameIx]
        }
        // now we are processing the actual (leaf) class, so need to check if there is a model catalogue id (dcCode)
        if (dcCode && (dc = DataClass.findByModelCatalogueIdAndDataModel(className, dataModel))) {
            if (className != dc.getName()) { // yes, check if the name has changed
                dc.setName(className)
                dc.save()
            }
        } else {
            // see if there is a data class with this name - if so make sure you get the right version i.e. highest version number
            // it will be the latest one - only one of the same name per class and the data model is version specific
            dc = DataClass.findByNameAndDataModel(className, dataModel)
        }
        if (dc) { // we found a DC, just need to check the description
            if (dcDescription != dc.getDescription()) {
                dc.setDescription(dcDescription)
                dc.save()
            }
        } else { // need to create one - this time with all the parameters
            // the data class doesn't already exist in the model so create it
            def params = paramsAddCodeNameDesc([dataModel: dataModel], dcCode, className, dcDescription)
            dc = new DataClass(params).save()
        }
        // maybe check if the parent link is already in the incomingRelationships before calling addToChildOf?
        if (parentDC) {
            addAsChildTo(dc, parentDC)
        }

        return dc
    }
    /**
     *
     * @param dataModel
     * @param headersMap
     * @param rowMap
     * @return
     */
    MeasurementUnit processMeasurementUnit(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap) {
        //import the measurement unit for the data type (to be used in the creation of data type if applicable)
        String muCatId = tryHeader(ConfigHeadersMap.measurementUnitCode, headersMap, rowMap)
        String muSymbol = tryHeader(ConfigHeadersMap.measurementUnitSymbol, headersMap, rowMap)
        String muName = tryHeader(ConfigHeadersMap.measurementUnitName, headersMap, rowMap) ?: (muSymbol ?: (muCatId ?: DEFAULT_MU_NAME))
        if(muName == null){
            return null  //Best go no further and return a null object
        }
        MeasurementUnit mu

        if (muName == DEFAULT_MU_NAME) { // there is no measurement unit
            return null
        }

        if (muCatId) {
            mu = MeasurementUnit.findByModelCatalogueIdAndDataModel(muCatId, dataModel)
        } else if (muName) { //see if a datatype with this name already exists in this model
            mu = MeasurementUnit.findByNameAndDataModel(muName, dataModel)
        } else if (muSymbol) {
            mu = MeasurementUnit.findBySymbolAndDataModel(muSymbol, dataModel)
        }
        // all this to test
        //if no mu then create one
        if(!mu) {
            mu  = new MeasurementUnit()
            mu.setDataModel(dataModel)
            mu.setName(muName)
            if (muCatId)
                mu.setModelCatalogueId(muCatId)
            if (muSymbol)
                mu.setSymbol(muSymbol)
            mu.save()
//            mu  = new MeasurementUnit(params).save()
        } else {
            Map<String, String> params = update('modelCatalogueId', mu.getModelCatalogueId(), muCatId)
            params = update('name', mu.getName(), muName, params)
            params = update('symbol', mu.getSymbol(), muSymbol, params)
            if (params ) { // will be null if no updates
                mu.save(params)
            }

        }
        return mu
    }
    /**
     *
     * @param dataModel
     * @param headersMap
     * @param rowMap
     * @param mu
     * @return
     */
    DataType processDataType(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, MeasurementUnit mu) {
        Boolean updated = false
        String dtCode = tryHeader(ConfigHeadersMap.dataTypeCode, headersMap, rowMap)
        String dtName = tryHeader(ConfigHeadersMap.dataTypeName, headersMap, rowMap)
        if(dtName == null){
            return null  //Best go no further and return a null object
        }
        DataType dt

        //see if a datatype with the model catalogue id already exists in this model
        if (dtCode && (dt = DataType.findByModelCatalogueIdAndDataModel(dtCode, dataModel))) {
            if ((dtName ?: '') != dt.getName()) {
                dt.setName(dtName)
                updated = true
            }
        } else if (dtName && (dt = DataType.findByNameAndDataModel(dtName, dataModel))) { //see if a datatype with this name already exists in this model
            if (dtCode != dt.getModelCatalogueId()) {
                dt = null // create a new datatype further on - it is unlikely to have datatypes with the same name but different catalogue ids
//                dt.setModelCatalogueId(dtCode)
//                updated = true
            }
        }
        //if no dt then create one
        if (!dt) {
            if (mu) {
                def params = paramsAddCodeNameDesc([dataModel: dataModel, measurementUnit: mu], dtCode, dtName)
                dt  = new PrimitiveType(params)
            } else {
                def params = paramsAddCodeNameDesc([dataModel: dataModel], dtCode, dtName)
                dt = new DataType(params)
            }
            updated = true
        }
        if (updated) {
            dt.save()
        }
        return dt
    }
    /**
     *
     * @param de
     * @param rowMap
     * @param metadataKeys
     * @return
     */
    Boolean addMetadata(DataElement de, Map<String, String> rowMap, List<String> metadataKeys) {
        Boolean updated = false
        for (String key in metadataKeys) {
            String keyValue =  rowMap.get(key)
            if (keyValue) {
                de.ext.put(key, keyValue.take(MAX_METADATA_LEN).toString())
                updated = true
            }
        }
        return updated
    }
    /**
     *
     * @param de
     * @param headersMap
     * @param rowMap
     * @param metadataKeys
     * @return
     */
    Boolean updateMetadata(DataElement de, Map<String, Object> headersMap, Map<String, String> rowMap, List<String> metadataKeys) {
        Boolean updated = false
        if (de.ext.isEmpty()) { // no existing metadata, so just insert the new metadata
            updated = addMetadata(de, rowMap, metadataKeys)
        } else { // we need to update it (possible inserts, edits & deletes)
            for (String newKey in metadataKeys) { // first go through the new row
                String newValue =  rowMap.get(newKey)?.take(MAX_METADATA_LEN)
                String oldValue = de.ext.get(newKey)
                if (oldValue != newValue) {
                    de.ext.put(newKey, newValue) // inserts or updates
                    updated = true
                }
            }
            for (oldKey in de.ext.keySet()) {
                if (!rowMap.get(oldKey)) {
                    de.ext.remove(oldKey)
                    updated = true
                }
            }
        }
        return  updated
    }
    /**
     *
     * @param dataModel
     * @param dt
     * @param rowMap
     * @param metadataKeys
     * @param deCode
     * @param deName
     * @param deDescription
     * @return
     */
    DataElement newDataElement(DataModel dataModel, DataType dt, Map<String, String> rowMap, List<String> metadataKeys, String deCode, String deName, String deDescription) {
        def params = paramsAddCodeNameDesc([dataModel: dataModel, dataType: dt], deCode, deName, deDescription)
        DataElement de = new DataElement(params).save()
        addMetadata(de, rowMap, metadataKeys)
        return de
    }
    /**
     *
     * @param dataModel
     * @param headersMap
     * @param rowMap
     * @param dt
     * @return
     */
    DataElement processDataElement(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, DataType dt) {
        Boolean updated = false
        List<String> metadataKeys = headersMap['metadata']
        String deCode = tryHeader(ConfigHeadersMap.dataElementCode, headersMap, rowMap)
        String deName = tryHeader(ConfigHeadersMap.dataElementName, headersMap, rowMap)
        if(deName == null){
            return null  //Best go no further and return a null object
        }
        String deDescription = tryHeader(ConfigHeadersMap.dataElementDescription, headersMap, rowMap)
        //see if a data element exists with this model catalogue id
        DataElement de

        if (deCode && (de = DataElement.findByModelCatalogueIdAndDataModel(deCode, dataModel))) {
            String oldDeName = de.getName()
            if (deName != oldDeName) {
                de.setName(deName)
                updated = true
            }

        } else if (deName && (de = DataElement.findByNameAndDataModel(deName, dataModel))) { //if not see if a data element exists in this model with the same name
            String oldDeCatId = de.getModelCatalogueId()
            if (deCode != oldDeCatId) { // have a new DE - will not happen if no code (cat id)
                de = newDataElement(dataModel, dt, rowMap, metadataKeys, deCode, deName, deDescription)
                updated = true
            }
        }
        if (de) {
            DataType oldDeDataType = de.getDataType()
            String oldDeDescription = de.getDescription()
            if (deDescription != oldDeDescription) {
                de.setDescription(deDescription)
                updated = true
            }
            if (dt != oldDeDataType) {
                de.setDataType(dt)
                updated = true
            }
            if (updateMetadata(de, headersMap, rowMap, metadataKeys)) {
                updated = true
            }
        } else { //if no de then create one
            de = newDataElement(dataModel, dt, rowMap, metadataKeys, deCode, deName, deDescription)
            updated = true
        }
        if (updated) {
            de.save()
        }
        return de
    }
    /**
     *
     * @param rowMaps
     * @param headersMap
     * @param dataModelName
     * @return
     */
    def processRowMaps(List<Map<String, String>> rowMaps, Map<String, Object> headersMap, String dataModelName = this.dataModelName) {
        int count = 0
        int batchSize = 50
        DataModel dataModel = processDataModel(dataModelName)
        for (Map<String, String> rowMap in rowMaps) {
            println("creating row" + count)
            DataClass dc = processDataClass(dataModel, headersMap, rowMap)
            MeasurementUnit mu = processMeasurementUnit(dataModel, headersMap, rowMap)
            DataType dt = processDataType(dataModel, headersMap, rowMap, mu)
            DataElement de = processDataElement(dataModel, headersMap, rowMap, dt)
            if(de!=null){
                de.addToContainedIn(dc)
            }
            if (++count % batchSize == 0) {
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
    String buildModelFromWorkbookSheet(Map<String, String> headersMap, Workbook workbook, int index = 0) {
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
            Sheet sheet = workbook.getSheetAt(index)
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
    String buildModel(Workbook workbook) {
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
        Sheet sheet = workbook.getSheetAt(0)
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
}
