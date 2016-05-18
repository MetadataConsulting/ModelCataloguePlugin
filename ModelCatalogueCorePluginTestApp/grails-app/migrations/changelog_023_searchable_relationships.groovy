databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "201605171022-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_type", columnName: "searchable"
            }
        }
        addColumn(tableName: 'relationship_type') {
            column name: 'searchable', type: 'BIT', defaultValueBoolean: false
        }
    }

    changeSet(author: "Vladimir Orany", id: "201605171022-02") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'favourite' and searchable = true
            """
        }


        // language=SQL
        sql """
          update relationship_type set searchable = true where name = 'favourite'
        """
    }

    changeSet(author: "Vladimir Orany", id: "201605171022-03") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where searchable is null
            """
        }


        // language=SQL
        sql """
          update relationship_type set searchable = false where searchable is null
        """
    }
}

