databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1512847974030-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "security_user_old"
            }
        }

        dropAllForeignKeyConstraints baseTableName: 'user'
        dropAllForeignKeyConstraints baseTableName: 'user_role'

        renameTable oldTableName: 'user', newTableName: 'security_user_old'


        createTable(tableName: "user") {
            column(name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
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
            column(name: "default_classification_id", type: "BIGINT")
        }

        createIndex(indexName: "username", tableName: "user", unique: "true") {
            column(name: "username")
        }


        // language=SQL
        sql """
            insert into user (id, account_expired, account_locked, enabled, password, password_expired, username, email) select id + (select max(ce.id) from catalogue_element as ce) as newId, account_expired, account_locked, enabled, password, password_expired, username, email from security_user_old;
            insert into user_role (user_id, role_id) select u.id, ur.role_id from user as u join security_user_old suo on u.username = suo.username join user_role as ur on ur.user_id = suo.id;
            delete ur from user_role as ur left join user as u on u.id = ur.user_id where u.id is null;
            insert into catalogue_element (id, name, version, date_created, last_updated, model_catalogue_id) select id, username, 1, current_timestamp, current_timestamp, concat('MC_', id,'fc2e-5cae-4bf7-8812-f65abb7c114b_1') from user;
            insert into extendible_element (id) select id from user;
        """

        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "user", constraintName: "FKEA5792AFB738USER", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "extendible_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK143BF46A40A240E4", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "default_classification_id", baseTableName: "user", constraintName: "FK74B46B67136D25CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

    }

}

