databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1612847974091-01") {
        preConditions (onFail: 'MARK_RAN') {
            indexExists(tableName: 'relationship', indexName: 'relationship_unique_index')
        }


        createIndex(indexName: "relationship_unique_index", tableName: "relationship", unique: "true") {
            column(name: "destination_id")
            column(name: "source_id")
            column(name: "relationship_type_id")
        }
    }

}