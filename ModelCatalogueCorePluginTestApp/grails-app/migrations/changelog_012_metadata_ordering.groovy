databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1612847974092-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "extension_value", columnName: "extension_value"
            }
        }
        addColumn(tableName: 'extension_value') {
            column name: 'order_index', type: 'BIGINT'
        }
        // language=SQL
        sql """
          update extension_value set order_index = id where order_index is null or order_index = 0
        """
    }

    changeSet(author: "Vladimir Orany", id: "1612847974092-02") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_metadata", columnName: "order_index"
            }
        }
        addColumn(tableName: 'relationship_metadata') {
            column name: 'order_index', type: 'BIGINT'
        }
        // language=SQL
        sql """
          update relationship_metadata set order_index = id where order_index is null or order_index = 0
        """
    }

}

