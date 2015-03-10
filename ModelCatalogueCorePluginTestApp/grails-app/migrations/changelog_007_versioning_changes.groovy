databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974051-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship_type", columnName: "version_specific"
            }
        }


        addColumn(tableName: 'relationship_type') {
            column name: 'version_specific', type: 'BIT'
        }

        // language=SQL
        sql """
          update relationship_type set version_specific = 1 where name in ('containment', 'hierarchy')
        """
    }
}

