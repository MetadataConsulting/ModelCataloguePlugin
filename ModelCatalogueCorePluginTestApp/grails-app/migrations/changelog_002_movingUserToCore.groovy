databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974030-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "security_user_old"
            }
        }

        dropAllForeignKeyConstraints baseTableName: 'user'
        dropAllForeignKeyConstraints baseTableName: 'user_role'

        renameTable oldTableName: 'user', newTableName: 'security_user_old'


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

            column(name: "email", type: "VARCHAR(255)")
        }

        createIndex(indexName: "username", tableName: "user", unique: "true") {
            column(name: "username")
        }

	}

    // language=SQL
    sql """
            insert into user (id, version, account_expired, account_locked, enabled, password, password_expired, username, email) select id + (select max(ce.id) from catalogue_element as ce) as newId, version, account_expired, account_locked, enabled, password, password_expired, username, email from security_user_old;
            insert into user_role(user_id, role_id) select (select u.id, ur.role_id from user as u join user_role as ur on ur.user_id = u.id);
            delete ur from user_role as ur left join user as u on u.id = ur.user_id where u.id is null;
            insert into catalogue_element (id, name, version, date_created, last_updated) select id, username, 1, current_timestamp, current_timestamp from user;
        """

    addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "user", constraintName: "FKEA5792AFB738USER", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
    addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK143BF46A40A240E4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")

}

