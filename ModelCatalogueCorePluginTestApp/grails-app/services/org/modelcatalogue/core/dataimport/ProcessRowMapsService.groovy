package org.modelcatalogue.core.dataimport

import org.hibernate.SessionFactory
import org.hibernate.StatelessSession
import org.hibernate.Transaction
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataimport.excel.ConfigHeadersMap
import org.modelcatalogue.core.publishing.DraftContext
import org.modelcatalogue.core.publishing.PublishingContext

class ProcessRowMapsService {
    final String DEFAULT_MU_NAME = null

    ProcessDataRowsDaoService processDataRowsDaoService
    SessionFactory sessionFactory
    def elementService

    DataModel processRowMaps(List<Map<String, String>> rowMaps, Map<String, Object> headersMap, String dataModelName) {
        int count = 0
        StatelessSession session = sessionFactory.openStatelessSession()
        Transaction tx = session.beginTransaction()
        DataModel dataModel = processDataModel(dataModelName, session)
        for (Map<String, String> rowMap in rowMaps) {
            if (rowIsValid(headersMap, rowMap)) {
                println("creating row " + count++)
                DataClass dc = processDataClass(dataModel, headersMap, rowMap, session)
                MeasurementUnit mu = processMeasurementUnit(dataModel, headersMap, rowMap, session)
                DataType dt = processDataType(dataModel, headersMap, rowMap, mu, session)
                DataElement de = processDataElement(dataModel, headersMap, rowMap, dt, session)
                addToContainedIn(dataModel, de, dc, session)
            } else {
                println("ignoring row " + count++)
            }
        }
        tx.commit()
        dataModel
    }

    DataElement processDataElement(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, DataType dt, StatelessSession session) {
        Boolean updated = false
        List<String> metadataKeys = headersMap['metadata']
        String deCode = tryHeader(ConfigHeadersMap.dataElementCode, headersMap, rowMap)
        String deName = tryHeader(ConfigHeadersMap.dataElementName, headersMap, rowMap)
        String deDescription = tryHeader(ConfigHeadersMap.dataElementDescription, headersMap, rowMap)
        //see if a data element exists with this model catalogue id
        DataElement de

        if (deCode && (de = processDataRowsDaoService.findDataElementByModelCatalogueIdAndDataModel(deCode, dataModel))) {
            String oldDeName = de.getName()
            if (deName != oldDeName) {
                de.setName(deName)
                updated = true
            }

        } else if (deName && (de = processDataRowsDaoService.findDataElementByNameAndDataModel(deName, dataModel))) { //if not see if a data element exists in this model with the same name
            String oldDeCatId = de.getModelCatalogueId()
            if (deCode != oldDeCatId) { // have a new DE - will not happen if no code (cat id)
                return newDataElement(dataModel, dt, rowMap, metadataKeys, deCode, deName, deDescription, session)
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
//            updateMetadata(de, headersMap, rowMap, metadataKeys)
        } else { //if no de then create one
            return newDataElement(dataModel, dt, rowMap, metadataKeys, deCode, deName, deDescription, session)
        }
        if ( updated ) {
            updateCatalogueElement(de, session)
        }
        de
    }

    DataType processDataType(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, MeasurementUnit mu, StatelessSession session) {
        String dtCode = tryHeader(ConfigHeadersMap.dataTypeCode, headersMap, rowMap)
        String dtName = tryHeader(ConfigHeadersMap.dataTypeName, headersMap, rowMap)
        DataType dt

        if ( !dtName ) {
            dtName = 'NoDataType'
        }

        //see if a datatype with the model catalogue id already exists in this model
        if (dtCode && (dt = processDataRowsDaoService.findDataTypeByModelCatalogueIdAndDataModel(dtCode, dataModel))) {
            if ((dtName ?: '') != dt.getName()) {
                dt.setName(dtName)
                updateCatalogueElement(dt, session)
            }
        } else if (dtName && (dt = processDataRowsDaoService.findDataTypeByNameAndDataModel(dtName, dataModel))) { //see if a datatype with this name already exists in this model
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
            insertCatalogueElement(dt, session)
        }
        return dt
    }

    MeasurementUnit processMeasurementUnit(DataModel dataModel, Map<String, Object> headersMap, Map<String, String> rowMap, StatelessSession session) {
        //import the measurement unit for the data type (to be used in the creation of data type if applicable)
        String muCatId = tryHeader(ConfigHeadersMap.measurementUnitCode, headersMap, rowMap)
        String muSymbol = tryHeader(ConfigHeadersMap.measurementUnitSymbol, headersMap, rowMap)
        String muName = tryHeader(ConfigHeadersMap.measurementUnitName, headersMap, rowMap) ?: (muSymbol ?: (muCatId ?: DEFAULT_MU_NAME))

        MeasurementUnit mu

        if (muName == DEFAULT_MU_NAME) { // there is no measurement unit
            return null
        }

        if (muCatId) {
            mu = processDataRowsDaoService.findMeasurementUnitByModelCatalogueIdAndDataModel(muCatId, dataModel)
        } else if (muName) { //see if a datatype with this name already exists in this model
            mu = processDataRowsDaoService.findMeasurementUnitByNameAndDataModel(muName, dataModel)

        } else if (muSymbol) {
            mu = processDataRowsDaoService.findMeasurementUnitBySymbolAndDataModel(muSymbol, dataModel)
        }
        // all this to test
        //if no mu then create one
        if ( !mu ) {
            mu  = new MeasurementUnit()
            mu.setDataModel(dataModel)
            mu.setName(muName)
            if (muCatId) {
                mu.setModelCatalogueId(muCatId)
            }
            if (muSymbol) {
                mu.setSymbol(muSymbol)
            }
            insertCatalogueElement(mu, session)
        } else {
            Boolean updated = false
            if (muCatId != mu.getModelCatalogueId()) {
                mu.setModelCatalogueId(muCatId)
                updated = true
            }
            if (!equalsIgnoreCase(muName, mu.getName())) {
                mu.setName(muName)
                updated = true
            }
            if (!equalsIgnoreCase(muSymbol, mu.getSymbol())) {
                mu.setSymbol(muSymbol)
                updated = true
            }
            if (updated) { // will be false if no updates
                updateCatalogueElement(mu, session)
            }
        }
        return mu
    }

    Boolean equalsIgnoreCase(String s1, String s2) {
        return s1 ? s1.equalsIgnoreCase(s2) : s1 == s2
    }

    DataClass processDataClass(DataModel dataModel,
                               Map<String, Object> headersMap,
                               Map<String, String> rowMap,
                               StatelessSession session) {
        String regEx = headersMap['classSeparator'] ?: "\\."
        //take the class name and split to see if there is a hierarchy
        String dcCode = tryHeader(ConfigHeadersMap.containingDataClassCode, headersMap, rowMap)
        String dcNames = tryHeader(ConfigHeadersMap.containingDataClassName, headersMap, rowMap)
        String dcDescription = tryHeader(ConfigHeadersMap.containingDataClassDescription, headersMap, rowMap)
        String[] dcNameList = dcNames.split(regEx)
        Integer maxDcNameIx = dcNameList.length - 1
        Integer dcNameIx = 0
        DataClass dc, parentDC
        String className = dcNameList[dcNameIx]

        //if "class" separated by . (regEx) create class hierarchy if applicable,
        //if not then populate the parent data class with the appropriate data element
        while (dcNameIx < maxDcNameIx) { // we are just checking names at this point (above the leaf)
            if (!(dc = processDataRowsDaoService.findDataClassByfindByNameAndDataModel(className, dataModel))) {

                dc = new DataClass(name: className, dataModel: dataModel) // any cat id or description will not apply here
                insertCatalogueElement(dc, session)
            }
            // maybe check if the parent link is already in the incomingRelationships before calling addToChildOf?
            if (parentDC) {
                addAsChildTo(dataModel, dc, parentDC, session)
            }

            //last one will be the one that contains the data element
            parentDC = dc
            className = dcNameList[++dcNameIx]
        }
        // now we are processing the actual (leaf) class, so need to check if there is a model catalogue id (dcCode)
        if (dcCode && (dc = processDataRowsDaoService.findDataClassByModelCatalogueIdAndDataModel(className, dataModel))) {

            if (className != dc.getName()) { // yes, check if the name has changed
                dc.setName(className)
                updateCatalogueElement(dc, session)
            }
        } else {
            // see if there is a data class with this name - if so make sure you get the right version i.e. highest version number
            // it will be the latest one - only one of the same name per class and the data model is version specific
            dc = processDataRowsDaoService.findDataClassByfindByNameAndDataModel(className, dataModel)
        }
        if (dc) { // we found a DC, just need to check the description
            if (dcDescription != dc.getDescription()) {
                dc.setDescription(dcDescription)
                updateCatalogueElement(dc, session)
            }
        } else { // need to create one - this time with all the parameters
            // the data class doesn't already exist in the model so create it
            def params = paramsAddCodeNameDesc([dataModel: dataModel], dcCode, className, dcDescription)
            dc = new DataClass(params)
            insertCatalogueElement(dc, session)
        }
        // maybe check if the parent link is already in the incomingRelationships before calling addToChildOf?
        if (parentDC) {
            addAsChildTo(dataModel, dc, parentDC, session)
        }
        dc
    }

    void addAsChildTo(DataModel dm, CatalogueElement child, CatalogueElement parent, StatelessSession session) {
        if ( processDataRowsDaoService.findRelationshipBySourceAndDestinationAndRelationshipType(parent, child, RelationshipType.hierarchyType) ) {
            pr("#${child.getId()} ${child.getName()} already child to #${parent.getId()} ${parent.getName()}")
        } else {
            Relationship rel = new Relationship()
            rel.setDataModel(dm)
            rel.setSource(parent)
            rel.setDestination(child)
            rel.setRelationshipType(RelationshipType.hierarchyType)
            session.insert(rel)
            pr("added as child to rel: ", rel)
        }
    }

    DataModel processDataModel(String dataModelName, StatelessSession session) {
        //see if an open EHR model already exists, if not create one
        //could consider changing this - if there are multiple versions - should make sure we use the latest one.
        DataModel dataModel = processDataRowsDaoService.findDataModelByName(dataModelName)

        if (!dataModel) {
            log.info("Creating new DataModel: ${dataModelName}")
            dataModel = new DataModel(name: dataModelName)
            insertCatalogueElement(dataModel, session)
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

    Boolean rowIsValid(Map<String, Object> headersMap, Map<String, String> rowMap) {
        tryHeader(ConfigHeadersMap.containingDataClassName, headersMap, rowMap) &&
                tryHeader(ConfigHeadersMap.dataElementName, headersMap, rowMap)
    }


    String tryHeader(String internalHeaderName, Map<String, Object> headersMap, Map<String, String> rowMap) {
        rowMap.get(headersMap.get(internalHeaderName)) ?: null
    }

    DataElement newDataElement(DataModel dataModel,
                               DataType dt,
                               Map<String, String> rowMap,
                               List<String> metadataKeys,
                               String deCode,
                               String deName,
                               String deDescription,
                               StatelessSession session) {
        def params = paramsAddCodeNameDesc([dataModel: dataModel, dataType: dt], deCode, deName, deDescription)
        DataElement de = new DataElement(params)
        insertCatalogueElement(de, session)
        addMetadata(de, rowMap, metadataKeys, session)
        de
    }

    void addToContainedIn(DataModel dm,
                          DataElement de,
                          DataClass dc,
                          StatelessSession session) {
        Relationship rel = new Relationship()
        rel.setDataModel(dm)
        rel.setSource(dc)
        rel.setDestination(de)
        rel.setRelationshipType(RelationshipType.containmentType)
        session.insert(rel)
        pr("added to contained in: ", rel)
    }

    void insertCatalogueElement(CatalogueElement ce, StatelessSession session) {
        Date now = new Date()
        ce.setDateCreated(now)
        ce.setLastUpdated(now)
        session.insert(ce)
        pr("inserted ce: ", ce)
    }

    void updateCatalogueElement(CatalogueElement ce, StatelessSession session) {
        ce.setLastUpdated(new Date())
        session.update(ce)
    }

    void insertExtensionValue(ExtensionValue ev, StatelessSession session) {
        ev.setVersion(0)
        session.insert(ev)
    }

    void updateExtensionValue(ExtensionValue ev, StatelessSession session) {
        session.update(ev)
        pr("updated ev: ", ev)
    }

    Boolean addMetadata(DataElement de,
                        Map<String, String> rowMap,
                        List<String> metadataKeys,
                        StatelessSession session) {
        Boolean updated = false
        for (String key in metadataKeys) {
            String keyValue =  rowMap.get(key)
            if (keyValue) {
                ExtensionValue ev = new ExtensionValue()
                ev.setName(key)
                ev.setExtensionValue(keyValue.take(MAX_METADATA_LEN).toString())
                ev.setElement(de)
                insertExtensionValue(ev, session)
                updated = true
            }
        }
        updated
    }

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
        params
    }

    void pr(String msg, Object obj = null) {
        if (obj) {
            println(msg + obj.toString())
        } else {
            println(msg)
        }
        println(msg + obj?.toString() ?: '')
    }
}
