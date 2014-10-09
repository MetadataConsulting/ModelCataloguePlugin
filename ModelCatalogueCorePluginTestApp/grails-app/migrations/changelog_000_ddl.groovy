databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1412847974028-6") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "batch"
            }
        }
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

	changeSet(author: "Vladimir Orany", id: "1412847974028-1") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "action"
            }
        }
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

        addForeignKeyConstraint(baseColumnNames: "batch_id", baseTableName: "action", constraintName: "FKAB2F7E36BAC363DE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "batch", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-2") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "action_dependency"
            }
        }
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

        addForeignKeyConstraint(baseColumnNames: "dependant_id", baseTableName: "action_dependency", constraintName: "FK1F9BCCF4ED9E4631", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "action_dependency", constraintName: "FK1F9BCCF45173F07B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-3") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "action_parameter"
            }
        }
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
        addForeignKeyConstraint(baseColumnNames: "action_id", baseTableName: "action_parameter", constraintName: "FKD06760E0E234D616", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "action", referencesUniqueColumn: "false")
	}


	changeSet(author: "Vladimir Orany", id: "1412847974028-5") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "asset_file"
            }
        }
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

	changeSet(author: "Vladimir Orany", id: "1412847974028-7") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "catalogue_element"
            }
        }
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
        createIndex(indexName: "model_catalogue_id", tableName: "catalogue_element", unique: "true") {
            column(name: "model_catalogue_id")
        }
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-8") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "classification"
            }
        }
		createTable(tableName: "classification") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "namespace", type: "VARCHAR(255)")
		}

        createIndex(indexName: "namespace", tableName: "classification", unique: "true") {
            column(name: "namespace")
        }

        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "classification", constraintName: "FK16CA33E6B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-11") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "conceptual_domain"
            }
        }
		createTable(tableName: "conceptual_domain") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "namespace", type: "VARCHAR(255)")
		}

        createIndex(indexName: "namespace", tableName: "conceptual_domain", unique: "true") {
            column(name: "namespace")
        }
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "conceptual_domain", constraintName: "FKDCE91AEBB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-13") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "csv_transformation"
            }
        }
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

	changeSet(author: "Vladimir Orany", id: "1412847974028-17") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "data_type"
            }
        }
		createTable(tableName: "data_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_type", constraintName: "FKEA5792AFB7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-18") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "enumerated_type"
            }
        }
		createTable(tableName: "enumerated_type") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "enum_as_string", type: "LONGTEXT") {
				constraints(nullable: "false")
			}
		}
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "enumerated_type", constraintName: "FK1F338199AE83016A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-19") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "extendible_element"
            }
        }
		createTable(tableName: "extendible_element") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}
		}
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "extendible_element", constraintName: "FK1E2A7429B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-20") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "extension_value"
            }
        }
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

        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "extension_value", constraintName: "FK65A9A5715639AA43", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-24") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "mapping"
            }
        }
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

        createIndex(indexName: "destination_id", tableName: "mapping", unique: "true") {
            column(name: "destination_id")
            column(name: "source_id")
        }

        addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "mapping", constraintName: "FK31EC18CEEDA3488A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "mapping", constraintName: "FK31EC18CE51FC8D5D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-25") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "measurement_unit"
            }
        }
		createTable(tableName: "measurement_unit") {
			column(name: "id", type: "BIGINT") {
				constraints(nullable: "false", primaryKey: "true")
			}

			column(name: "symbol", type: "VARCHAR(100)")
		}

        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "measurement_unit", constraintName: "FKEDEAC27B7384AB9", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-27") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "published_element"
            }
        }
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

        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "published_element", constraintName: "FK4C2699AB33E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencesUniqueColumn: "false")
	}


    changeSet(author: "Vladimir Orany", id: "1412847974028-4") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "asset"
            }
        }
        createTable(tableName: "asset") {
            column(name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
            }

            column(name: "content_type", type: "VARCHAR(255)")

            column(name: "md5", type: "VARCHAR(32)")

            column(name: "original_file_name", type: "VARCHAR(255)")

            column(name: "size", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "asset", constraintName: "FK58CEAF0136D2594", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencesUniqueColumn: "false")
    }

    changeSet(author: "Vladimir Orany", id: "1412847974028-26") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "model"
            }
        }
        createTable(tableName: "model") {
            column(name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
            }
        }

        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "model", constraintName: "FK633FB29136D2594", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencesUniqueColumn: "false")
    }

    changeSet(author: "Vladimir Orany", id: "1412847974028-15") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "data_import"
            }
        }
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
        addForeignKeyConstraint(baseColumnNames: "asset_id", baseTableName: "data_import", constraintName: "FK9F4F6DDA861A014F", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "asset", referencesUniqueColumn: "false")
    }

    changeSet(author: "Vladimir Orany", id: "1412847974028-21") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "import_row"
            }
        }
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


    changeSet(author: "Vladimir Orany", id: "1412847974028-33") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "row_action"
            }
        }
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

    changeSet(author: "Vladimir Orany", id: "1412847974028-22") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "import_row_metadata"
            }
        }
        createTable(tableName: "import_row_metadata") {
            column(name: "metadata", type: "BIGINT")

            column(name: "metadata_idx", type: "VARCHAR(255)")

            column(name: "metadata_elt", type: "LONGTEXT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974028-23") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "import_row_row_action"
            }
        }
        createTable(tableName: "import_row_row_action") {
            column(name: "import_row_row_actions_id", type: "BIGINT")

            column(name: "row_action_id", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "import_row_row_actions_id", baseTableName: "import_row_row_action", constraintName: "FKF74C439AFD88BEA8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "import_row", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "row_action_id", baseTableName: "import_row_row_action", constraintName: "FKF74C439A724B7401", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "row_action", referencesUniqueColumn: "false")
    }

    changeSet(author: "Vladimir Orany", id: "1412847974028-16") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "data_import_import_row"
            }
        }
        createTable(tableName: "data_import_import_row") {
            column(name: "data_import_import_queue_id", type: "BIGINT")

            column(name: "import_row_id", type: "BIGINT")

            column(name: "data_import_imported_id", type: "BIGINT")

            column(name: "data_import_pending_action_id", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "data_import_import_queue_id", baseTableName: "data_import_import_row", constraintName: "FKF9919DC5A4DB787B", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "data_import_imported_id", baseTableName: "data_import_import_row", constraintName: "FKF9919DC58E4B3BCE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "data_import_pending_action_id", baseTableName: "data_import_import_row", constraintName: "FKF9919DC5DCB4F674", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_import", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "import_row_id", baseTableName: "data_import_import_row", constraintName: "FKF9919DC5807B6721", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "import_row", referencesUniqueColumn: "false")
    }


    changeSet(author: "Vladimir Orany", id: "1412847974028-30") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "relationship_type"
            }
        }
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

        createIndex(indexName: "name", tableName: "relationship_type", unique: "true") {
            column(name: "name")
        }
    }

	changeSet(author: "Vladimir Orany", id: "1412847974028-28") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "relationship"
            }
        }
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

        createIndex(indexName: "destination_id", tableName: "relationship", unique: "true") {
            column(name: "destination_id")
            column(name: "source_id")
            column(name: "relationship_type_id")
        }

        addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "relationship", constraintName: "FKF0647638EDA3488A", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "relationship_type_id", baseTableName: "relationship", constraintName: "FKF0647638215A4E16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "relationship_type", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "relationship", constraintName: "FKF064763851FC8D5D", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-29") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "relationship_metadata"
            }
        }
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

        addForeignKeyConstraint(baseColumnNames: "relationship_id", baseTableName: "relationship_metadata", constraintName: "FK74E3B55694DCEF85", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "relationship", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-31") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "requestmap"
            }
        }
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

        createIndex(indexName: "http_method", tableName: "requestmap", unique: "true") {
            column(name: "http_method")
            column(name: "url")
        }
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-32") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "role"
            }
        }
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

        createIndex(indexName: "authority", tableName: "role", unique: "true") {
            column(name: "authority")
        }
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-34") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "user"
            }
        }
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

        createIndex(indexName: "username", tableName: "user", unique: "true") {
            column(name: "username")
        }
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-35") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "user_role"
            }
        }
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "BIGINT") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "BIGINT") {
				constraints(nullable: "false")
			}
		}

        addPrimaryKey(columnNames: "role_id, user_id", tableName: "user_role")

        addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", constraintName: "FK143BF46A9B777D04", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK143BF46A40A240E4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "Vladimir Orany", id: "1412847974028-36") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "value_domain"
            }
        }
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

        addForeignKeyConstraint(baseColumnNames: "data_type_id", baseTableName: "value_domain", constraintName: "FK1706EF52537B20FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "value_domain", constraintName: "FK1706EF5233E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "unit_of_measure_id", baseTableName: "value_domain", constraintName: "FK1706EF524DE6923E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "measurement_unit", referencesUniqueColumn: "false")
	}

    changeSet(author: "Vladimir Orany", id: "1412847974028-12") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "conceptual_domain_value_domains"
            }
        }
        createTable(tableName: "conceptual_domain_value_domains") {
            column(name: "value_domain_id", type: "BIGINT")

            column(name: "conceptual_domain_id", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "conceptual_domain_id", baseTableName: "conceptual_domain_value_domains", constraintName: "FKF182F10DC05D8D16", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "conceptual_domain", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "value_domain_id", baseTableName: "conceptual_domain_value_domains", constraintName: "FKF182F10DE5AA9492", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "value_domain", referencesUniqueColumn: "false")
    }

    changeSet(author: "Vladimir Orany", id: "1412847974028-14") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "data_element"
            }
        }
        createTable(tableName: "data_element") {
            column(name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
            }

            column(name: "value_domain_id", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_element", constraintName: "FK74B46B67136D2594", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "value_domain_id", baseTableName: "data_element", constraintName: "FK74B46B67E5AA9492", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "value_domain", referencesUniqueColumn: "false")
    }


    changeSet(author: "Vladimir Orany", id: "1412847974028-10") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "column_transformation_definition"
            }
        }
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
        addForeignKeyConstraint(baseColumnNames: "transformation_id", baseTableName: "column_transformation_definition", constraintName: "FK5AD5F4A09BF45272", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "csv_transformation", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "column_transformation_definition", constraintName: "FK5AD5F4A01F7530B3", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "source_id", baseTableName: "column_transformation_definition", constraintName: "FK5AD5F4A083CE7586", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_element", referencesUniqueColumn: "false")
    }


    changeSet(author: "Vladimir Orany", id: "1412847974028-9") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "classification_classifies"
            }
        }
        createTable(tableName: "classification_classifies") {
            column(name: "published_element_id", type: "BIGINT")

            column(name: "classification_id", type: "BIGINT")
        }
        addForeignKeyConstraint(baseColumnNames: "classification_id", baseTableName: "classification_classifies", constraintName: "FK2499F01B24A7E205", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "classification", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "published_element_id", baseTableName: "classification_classifies", constraintName: "FK2499F01BD3630CA8", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencesUniqueColumn: "false")
    }
}
