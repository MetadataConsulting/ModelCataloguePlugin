databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1612847974095-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_type", columnName: "source_to_destination_description"
            }
        }
        addColumn(tableName: 'relationship_type') {
            column name: 'source_to_destination_description', type: 'LONGTEXT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1612847974095-02") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_type", columnName: "destination_to_source_description"
            }
        }
        addColumn(tableName: 'relationship_type') {
            column name: 'destination_to_source_description', type: 'LONGTEXT'
        }
    }

}

