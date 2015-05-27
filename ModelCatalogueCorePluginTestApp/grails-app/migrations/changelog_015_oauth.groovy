databaseChangeLog = {

	changeSet(author: "ladin (generated)", id: "1432717633382-3") {
		preConditions (onFail: 'MARK_RAN') {
			not {
				tableExists tableName: "oauthid"
			}
		}
		createTable(tableName: "oauthid") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "oauthidPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "access_token", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "provider", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "bigint") {
				constraints(nullable: "false")
			}
		}
		createIndex(indexName: "FK9C00D1F2E4FDB89D", tableName: "oauthid") {
			column(name: "user_id")
		}
		createIndex(indexName: "access_token_uniq_1432717632755", tableName: "oauthid", unique: "true") {
			column(name: "access_token")
		}
		createIndex(indexName: "identity_idx", tableName: "oauthid") {
			column(name: "access_token")
			column(name: "provider")
		}
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "oauthid", constraintName: "FK9C00D1F2E4FDB89D", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}
}
