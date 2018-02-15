package org.modelcatalogue.core.dataimport.excel

import grails.util.Holders
import groovy.util.logging.Log
import org.apache.poi.ss.usermodel.*
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.modelcatalogue.core.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.PublishingContext
import org.springframework.context.ApplicationContext

/**
 * This uses an XML file that specifies the mapping from columns to concepts
 */
@Log
class ConfigExcelLoader extends ExcelLoader {
    ElementService elementService
    final String DEFAULT_MU_NAME = null
    final Integer MAX_METADATA_LEN = 2000
    final Integer MAX_NAME_LEN = 255
    final Integer GORM_BATCH_SIZE = 50
    final Integer EXCEL_BATCH_SIZE = 1000
//    AuditService auditService
    private String dataModelName
    private DataModel dataModel = null
    Map<String, Object> headersMap = null
    String classHierarchySeparatorRegEx = "\\."
    String ruleSeparatorRegEx = "\\n"
    Integer headerRow = 1
    Integer dataStartRow = 2

    ConfigExcelLoader(String dataModelName, InputStream xmlInput, ElementService elementService) {
        this.dataModelName = dataModelName
        this.headersMap = this.parseXml(xmlInput)
        this.elementService = elementService
    }
    /**
     * This also processes the tag
     * @param tag
     * @param value
     * @return true if it was a metatag and has been processed
     */
    Boolean isMetaTag(String tag, String value) {
        switch (tag) {
            case 'classHierarchySeparator':
                classHierarchySeparatorRegEx = value
                return true
            case 'ruleSeparator':
                ruleSeparatorRegEx = value
                return true
            case 'headerRow':
                if (value.isInteger()) {
                    headerRow = value.toInteger()
                }
                return true
            case 'dataStartRow':
                if (value.isInteger()) {
                    dataStartRow = value.toInteger()
                }
                return true
            default:
                return false
        }
    }
    /**
     * This iterates through the headersMap XML nodes and builds a Map object with the mappings.
     * It puts all the metadata keys into a list of strings that are inserted into the map with key metadata
     * If there is a node named classHierarchySeparator, the text (which should be a reg ex) is stored in the class variable classHierarchySeparatorRegEx
     * @param xml
     * @return Map
     */
    Map<String, Object> parseHeadersMap(groovy.util.slurpersupport.GPathResult xml) {
        Map<String, Object> hdrMap = [:]
        List<String> metadataKeys = []
        for (groovy.util.slurpersupport.Node n in xml.childNodes()) {
            String nName = n.name()
            String nValue = n.text()
//            log.info(nName + ": '" + nValue + "',")
            if (nName == 'metadata') {
                metadataKeys += nValue
            } else if (isMetaTag(nName, nValue)) {
                // no need to do anything
            } else if (nValue){
                hdrMap[nName] = n.text()
            }
        }
        hdrMap['metadata'] = metadataKeys
        return hdrMap
    }
    /**
     * This iterates through the headersMap XML nodes and builds a Map object with the mappings.
     * It puts all the metadata keys into a list of strings that are inserted into the map with key metadata
     * If there is a node named classHierarchySeparator, the text (which should be a reg ex) is stored in the class variable classHierarchySeparatorRegEx
     * @param xml
     * @return Map
     */
    Map<String, Object> parseHeadersMap(groovy.util.slurpersupport.Node xml) {
        Map<String, Object> hdrMap = [:]
        List<String> metadataKeys = []
        for (groovy.util.slurpersupport.Node n in xml.childNodes()) {
            String nName = n.name()
            String nValue = n.text()
//            log.info(nName + ": '" + nValue + "',")
            if (nName == 'metadata') {
                metadataKeys += nValue
            } else if (isMetaTag(nName, nValue)) {
                // no need to do anything
            } else if (nValue){
                hdrMap[nName] = n.text()
            }
        }
        hdrMap['metadata'] = metadataKeys
        return hdrMap
    }
    /**
     * This parses an XML file that contains a headersMap (maps logical to physical column names)
     * It also assigns the result to a class variable which is used as a default map if none is passed
     * @param xmlInput
     * @return the parsed XML file as a headers map, or null if XML file was not a headersMap
     */
    Map<String, Object> parseXml(groovy.util.slurpersupport.GPathResult xml) {
        Map<String, Object> hdrMap = null
        switch (xml.name()) {
            case 'mcExcelConfig':
                for (groovy.util.slurpersupport.Node n in xml.childNodes()) {
                    String nName = n.name()
                    String nValue = n.text()
//                  log.info(nName + ": '" + nValue + "',")
                    if (nName == 'headersMap') {
                        hdrMap = parseHeadersMap(n)
                    } else if (isMetaTag(nName, nValue)) {
                        // no need to do anything
                    } else {
                        log.info("parseXml: unexpected tag: ${nName} => ${nValue}")
                    }
                }
                break;
            case 'headersMap':
                hdrMap = parseHeadersMap(xml)
                break;
            default:
                hdrMap = null
                break;
        }
        return this.headersMap = hdrMap
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
        dataModel = processDataModel(dataModelName)
        Iterator<Row> rowIt = sheet.rowIterator()
        List<String> headers
        List<Map<String, String>> rowMaps = []
        int rowNum = 1
        while (rowIt.hasNext()) {
//            println("processing row" + counter)
            if(rowNum % EXCEL_BATCH_SIZE == 0 ) {
                processRowMaps(rowMaps, headersMap)
                rowMaps.clear()
            }
            Row row = rowIt.next()
            if (rowNum == headerRow) {
                headers = getRowData(row)
                log.info("Headers are ${headers as String}")
            } else if (rowNum >= dataStartRow) {
                if (!isRowEmpty(row)) {
                    Map<String, String> rowMap = createRowMap(row, headers)
                    rowMaps << rowMap
                }
            } else {
                // we are before the header row or between the header and data rows
                log.info("Ignoring row ${rowNum}: ${row.toString()}")
            }
            ++rowNum
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

    String toName(String name) {
        return name.length() <= MAX_NAME_LEN ? name : name.substring(0, MAX_NAME_LEN)
    }

    void setName(CatalogueElement ce, String name) {
        ce.name = toName(name)
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
        DataModel dm
        List<DataModel> dataModels =  DataModel.findAllByName(dataModelName, [sort: 'versionNumber', order: 'desc'])
        if(dataModels) {
            dm = dataModels.first()
        }

        if (!dm) {
            log.info("Creating new DataModel: ${dataModelName}")
            dm = new DataModel(name: dataModelName).save()
        } else {
            log.info("Found Data Model: ${dataModelName}")
            //if one exists, check to see if it's a draft
            // but if it's finalised create a new version
            if(dm.status != ElementStatus.DRAFT) {
                DraftContext context = DraftContext.userFriendly()
                dm = elementService.createDraftVersion(dm, PublishingContext.nextPatchVersion(dm.semanticVersion), context)
            }
        }
        return dm
    }

    String[] toStringArray(String s) {
        String[] sa = new String[1]
        sa[0] = s
        return sa
    }
    /**
     *
     * @param dataModel
     * @param headersMap
     * @param rowMap
     * @return
     */
    DataClass processDataClass(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap) {
        //take the class name and split to see if there is a hierarchy
        String dcCode = tryHeader(ConfigHeadersMap.containingDataClassCode, headersMap, rowMap)
        String dcNames = tryHeader(ConfigHeadersMap.containingDataClassName, headersMap, rowMap)
        if(dcNames == null){
            return null  //Best go no further and return a null object
        }
        String dcDescription = tryHeader(ConfigHeadersMap.containingDataClassDescription, headersMap, rowMap)

        String[] dcNameList = classHierarchySeparatorRegEx ? dcNames.split(classHierarchySeparatorRegEx) : toStringArray(dcNames)
        Integer maxDcNameIx = dcNameList.length - 1
        Integer dcNameIx = 0
        DataClass dc, parentDC = null
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
        DataType dt = null

        if (!(dtCode || dtName)) { // there is no code or name, so no data type
            return null
        }
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
            log.info("NEW TYPE: ${dt}")
            updated = true
        }
        if (dt && updated) {
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
    Relationship findSource(List<Relationship> rels, CatalogueElement src) {
        if (rels && src) {
            for (Relationship rel in rels) {
                if (rel.source == src) {
                    return rel
                }
            }
        }
        return null
    }
    Boolean containsSource(List<Relationship> rels, CatalogueElement src) {
        Relationship r = findSource(rels, src)
        return (r != null)
    }
    Relationship findSourceName(List<Relationship> rels, String srcName) {
        String srcName2 = toName(srcName)
        if (rels && srcName2) {
            for (Relationship rel in rels) {
                if (rel.source.name == srcName2) {
                    return rel
                }
            }
        }
        return null
    }
    Boolean containsSourceName(List<Relationship> rels, String srcName) {
        Relationship r = findSourceName(rels, srcName)
        return (r != null)
    }
    Relationship findSourceDesc(List<Relationship> rels, String srcDesc) {
        if (rels && srcDesc) {
            for (Relationship rel in rels) {
                if (rel.source.description == srcDesc) {
                    return rel
                }
            }
        }
        return null
    }
    Boolean containsSourceDesc(List<Relationship> rels, String srcDesc) {
        Relationship r = findSourceDesc(rels, srcDesc)
        return (r != null)
    }
    Boolean isMandatory(Map<String, Object> headersMap, Map<String, String> rowMap) {
        String mandStr = tryHeader(ConfigHeadersMap.mandatoryRule, headersMap, rowMap)
        return mandStr == null ? null : ('M' == mandStr)
    }
    void setMandatory(Relationship rel, Boolean mandatory) {
        if (mandatory != null) {
            rel.ext['Usage'] = (mandatory ? 'Required' : 'Optional')
            rel.ext['Min Occurs'] = (mandatory ? '1' : '0')
            rel.ext['Max Occurs'] = '1'
        }
    }
    Relationship createRelationship(DataModel dm, CatalogueElement dest, CatalogueElement src, RelationshipType rt) {
        Relationship rel = new Relationship()
        rel.dataModel = dm
        rel.destination = dest
        rel.source = src
        rel.relationshipType = rt
        dest.addToIncomingRelationships(rel)
        rel.save()
        return rel
    }
    void addToContainedIn(DataModel dm, DataElement de, DataClass dc, Boolean mandatory) {
        List<Relationship> containers = de.getIncomingRelationshipsByType(RelationshipType.containmentType)
        Relationship container = findSource(containers, dc)
        if (container == null) {
            de.addToContainedIn(dc)
            containers = de.getIncomingRelationshipsByType(RelationshipType.containmentType)
            container = findSource(containers, dc)
//            log.info("added to contained in: ", container)
        }
        if (mandatory != null) { // only update if supplied
            setMandatory(container, mandatory)
            container.save()
        }
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
        def params = [dataModel: dataModel, dataType: dt]
        params = paramsAddCodeNameDesc(params, deCode, deName, deDescription)
//        log.info(params.toString())
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
    DataElement processDataElement(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, DataClass dc, DataType dt) {
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

        if (!(deCode || deName)) { // there is no code or name, so no data element
            return null
        }
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
        if (de && updated) {
            de.save()
        }
        if (de && dc) {
            addToContainedIn(dataModel, de, dc, isMandatory(headersMap, rowMap))    // was            de.addToContainedIn(dc)
        }
        return de
    }


    /**
     *
     * @param dataModel
     * @param headersMap
     * @param rowMap
     * @param dc
     * @param de
     * @return
     */
    void processValidationRules(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, DataClass dc, DataElement de) {
//        Boolean updated = false
        String vRules = tryHeader(ConfigHeadersMap.validationRules, headersMap, rowMap)
        List<Relationship> deRules = de?.getIncomingRelationshipsByType(RelationshipType.involvednessType)
        List<Relationship> dcRules = dc?.getIncomingRelationshipsByType(RelationshipType.ruleContextType)
//        log.info("processValidationRules:\n\tdeRules = ${deRules ? deRules.toString() : 'NULL'}\n\tdcRules = ${dcRules ? dcRules.toString() : 'NULL'}")
        if (vRules != null) { // just insert them at the moment
            String[] vRuleList = ruleSeparatorRegEx ? vRules.split(ruleSeparatorRegEx) : toStringArray(vRules)
//            log.info("processValidationRules: vRuleList = ${vRuleList.toString()}")
            //ValidationRule
            for (String vRule in vRuleList) {
                if (vRule) { // ignore any blank lines
                    ValidationRule vr = new ValidationRule()
                    vr.dataModel = dataModel
                    vr.name = toName(vRule)
                    vr.description = vRule
                    vr.save()
                    if (de && !containsSourceDesc(deRules, vRule)) {
                        createRelationship(dataModel, de, vr, RelationshipType.involvednessType)
                    }
                    if (dc && !containsSourceDesc(dcRules, vRule)) {
                        createRelationship(dataModel, dc, vr, RelationshipType.ruleContextType)
                    }
                }
            }
        } else { // do nothing at the moment  - should we delete existing rules??
        }
    }
    /**
     *
     * @param rowMaps
     * @param headersMap
     * @param dataModelName
     * @return
     */
    def processRowMaps(List<Map<String, String>> rowMaps, Map<String, Object> headersMap) {
        int rowNum = 1
        if (rowMaps && headersMap) {
//            DataModel dataModel = processDataModel(dataModelName)

            for (Map<String, String> rowMap in rowMaps) {
                log.info("creating row " + rowNum)
                DataClass dc = processDataClass(dataModel, headersMap, rowMap)
                MeasurementUnit mu = processMeasurementUnit(dataModel, headersMap, rowMap)
                DataType dt = processDataType(dataModel, headersMap, rowMap, mu)
                DataElement de = processDataElement(dataModel, headersMap, rowMap, dc, dt)
                processValidationRules(dataModel, headersMap, rowMap, dc, de)
                if (rowNum++ % GORM_BATCH_SIZE == 0) {
                    cleanGORM()
                }
            }
            cleanGORM()
        } else {
            throw new IllegalArgumentException(headersMap ? "Spreadsheet could not be processed" : "Headers Map could not be processed")
        }
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
        Sheet sheet = workbook.getSheetAt(index)
        List<Map<String,String>> rowMaps = getRowMaps(sheet, headersMap)
        //Iterate through the modelMaps to build new DataModel
        processRowMaps(rowMaps, headersMap)
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
        Sheet sheet = workbook.getSheetAt(0)
        List<Map<String,String>> rowMaps = getRowMaps(sheet, headersMap)
        //Iterate through the modelMaps to build new DataModel
        processRowMaps(rowMaps, headersMap)
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
