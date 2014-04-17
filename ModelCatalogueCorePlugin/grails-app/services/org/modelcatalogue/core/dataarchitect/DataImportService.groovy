package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ValueDomain

@Transactional
class DataImportService {

    private static final QUOTED_CHARS = ["\\": "&#92;", ":" : "&#58;", "|" : "&#124;", "%" : "&#37;"]
    //the import script accepts and array of headers these should include the following:
    //Data Item Name, Data Item Description, Parent Section, Section, Measurement Unit, Data type
    //these will allow the import script to identify the rows

    def importData(ArrayList headers, ArrayList rows, String conceptualDomain, String conceptualDomainDescription, ArrayList parentModels) {
        //get indexes of the appropriate sections

        def newImport = new Import()

        def dataItemNameIndex = headers.indexOf("Data Item Name")
        def dataItemDescriptionIndex = headers.indexOf("Data Item Description")
        def parentSecIndex = headers.indexOf("Parent Model")
        def sectionIndex = headers.indexOf("Model")
        def unitsIndex = headers.indexOf("Measurement Unit")
        def dataTypeIndex = headers.indexOf("Data type")
        def metadataStartIndex = headers.indexOf("Metadata") + 1
        def metadataEndIndex = headers.size() - 1
        def dataElementCodeIndex = headers.indexOf("Data Element Code")
        def parentModelCodeIndex = headers.indexOf("Parent Model Code")
        def modelCodeIndex = headers.indexOf("Model Code")
        def elements = []
        if (dataItemNameIndex == -1) throw new Exception("Can not find 'Data Item Name' column")
        //iterate through the rows and import each line
        rows.eachWithIndex { def row, int i ->

            ImportRow importRow = new ImportRow()

            importRow.dataElementName = (dataItemNameIndex)?row[dataItemNameIndex]:null
            importRow.parentModelName = (parentSecIndex)?row[parentSecIndex]:null
            importRow.parentModelCode = (parentModelCodeIndex)?row[parentModelCodeIndex]:null
            importRow.containingModelName = (sectionIndex)?row[sectionIndex]:null
            importRow.containingModelCode = (dataElementCodeIndex)?row[dataElementCodeIndex]:null
            importRow.dataType =   (dataTypeIndex)?row[dataTypeIndex]:null
            importRow.dataElementDescription =   (dataItemDescriptionIndex)?row[dataItemDescriptionIndex]:null
            importRow.measurementUnitName =   (unitsIndex)?row[unitsIndex]:null
            importRow.conceptualDomainName = conceptualDomain
            importRow.conceptualDomainDescription = conceptualDomainDescription

            def counter = metadataStartIndex
            def metadataColumns = [:]
            while (counter <= metadataEndIndex) {
                metadataColumns.put(headers[counter], row[counter])
                counter++
            }
            importRow.metadata =   (metadataColumns)?metadataColumns:null

            if(parentModel){parentModels.add(parentModel)}
            if(model){parentModels.add(model)}

            validateRow(newImport, conceptualDomain, conceptualDomainDescription, parentModels, name, valueDomainInfo, description, metadataColumns)
            //importLine(conceptualDomain, conceptualDomainDescription, parentModels, name, valueDomainInfo, description, metadataColumns)
        }

        return newImport
    }


    @Transactional
    protected void validateRow(Import newImport, String conceptualDomain, String conceptualDomainDescription, ArrayList categories, String name, valueDomainInfo, String description, Map metadataColumns) {

        if (name.isEmpty()) {
            messages.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        } else if (conceptualDomain.isEmpty()) {
            valid = false
            messages.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        } else if (categories.isEmpty()) {
            valid = false
            messages.put(metadataColumns.get("NHIC_Identifier"), "no models specified for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }


        def dataType
        def cd = findOrCreateConceptualDomain(conceptualDomain, conceptualDomainDescription)
        def models = importModels(categories, cd)

        if (description) { description = description.take(2000) }
        if (valueDomainInfo != null && !valueDomainInfo.isEmpty()) { dataType = findOrCreateDataType(name, [valueDomainInfo]) }

        def valid = true

        if (valid) {
            def de, vd

            de = createDataElement([name: name, description: description], metadataColumns, models)
            if (valueDomainInfo != null && dataType != null) {vd = createValueDomain(name, valueDomainInfo,dataType, cd, de)}
            println "importing: " + name + categories.last()

        } else {

            println("invalid data item")

        }
    }



    @Transactional
    protected void importLine(conceptualDomain, conceptualDomainDescription, categories, name, valueDomainInfo, description, metadataColumns) {
        def dataType
        def cd = findOrCreateConceptualDomain(conceptualDomain, conceptualDomainDescription)
        def models = importModels(categories, cd)

        if (description) { description = description.take(2000) }
        if (valueDomainInfo != null && !valueDomainInfo.isEmpty()) { dataType = findOrCreateDataType(name, [valueDomainInfo]) }

        def valid = true

        if (name.isEmpty()) {
            valid = false
            messages.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        } else if (conceptualDomain.isEmpty()) {
            valid = false
            messages.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        } else if (categories.isEmpty()) {
            valid = false
            messages.put(metadataColumns.get("NHIC_Identifier"), "no models specified for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
        }

        if (valid) {
            def de, vd

            de = createDataElement([name: name, description: description], metadataColumns, models)
            if (valueDomainInfo != null && dataType != null) {vd = createValueDomain(name, valueDomainInfo,dataType, cd, de)}
            println "importing: " + name + categories.last()

        } else {

            println("invalid data item")

        }
    }



    @Transactional
    protected importModels(categories, ConceptualDomain conceptualDomain) {
        //categories look something like ["Animals", "Mammals", "Dogs"]
        //where animal is a parent of mammals which is a parent of dogs......

        def modelToReturn
        categories.inject { parentName, childName ->
            parentName = parentName.trim()
            //if there isn't a name for the child return the parentName
            if (childName.equals("") || childName == null) {
                return parentName;
            } else {
                childName = childName.trim()
            }

            //def matches = Model.findAllWhere("name" : name, "parentName" : models)
            //see if there are any models with this name
            Model match
            def namedChildren = Model.findAllWhere("name": childName)

            //see if there are any models with this name that have the same parentName
            if (namedChildren.size() > 0) {
                namedChildren.each { Model childModel ->
                    if (childModel.childOf.collect { it.name }.contains(parentName)) {
                        match = childModel
                    }
                }
            }

            //if there isn't a matching model with the same name and parentName
            if (!match) {
                //new Model('name': name, 'parentName': parentName).save()
                Model child
                Model parent

                //create the child model
                child = new Model('name': childName).save()
                child.addToHasContextOf(conceptualDomain)
                modelToReturn = child

                //see if the parent model exists
                parent = Model.findWhere("name": parentName)

                //FIXME we should probably have unique names for models (or codes)
                // or at least within conceptual domains
                // or we need to have a way of choosing the model parent to use
                // at the moment it just uses the first one Model that is returned

                if (!parent) {
                    parent = new Model('name': parentName).save()
                    parent.addToHasContextOf(conceptualDomain)
                }

                child.addToChildOf(parent)
                child.name

                //add the parent child relationship between models
            } else {
                modelToReturn = match
                match.name
            }
        }
        modelToReturn
    }

    @Transactional
    protected findOrCreateDataType(name, dataType) {

        //default data type to return is the string data type
        def dataTypeReturn

        dataType.each { line ->
            String[] lines = line.split("\\r?\\n");

//          if there is more than one line assume that the data type is enumerated and parse enumerations
//          the script accepts enumerations in the format
//          01=theatre and recovery
//          02=recovery only (usrd as a temporary ccu)
//          03=other ward

            if (lines.size() > 0 && lines[] != null) {
                Map enumerations = new HashMap()
                lines.each { enumeratedValues ->
                    def EV = enumeratedValues.split(":")
                    if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                        def key = EV[0]
                        def value = EV[1]
                        if (value.size() > 244) {
                            value = value[0..244]
                        }
                        key = key.trim()
                        value = value.trim()
                        if (value.isEmpty()) {
                            value = "_"
                        }
                        enumerations.put(key, value)
                    }
                }

                if (!enumerations.isEmpty()) {

                    String enumString = enumerations.sort() collect { key, val ->
                        "${this.quote(key)}:${this.quote(val)}"
                    }.join('|')

                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)
                    if (!dataTypeReturn) {
                        dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
                    }

                }
            }
        }

        if(!dataTypeReturn){dataTypeReturn = (DataType.findByNameLike(name))}

        return dataTypeReturn
    }


    @Transactional
    protected ConceptualDomain findOrCreateConceptualDomain(String name, String description) {
        name = name.trim()
        def cd = ConceptualDomain.findByName(name)
        if (!cd) {
            cd = new ConceptualDomain(name: name, description: description).save()
        }
        return cd
    }


    @Transactional
    protected ValueDomain createValueDomain(String name, String description, DataType dataType, ConceptualDomain cd){
        def vd = new ValueDomain(name: name.replaceAll("\\s", "_"),
                dataType: dataType,
                description: description.take(2000)).save(failOnError: true);

        vd.addToIncludedIn(cd)
        return vd
    }


    @Transactional
    protected ValueDomain createValueDomain(String name, String description, DataType dataType, ConceptualDomain cd, DataElement de){
        def vd = new ValueDomain(name: name.replaceAll("\\s", "_"),
                //conceptualDomain: cd,
                dataType: dataType,
                description: description.take(2000)).save(failOnError: true);

        vd.addToIncludedIn(cd)
        de.addToInstantiatedBy(vd)

        return vd
    }

    @Transactional
    protected DataElement createDataElement(Map params){
        def de = new DataElement(params).save()
        return de
    }

    @Transactional
    protected DataElement createDataElement(Map params, Map metadata){
        def de = new DataElement(params).save()

        metadata.each { key, value ->
            if (key) { key = key.take(255)}
            if (value) {value = value.take(255)}
            de.ext.put(key, value)
        }

        return de
    }


    @Transactional
    protected DataElement createDataElement(Map params, Map metadata, Model model){
        def de = new DataElement(params).save()

        metadata.each { key, value ->
            if (key) { key = key.take(255)}
            if (value) {value = value.take(255)}
            de.ext.put(key, value)
        }

        de.addToContainedIn(model)

        return de
    }


    @Transactional
    protected String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

}
