databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974031-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "relationship", columnName: "classification_id"
            }
        }


        addColumn(tableName: 'relationship') {
            column name: 'classification_id', type: 'BIGINT'
        }
        addForeignKeyConstraint(baseColumnNames: "classification_id", baseTableName: "relationship", constraintName: "FK74B46B67136CLASS", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

    }

}

