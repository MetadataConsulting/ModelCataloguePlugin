databaseChangeLog = {

    changeSet(author: "adam (generated)", id: "1502360441815-154") {
        dropPrimaryKey(tableName: "user_role")

        dropColumn(tableName: 'user_role', columnName: "id")

        addColumn(tableName: 'user_role') {

            column(autoIncrement: "true", name: "id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "user_rolePK")
            }

        }
    }

    changeSet(author: "adam (generated)", id: "1502360441815-1") {
        modifyDataType(columnName: "outcome", newDataType: "varchar(10000)", tableName: "action")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-2") {
        modifyDataType(columnName: "result", newDataType: "varchar(1000)", tableName: "action")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-3") {
        modifyDataType(columnName: "extension_value", newDataType: "varchar(1000)", tableName: "action_parameter")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-4") {
        modifyDataType(columnName: "description", newDataType: "varchar(2000)", tableName: "batch")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-5") {
        modifyDataType(columnName: "revision_notes", newDataType: "varchar(2000)", tableName: "data_model")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-6") {
        modifyDataType(columnName: "policy_text", newDataType: "varchar(10000)", tableName: "data_model_policy")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-7") {
        modifyDataType(columnName: "rule", newDataType: "varchar(10000)", tableName: "data_type")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-8") {
        modifyDataType(columnName: "extension_value", newDataType: "varchar(10000)", tableName: "extension_value")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-9") {
        modifyDataType(columnName: "mapping", newDataType: "varchar(10000)", tableName: "mapping")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-10") {
        modifyDataType(columnName: "extension_value", newDataType: "varchar(10000)", tableName: "relationship_metadata")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-11") {
        modifyDataType(columnName: "destination_to_source_description", newDataType: "varchar(2000)", tableName: "relationship_type")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-12") {
        modifyDataType(columnName: "source_to_destination_description", newDataType: "varchar(2000)", tableName: "relationship_type")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-13") {
        modifyDataType(columnName: "rule", newDataType: "varchar(10000)", tableName: "validation_rule")
    }



    changeSet(author: "adam (generated)", id: "1502360441815-16") {
        dropForeignKeyConstraint(baseTableName: "classification_classifies", baseTableSchemaName: "ntBeforeImport", constraintName: "FK2499F01B24A7E205")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-17") {
        dropForeignKeyConstraint(baseTableName: "classification_classifies", baseTableSchemaName: "ntBeforeImport", constraintName: "FK2499F01BD3630CA8")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-18") {
        dropForeignKeyConstraint(baseTableName: "conceptual_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FKDCE91AEBB7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-19") {
        dropForeignKeyConstraint(baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF182F10DC05D8D16")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-20") {
        dropForeignKeyConstraint(baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF182F10DE5AA9492")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-21") {
        dropForeignKeyConstraint(baseTableName: "data_import", baseTableSchemaName: "ntBeforeImport", constraintName: "FK9F4F6DDA861A014F")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-22") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC5A4DB787B")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-23") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC58E4B3BCE")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-24") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC5DCB4F674")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-25") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC5807B6721")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-26") {
        dropForeignKeyConstraint(baseTableName: "extendible_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1E2A7429B7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-27") {
        dropForeignKeyConstraint(baseTableName: "import_row_row_action", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF74C439AFD88BEA8")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-28") {
        dropForeignKeyConstraint(baseTableName: "import_row_row_action", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF74C439A724B7401")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-29") {
        dropForeignKeyConstraint(baseTableName: "model", baseTableSchemaName: "ntBeforeImport", constraintName: "FK633FB29B7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-30") {
        dropForeignKeyConstraint(baseTableName: "published_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK4C2699AB33E59620")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-31") {
        dropForeignKeyConstraint(baseTableName: "published_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK74B46B67136PES")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-32") {
        dropForeignKeyConstraint(baseTableName: "value_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1706EF52537B20FA")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-33") {
        dropForeignKeyConstraint(baseTableName: "value_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1706EF52B7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-34") {
        dropForeignKeyConstraint(baseTableName: "value_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1706EF524DE6923E")
    }


    changeSet(author: "adam (generated)", id: "1502360441815-40") {
        dropIndex(indexName: "namespace", tableName: "conceptual_domain")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-41") {
        dropIndex(indexName: "username", tableName: "security_user_old")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-42") {
        dropTable(tableName: "categories_for_classifications")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-43") {
        dropTable(tableName: "classification")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-44") {
        dropTable(tableName: "classification_classifies")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-45") {
        dropTable(tableName: "conceptual_domain")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-46") {
        dropTable(tableName: "conceptual_domain_value_domains")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-47") {
        dropTable(tableName: "data_import")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-48") {
        dropTable(tableName: "data_import_import_row")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-49") {
        dropTable(tableName: "extendible_element")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-50") {
        dropTable(tableName: "import_row")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-51") {
        dropTable(tableName: "import_row_metadata")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-52") {
        dropTable(tableName: "import_row_row_action")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-53") {
        dropTable(tableName: "model")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-54") {
        dropTable(tableName: "published_element")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-55") {
        dropTable(tableName: "row_action")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-56") {
        dropTable(tableName: "security_user_old")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-57") {
        dropTable(tableName: "topics_for_elements")
    }

    changeSet(author: "adam (generated)", id: "1502360441815-58") {
        dropTable(tableName: "value_domain")
    }
}
