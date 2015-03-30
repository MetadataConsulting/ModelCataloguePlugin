databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1612847974092-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "extension_value", columnName: "`index`"
            }
        }
        addColumn(tableName: 'extension_value') {
            column name: '`index`', type: 'BIGINT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1612847974092-02") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_metadata", columnName: "`index`"
            }
        }
        addColumn(tableName: 'relationship_metadata') {
            column name: '`index`', type: 'BIGINT'
        }
    }

}

