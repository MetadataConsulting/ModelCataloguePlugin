package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import groovy.transform.CompileStatic
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionService
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.actions.CreateMatch
import org.modelcatalogue.core.persistence.BatchGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.RelationshipGormService
import org.modelcatalogue.core.util.FriendlyErrors
import javax.annotation.PostConstruct

@Slf4j
@CompileStatic
class MappingSuggestionsGeneratorService implements MappingSuggestionsGenerator {

    DataElementGormService dataElementGormService

    DataClassGormService dataClassGormService

    CompareByNameService compareByNameService

    MatchParamsService matchParamsService

    BatchGormService batchGormService

    ActionService actionService

    RelationshipGormService relationshipGormService

    def sessionFactory

    int sourcePageSize

    int destinationPageSize

    int maxSuggestions

    GrailsApplication grailsApplication

    @CompileDynamic
    @PostConstruct
    private void init() {
        sourcePageSize = grailsApplication.config.mc.mappingsuggestions.source.pageSize ?: 100
        destinationPageSize = grailsApplication.config.mc.mappingsuggestions.destination.pageSize ?: 100
        maxSuggestions = grailsApplication.config.mc.mappingsuggestions.max ?: 3
    }

    @CompileDynamic
    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
    }

    @Override
    void execute(Long batchId, Class sourceClazz, DataModel sourceDataModel, Class destionationClazz, DataModel destinationDataModel, Float minDistance, MatchAgainst matchAgainst) {
        MappingGenerationConfiguration config = instantiateMappingGenerationConfiguration(minDistance, matchAgainst)
        generateMappings(batchId, sourceClazz, sourceDataModel, destionationClazz, destinationDataModel, config)
    }

    List findAllByDataModel(Class sourceClass, DataModel dataModel, int offset, int max) {
        if ( sourceClass == DataElement ) {
            return dataElementGormService.findAllByDataModel(dataModel, offset, max)
        } else if ( sourceClass == DataClass ) {
            return dataClassGormService.findAllByDataModel(dataModel, offset, max)
        }
        []
    }

    List findAllByDataModelAndKeywordList(Class clazz, DataModel dataModel, List<String> keywords, int offset, int max) {
        if ( clazz == DataElement ) {
            return dataElementGormService.findAllByDataModelAndKeywordList(dataModel, keywords, offset, max)
        } else if ( clazz == DataClass ) {
            return dataClassGormService.findAllByDataModelAndKeywordList(dataModel, keywords, offset, max)
        }
        []
    }

    int countByDataModelAndKeywordList(Class clazz, DataModel dataModel, List<String> keywords) {
        if ( clazz == DataElement ) {
            return (dataElementGormService.countByDataModelAndKeywordList(dataModel, keywords) ?: 0) as int
        } else if ( clazz == DataClass ) {
            return (dataClassGormService.countByDataModelAndKeywordList(dataModel, keywords) ?: 0) as int
        }
        0
    }

    int countByDataModel(Class clazz, DataModel dataModel) {
        if ( clazz == DataElement ) {
            return (dataElementGormService.countByDataModel(dataModel) ?: 0) as int
        } else if ( clazz == DataClass ) {
            return (dataClassGormService.countByDataModel(dataModel) ?: 0) as int
        }
        0
    }

    MappingGenerationConfiguration instantiateMappingGenerationConfiguration(float minDistance, MatchAgainst matchAgainst) {
        new MappingGenerationConfiguration(
                matchAgainst: matchAgainst,
                pageSizeSource: sourcePageSize,
                pageSizeDestination: destinationPageSize,
                minDistance: minDistance,
                maxSuggestions: maxSuggestions,
        )
    }

    void logSuggestions(List<SourceDestinationMappingSuggestion> suggestions) {
        if ( !suggestions ) {
            log.info 'No suggestions found'
            return
        }
        for (SourceDestinationMappingSuggestion suggestion : suggestions) {
                log.info '{} vs {} distance: {}',
                        suggestion.source.name,
                        suggestion.destination.name,
                        suggestion.distance
        }
    }

    List<SourceDestinationMappingSuggestion> suggestionsForSource(CatalogueElement source, Class destinationClazz, DataModel destinationDataModel, MappingGenerationConfiguration config) {
        List<String> sourceKeywords = keywordsDependingOnMatchAgainst(config.matchAgainst, source)
        int totalDestination = totalDependingOnMatchAgainst(config.matchAgainst, destinationClazz, destinationDataModel, sourceKeywords)
        suggestionsForSource(source, sourceKeywords, destinationClazz, destinationDataModel, totalDestination, config)
    }

    List findAllDependingOnMatchAgainst(List<String> keywords, Class clazz, DataModel dataModel, int offset, MappingGenerationConfiguration config) {
        if (config.matchAgainst == MatchAgainst.CONTAINS_STEMMED_KEYWORDS ) {
            return findAllByDataModelAndKeywordList(clazz, dataModel, keywords, offset, config.pageSizeDestination)
        } else if (config.matchAgainst == MatchAgainst.ALL ) {
            return findAllByDataModel(clazz, dataModel, offset, config.pageSizeDestination)
        }
        []
    }

    int totalDependingOnMatchAgainst(MatchAgainst matchAgainst, Class clazz, DataModel dataModel, List<String> keywords) {
        if (matchAgainst == MatchAgainst.CONTAINS_STEMMED_KEYWORDS ) {
            return countByDataModelAndKeywordList(clazz, dataModel, keywords) as int
        } else if (matchAgainst == MatchAgainst.ALL ) {
            return countByDataModel(clazz, dataModel) as int
        }
        0
    }
    List<String> keywordsDependingOnMatchAgainst(MatchAgainst matchAgainst, CatalogueElement el) {
        if ( matchAgainst == MatchAgainst.CONTAINS_STEMMED_KEYWORDS ) {
            return keywords(el.name)
        }
        []
    }

    List<SourceDestinationMappingSuggestion> suggestionsForSource(CatalogueElement source, List<String> sourceKeywords, Class destinationClazz, DataModel destinationDataModel, int totalDestination, MappingGenerationConfiguration config) {
        log.info('Generating suggestions for {}', source.name)
        List<SourceDestinationMappingSuggestion> suggestions = []
        for (int offsetDestination = 0; offsetDestination < totalDestination; offsetDestination = (offsetDestination + config.pageSizeDestination)) {
            List destinationList = findAllDependingOnMatchAgainst(sourceKeywords, destinationClazz, destinationDataModel, offsetDestination, config)
            for (Object obj : destinationList) {
                if ( obj instanceof CatalogueElement) {
                    CatalogueElement destination = (CatalogueElement) obj
                    float distance = compareByNameService.distance(new CatalogueElementWithNameAdapter(source), new CatalogueElementWithNameAdapter(destination))
                    distance *= 100.0f
                    log.debug '{} vs {}:{} {}', source.name, destinationDataModel.name, destination.name, distance
                    if (distance > 0.0 && (distance > config.minDistance)) {
                        suggestions = addToSuggestions(suggestions, source, destination, distance, config.maxSuggestions)
                    }
                }
            }
            cleanUpGorm()
        }
        suggestions
    }

    @CompileDynamic
    List<SourceDestinationMappingSuggestion> addToSuggestions(List<SourceDestinationMappingSuggestion> suggestions, CatalogueElement source, CatalogueElement destination, float distance, int maxSuggestions) {
        SourceDestinationMappingSuggestion suggestion = new SourceDestinationMappingSuggestion(source: source, destination: destination, distance: distance)
        suggestions.add(suggestion)
        suggestions = suggestions.sort()
        if ( suggestions.size() > maxSuggestions) {
            suggestions = suggestions.drop( (suggestions.size() - maxSuggestions) )
        }
        suggestions.reverse()
    }

    void generateMappings(Long batchId, Class sourceClazz, DataModel sourceDataModel, Class destinationClazz, DataModel destinationDataModel, MappingGenerationConfiguration config) {
        int totalSource = countByDataModel(sourceClazz, sourceDataModel)

        for ( int offsetSource = 0;  offsetSource < totalSource; offsetSource = (offsetSource + config.pageSizeSource)) {
            List sourceList = findAllByDataModel(sourceClazz, sourceDataModel, offsetSource, config.pageSizeSource)
            for (Object obj : sourceList ) {
                if ( obj instanceof CatalogueElement) {
                    CatalogueElement source = (CatalogueElement) obj
                    List<String> sourceKeywords = keywordsDependingOnMatchAgainst(config.matchAgainst, source)
                    int totalDestination = totalDependingOnMatchAgainst(config.matchAgainst, destinationClazz, destinationDataModel, sourceKeywords)
                    List<SourceDestinationMappingSuggestion> suggestions = suggestionsForSource(source, sourceKeywords, destinationClazz, destinationDataModel, totalDestination, config)
                    processSuggestions(batchId, suggestions)
                }
            }
            cleanUpGorm()
        }
    }

    void processSuggestions(Long batchId, List<SourceDestinationMappingSuggestion> suggestions) {
        logSuggestions(suggestions)

        Batch.withNewTransaction {
            Batch.withNewSession {
                RelationshipType type = RelationshipType.readByName("relatedTo")
                Batch batch = batchGormService.findById(batchId)

                for ( SourceDestinationMappingSuggestion suggestion : suggestions ) {
                    if ( !(relationshipGormService.countByRelationshipTypeAndSourceAndDestination(type, suggestion.source, suggestion.destination) as boolean) ) {
                        Map<String, Object> matchParams = matchParamsService.matchParms(suggestion, type.id)
                        Action action = actionService.create(matchParams, batch, CreateMatch)
                        if (action.hasErrors()) {
                            log.error(FriendlyErrors.printErrors("Error generating create synonym action", action.errors))
                        }
                    }
                }
            }
        }
    }

    String stemTerm (String term) {
        if ( !term ) {
            return term
        }
        term = term.toLowerCase()
        Stemmer stemmer = new Stemmer()
        term.toCharArray().each { char c ->
            stemmer.add(c)
        }
        stemmer.stem()
        stemmer.toString()
    }

    String cleanup(String term) {
        if ( !term ) {
            return term
        }

        term.replaceAll('/', ' ')
            .replaceAll('\\(', ' ')
            .replaceAll(']', ' ')
            .replaceAll('\\[', ' ')
            .trim().replaceAll(" +", " ");
    }

    List<String> keywords(String term) {
        String[] arr = cleanup(term).split(' ')
        List<String> result = []
        for ( String word : arr ) {
            result.add(stemTerm(word))
        }
        result.findAll { !EnglishGrammar.ALL.contains(it) && it.length() > 1 }
    }
}