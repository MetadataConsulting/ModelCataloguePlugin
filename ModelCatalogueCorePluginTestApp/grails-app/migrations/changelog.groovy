databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974028-1") {
		createTable(tableName: "action") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "batch_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "outcome", type: "LONGTEXT")

			column(name: "result", type: "LONGTEXT")

			column(name: "state", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-2") {
		createTable(tableName: "action_dependency") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "dependant_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "provider_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "role", type: "VARCHAR(100)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-3") {
		createTable(tableName: "action_parameter") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "action_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "extension_value", type: "LONGTEXT")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-4") {
		createTable(tableName: "asset") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "content_type", type: "VARCHAR(255)")

			column(name: "md5", type: "VARCHAR(32)")

			column(name: "original_file_name", type: "VARCHAR(255)")

			column(name: "size", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-5") {
		createTable(tableName: "asset_file") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "content", type: "LONGBLOB") {
				constraints(nullable: "false")
			}

			column(name: "path", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-6") {
		createTable(tableName: "batch") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "archived", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-7") {
		createTable(tableName: "catalogue_element") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "model_catalogue_id", type: "VARCHAR(255)")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-8") {
		createTable(tableName: "classification") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "namespace", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-9") {
		createTable(tableName: "classification_classifies") {
			column(name: "published_element_id", type: "BIGINT")

			column(name: "classification_id", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-10") {
		createTable(tableName: "column_transformation_definition") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "destination_id", type: "BIGINT")

			column(name: "header", type: "VARCHAR(255)")

			column(name: "source_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "transformation_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "column_definitions_idx", type: "INT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-11") {
		createTable(tableName: "conceptual_domain") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "namespace", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-12") {
		createTable(tableName: "conceptual_domain_value_domains") {
			column(name: "value_domain_id", type: "BIGINT")

			column(name: "conceptual_domain_id", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-13") {
		createTable(tableName: "csv_transformation") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "VARCHAR(255)")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-14") {
		createTable(tableName: "data_element") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "value_domain_id", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-15") {
		createTable(tableName: "data_import") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "asset_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-16") {
		createTable(tableName: "data_import_import_row") {
			column(name: "data_import_import_queue_id", type: "BIGINT")

			column(name: "import_row_id", type: "BIGINT")

			column(name: "data_import_imported_id", type: "BIGINT")

			column(name: "data_import_pending_action_id", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-17") {
		createTable(tableName: "data_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-18") {
		createTable(tableName: "enumerated_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "enum_as_string", type: "LONGTEXT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-19") {
		createTable(tableName: "extendible_element") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-20") {
		createTable(tableName: "extension_value") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "element_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "extension_value", type: "LONGTEXT")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-21") {
		createTable(tableName: "import_row") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "classification", type: "VARCHAR(255)")

			column(name: "conceptual_domain_description", type: "LONGTEXT")

			column(name: "conceptual_domain_name", type: "VARCHAR(255)")

			column(name: "containing_model_code", type: "VARCHAR(255)")

			column(name: "containing_model_name", type: "VARCHAR(255)")

			column(name: "data_element_code", type: "VARCHAR(255)")

			column(name: "data_element_description", type: "LONGTEXT")

			column(name: "data_element_name", type: "VARCHAR(255)")

			column(name: "data_type", type: "LONGTEXT")

			column(name: "imported", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "measurement_symbol", type: "VARCHAR(255)")

			column(name: "measurement_unit_name", type: "VARCHAR(255)")

			column(name: "parent_model_code", type: "VARCHAR(255)")

			column(name: "parent_model_name", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-22") {
		createTable(tableName: "import_row_metadata") {
			column(name: "metadata", type: "BIGINT")

			column(name: "metadata_idx", type: "VARCHAR(255)")

			column(name: "metadata_elt", type: "LONGTEXT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-23") {
		createTable(tableName: "import_row_row_action") {
			column(name: "import_row_row_actions_id", type: "BIGINT")

			column(name: "row_action_id", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-24") {
		createTable(tableName: "mapping") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "destination_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "mapping", type: "LONGTEXT") {
				constraints(nullable: "false")
			}

			column(name: "source_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-25") {
		createTable(tableName: "measurement_unit") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "symbol", type: "VARCHAR(100)")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-26") {
		createTable(tableName: "model") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-27") {
		createTable(tableName: "published_element") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "status", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "version_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "version_number", type: "INT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-28") {
		createTable(tableName: "relationship") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "archived", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "destination_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "relationship_type_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "source_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-29") {
		createTable(tableName: "relationship_metadata") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "extension_value", type: "LONGTEXT")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "relationship_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-30") {
		createTable(tableName: "relationship_type") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "bidirectional", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "destination_class", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "destination_to_source", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "metadata_hints", type: "LONGTEXT")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "rule", type: "LONGTEXT")

			column(name: "source_class", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "source_to_destination", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "system", type: "BIT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-31") {
		createTable(tableName: "requestmap") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "config_attribute", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "http_method", type: "VARCHAR(255)")

			column(name: "url", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-32") {
		createTable(tableName: "role") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-33") {
		createTable(tableName: "row_action") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "action", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "action_type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "field", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-34") {
		createTable(tableName: "user") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-35") {
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-36") {
		createTable(tableName: "value_domain") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "data_type_id", type: "BIGINT")

			column(name: "multiple", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "rule", type: "LONGTEXT")

			column(name: "unit_of_measure_id", type: "BIGINT")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-37") {
		addPrimaryKey(columnNames: "role_id, user_id", tableName: "user_role")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-79") {
		createIndex(indexName: "model_catalogue_id", tableName: "catalogue_element", unique: "true") {
			column(name: "model_catalogue_id")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-80") {
		createIndex(indexName: "namespace", tableName: "classification", unique: "true") {
			column(name: "namespace")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-81") {
		createIndex(indexName: "namespace", tableName: "conceptual_domain", unique: "true") {
			column(name: "namespace")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-82") {
		createIndex(indexName: "destination_id", tableName: "mapping", unique: "true") {
			column(name: "destination_id")

			column(name: "source_id")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-83") {
		createIndex(indexName: "destination_id", tableName: "relationship", unique: "true") {
			column(name: "destination_id")

			column(name: "source_id")

			column(name: "relationship_type_id")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-84") {
		createIndex(indexName: "name", tableName: "relationship_type", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-85") {
		createIndex(indexName: "http_method", tableName: "requestmap", unique: "true") {
			column(name: "http_method")

			column(name: "url")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-86") {
		createIndex(indexName: "authority", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-87") {
		createIndex(indexName: "username", tableName: "user", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-38") {
		addForeignKeyConstraint(baseColumnNames: "batch_id", baseTableName: "action", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKAB2F7E36BAC363DE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "batch", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-39") {
		addForeignKeyConstraint(baseColumnNames: "dependant_id", baseTableName: "action_dependency", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1F9BCCF4ED9E4631", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-40") {
		addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "action_dependency", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1F9BCCF45173F07B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-41") {
		addForeignKeyConstraint(baseColumnNames: "action_id", baseTableName: "action_parameter", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKD06760E0E234D616", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-42") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "asset", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK58CEAF0136D2594", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-43") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "classification", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK16CA33E6B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-44") {
		addForeignKeyConstraint(baseColumnNames: "classification_id", baseTableName: "classification_classifies", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK2499F01B24A7E205", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "classification", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-45") {
		addForeignKeyConstraint(baseColumnNames: "published_element_id", baseTableName: "classification_classifies", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK2499F01BD3630CA8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-46") {
		addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "column_transformation_definition", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK5AD5F4A01F7530B3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-47") {
		addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "column_transformation_definition", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK5AD5F4A083CE7586", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-48") {
		addForeignKeyConstraint(baseColumnNames: "transformation_id", baseTableName: "column_transformation_definition", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK5AD5F4A09BF45272", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "csv_transformation", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-49") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "conceptual_domain", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKDCE91AEBB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-50") {
		addForeignKeyConstraint(baseColumnNames: "conceptual_domain_id", baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF182F10DC05D8D16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "conceptual_domain", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-51") {
		addForeignKeyConstraint(baseColumnNames: "value_domain_id", baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF182F10DE5AA9492", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "value_domain", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-52") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_element", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK74B46B67136D2594", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-53") {
		addForeignKeyConstraint(baseColumnNames: "value_domain_id", baseTableName: "data_element", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK74B46B67E5AA9492", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "value_domain", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-54") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "data_import", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK9F4F6DDA861A014F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "asset", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-55") {
		addForeignKeyConstraint(baseColumnNames: "data_import_import_queue_id", baseTableName: "data_import_import_row", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF9919DC5A4DB787B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-56") {
		addForeignKeyConstraint(baseColumnNames: "data_import_imported_id", baseTableName: "data_import_import_row", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF9919DC58E4B3BCE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-57") {
		addForeignKeyConstraint(baseColumnNames: "data_import_pending_action_id", baseTableName: "data_import_import_row", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF9919DC5DCB4F674", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-58") {
		addForeignKeyConstraint(baseColumnNames: "import_row_id", baseTableName: "data_import_import_row", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF9919DC5807B6721", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "import_row", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-59") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_type", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKEA5792AFB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-60") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "enumerated_type", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1F338199AE83016A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-61") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "extendible_element", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1E2A7429B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-62") {
		addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "extension_value", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK65A9A5715639AA43", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-63") {
		addForeignKeyConstraint(baseColumnNames: "import_row_row_actions_id", baseTableName: "import_row_row_action", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF74C439AFD88BEA8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "import_row", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-64") {
		addForeignKeyConstraint(baseColumnNames: "row_action_id", baseTableName: "import_row_row_action", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF74C439A724B7401", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "row_action", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-65") {
		addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "mapping", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK31EC18CEEDA3488A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-66") {
		addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "mapping", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK31EC18CE51FC8D5D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-67") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "measurement_unit", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKEDEAC27B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-68") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "model", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK633FB29136D2594", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-69") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "published_element", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK4C2699AB33E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-70") {
		addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "relationship", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF0647638EDA3488A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-71") {
		addForeignKeyConstraint(baseColumnNames: "relationship_type_id", baseTableName: "relationship", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF0647638215A4E16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "relationship_type", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-72") {
		addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "relationship", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FKF064763851FC8D5D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-73") {
		addForeignKeyConstraint(baseColumnNames: "relationship_id", baseTableName: "relationship_metadata", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK74E3B55694DCEF85", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "relationship", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-74") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK143BF46A9B777D04", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-75") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK143BF46A40A240E4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-76") {
		addForeignKeyConstraint(baseColumnNames: "data_type_id", baseTableName: "value_domain", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1706EF52537B20FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-77") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "value_domain", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1706EF5233E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-78") {
		addForeignKeyConstraint(baseColumnNames: "unit_of_measure_id", baseTableName: "value_domain", baseTableSchemaName: "modelcatalogue-core-testapp", constraintName: "FK1706EF524DE6923E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "measurement_unit", referencedTableSchemaName: "modelcatalogue-core-testapp", referencesUniqueColumn: "false")
	}
}
