databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974050-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "user", columnName: "default_classification_id"
            }
        }

        dropColumn tableName: 'user', columnName: 'default_classification_id'
    }


	changeSet(author: "Vladimir Orany", id: "1412847974050-02") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "user_classification"
            }
        }

        createTable(tableName: "user_classification") {
            column(name: "user_classifications_id", type: "BIGINT")
            column(name: "classification_id", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "classification_id", baseTableName: "user_classification", constraintName: "FK2499F01B24A7UCCI", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "classification", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "user_classifications_id", baseTableName: "user_classification", constraintName: "FK2499F01BD363UCUI", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
    }


}

