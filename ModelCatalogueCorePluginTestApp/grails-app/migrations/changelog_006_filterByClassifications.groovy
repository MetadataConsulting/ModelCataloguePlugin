databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974050-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "user", columnName: "default_classification_id"
            }
        }

        dropColumn tableName: 'user', columnName: 'default_classification_id'
    }

    changeSet(author: "Vladimir Orany", id: "1412847974051-01") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "extendible_element"
        }

        dropAllForeignKeyConstraints baseTableName: 'extension_value'
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "extension_value", constraintName: "CAT_EL_EXTENSIONS", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'published_element'
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "published_element", constraintName: "FK4C2699AB33E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'value_domain'
        addForeignKeyConstraint(baseColumnNames: "data_type_id", baseTableName: "value_domain", constraintName: "FK1706EF52537B20FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "value_domain", constraintName: "FK1706EF5233E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "unit_of_measure_id", baseTableName: "value_domain", constraintName: "FK1706EF524DE6923E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "measurement_unit", referencesUniqueColumn: "false")

        dropTable tableName: 'extendible_element'
    }

}

