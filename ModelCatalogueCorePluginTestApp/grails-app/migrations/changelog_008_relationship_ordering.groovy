databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1412847974090-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship", columnName: "incoming_index"
            }
        }
        addColumn(tableName: 'relationship') {
            column name: 'incoming_index', type: 'BIGINT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974090-02") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_type", columnName: "sortable"
            }
        }
        addColumn(tableName: 'relationship_type') {
            column name: 'sortable', type: 'BIT'
        }
    }
}

