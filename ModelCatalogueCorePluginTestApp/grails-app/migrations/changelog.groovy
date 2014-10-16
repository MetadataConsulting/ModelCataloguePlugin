databaseChangeLog = {
    include file: 'changelog_000_ddl.groovy'
    include file: 'changelog_001_archivedToDeprecated.groovy'
    include file: 'changelog_002_movingUserToCore.groovy'
    include file: 'changelog_003_relationshipClassification.groovy'
    include file: 'changelog_004_favourites.groovy'
}
