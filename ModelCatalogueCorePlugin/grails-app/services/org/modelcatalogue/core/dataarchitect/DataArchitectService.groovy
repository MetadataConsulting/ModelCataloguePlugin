package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists

class DataArchitectService {

    def modelCatalogueSearchService, relationshipService

    ListWithTotal<DataElement> uninstantiatedDataElements(Map params){
        Lists.fromCriteria(params, DataElement) {
            'in'('status', PublishedElementStatus.DRAFT, PublishedElementStatus.PENDING, PublishedElementStatus.UPDATED, PublishedElementStatus.FINALIZED)
            isNull 'valueDomain'
        }
    }

    ListWithTotal<ValueDomain> incompleteValueDomains(Map params){
        Lists.fromCriteria(params, ValueDomain) {
            isNull 'dataType'
        }
    }

    ListWithTotal<DataElement> metadataKeyCheck(Map params){
        def searchParams = getParams(params)

        return Lists.fromQuery(searchParams, DataElement, "SELECT DISTINCT a FROM DataElement a " +
                "WHERE a.extensions IS EMPTY " +
                "OR a NOT IN " +
                "(SELECT a2 from DataElement a2 " +
                "JOIN a2.extensions e2 " +
                "WHERE e2.name = :key)" ,"SELECT COUNT(a) FROM DataElement a " +
                "WHERE a.extensions IS EMPTY " +
                "OR a NOT IN " +
                "(SELECT a2 from DataElement a2 " +
                "JOIN a2.extensions e2 " +
                "WHERE e2.name = :key)", [key: searchParams.key])
    }

    ListWithTotal<DataElement> findRelationsByMetadataKeys(String keyOne, String keyTwo, Map params){

        ListAndCount results = new ListAndCount()
        def searchParams = getParams(params)
        def synonymDataElements = []
        //FIXME the relationship type should be configurable
        def relType = RelationshipType.findByName("relatedTo")

        def key1Elements = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                "inner join a.extensions e " +
                "WHERE e.name = ?)", [keyOne])//, [max: searchParams.max, offset: searchParams.offset])

        key1Elements.eachWithIndex { DataElement dataElement, int dataElementIndex ->
            def extensionName = dataElement.extensions[dataElement.extensions.findIndexOf {
                it.name == keyOne
            }].extensionValue
            def key2Elements = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                    "inner join a.extensions e " +
                    "WHERE e.name = ? and e.extensionValue = ?) ", [keyTwo, extensionName], [max: searchParams.max, offset: searchParams.offset])

            // Create a Map
            key2Elements.each {
                //FIXME the relationship type needs to be configurable
                def relationship = new Relationship(source: dataElement, destination: it, relationshipType: relType)
                synonymDataElements << relationship
            }
        }

        results.list = synonymDataElements
        results.count = synonymDataElements.size()
        return results
    }

    def actionRelationshipList(ArrayList<Relationship> list){
        list.each { relationship ->
            relationship.save()
        }
    }

    private static Map getParams(Map params){
        def searchParams = [:]
        if(params.key){searchParams.put("key" , params.key)}
        if(params.keyOne){searchParams.put("keyOne" , params.keyOne)}
        if(params.keyTwo){searchParams.put("keyTwo" , params.keyTwo)}
        if(params.max){searchParams.put("max" , params.max.toInteger())}else{searchParams.put("max" , 10)}
       // if(params.sort){searchParams.put("sort" , "$params.sort")}else{searchParams.put("sort" , "name")}
       // if(params.order){searchParams.put("order" , params.order.toLowerCase())}else{searchParams.put("order" , "asc")}
        if(params.offset){searchParams.put("offset" , params.offset.toInteger())}else{searchParams.put("offset" , 0)}
        return searchParams
    }

    List<Object> matchDataElementsWithCSVHeaders(String[] headers) {
        List<Object> elements = []

        for (String header in headers) {
            def element = DataElement.findByNameIlikeAndStatus(header, PublishedElementStatus.FINALIZED)
            if (!element) {
                element = DataElement.findByModelCatalogueId(header)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = DataElement.findByNameIlikeAndStatus(header.replace('_', ' '), PublishedElementStatus.FINALIZED)
                } else {
                    element = DataElement.findByNameIlikeAndStatus(header.replace(' ', '_'), PublishedElementStatus.FINALIZED)
                }
            }
            if (!element) {
                element = DataElement.findByNameIlikeAndStatus(header, PublishedElementStatus.DRAFT)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = DataElement.findByNameIlikeAndStatus(header.replace('_', ' '), PublishedElementStatus.DRAFT)
                } else {
                    element = DataElement.findByNameIlikeAndStatus(header.replace(' ', '_'), PublishedElementStatus.DRAFT)
                }
            }
            if (element) {
                elements << element
            } else {
                def searchResult = modelCatalogueSearchService.search(DataElement, [search: header])
                // only if we have single hit
                if (searchResult.total == 1) {
                    elements << searchResult.searchResults[0]
                } else {
                    elements << header
                }
            }
        }

        elements
    }

    void transformData(CsvTransformation transformation, Reader input, Writer output) {
        if (!transformation) throw new IllegalArgumentException("Transformation missing!")
        if (!transformation.columnDefinitions) throw new IllegalArgumentException("Nothing to transform. Column definitions missing!")

        Character separatorChar = transformation.separator?.charAt(0)

        CSVReader reader = new CSVReader(input, separatorChar)

        try {
            List<Object> dataElementsFromHeaders = matchDataElementsWithCSVHeaders(reader.readNext())

            List<Map<String, Object>> outputMappings = new ArrayList(dataElementsFromHeaders.size())

            for (ColumnTransformationDefinition definition in transformation.columnDefinitions) {
                def mapping = [:]
                mapping.header = definition.header
                mapping.index  = dataElementsFromHeaders.indexOf(definition.source)

                if (mapping.index == -1) {
                    mapping.mapping = Mapping.DIRECT_MAPPING
                } else {
                    mapping.mapping = findDomainMapping(definition.source, definition.destination)
                }
                outputMappings << mapping
            }

            CSVWriter writer = new CSVWriter(output, separatorChar)

            try {
                writer.writeNext(outputMappings.collect { it.header } as String[])

                String[] line = reader.readNext()
                while (line) {
                    List<String> transformed = new ArrayList<String>(line.size())

                    for (Map<String, Object> outputMapping in outputMappings) {
                        transformed << outputMapping.mapping.map(line[outputMapping.index])
                    }

                    writer.writeNext(transformed as String[])

                    line = reader.readNext()
                }
            } finally {
                writer.flush()
                writer.close()
            }
        } finally {
            reader.close()
        }

    }

    /**
     * Finds mapping between selected data elements or Mapping#DIRECT_MAPPING.
     * @param source source data element
     * @param destination destination data element
     * @return mapping if exists between data elements's value domains or direct mapping
     */
    Mapping findDomainMapping(DataElement source, DataElement destination) {
        if (!source?.valueDomain || !destination?.valueDomain) {
            return Mapping.DIRECT_MAPPING
        }
        Mapping mapping = Mapping.findBySourceAndDestination(source.valueDomain, destination.valueDomain)
        if (mapping) {
            return mapping
        }
        return Mapping.DIRECT_MAPPING
    }
}
