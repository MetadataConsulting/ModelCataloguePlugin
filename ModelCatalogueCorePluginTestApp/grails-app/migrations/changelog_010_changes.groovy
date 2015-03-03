databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1412847974092-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "change"
            }
        }
        createTable(tableName: "change") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
            }

            column(name: "latest_version_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "changed_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "author_id", type: "BIGINT") {
                constraints(nullable: "true")
            }

            column(name: "date_created", type: "DATETIME") {
                constraints(nullable: "false")
            }

            column(name: "property", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }

            column(name: "other_side", type: "BIT") {
                constraints(nullable: "true")
            }

            column(name: "type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "new_value", type: "LONGTEXT")
            column(name: "old_value", type: "LONGTEXT")
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974092-02") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'classification' and destination_to_source = 'is classified by'
            """
        }


        // language=SQL
        sql """
          update relationship_type set destination_to_source = 'is classified by' where name = 'classification'
        """
    }


    changeSet(author: "Vladimir Orany", id: "1412847974092-03") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "change", columnName: "undone"
            }
        }
        addColumn(tableName: 'change') {
            column name: 'undone', type: 'BIT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974092-04") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "change", columnName: "parent_id"
            }
        }
        addColumn(tableName: 'change') {
            column name: 'parent_id', type: 'BIGINT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974092-05") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "change", columnName: "system"
            }
        }
        addColumn(tableName: 'change') {
            column name: 'system', type: 'BIT'
        }
    }
}
