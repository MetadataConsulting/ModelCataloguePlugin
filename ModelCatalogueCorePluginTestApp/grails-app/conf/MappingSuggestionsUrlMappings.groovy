class MappingSuggestionsUrlMappings {

    static mappings = {
        "/mappingsuggestions"(controller: 'mappingSuggestions')
        "/mappingsuggestions/reject"(controller: 'mappingSuggestions', action: 'reject')
        "/mappingsuggestions/approve"(controller: 'mappingSuggestions', action: 'approve')
    }
}
