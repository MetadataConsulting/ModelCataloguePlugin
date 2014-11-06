databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974050-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "user", columnName: "default_classification_id"
            }
        }

        dropColumn tableName: 'user', columnName: 'default_classification_id'
    }

    changeSet(author: "Vladimir Orany", id: "1412847974051-01") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "extendible_element"
        }

        dropAllForeignKeyConstraints baseTableName: 'extension_value'
        addForeignKeyConstraint(baseColumnNames: "element_id", baseTableName: "extension_value", constraintName: "CAT_EL_EXTENSIONS", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'published_element'
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "published_element", constraintName: "FK4C2699AB33E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'value_domain'
        addForeignKeyConstraint(baseColumnNames: "data_type_id", baseTableName: "value_domain", constraintName: "FK1706EF52537B20FA", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_type", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "value_domain", constraintName: "FK1706EF5233E59620", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "unit_of_measure_id", baseTableName: "value_domain", constraintName: "FK1706EF524DE6923E", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "measurement_unit", referencesUniqueColumn: "false")

        dropTable tableName: 'extendible_element'
    }

    changeSet(author: "Vladimir Orany", id: "1412847974051-02") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'classification'
            """
        }


        // language=SQL
        sql """
          insert into relationship_type (name, version, system, bidirectional, source_class, source_to_destination, destination_class, destination_to_source)
          value ('classification', 1, 1, 0, 'org.modelcatalogue.core.Classification', 'classifies', 'org.modelcatalogue.core.CatalogueElement', 'classifications')
        """

        grailsChange {
            change {
                sql.eachRow "select distinct cc.classification_id, cc.published_element_id, pe.status = 'DEPRECATED' from classification_classifies cc join published_element pe on cc.published_element_id = pe.id", { row ->
                    def cid = row[0]
                    def pid = row[1]
                    def arc = row[2]

                    def existing = sql.firstRow "select * from relationship where destination_id = $pid and source_id = $cid and relationship_type_id = (select id from relationship_type where name = 'classification')"

                    if (!existing) {
                        sql.executeUpdate """
                            insert into relationship (version, archived, destination_id, relationship_type_id, source_id)
                            value (1, $arc, $pid, (select id from relationship_type where name = 'classification'), $cid)
                        """
                    }
                }
            }
        }

        dropTable tableName: 'classification_classifies'
    }

    changeSet(author: "Vladimir Orany", id: "1412847974051-03") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "published_element"
        }

        addColumn(tableName: 'catalogue_element') {
            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        addColumn(tableName: 'catalogue_element') {
            column(name: "version_created", type: "DATETIME") {
                constraints(nullable: "false")
            }
        }

        addColumn(tableName: 'catalogue_element') {
            column(name: "version_number", type: "INT") {
                constraints(nullable: "false")
            }
        }

        addColumn(tableName: 'catalogue_element') {
            column name: 'latest_version_id', type: 'BIGINT'
        }

        addForeignKeyConstraint(baseColumnNames: "latest_version_id", baseTableName: "catalogue_element", constraintName: "CE_LATEST_VERSION", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'asset'
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "asset", constraintName: "ASSET_IS_CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'model'
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "model", constraintName: "MODEL_IS_CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")

        dropAllForeignKeyConstraints baseTableName: 'data_element'
        addForeignKeyConstraint(baseColumnNames: "id", baseTableName: "data_element", constraintName: "DATA_ELEMENT_IS_CE", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "catalogue_element", referencesUniqueColumn: "false")
        addForeignKeyConstraint(baseColumnNames: "value_domain_id", baseTableName: "data_element", constraintName: "FK74B46B67E5AA9492", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "value_domain", referencesUniqueColumn: "false")

        grailsChange {
            change {
                sql.eachRow "select id, status, version_created, version_number from published_element ", { row ->
                    def peid            = row[0]
                    def status          = row[1]
                    def versionCreated  = row[2]
                    def versionNumber   = row[3]

                    sql.executeUpdate "update catalogue_element set status = $status, version_created = $versionCreated, version_number=$versionNumber where id = $peid"
                }
            }
        }

        sql """
            update catalogue_element set version_created = date_created where version_created is null;
            update catalogue_element set status = 'FINALIZED' where status = '' or status is null;
            update relationship_type set source_class = 'org.modelcatalogue.core.CatalogueElement' where source_class = 'org.modelcatalogue.core.PublishedElement';
            update relationship_type set destination_class = 'org.modelcatalogue.core.CatalogueElement' where destination_class = 'org.modelcatalogue.core.PublishedElement';
        """

        dropTable tableName: 'published_element'
    }

    changeSet(author: "Vladimir Orany", id: "1412847974051-04") {
        preConditions (onFail: 'MARK_RAN') {
            tableExists tableName: 'conceptual_domain'
            tableExists tableName: 'conceptual_domain_value_domains'
        }

        grailsChange {
            change {
                sql.eachRow "select cd.id, cd.namespace, ce.name from conceptual_domain cd join catalogue_element ce on ce.id = cd.id", { row ->
                    Number cdid = row[0]
                    String cdns = row[1]
                    String name = row[2]

                    def classificationSelector = "select cs.id, cs.namespace, ce.name from classification cs join catalogue_element ce on ce.id = cs.id where ce.name = $name"

                    def classification = sql.firstRow classificationSelector

                    if (!classification) {
                        def newids = sql.executeInsert """
                            insert into catalogue_element (
                                version, date_created, description, last_updated,
                                name, model_catalogue_id_old,
                                status, version_created, version_number, latest_version_id
                            )
                            select version, date_created, description, last_updated,
                                name, model_catalogue_id,
                                status, version_created, version_number, latest_version_id
                                from catalogue_element where id = $cdid
                        """
                        Number newId = newids[0][0]
                        sql.executeInsert "insert into classification (id, namespace) value ($newId, $cdns)"
                    }

                    classification = sql.firstRow classificationSelector

                    if (!classification) {
                        throw new IllegalArgumentException("Failed to convert conceptual domain $name to classification!")
                    }

                    Number clid = classification[0]

                    sql.eachRow "select value_domain_id from conceptual_domain_value_domains where conceptual_domain_id = $cdid ", { cdvd ->
                        Number vdid = cdvd[0]

                        def existing = sql.firstRow "select * from relationship where destination_id = $vdid and source_id = $clid and relationship_type_id = (select id from relationship_type where name = 'classification')"

                        if (!existing) {
                            sql.executeUpdate """
                            insert into relationship (version, archived, destination_id, relationship_type_id, source_id)
                            value (1, false, $vdid, (select id from relationship_type where name = 'classification'), $clid)
                        """
                        }
                    }

                }
            }
        }

        sql """
            delete from relationship where relationship_type_id = (select id from relationship_type where name = 'context');
            delete from relationship_type where name = 'context';
        """

        dropTable tableName: 'conceptual_domain_value_domains'
        dropTable tableName: 'conceptual_domain'
    }


}

