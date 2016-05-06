databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1412847975556-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "validation_rule"
            }
        }
        createTable(tableName: "validation_rule") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
            }

            column(name: "component", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
            column(name: "rule_focus", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
            column(name: "rule", type: "LONGTEXT")
            column(name: "rule_trigger", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
            column(name: "error_condition", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
            column(name: "issue_record", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
            column(name: "notification", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
            column(name: "notification_target", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
        }
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "validation_rule", constraintName: "FK_VR_CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
    }

}
