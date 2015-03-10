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
        preConditions (onFail: 'MARK_RAN') {
            and {
                not {
                    sqlCheck expectedResult: '0', """
                    select count(id) from catalogue_element where model_catalogue_id like 'MC%'
                    """
                }
                not {
                    columnExists tableName: "catalogue_element", columnName: "model_catalogue_id_old"
                }
            }
        }
        grailsChange {
            change {
                sql.eachRow "select pe.id, ce.model_catalogue_id from published_element pe join catalogue_element ce on pe.id = ce.id where pe.latest_version_id is null", { row ->
                    String mcid = row[1]
                    Number peid = row[0]
                    String baseId = mcid.substring(0, mcid.lastIndexOf('_')) + '%'
                    def result = sql.firstRow "select pe.id from published_element pe join catalogue_element ce on pe.id = ce.id where ce.model_catalogue_id like $baseId order by pe.version_number desc limit 1"
                    if (!result) {
                        throw new IllegalStateException("No matching rows found for $baseId")
                    }
                    Number lvid = result[0]
                    sql.executeUpdate "update published_element pe set pe.latest_version_id = $lvid where pe.id = $peid"
                    sql.executeUpdate "update asset_file af set af.path = concat('assets/', $peid) where af.path = concat('assets/', $mcid)"

                }
            }
        }

        renameColumn tableName: "catalogue_element", columnDataType: "VARCHAR(255)", newColumnName: "model_catalogue_id_old", oldColumnName: "model_catalogue_id"
        addColumn(tableName: 'catalogue_element') {
            column name: 'model_catalogue_id', type: 'VARCHAR(255)'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1412847974035-03") {
        sql """update catalogue_element e set e.model_catalogue_id = (select concat('http://purl.obolibrary.org/obo/', replace(ev.extension_value, ':', '_')) from extension_value ev where ev.name = 'OBO ID' and e.id = ev.element_id);"""
    }

}

