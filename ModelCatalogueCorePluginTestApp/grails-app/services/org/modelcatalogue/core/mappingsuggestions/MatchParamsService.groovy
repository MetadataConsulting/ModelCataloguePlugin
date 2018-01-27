package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.modelcatalogue.core.util.MatchResult
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity

@CompileStatic
class MatchParamsService {

    Map<String, Object> matchParams(MatchResult match,
                                    MetadataDomain sourceDomain,
                                    MetadataDomain destinationDomain,
                                    Long relationshipTypeId) {
        matchParams(match.dataElementAId,
                sourceDomain,
                match.dataElementBId,
                destinationDomain,
                relationshipTypeId,
                match.matchScore,
                match.message)
    }

    Map<String, Object> matchParms(SourceDestinationMappingSuggestion suggestion, Long relationshipTypeId) {
        matchParams(suggestion.source.id,
                MetadataDomain.of(suggestion.source),
                suggestion.destination.id,
                MetadataDomain.of(suggestion.destination),
                relationshipTypeId,
                suggestion.distance
        )
    }

    Map<String, Object> matchParams(Long sourceId,
                                    MetadataDomain sourceDomain,
                                    Long destinationId,
                                    MetadataDomain destinationDomain,
                                    Long relationshipTypeId,
                                    Float matchScore,
                                    String message = null,
                                    String matchOn = 'ElementName') {
        Map<String, Object> params = new HashMap<>()
        params.put("""source""", """${MetadataDomainEntity.stringRepresentation(sourceDomain, sourceId)}""")
        params.put("""destination""", """${MetadataDomainEntity.stringRepresentation(destinationDomain, destinationId)}""")
        params.put("""type""", """${MetadataDomainEntity.stringRepresentation(MetadataDomain.RELATIONSHIP_TYPE, relationshipTypeId)}""")
        params.put("""matchScore""", """${(matchScore) ? matchScore as Integer : 0}""")
        params.put("""matchOn""", """${matchOn}""")
        if ( message ) {
            params.put("""message""", """${message}""")
        }
        params
    }
}
