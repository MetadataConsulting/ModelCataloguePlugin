databaseChangeLog = {

    changeSet(author: "adam (generated)", id: "1518968359008-1") {
        modifyDataType(columnName: "outcome", newDataType: "varchar(10000)", tableName: "action")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-2") {
        modifyDataType(columnName: "result", newDataType: "varchar(1000)", tableName: "action")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-3") {
        modifyDataType(columnName: "extension_value", newDataType: "varchar(1000)", tableName: "action_parameter")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-4") {
        modifyDataType(columnName: "description", newDataType: "varchar(2000)", tableName: "batch")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-5") {
        modifyDataType(columnName: "revision_notes", newDataType: "varchar(2000)", tableName: "data_model")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-6") {
        modifyDataType(columnName: "policy_text", newDataType: "varchar(10000)", tableName: "data_model_policy")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-7") {
        modifyDataType(columnName: "rule", newDataType: "varchar(10000)", tableName: "data_type")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-8") {
        modifyDataType(columnName: "extension_value", newDataType: "varchar(10000)", tableName: "extension_value")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-9") {
        modifyDataType(columnName: "mapping", newDataType: "varchar(10000)", tableName: "mapping")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-10") {
        modifyDataType(columnName: "extension_value", newDataType: "varchar(10000)", tableName: "relationship_metadata")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-11") {
        modifyDataType(columnName: "destination_to_source_description", newDataType: "varchar(2000)", tableName: "relationship_type")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-12") {
        modifyDataType(columnName: "source_to_destination_description", newDataType: "varchar(2000)", tableName: "relationship_type")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-13") {
        modifyDataType(columnName: "rule", newDataType: "varchar(10000)", tableName: "validation_rule")
    }

//    changeSet(author: "adam (generated)", id: "1518968359008-14") {
//        addPrimaryKey(columnNames: "id", constraintName: "user_rolePK", tableName: "user_role")
//    }
//
//    changeSet(author: "adam (generated)", id: "1518968359008-15") {
//        dropPrimaryKey(tableName: "user_role")
//    }

    changeSet(author: "adam (generated)", id: "1518968359008-16") {
        dropForeignKeyConstraint(baseTableName: "classification_classifies", baseTableSchemaName: "gel_latest", constraintName: "FK2499F01B24A7E205")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-17") {
        dropForeignKeyConstraint(baseTableName: "classification_classifies", baseTableSchemaName: "gel_latest", constraintName: "FK2499F01BD3630CA8")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-18") {
        dropForeignKeyConstraint(baseTableName: "conceptual_domain", baseTableSchemaName: "gel_latest", constraintName: "FKDCE91AEBB7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-19") {
        dropForeignKeyConstraint(baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "gel_latest", constraintName: "FKF182F10DC05D8D16")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-20") {
        dropForeignKeyConstraint(baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "gel_latest", constraintName: "FKF182F10DE5AA9492")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-21") {
        dropForeignKeyConstraint(baseTableName: "data_import", baseTableSchemaName: "gel_latest", constraintName: "FK9F4F6DDA861A014F")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-22") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "gel_latest", constraintName: "FKF9919DC5A4DB787B")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-23") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "gel_latest", constraintName: "FKF9919DC58E4B3BCE")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-24") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "gel_latest", constraintName: "FKF9919DC5DCB4F674")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-25") {
        dropForeignKeyConstraint(baseTableName: "data_import_import_row", baseTableSchemaName: "gel_latest", constraintName: "FKF9919DC5807B6721")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-26") {
        dropForeignKeyConstraint(baseTableName: "extendible_element", baseTableSchemaName: "gel_latest", constraintName: "FK1E2A7429B7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-27") {
        dropForeignKeyConstraint(baseTableName: "import_row_row_action", baseTableSchemaName: "gel_latest", constraintName: "FKF74C439AFD88BEA8")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-28") {
        dropForeignKeyConstraint(baseTableName: "import_row_row_action", baseTableSchemaName: "gel_latest", constraintName: "FKF74C439A724B7401")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-29") {
        dropForeignKeyConstraint(baseTableName: "model", baseTableSchemaName: "gel_latest", constraintName: "FK633FB29B7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-30") {
        dropForeignKeyConstraint(baseTableName: "published_element", baseTableSchemaName: "gel_latest", constraintName: "FK4C2699AB33E59620")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-31") {
        dropForeignKeyConstraint(baseTableName: "published_element", baseTableSchemaName: "gel_latest", constraintName: "FK74B46B67136PES")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-32") {
        dropForeignKeyConstraint(baseTableName: "value_domain", baseTableSchemaName: "gel_latest", constraintName: "FK1706EF52537B20FA")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-33") {
        dropForeignKeyConstraint(baseTableName: "value_domain", baseTableSchemaName: "gel_latest", constraintName: "FK1706EF52B7384AB9")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-34") {
        dropForeignKeyConstraint(baseTableName: "value_domain", baseTableSchemaName: "gel_latest", constraintName: "FK1706EF524DE6923E")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-35") {
        dropIndex(indexName: "change_idx", tableName: "change")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-36") {
        dropIndex(indexName: "change_idx_1", tableName: "change")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-37") {
        dropIndex(indexName: "change_idx_2", tableName: "change")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-38") {
        dropIndex(indexName: "change_idx_3", tableName: "change")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-39") {
        dropIndex(indexName: "namespace", tableName: "classification")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-40") {
        dropIndex(indexName: "namespace", tableName: "conceptual_domain")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-41") {
        dropIndex(indexName: "username", tableName: "security_user_old")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-42") {
        createIndex(indexName: "FK_fhuoesmjef3mrv0gpja4shvcr", tableName: "acl_entry") {
            column(name: "acl_object_identity")
        }
    }

    changeSet(author: "adam (generated)", id: "1518968359008-43") {
        createIndex(indexName: "FK_6c3ugmk053uy27bk2sred31lf", tableName: "acl_object_identity") {
            column(name: "object_id_class")
        }
    }

    changeSet(author: "adam (generated)", id: "1518968359008-44") {
        createIndex(indexName: "email_uniq_1518968358674", tableName: "user", unique: "true") {
            column(name: "email")
        }
    }

    changeSet(author: "adam (generated)", id: "1518968359008-45") {
        dropTable(tableName: "categories_for_classifications")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-46") {
        dropTable(tableName: "classification")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-47") {
        dropTable(tableName: "classification_classifies")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-48") {
        dropTable(tableName: "conceptual_domain")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-49") {
        dropTable(tableName: "conceptual_domain_value_domains")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-50") {
        dropTable(tableName: "data_import")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-51") {
        dropTable(tableName: "data_import_import_row")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-52") {
        dropTable(tableName: "extendible_element")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-53") {
        dropTable(tableName: "import_row")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-54") {
        dropTable(tableName: "import_row_metadata")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-55") {
        dropTable(tableName: "import_row_row_action")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-56") {
        dropTable(tableName: "model")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-57") {
        dropTable(tableName: "published_element")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-58") {
        dropTable(tableName: "row_action")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-59") {
        dropTable(tableName: "security_user_old")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-60") {
        dropTable(tableName: "topics_for_elements")
    }

    changeSet(author: "adam (generated)", id: "1518968359008-61") {
        dropTable(tableName: "value_domain")
    }
}
