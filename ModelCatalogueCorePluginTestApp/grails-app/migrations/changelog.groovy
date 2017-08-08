databaseChangeLog = {

	changeSet(author: "adam (generated)", id: "1502144740329-1") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-2") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-3") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-4") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-5") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-6") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-7") {
		createTable(tableName: "catalogue_element") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "data_model_id", type: "BIGINT")

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "LONGTEXT")

			column(name: "last_updated", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "latest_version_id", type: "BIGINT")

			column(name: "model_catalogue_id", type: "VARCHAR(255)")

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
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

	changeSet(author: "adam (generated)", id: "1502144740329-8") {
		createTable(tableName: "categories_for_classifications") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "classification_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "discourse_category_name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-9") {
		createTable(tableName: "change") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "author_id", type: "BIGINT")

			column(name: "changed_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "latest_version_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "new_value", type: "LONGTEXT")

			column(name: "old_value", type: "LONGTEXT")

			column(name: "other_side", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "parent_id", type: "BIGINT")

			column(name: "property", type: "VARCHAR(255)")

			column(name: "system", type: "BIT")

			column(name: "type", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "undone", type: "BIT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-10") {
		createTable(tableName: "classification") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "namespace", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-11") {
		createTable(tableName: "classification_classifies") {
			column(name: "published_element_id", type: "BIGINT")

			column(name: "classification_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-12") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-13") {
		createTable(tableName: "conceptual_domain") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "namespace", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-14") {
		createTable(tableName: "conceptual_domain_value_domains") {
			column(name: "value_domain_id", type: "BIGINT")

			column(name: "conceptual_domain_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-15") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-16") {
		createTable(tableName: "data_class") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-17") {
		createTable(tableName: "data_element") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "data_type_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-18") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-19") {
		createTable(tableName: "data_import_import_row") {
			column(name: "data_import_import_queue_id", type: "BIGINT")

			column(name: "import_row_id", type: "BIGINT")

			column(name: "data_import_imported_id", type: "BIGINT")

			column(name: "data_import_pending_action_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-20") {
		createTable(tableName: "data_model") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "revision_notes", type: "LONGTEXT")

			column(name: "semantic_version", type: "VARCHAR(20)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-21") {
		createTable(tableName: "data_model_data_model_policy") {
			column(name: "data_model_policies_id", type: "BIGINT")

			column(name: "data_model_policy_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-22") {
		createTable(tableName: "data_model_policy") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "policy_text", type: "LONGTEXT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-23") {
		createTable(tableName: "data_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "rule", type: "LONGTEXT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-24") {
		createTable(tableName: "enumerated_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "enum_as_string", type: "LONGTEXT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-25") {
		createTable(tableName: "extendible_element") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-26") {
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

			column(name: "order_index", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-27") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-28") {
		createTable(tableName: "import_row_metadata") {
			column(name: "metadata", type: "BIGINT")

			column(name: "metadata_idx", type: "VARCHAR(255)")

			column(name: "metadata_elt", type: "LONGTEXT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-29") {
		createTable(tableName: "import_row_row_action") {
			column(name: "import_row_row_actions_id", type: "BIGINT")

			column(name: "row_action_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-30") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-31") {
		createTable(tableName: "measurement_unit") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "symbol", type: "VARCHAR(100)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-32") {
		createTable(tableName: "model") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-33") {
		createTable(tableName: "oauthid") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "access_token", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "provider", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-34") {
		createTable(tableName: "primitive_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "measurement_unit_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-35") {
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

			column(name: "latest_version_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-36") {
		createTable(tableName: "reference_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "data_class_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-37") {
		createTable(tableName: "registration_code") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "date_created", type: "DATETIME") {
				constraints(nullable: "false")
			}

			column(name: "token", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "VARCHAR(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-38") {
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

			column(name: "data_model_id", type: "BIGINT")

			column(name: "destination_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "incoming_index", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "inherited", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "outgoing_index", type: "BIGINT") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-39") {
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

			column(name: "order_index", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "relationship_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-40") {
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

			column(name: "destination_to_source_description", type: "LONGTEXT")

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

			column(name: "source_to_destination_description", type: "LONGTEXT")

			column(name: "system", type: "BIT") {
				constraints(nullable: "false")
			}

			column(name: "version_specific", type: "BIT") {
				constraints(nullable: "false")
			}

			column(defaultValueBoolean: "false", name: "searchable", type: "BIT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-41") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-42") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-43") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-44") {
		createTable(tableName: "security_user_old") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "account_expired", type: "BIT")

			column(name: "account_locked", type: "BIT")

			column(name: "email", type: "VARCHAR(255)")

			column(name: "enabled", type: "BIT")

			column(name: "password", type: "VARCHAR(255)")

			column(name: "password_expired", type: "BIT")

			column(name: "username", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-45") {
		createTable(tableName: "tag") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-46") {
		createTable(tableName: "topics_for_elements") {
			column(autoIncrement: "true", name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "version", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "catalogue_element_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "topic_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-47") {
		createTable(tableName: "user") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "account_expired", type: "BIT")

			column(name: "account_locked", type: "BIT")

			column(name: "email", type: "VARCHAR(255)")

			column(name: "enabled", type: "BIT")

			column(name: "password", type: "VARCHAR(255)")

			column(name: "password_expired", type: "BIT")

			column(name: "username", type: "VARCHAR(255)")

			column(name: "api_key", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-48") {
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "data_model_id", type: "BIGINT")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-49") {
		createTable(tableName: "validation_rule") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "component", type: "VARCHAR(255)")

			column(name: "error_condition", type: "VARCHAR(255)")

			column(name: "issue_record", type: "VARCHAR(255)")

			column(name: "notification", type: "VARCHAR(255)")

			column(name: "notification_target", type: "VARCHAR(255)")

			column(name: "rule", type: "LONGTEXT")

			column(name: "rule_focus", type: "VARCHAR(255)")

			column(name: "rule_trigger", type: "VARCHAR(255)")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-50") {
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

	changeSet(author: "adam (generated)", id: "1502144740329-51") {
		addPrimaryKey(columnNames: "role_id, user_id", tableName: "user_role")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-108") {
		createIndex(indexName: "CtlgElement_latestVersionId_idx", tableName: "catalogue_element", unique: "false") {
			column(name: "latest_version_id")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-109") {
		createIndex(indexName: "CtlgElement_modelCatalogueId_idx", tableName: "catalogue_element", unique: "false") {
			column(name: "model_catalogue_id")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-110") {
		createIndex(indexName: "CtlgElement_name_idx", tableName: "catalogue_element", unique: "false") {
			column(name: "name")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-111") {
		createIndex(indexName: "change_idx", tableName: "change", unique: "false") {
			column(name: "changed_id")

			column(name: "system")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-112") {
		createIndex(indexName: "change_idx_1", tableName: "change", unique: "false") {
			column(name: "changed_id")

			column(name: "system")

			column(name: "undone")

			column(name: "parent_id")

			column(name: "type")

			column(name: "date_created")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-113") {
		createIndex(indexName: "change_idx_2", tableName: "change", unique: "false") {
			column(name: "parent_id")

			column(name: "system")

			column(name: "other_side")

			column(name: "changed_id")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-114") {
		createIndex(indexName: "change_idx_3", tableName: "change", unique: "false") {
			column(name: "author_id")

			column(name: "other_side")

			column(name: "system")

			column(name: "date_created")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-115") {
		createIndex(indexName: "change_idx_4", tableName: "change", unique: "false") {
			column(name: "latest_version_id")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-116") {
		createIndex(indexName: "namespace", tableName: "classification", unique: "true") {
			column(name: "namespace")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-117") {
		createIndex(indexName: "namespace", tableName: "conceptual_domain", unique: "true") {
			column(name: "namespace")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-118") {
		createIndex(indexName: "name", tableName: "data_model_policy", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-119") {
		createIndex(indexName: "destination_id", tableName: "mapping", unique: "true") {
			column(name: "destination_id")

			column(name: "source_id")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-120") {
		createIndex(indexName: "access_token", tableName: "oauthid", unique: "true") {
			column(name: "access_token")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-121") {
		createIndex(indexName: "identity_idx", tableName: "oauthid", unique: "false") {
			column(name: "access_token")

			column(name: "provider")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-122") {
		createIndex(indexName: "RelationType_destinationClass_idx", tableName: "relationship_type", unique: "false") {
			column(name: "destination_class")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-123") {
		createIndex(indexName: "RelationType_name_idx", tableName: "relationship_type", unique: "false") {
			column(name: "name")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-124") {
		createIndex(indexName: "name", tableName: "relationship_type", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-125") {
		createIndex(indexName: "http_method", tableName: "requestmap", unique: "true") {
			column(name: "http_method")

			column(name: "url")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-126") {
		createIndex(indexName: "authority", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-127") {
		createIndex(indexName: "username", tableName: "security_user_old", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-128") {
		createIndex(indexName: "username", tableName: "user", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "adam (generated)", id: "1502144740329-52") {
		addForeignKeyConstraint(baseColumnNames: "batch_id", baseTableName: "action", baseTableSchemaName: "ntBeforeImport", constraintName: "FKAB2F7E36BAC363DE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "batch", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-53") {
		addForeignKeyConstraint(baseColumnNames: "dependant_id", baseTableName: "action_dependency", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1F9BCCF4ED9E4631", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-54") {
		addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "action_dependency", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1F9BCCF45173F07B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-55") {
		addForeignKeyConstraint(baseColumnNames: "action_id", baseTableName: "action_parameter", baseTableSchemaName: "ntBeforeImport", constraintName: "FKD06760E0E234D616", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-56") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "asset", baseTableSchemaName: "ntBeforeImport", constraintName: "FK58CEAF0B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-57") {
		addForeignKeyConstraint(baseColumnNames: "data_model_id", baseTableName: "catalogue_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK21C019261EE6B39A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_model", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-58") {
		addForeignKeyConstraint(baseColumnNames: "classification_id", baseTableName: "classification_classifies", baseTableSchemaName: "ntBeforeImport", constraintName: "FK2499F01B24A7E205", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "classification", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-59") {
		addForeignKeyConstraint(baseColumnNames: "published_element_id", baseTableName: "classification_classifies", baseTableSchemaName: "ntBeforeImport", constraintName: "FK2499F01BD3630CA8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-60") {
		addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "column_transformation_definition", baseTableSchemaName: "ntBeforeImport", constraintName: "FK5AD5F4A01F7530B3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-61") {
		addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "column_transformation_definition", baseTableSchemaName: "ntBeforeImport", constraintName: "FK5AD5F4A083CE7586", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-62") {
		addForeignKeyConstraint(baseColumnNames: "transformation_id", baseTableName: "column_transformation_definition", baseTableSchemaName: "ntBeforeImport", constraintName: "FK5AD5F4A09BF45272", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "csv_transformation", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-63") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "conceptual_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FKDCE91AEBB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-64") {
		addForeignKeyConstraint(baseColumnNames: "conceptual_domain_id", baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF182F10DC05D8D16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "conceptual_domain", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-65") {
		addForeignKeyConstraint(baseColumnNames: "value_domain_id", baseTableName: "conceptual_domain_value_domains", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF182F10DE5AA9492", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "value_domain", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-66") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_class", baseTableSchemaName: "ntBeforeImport", constraintName: "FK5FA514A3B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-67") {
		addForeignKeyConstraint(baseColumnNames: "data_type_id", baseTableName: "data_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK74B46B67537B20FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-68") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK74B46B67B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-69") {
		addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "data_import", baseTableSchemaName: "ntBeforeImport", constraintName: "FK9F4F6DDA861A014F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "asset", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-70") {
		addForeignKeyConstraint(baseColumnNames: "data_import_import_queue_id", baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC5A4DB787B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-71") {
		addForeignKeyConstraint(baseColumnNames: "data_import_imported_id", baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC58E4B3BCE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-72") {
		addForeignKeyConstraint(baseColumnNames: "data_import_pending_action_id", baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC5DCB4F674", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-73") {
		addForeignKeyConstraint(baseColumnNames: "import_row_id", baseTableName: "data_import_import_row", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF9919DC5807B6721", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "import_row", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-74") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_model", baseTableSchemaName: "ntBeforeImport", constraintName: "FK60336654B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-75") {
		addForeignKeyConstraint(baseColumnNames: "data_model_policies_id", baseTableName: "data_model_data_model_policy", baseTableSchemaName: "ntBeforeImport", constraintName: "FKEF7F185285C45DB3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_model", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-76") {
		addForeignKeyConstraint(baseColumnNames: "data_model_policy_id", baseTableName: "data_model_data_model_policy", baseTableSchemaName: "ntBeforeImport", constraintName: "FKEF7F1852E8A26223", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_model_policy", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-77") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_type", baseTableSchemaName: "ntBeforeImport", constraintName: "FKEA5792AFB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-78") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "enumerated_type", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1F338199AE83016A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-79") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "extendible_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1E2A7429B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-80") {
		addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "extension_value", baseTableSchemaName: "ntBeforeImport", constraintName: "FK65A9A571D98C5EDC", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-81") {
		addForeignKeyConstraint(baseColumnNames: "import_row_row_actions_id", baseTableName: "import_row_row_action", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF74C439AFD88BEA8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "import_row", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-82") {
		addForeignKeyConstraint(baseColumnNames: "row_action_id", baseTableName: "import_row_row_action", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF74C439A724B7401", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "row_action", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-83") {
		addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "mapping", baseTableSchemaName: "ntBeforeImport", constraintName: "FK31EC18CEEDA3488A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-84") {
		addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "mapping", baseTableSchemaName: "ntBeforeImport", constraintName: "FK31EC18CE51FC8D5D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-85") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "measurement_unit", baseTableSchemaName: "ntBeforeImport", constraintName: "FKEDEAC27B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-86") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "model", baseTableSchemaName: "ntBeforeImport", constraintName: "FK633FB29B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-87") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "oauthid", baseTableSchemaName: "ntBeforeImport", constraintName: "FK9C00D1F2E4FDB89D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-88") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "primitive_type", baseTableSchemaName: "ntBeforeImport", constraintName: "FKA5053392AE83016A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-89") {
		addForeignKeyConstraint(baseColumnNames: "measurement_unit_id", baseTableName: "primitive_type", baseTableSchemaName: "ntBeforeImport", constraintName: "FKA5053392FEBD6AE8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "measurement_unit", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-90") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "published_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK4C2699AB33E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-91") {
		addForeignKeyConstraint(baseColumnNames: "latest_version_id", baseTableName: "published_element", baseTableSchemaName: "ntBeforeImport", constraintName: "FK74B46B67136PES", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-92") {
		addForeignKeyConstraint(baseColumnNames: "data_class_id", baseTableName: "reference_type", baseTableSchemaName: "ntBeforeImport", constraintName: "FK8EC0DFAE6C83DD3A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_class", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-93") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "reference_type", baseTableSchemaName: "ntBeforeImport", constraintName: "FK8EC0DFAEAE83016A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-94") {
		addForeignKeyConstraint(baseColumnNames: "data_model_id", baseTableName: "relationship", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF06476381EE6B39A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_model", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-95") {
		addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "relationship", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF0647638EDA3488A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-96") {
		addForeignKeyConstraint(baseColumnNames: "relationship_type_id", baseTableName: "relationship", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF0647638215A4E16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "relationship_type", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-97") {
		addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "relationship", baseTableSchemaName: "ntBeforeImport", constraintName: "FKF064763851FC8D5D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-98") {
		addForeignKeyConstraint(baseColumnNames: "relationship_id", baseTableName: "relationship_metadata", baseTableSchemaName: "ntBeforeImport", constraintName: "FK74E3B55694DCEF85", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "relationship", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-99") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "tag", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1BF9AB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-100") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "user", baseTableSchemaName: "ntBeforeImport", constraintName: "FK36EBCBB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-101") {
		addForeignKeyConstraint(baseColumnNames: "data_model_id", baseTableName: "user_role", baseTableSchemaName: "ntBeforeImport", constraintName: "FK143BF46A1EE6B39A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_model", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-102") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", baseTableSchemaName: "ntBeforeImport", constraintName: "FK143BF46A3FD2F4BD", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-103") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", baseTableSchemaName: "ntBeforeImport", constraintName: "FK143BF46AE4FDB89D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-104") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "validation_rule", baseTableSchemaName: "ntBeforeImport", constraintName: "FKFBD06B02B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-105") {
		addForeignKeyConstraint(baseColumnNames: "data_type_id", baseTableName: "value_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1706EF52537B20FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-106") {
		addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "value_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1706EF52B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}

	changeSet(author: "adam (generated)", id: "1502144740329-107") {
		addForeignKeyConstraint(baseColumnNames: "unit_of_measure_id", baseTableName: "value_domain", baseTableSchemaName: "ntBeforeImport", constraintName: "FK1706EF524DE6923E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "measurement_unit", referencedTableSchemaName: "ntBeforeImport", referencesUniqueColumn: "false")
	}
}
