databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1412847979999-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                tableExists tableName: "tag"
            }
        }
        createTable(tableName: "tag") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true")
            }

        }
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "tag", constraintName: "FK_TAG_CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
    }

}
