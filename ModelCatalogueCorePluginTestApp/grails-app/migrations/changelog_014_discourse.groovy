databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1432717633382-1") {
		createTable(tableName: "categories_for_classifications") {
			preConditions (onFail: 'MARK_RAN') {
				not {
					tableExists tableName: "categories_for_classifications"
				}
			}
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "categories_foPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "classification_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "discourse_category_name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "Vladimir Orany", id: "1432717633382-4") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "topics_for_elements"
            }
        }
		createTable(tableName: "topics_for_elements") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "topics_for_elPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "catalogue_element_id", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "topic_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
	}
}
