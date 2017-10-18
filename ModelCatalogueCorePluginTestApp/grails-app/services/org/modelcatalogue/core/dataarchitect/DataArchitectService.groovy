package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.hibernate.Criteria;
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.util.ElasticMatchResult
import org.modelcatalogue.core.util.FriendlyErrors
import org.modelcatalogue.core.util.MatchResult
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
    DataModelGormService dataModelGormService

    //commented out the functions that are no longer relevant or don't work
    //TODO: finish these functions

    private Map<String,Runnable> suggestions = [
//        'Inline Models': this.&generateInlineModel,
        'Enum Duplicates and Synonyms': this.&generatePossibleEnumDuplicatesAndSynonyms,
        'Data Element Exact Match':this.&generateDataElementSuggestionsExact,
        'Data Element Fuzzy Match':this.&generateDataElementSuggestionsFuzzy,
//        'Data Element and Type Exact Match':this.&generateDataElementAndTypeSuggestionsExact,
//        'Data Element and Type Fuzzy Match':this.&generateDataElementAndTypeSuggestionsFuzzy

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

    def generateSuggestions(String suggestion = null, String dataModel1ID, String dataModel2ID, String minScore = 10, modelCatalogueSecurityService = modelCatalogueSecurityService) {

        def execute = { String label, Runnable cl ->
            log.info "Creating suggestions '$label'"
            cl.run()
            log.info "Suggestions '$label' created"
        }

        Runnable runnable = new Runnable() {
            void run() {
                switch (suggestion) {
                    case 'Enum Duplicates and Synonyms':
                        generatePossibleEnumDuplicatesAndSynonyms(dataModel1ID, dataModel2ID)
                        break
                    case 'Data Element Fuzzy Match':
                        generateDataElementSuggestionsFuzzy(dataModel1ID, dataModel2ID, minScore)
                        break
                    case 'Data Element Exact Match':
                        generateDataElementSuggestionsExact(dataModel1ID, dataModel2ID)
                        break
                    default:
                        generateDataElementSuggestionsExact(dataModel1ID, dataModel2ID)
                        break
                }
            }
        }

        if (suggestion) {
            execute suggestion, runnable
        }else{
            log.warn("Trying to run unknown suggestion '$suggestion'")
            return
        }

    }

    def deleteSuggestions() {

        def execute = { Runnable cl ->
            log.info "Deleting suggestions"
            cl.run()
            log.info "Suggestions deleted"
        }

        def batchList = Batch.list()

        Runnable runnable = new Runnable() {
            void run() {
                batchList.each{ Batch btch ->
                    btch.delete()
                }
            }
        }

        execute runnable
    }

    private void generateInlineModel() {
        Batch.findAllByNameIlike("Inline Data Class '%'").each reset
        elementService.findClassesToBeInlined().each { sourceId, destId ->
            DataClass dataClass = DataClass.get(sourceId)
            Batch batch = Batch.findOrSaveByName("Inline Data Class '$dataClass.name'")
            batch.description = """Data Class '$dataClass.name' was created from XML Schema element but it is actually used only in one place an can be replaced by its type"""
            Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.DataClass:$sourceId", destination: "gorm://org.modelcatalogue.core.DataClass:$destId"
            if (action.hasErrors()) {
                log.error(FriendlyErrors.printErrors("Error generating merge data class action", action.errors))
            }
            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generateMergeClasses() {

        def duplicateClassesSuggestions = elementService.findDuplicateClassesSuggestions()

        Batch.findAllByNameIlike("Create Synonyms for Data Class '%'").each reset

        duplicateClassesSuggestions.each { destId, sources ->
            DataClass model = DataClass.get(destId)
            Batch batch = Batch.findOrSaveByName("Create Synonyms for Data Class '$model.name'")
            RelationshipType type = RelationshipType.readByName("relatedTo")

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
        duplicateClassesSuggestions.each { destId, sources ->
            DataClass dataClass = DataClass.get(destId)
            Batch batch = Batch.findOrSaveByName("Merge Data Class '$dataClass.name'")
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

    private void generatePossibleEnumDuplicatesAndSynonyms(String dataModelAID, String dataModelBID){
        DataModel dataModelA = dataModelGormService.read(dataModelAID as Long)
        DataModel dataModelB = dataModelGormService.read(dataModelBID as Long)
        Batch.findAllByNameIlike("Suggested DataType Synonyms for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'").each reset
        Batch batch = Batch.findOrSaveByName("Generating Suggested DataType Synonyms for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'")
        def matchingDataElements = elementService.findDuplicateEnumerationsSuggestions(dataModelA.id, dataModelB.id)
        batch.name = "Processing Suggested DataType Synonyms for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'"
        batch.save()
        matchingDataElements.each { first, other ->
            RelationshipType type = RelationshipType.readByName("relatedTo")
            def matchScore = 100
            other.each { otherId ->
                Map<String, String> params = new HashMap<String,String>()
                params.put("""source""","""gorm://org.modelcatalogue.core.DataType:$otherId""")
                params.put("""destination""","""gorm://org.modelcatalogue.core.DataType:$first""")
                params.put("""type""","""gorm://org.modelcatalogue.core.RelationshipType:$type.id""")
                params.put("""matchScore""","""$matchScore""")
                params.put("""matchOn""","""TypeEnums""")
                Action action
                action = actionService.create(params, batch, CreateMatch)
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }
        batch.name = "Suggested DataType Synonyms for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'"
        batch.save()

    }
    /**
     * generateDataElementSuggestionsExact
     *
     */
    private void generateDataElementSuggestionsExact(String dataModelAID, String dataModelBID){
        DataModel dataModelA = dataModelGormService.read(dataModelAID as Long)
        DataModel dataModelB = dataModelGormService.read(dataModelBID as Long)
        Batch.findAllByNameIlike("Suggested DataElement Exact Matches for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'").each reset
        Batch batch = Batch.findOrSaveByName("Generating Suggested DataElement Exact Matches for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'")
        def matchingDataElements = elementService.findDuplicateDataElementSuggestions(dataModelA,dataModelB)
        batch.name = "Processing DataElement Exact Matches for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'"
        batch.save()
        def matchScore = 100
        matchingDataElements.each { first, other ->
            RelationshipType type = RelationshipType.readByName("relatedTo")
            other.each { otherId ->
                Map<String, String> params = new HashMap<String,String>()
                params.put("""source""","""gorm://org.modelcatalogue.core.DataElement:$otherId""")
                params.put("""destination""","""gorm://org.modelcatalogue.core.DataElement:$first""")
                params.put("""type""","""gorm://org.modelcatalogue.core.RelationshipType:$type.id""")
                params.put("""matchScore""","""$matchScore""")
                params.put("""matchOn""","""ElementName""")
                Action action
                action = actionService.create(params, batch, CreateMatch)
                if (action.hasErrors()) {
                    log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }
        batch.name = "Suggested DataElement Exact Matches for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'"
        batch.save()

    }



    /**
     * generateDataElementSuggestionsFuzzy
     *
     */

    private void generateDataElementSuggestionsFuzzy(String dataModelAID, String dataModelBID, String minScore){
        DataModel dataModelA = dataModelGormService.read(dataModelAID as Long)
        DataModel dataModelB = dataModelGormService.read(dataModelBID as Long)
        Batch.findAllByNameIlike("Suggested Fuzzy DataElement Relations for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'").each reset
        Batch batch = Batch.findOrSaveByName("Generating suggested Fuzzy DataElement Relations for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'")
        def score
        try{ score  = Long.parseLong( minScore ) }catch(Exception e){ score  = 10}
        def matchingDataElements = elementService.findFuzzyDataElementSuggestions(dataModelA,dataModelB, score)
        batch.name = "Processing suggested Fuzzy DataElement Relations for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'"
        batch.save()
        matchingDataElements.each { ElasticMatchResult match ->
            RelationshipType type = RelationshipType.readByName("relatedTo")
            Map<String, String> params = new HashMap<String, String>()
            params.put("""source""", """gorm://org.modelcatalogue.core.DataElement:$match.dataElementA.id""")
            params.put("""destination""", """gorm://org.modelcatalogue.core.DataElement:$match.dataElementB.id""")
            params.put("""type""", """gorm://org.modelcatalogue.core.RelationshipType:$type.id""")
            params.put("""matchScore""", """${(match.matchScore) ? match.matchScore : 0}""")
            params.put("""matchOn""", """ElementName""")
            params.put("""message""", """$match.message""")
            Action action
            action = actionService.create(params, batch, CreateMatch)
            if (action.hasErrors()) {
                log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
            }
            batch.archived = false
            batch.save()
        }
        batch.name = "Suggested Fuzzy DataElement Relations for '${dataModelA.name} (${dataModelA.dataModelSemanticVersion})' and '${dataModelB.name} (${dataModelB.dataModelSemanticVersion})'"
        batch.save()

        }

    /**
     * generateDataElementAndTypeSuggestionsExact
     *
     */
    private void generateDataElementAndTypeSuggestionsExact(){

    }


    /**
     * generateDataElementAndTypeSuggestionsFuzzy
     *
     */
    private void generateDataElementAndTypeSuggestionsFuzzy(){

    }
}

