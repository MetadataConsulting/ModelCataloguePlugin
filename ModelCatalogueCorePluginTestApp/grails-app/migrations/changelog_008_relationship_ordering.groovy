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
                columnExists tableName: "relationship", columnName: "outgoing_index"
            }
        }
        addColumn(tableName: 'relationship') {
            column name: 'outgoing_index', type: 'BIGINT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974090-03") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship", columnName: "combined_index"
            }
        }
        addColumn(tableName: 'relationship') {
            column name: 'combined_index', type: 'BIGINT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974090-04") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                // language=SQL
                sqlCheck expectedResult: '0', """
                select count(id) from relationship where incoming_index is null
                """
            }
        }


        // language=SQL
        sql """
          update relationship set incoming_index = id where incoming_index is null
        """
    }


    changeSet(author: "Vladimir Orany", id: "1412847974090-05") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                // language=SQL
                sqlCheck expectedResult: '0', """
                select count(id) from relationship where outgoing_index is null
                """
            }

        }


        // language=SQL
        sql """
          update relationship set outgoing_index = id where outgoing_index is null
        """
    }


    changeSet(author: "Vladimir Orany", id: "1412847974090-06") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                // language=SQL
                sqlCheck expectedResult: '0', """
                select count(id) from relationship where combined_index is null
                """
            }
        }


        // language=SQL
        sql """
          update relationship set combined_index = id where combined_index is null
        """
    }


}

