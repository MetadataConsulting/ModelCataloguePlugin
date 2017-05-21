package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.hibernate.Criteria;
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.lists.ListWithTotal
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.SecuredRuleExecutor

class DataArchitectService {

    static transactional = false

    def modelCatalogueSecurityService
    def modelCatalogueSearchService
    def relationshipService
    def elementService
    def actionService
    def dataModelService
    def sessionFactory


    private Map<String,Runnable> suggestions = [

        'Inline Models': this.&generateInlineModel,
        'Merge Models': this.&generateMergeModels,
        'Enum Duplicates and Synonyms': this.&generatePossibleEnumDuplicatesAndSynonyms,
        'Data Element Exact Match':this.&generateDataElementSuggestionsExact,
        'Data Element Fuzzy Match':this.&generateDataElementSuggestionsFuzzy,
        'Data Element and Type Exact Match':this.&generateDataElementAndTypeSuggestionsExact,
        'Data Element and Type Fuzzy Match':this.&generateDataElementAndTypeSuggestionsFuzzy

    ]

    Set<String> getSuggestionsNames() {
        suggestions.keySet().sort()
    }

    ListWithTotal<DataElement> metadataKeyCheck(Map params){
        def searchParams = getParams(params)

        //language=HQL
        return Lists.fromQuery(searchParams, DataElement, """
            SELECT DISTINCT a FROM DataElement a
            WHERE a.extensions IS EMPTY
            OR a NOT IN (SELECT a2 from DataElement a2 JOIN a2.extensions e2 WHERE e2.name = :key)
        """ , """
            SELECT COUNT(a) FROM DataElement a
            WHERE a.extensions IS EMPTY
            OR a NOT IN (SELECT a2 from DataElement a2 JOIN a2.extensions e2 WHERE e2.name = :key)
        """, [key: searchParams.key])
    }

    ListWithTotal<Relationship> findRelationsByMetadataKeys(String keyOne, String keyTwo, Map params){

        return Lists.lazy(params, Relationship) {
            def searchParams = getParams(params)
            List<Relationship> synonymDataElements = []
            //FIXME the relationship type should be configurable
            def relType = RelationshipType.readByName("relatedTo")

            def key1Elements = DataElement.executeQuery("SELECT DISTINCT a FROM DataElement a " +
                    "inner join a.extensions e " +
                    "WHERE e.name = ?)", [keyOne])//, [max: searchParams.max, offset: searchParams.offset])

            key1Elements.eachWithIndex { DataElement dataElement, Integer dataElementIndex ->
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
            synonymDataElements
        }

    }

    def actionRelationshipList(Collection<Relationship> list){
        list.each { relationship ->
            relationship.save(flush: true)
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
        matchCSVHeaders(DataElement, headers)
    }

    private List<Object> matchCSVHeaders(Class<? extends CatalogueElement> resource, String[] headers) {
        List<Object> elements = []

        for (String header in headers) {
            def element = resource.findByNameIlikeAndStatus(header, ElementStatus.FINALIZED)
            if (!element) {
                element = elementService.findByModelCatalogueId(CatalogueElement, header)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = resource.findByNameIlikeAndStatus(header.replace('_', ' '), ElementStatus.FINALIZED)
                } else {
                    element = resource.findByNameIlikeAndStatus(header.replace(' ', '_'), ElementStatus.FINALIZED)
                }
            }
            if (!element) {
                element = resource.findByNameIlikeAndStatus(header, ElementStatus.DRAFT)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = resource.findByNameIlikeAndStatus(header.replace('_', ' '), ElementStatus.DRAFT)
                } else {
                    element = resource.findByNameIlikeAndStatus(header.replace(' ', '_'), ElementStatus.DRAFT)
                }
            }
            if (element) {
                elements << element
            } else {
                def searchResult = modelCatalogueSearchService.search(resource, [search: header, max: 1])
                // expect that the first hit is the best hit
                if (searchResult.total >= 1L) {
                    elements << searchResult.items[0]
                } else {
                    elements << header
                }
            }
        }

        elements
    }

    List<Object> matchModelsWithCSVHeaders(String[] headers) {
        matchCSVHeaders(DataClass, headers)
    }

    void transformData(Map<String, String> options = [separator: ';'],CsvTransformation transformation, Reader input, Writer output) {
        if (!transformation) throw new IllegalArgumentException("Transformation missing!")
        if (!transformation.columnDefinitions) throw new IllegalArgumentException("Nothing to transform. Column definitions missing!")

        Character separatorChar = options.separator?.charAt(0)

        CSVReader reader = new CSVReader(input, separatorChar)

        try {
            List<Object> dataElementsFromHeaders = matchDataElementsWithCSVHeaders(reader.readNext())

            List<Map<String, Object>> outputMappings = new ArrayList(dataElementsFromHeaders.size())

            for (ColumnTransformationDefinition definition in transformation.columnDefinitions) {
                def mapping = [:]
                mapping.header = definition.header
                mapping.index  = dataElementsFromHeaders.indexOf(definition.source)

                if (mapping.index == -1) {
                    mapping.mapping = null
                } else {
                    mapping.mapping = findDomainMapper(definition.source, definition.destination)
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
                        String value = outputMapping.index >= 0 ? line[outputMapping.index] : null
                        if (outputMapping.mapping == null) {
                            transformed << value
                        } else {
                            transformed << outputMapping.mapping.execute(x: value)
                        }
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
    SecuredRuleExecutor.ReusableScript findDomainMapper(DataElement source, DataElement destination) {
        if (!source?.dataType || !destination?.dataType) {
            return null
        }
        Mapping mapping = Mapping.findBySourceAndDestination(source.dataType, destination.dataType)
        if (mapping) {
            return mapping.mapper()
        }
        return null
    }

    private static Closure getReset() {
        return { Batch batch ->
            for (Action action in new HashSet<Action>(batch.actions)) {
                if (action.state in [ActionState.FAILED, ActionState.PENDING]) {
                    batch.removeFromActions(action)
                    action.batch = null
                    action.delete(flush: true)
                }
            }
            batch.archived =  true
            batch.save(flush: true)
        }
    }


    void addSuggestion(String label, Closure suggestionGenerator) {
        suggestions[label] = suggestionGenerator
    }

    def generateSuggestions(String suggestion = null) {
        def execute = { String label, Runnable cl ->
            log.info "Creating suggestions '$label'"
            cl.run()
            log.info "Suggestions '$label' created"
        }
        if (!suggestion) {
            suggestions.each execute
        } else {
            Runnable runnable = suggestions[suggestion]
            if (!runnable) {
                log.warn("Trying to run unknown suggestion '$suggestion'")
                return
            }
            execute suggestion, runnable
        }
    }

    def deleteSuggestions() {

        def execute = { String label, Runnable cl ->
            log.info "Deleting suggestions"
            cl.run()
            log.info "Suggestions deleted"
        }

        execute Batch.list().each{  btch ->
            btch.delete()
        }
    }

    private void generateInlineModel() {
        Batch.findAllByNameIlike("Inline Data Class '%'").each reset
        elementService.findModelsToBeInlined().each { sourceId, destId ->
            DataClass model = DataClass.get(sourceId)
            Batch batch = Batch.findOrSaveByName("Inline Data Class '$model.name'")
            batch.description = """Data Class '$model.name' was created from XML Schema element but it is actually used only in one place an can be replaced by its type"""
            Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.DataClass:$sourceId", destination: "gorm://org.modelcatalogue.core.DataClass:$destId"
            if (action.hasErrors()) {
                log.error(FriendlyErrors.printErrors("Error generating merge data class action", action.errors))
            }
            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generateMergeModels() {
        def duplicateModelsSuggestions = elementService.findDuplicateModelsSuggestions()

        Batch.findAllByNameIlike("Create Synonyms for Data Class '%'").each reset
        duplicateModelsSuggestions.each { destId, sources ->
            DataClass model = DataClass.get(destId)
            Batch batch = Batch.findOrSaveByName("Create Synonyms for Data Class '$model.name'")
            RelationshipType type = RelationshipType.readByName("synonym")
            sources.each { srcId ->
                Action action = actionService.create batch, CreateRelationship, source: "gorm://org.modelcatalogue.core.DataClass:$srcId", destination: "gorm://org.modelcatalogue.core.DataClass:$destId", type: "gorm://org.modelcatalogue.core.RelationshipType:$type.id"
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save(flush: true)
        }

        Batch.findAllByNameIlike("Merge Data Class '%'").each reset
        duplicateModelsSuggestions.each { destId, sources ->
            DataClass model = DataClass.get(destId)
            Batch batch = Batch.findOrSaveByName("Merge Data Class '$model.name'")
            sources.each { srcId ->
                Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.DataClass:$srcId", destination: "gorm://org.modelcatalogue.core.DataClass:$destId"
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generatePossibleEnumDuplicatesAndSynonyms() {
        def possibleDuplicateEnums = elementService.findDuplicateEnumerationsSuggestions()

        Batch.findAllByNameIlike("Create Synonyms for Enumerated Type '%'").each reset
        possibleDuplicateEnums.each { first, other ->
            EnumeratedType enumeratedType = EnumeratedType.get(first)
            Batch batch = Batch.findOrSaveByName("Create Synonyms for Enumerated Type '$enumeratedType.name'")
            RelationshipType type = RelationshipType.readByName("synonym")
            other.each { otherId ->
                Action action = actionService.create batch, CreateRelationship, source: "gorm://org.modelcatalogue.core.EnumeratedType:$otherId", destination: "gorm://org.modelcatalogue.core.EnumeratedType:$first", type: "gorm://org.modelcatalogue.core.RelationshipType:$type.id"
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }

        Batch.findAllByNameIlike("Duplicate Candidates of Enumerated Type  '%'").each reset

        possibleDuplicateEnums.each { first, other ->
            EnumeratedType enumeratedType = EnumeratedType.get(first)
            Batch batch = Batch.findOrSaveByName("Duplicate Candidates of Enumerated Type '$enumeratedType.name'")
            other.each { otherId ->
                Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.EnumeratedType:$otherId", destination: "gorm://org.modelcatalogue.core.EnumeratedType:$first"
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating merge model action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }
    }
    /**
     * generateDataElementSuggestionsExact
     *
     */
    private void generateDataElementSuggestionsExact(){
        String dataModelA = "NHS_DATA_DICTIONARY"
        String dataModelB = "COSD"
        def matchingDataElements = elementService.findDuplicateDataElementSuggestions(dataModelA,dataModelB)
        matchingDataElements.each{
            println it
        }
        Batch.findAllByNameIlike("Suggested DataElement Synonyms for '${dataModelA}' and '${dataModelB}'").each reset
        matchingDataElements.each { first, other ->
            DataElement dataElement = DataElement.get(first)
            Batch batch = Batch.findOrSaveByName("Suggested DataElement Synonyms for '${dataModelA}' and '${dataModelB}'")
            RelationshipType type = RelationshipType.readByName("synonym")
            other.each { otherId ->
                Map<String, String> params = new HashMap<String,String>()
                params.put("""source""","""gorm://org.modelcatalogue.core.DataElement:$otherId""")
                params.put("""destination""","""gorm://org.modelcatalogue.core.DataElement:$first""")
                params.put("""type""","""gorm://org.modelcatalogue.core.RelationshipType:$type.id""")
                Action action
                action = actionService.create(params, batch, CreateMatch)
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }

//        Batch.findAllByNameIlike("Duplicate Candidates of Data Model '{dataModelA}'").each reset
//
//        matchingDataElements.each { first, other ->
//            DataElement dataElement = DataElement.get(first)
//            Batch batch = Batch.findOrSaveByName("Duplicate Candidates of Data Model '{dataModelA}'")
//            other.each { otherId ->
//                Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.DataElement:$otherId", destination: "gorm://org.modelcatalogue.core.DataElement:$first"
//                if (action.hasErrors()) {
//                    log.error(FriendlyErrors.printErrors("Error generating merge model action", action.errors))
//                }
//            }
//            batch.archived = false
//            batch.save()
//        }

    }

    /**
     * generateDataElementAndTypeSuggestionsExact
     *
     */
    private void generateDataElementAndTypeSuggestionsExact(){

    }

    /**
     * generateDataElementSuggestionsFuzzy
     *
     */
    private void generateDataElementSuggestionsFuzzy(){
        String dataModelA = "NHS_DATA_DICTIONARY"
        String dataModelB = "COSD"
        Map<Long, Set<Long>> fuzzyMatchingDataElements = elementService.findFuzzyDuplicateDataElementSuggestions(dataModelA,dataModelB )
        fuzzyMatchingDataElements.each{
            println it
        }
        Batch.findAllByNameIlike("Suggested Fuzzy Matches for DataElements in '${dataModelA}' and '${dataModelB}'").each reset
        fuzzyMatchingDataElements.each { first, other ->
            Batch batch = Batch.findOrSaveByName("Suggested Fuzzy Matches for DataElements in '${dataModelA}' and '${dataModelB}'")
            RelationshipType type = RelationshipType.readByName("synonym")
            other.each {
                def dataElementAId = other[0]
                def dataElementBId = other[1]
                Map<String, String> params = new HashMap<String,String>()
                params.put("""source""","""gorm://org.modelcatalogue.core.DataElement:$dataElementAId""")
                params.put("""destination""","""gorm://org.modelcatalogue.core.DataElement:$dataElementBId""")
                params.put("""type""","""gorm://org.modelcatalogue.core.RelationshipType:$type.id""")
                //@todo : This is the match score which still needs to be handled
                params.put("""matchScore""","""gorm://org.modelcatalogue.core.RelationshipType:$first""")
                Action action
                action = actionService.create(params, batch, CreateMatch)
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }

//        Batch.findAllByNameIlike("Duplicate Fuzzy Synonyms for Data Model ${dataModelA}").each reset
//
//        fuzzyMatchingDataElements.each { first, other ->
//            DataElement dataElement = DataElement.get(first)
//            Batch batch = Batch.findOrSaveByName("Duplicate Fuzzy Synonyms for Data Model ${dataModelA}")
//            other.each { otherId ->
//                Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.dataElement:$otherId", destination: "gorm://org.modelcatalogue.core.dataElement:$first"
//                if (action.hasErrors()) {
//                    log.error(FriendlyErrors.printErrors("Error generating merge model action", action.errors))
//                }
//            }
//            batch.archived = false
//            batch.save()
//        }
    }

    /**
     * generateDataElementAndTypeSuggestionsFuzzy
     *
     */
    private void generateDataElementAndTypeSuggestionsFuzzy(){

    }
}
