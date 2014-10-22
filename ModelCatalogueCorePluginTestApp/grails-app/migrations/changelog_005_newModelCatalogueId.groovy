databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974035-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "published_element", columnName: "latest_version_id"
            }
        }


        addColumn(tableName: 'published_element') {
            column name: 'latest_version_id', type: 'BIGINT'
        }
        addForeignKeyConstraint(baseColumnNames: "latest_version_id", baseTableName: "published_element", constraintName: "FK74B46B67136PES", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "published_element", referencesUniqueColumn: "false")
    }

	changeSet(author: "Vladimir Orany", id: "1412847974035-02") {
        grailsChange {
            change {
                sql.eachRow "select pe.id, ce.model_catalogue_id from published_element pe join catalogue_element ce on pe.id = ce.id where pe.latest_version_id is null", { row ->
                    String baseId = row[1].substring(0, row[1].lastIndexOf('_')) + '%'
                    def result = sql.firstRow "select pe.id from published_element pe join catalogue_element ce on pe.id = ce.id where ce.model_catalogue_id like $baseId order by pe.version_number desc limit 1"
                    if (!result) {
                        throw new IllegalStateException("No matching rows found for $baseId")
                    }
                    sql.executeUpdate "update published_element pe set pe.latest_version_id = ${result[0]} where pe.id = ${row[0]}"
                }
            }
        }
    }

}

