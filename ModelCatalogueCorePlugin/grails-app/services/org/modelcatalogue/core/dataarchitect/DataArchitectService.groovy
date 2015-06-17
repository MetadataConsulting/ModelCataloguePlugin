package org.modelcatalogue.core.dataarchitect

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.modelcatalogue.core.*
import org.modelcatalogue.core.actions.*
import org.modelcatalogue.core.util.ListAndCount
import org.modelcatalogue.core.util.ListWithTotal
import org.modelcatalogue.core.util.Lists
import org.modelcatalogue.core.util.SecuredRuleExecutor
import org.modelcatalogue.core.publishing.DraftContext

class DataArchitectService {

    static transactional = false

    def modelCatalogueSecurityService
    def modelCatalogueSearchService
    def relationshipService
    def elementService
    def actionService
    def classificationService

    private Map<String,Runnable> suggestions = [
            'Inline Models': this.&generateInlineModel,
            'Merge Data Elements': this.&generateMergeDataElements,
            'Merge Models': this.&generateMergeModels,
            'Enum Duplicates and Synonyms': this.&generatePossibleEnumDuplicatesAndSynonyms,
            'Rename Data Types and Value Domains': this.&generateRenameDataTypesAndValueDomain,
            'Deep Classification': this.&generateDeepClassify.curry(false),
            'Deep Classification (Unclassified Only)': this.&generateDeepClassify.curry(true)
    ]

    Set<String> getSuggestionsNames() {
        suggestions.keySet().sort()
    }

    ListWithTotal<DataElement> uninstantiatedDataElements(Map params){
        classificationService.classified Lists.fromCriteria(params, DataElement) {
            'in'('status', ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.UPDATED, ElementStatus.FINALIZED)
            isNull 'valueDomain'

        }
    }

    ListWithTotal<ValueDomain> incompleteValueDomains(Map params){
        classificationService.classified Lists.fromCriteria(params, ValueDomain) {
            isNull 'dataType'
        }
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

        ListAndCount<Relationship> results = new ListAndCount<Relationship>()
        def searchParams = getParams(params)
        List<Relationship> synonymDataElements = []
        //FIXME the relationship type should be configurable
        def relType = RelationshipType.readByName("relatedTo")

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
        List<Object> elements = []

        for (String header in headers) {
            def element = DataElement.findByNameIlikeAndStatus(header, ElementStatus.FINALIZED)
            if (!element) {
                element = DataElement.findByModelCatalogueId(header)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = DataElement.findByNameIlikeAndStatus(header.replace('_', ' '), ElementStatus.FINALIZED)
                } else {
                    element = DataElement.findByNameIlikeAndStatus(header.replace(' ', '_'), ElementStatus.FINALIZED)
                }
            }
            if (!element) {
                element = DataElement.findByNameIlikeAndStatus(header, ElementStatus.DRAFT)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = DataElement.findByNameIlikeAndStatus(header.replace('_', ' '), ElementStatus.DRAFT)
                } else {
                    element = DataElement.findByNameIlikeAndStatus(header.replace(' ', '_'), ElementStatus.DRAFT)
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

    List<Object> matchModelsWithCSVHeaders(String[] headers) {
        List<Object> elements = []

        for (String header in headers) {
            def element = Model.findByNameIlikeAndStatus(header, ElementStatus.FINALIZED)
            if (!element) {
                element = Model.findByModelCatalogueId(header)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = Model.findByNameIlikeAndStatus(header.replace('_', ' '), ElementStatus.FINALIZED)
                } else {
                    element = Model.findByNameIlikeAndStatus(header.replace(' ', '_'), ElementStatus.FINALIZED)
                }
            }
            if (!element) {
                element = Model.findByNameIlikeAndStatus(header, ElementStatus.DRAFT)
            }
            if (!element) {
                if (header.contains('_')) {
                    element = Model.findByNameIlikeAndStatus(header.replace('_', ' '), ElementStatus.DRAFT)
                } else {
                    element = Model.findByNameIlikeAndStatus(header.replace(' ', '_'), ElementStatus.DRAFT)
                }
            }
            if (element) {
                elements << element
            } else {
                def searchResult = modelCatalogueSearchService.search(Model, [search: header])
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

    ListWithTotal<ValueDomain> unusedValueDomains(Map params) {
        // TODO: create test
        // TODO: count with classificaitons in use
        Lists.fromQuery params, ValueDomain, """
            from ValueDomain v
            where
                v.id in (select vd.id from ValueDomain vd left join vd.dataElements de group by vd.id having count(de.id) = sum(case when de.status = :archived then 1 else 0 end))
        """, [archived: ElementStatus.DEPRECATED]
    }

    ListWithTotal<ValueDomain> duplicateValueDomains(Map params) {
        // TODO: create test
        // TODO: count with classificaitons in use
        Lists.fromQuery params, ValueDomain, """
            from ValueDomain v
            where
                v.id in (select vd.id from ValueDomain vd left join vd.dataElements de group by vd.id having count(de.id) = sum(case when de.status = :archived then 1 else 0 end))
            and
                v.name in (select vd.name from ValueDomain vd group by vd.name having count(vd.name) > 1)
        """, [archived: ElementStatus.DEPRECATED]
    }

    Map<Long, String> dataTypesNamesSuggestions() {
        // find all data types with duplicite name
        List<DataType> dataTypes = DataType.executeQuery("""
            from DataType d
            where d.name in (
                select d.name
                from DataType d
                where
                    d.name not like '%(in %)'
                and
                    d.latestVersionId = d.id or d.latestVersionId is null
                group by
                    d.name
                having
                    count(d.name) > 1
            )
            order by d.name
        """)

        Map<Long, Set<String>> suggestions = new LinkedHashMap<Long, Set<String>>().withDefault { new TreeSet<String>() }

        for (DataType dataType in dataTypes) {
            List<ValueDomain> domains = ValueDomain.findAllByDataTypeAndName(dataType, dataType.name)
            if (!domains) {
                // value domains have different names
                for (ValueDomain domain in ValueDomain.findAllByDataType(dataType)) {
                    suggestions[dataType.id] << domain.name
                }
            } else {
                // we have to try names of data element
                for (ValueDomain valueDomain in domains) {
                    List<DataElement> elements = DataElement.findAllByValueDomainAndName(valueDomain, dataType.name)
                    if (!elements) {
                        // elements have different names
                        for (DataElement element in DataElement.findAllByValueDomain(valueDomain)) {
                            suggestions[dataType.id] << element.name
                        }
                    } else {
                        // we have to rely on names of the model
                        def results = DataElement.executeQuery """
                            select re.source.name
                            from DataElement de
                                left join de.incomingRelationships re
                            where
                                de.valueDomain = :valueDomain
                            and
                                re.relationshipType = :containment
                            order by de.id
                        """, [containment: RelationshipType.containmentType, valueDomain: valueDomain]

                        for (row in results) {
                            suggestions[dataType.id] << row[0]
                        }
                    }
                }
            }
        }


        Map<Long, String> ret = [:]

        suggestions.each { Long id, Set<String> names ->
            ret[id] = DataType.suggestName(names)
        }

        ret
    }

    /**
     * Finds mapping between selected data elements or Mapping#DIRECT_MAPPING.
     * @param source source data element
     * @param destination destination data element
     * @return mapping if exists between data elements's value domains or direct mapping
     */
    SecuredRuleExecutor.ReusableScript findDomainMapper(DataElement source, DataElement destination) {
        if (!source?.valueDomain || !destination?.valueDomain) {
            return null
        }
        Mapping mapping = Mapping.findBySourceAndDestination(source.valueDomain, destination.valueDomain)
        if (mapping) {
            return mapping.mapper()
        }
        return null
    }

    private Closure getReset() {
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


    private void generateDeepClassify(boolean unclassifiedOnly = false) {
        Batch.findAllByNameIlike("Deep Classify '%'").each reset

        List<ElementStatus> statuses = [ElementStatus.FINALIZED, ElementStatus.DRAFT]

        log.info "Generating deep classification suggestions for models => models/data elements"
        generateDeepClassification(Relationship.executeQuery(unclassifyIfNeeded(unclassifiedOnly,  '''
            select rel.source, destination
            from Relationship rel
            join rel.destination source
            join source.outgoingRelationships outgoing
            join outgoing.destination destination
            where rel.relationshipType = :classificationType
            and outgoing.relationshipType in :inheriting
            and source.status in :statuses
            and destination.status in :statuses
            and destination not in (
                select rel2.destination
                from Relationship rel2
                where rel2.relationshipType = :classificationType
                and (rel2.source.id = rel.source.id or rel2.source.latestVersionId = rel.source.latestVersionId)
            )
        '''), [
            classificationType: RelationshipType.classificationType,
            inheriting: [RelationshipType.hierarchyType, RelationshipType.containmentType],
            statuses: statuses
        ]))


        log.info "Generating deep classification suggestions for data elements => value domains"
        //language=HQL
        generateDeepClassification(Relationship.executeQuery(unclassifyIfNeeded(unclassifiedOnly,  '''
            select rel.source, destination
            from DataElement source
            join source.incomingRelationships rel
            join source.valueDomain destination
            where rel.relationshipType = :classificationType
            and source.status in :statuses
            and destination.status in :statuses
            and destination not in (
                select rel2.destination
                from Relationship rel2
                where rel2.relationshipType = :classificationType
                and (rel2.source.id = rel.source.id or rel2.source.latestVersionId = rel.source.latestVersionId)
            )
        '''), [
                classificationType: RelationshipType.classificationType,
                statuses: statuses
        ]))

        log.info "Generating deep classification suggestions for value domains => data types"
        //language=HQL
        generateDeepClassification(Relationship.executeQuery(unclassifyIfNeeded(unclassifiedOnly, '''
            select rel.source, destination
            from ValueDomain source
            join source.incomingRelationships rel
            join source.dataType destination
            where rel.relationshipType = :classificationType
            and source.status in :statuses
            and destination.status in :statuses
            and destination not in (
                select rel2.destination
                from Relationship rel2
                where rel2.relationshipType = :classificationType
                and (rel2.source.id = rel.source.id or rel2.source.latestVersionId = rel.source.latestVersionId)
            )
        '''), [
                classificationType: RelationshipType.classificationType,
                statuses: statuses
        ]))
    }

    private static String unclassifyIfNeeded(boolean unclassifiedOnly, String hql, String classifier = "and (rel2.source.id = rel.source.id or rel2.source.latestVersionId = rel.source.latestVersionId)") {
        if (!unclassifiedOnly) {
            return hql
        }
        return hql.replace(classifier, '')
    }

    private void generateDeepClassification(result) {
        for (Object[] row in result) {
            Classification classification = row[0] as Classification
            CatalogueElement element = row[1] as CatalogueElement

            Batch batch = Batch.findOrSaveByName("Deep Classify '$classification.name'")

            Action action = actionService.create batch, CreateRelationship, source: AbstractActionRunner.encodeEntity(DraftContext.preferDraft(classification)), destination: AbstractActionRunner.encodeEntity(DraftContext.preferDraft(element)), type: "gorm://org.modelcatalogue.core.RelationshipType:${RelationshipType.classificationType.id}"
            if (action.hasErrors()) {
                log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating deep classification action", action.errors))
            }

            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generateInlineModel() {
        Batch.findAllByNameIlike("Inline Model '%'").each reset
        elementService.findModelsToBeInlined().each { sourceId, destId ->
            Model model = Model.get(sourceId)
            Batch batch = Batch.findOrSaveByName("Inline Model '$model.name'")
            batch.description = """Model '$model.name' was created from XML Schema element but it is actually used only in one place an can be replaced by its type"""
            Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.Model:$sourceId", destination: "gorm://org.modelcatalogue.core.Model:$destId"
            if (action.hasErrors()) {
                log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating merge model action", action.errors))
            }
            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generateMergeModels() {
        def duplicateModelsSuggestions = elementService.findDuplicateModelsSuggestions()

        Batch.findAllByNameIlike("Create Synonyms for Model '%'").each reset
        duplicateModelsSuggestions.each { destId, sources ->
            Model model = Model.get(destId)
            Batch batch = Batch.findOrSaveByName("Create Synonyms for Model '$model.name'")
            RelationshipType type = RelationshipType.readByName("synonym")
            sources.each { srcId ->
                Action action = actionService.create batch, CreateRelationship, source: "gorm://org.modelcatalogue.core.Model:$srcId", destination: "gorm://org.modelcatalogue.core.Model:$destId", type: "gorm://org.modelcatalogue.core.RelationshipType:$type.id"
                if (action.hasErrors()) {
                    log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save(flush: true)
        }

        Batch.findAllByNameIlike("Merge Model '%'").each reset
        duplicateModelsSuggestions.each { destId, sources ->
            Model model = Model.get(destId)
            Batch batch = Batch.findOrSaveByName("Merge Model '$model.name'")
            sources.each { srcId ->
                Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.Model:$srcId", destination: "gorm://org.modelcatalogue.core.Model:$destId"
                if (action.hasErrors()) {
                    log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generateMergeDataElements() {
        def duplicateDataElementsSuggestions = elementService.findDuplicateDataElementsSuggestions()

        Batch.findAllByNameIlike("Create Synonyms for Data Element '%'").each reset
        duplicateDataElementsSuggestions.each { destId, sources ->
            DataElement dataElement = DataElement.get(destId)
            Batch batch = Batch.findOrSaveByName("Create Synonyms for Data Element '$dataElement.name'")
            RelationshipType type = RelationshipType.readByName("synonym")
            sources.each { srcId ->
                Action action = actionService.create batch, CreateRelationship, source: "gorm://org.modelcatalogue.core.DataElement:$srcId", destination: "gorm://org.modelcatalogue.core.DataElement:$destId", type: "gorm://org.modelcatalogue.core.RelationshipType:$type.id"
                if (action.hasErrors()) {
                    log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save(flush: true)
        }

        Batch.findAllByNameIlike("Merge Data Element '%'").each reset
        duplicateDataElementsSuggestions.each { destId, sources ->
            DataElement dataElement = DataElement.get(destId)
            Batch batch = Batch.findOrSaveByName("Merge Data Element '$dataElement.name'")
            sources.each { srcId ->
                Action action = actionService.create batch, MergePublishedElements, source: "gorm://org.modelcatalogue.core.DataElement:$srcId", destination: "gorm://org.modelcatalogue.core.DataElement:$destId"
                if (action.hasErrors()) {
                    log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                }
            }
            batch.archived = false
            batch.save(flush: true)
        }
    }

    private void generateRenameDataTypesAndValueDomain() {
        Batch.findAllByName("Rename Data Types and Value Domains").each reset
        Map<Long, String> suggestions = dataTypesNamesSuggestions()
        if (suggestions) {
            Batch renameBatch = Batch.findOrSaveByName("Rename Data Types and Value Domains")
            suggestions.findAll { id, name -> name }.each { id, name ->
                DataType type = DataType.get(id)
                String originalName = type.name
                String newName = "$originalName (in $name)"
                Action updateDataType = actionService.create renameBatch, UpdateCatalogueElement, id: id, type: DataType.name, name: newName
                type.relatedValueDomains.each { ValueDomain it ->
                    actionService.create renameBatch, UpdateCatalogueElement, id: it.id, type: ValueDomain.name, name: newName, relatedDataType: updateDataType
                }
            }
            renameBatch.archived = false
            renameBatch.save(flush: true)
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
                    log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
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
                    log.error(org.modelcatalogue.core.util.FriendlyErrors.printErrors("Error generating merge model action", action.errors))
                }
            }
            batch.archived = false
            batch.save()
        }
    }
}
