databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1432717633383-01") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from relationship_type
                    where source_class = 'org.modelcatalogue.core.Classification'
                    and name = 'classification'
                """
                sqlCheck expectedResult: '1', """
                    select count(id) from relationship_type where name = 'declaration'
                """
            }
        }
        sql """
            update relationship_type
            set
                source_class = 'org.modelcatalogue.core.DataModel',
                name = 'declaration',
                source_to_destination = 'declares',
                source_to_destination_description = 'Data models can declare multiple elements. Based on this relationship you can narrow the elements shown in the catalogue using the data model filter in the bottom left corner. When data model is finalized all defined elements are finalized as well.',
                destination_to_source = 'declared within',
                destination_to_source_description = 'Any element can be declared within multiple data models. When new draft of the element is created then drafts for data models are created as well.'
            where source_class = 'org.modelcatalogue.core.Classification'
            and name = 'classification'
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-02") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from relationship_type
                    where source_class = 'org.modelcatalogue.core.Model'
                """
            }
        }
        sql """
            update relationship_type set source_class = 'org.modelcatalogue.core.DataClass'
            where source_class = 'org.modelcatalogue.core.Model'
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-03") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from relationship_type
                    where source_class = 'org.modelcatalogue.core.Classification'
                """
            }
        }
        sql """
            update relationship_type set source_class = 'org.modelcatalogue.core.DataModel'
            where source_class = 'org.modelcatalogue.core.Classification'
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-04") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from relationship_type
                    where destination_class = 'org.modelcatalogue.core.Model'
                """
            }
        }
        sql """
            update relationship_type set destination_class = 'org.modelcatalogue.core.DataClass'
            where destination_class = 'org.modelcatalogue.core.Model'
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-05") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from relationship_type
                    where destination_class = 'org.modelcatalogue.core.Classification'
                """
            }
        }
        sql """
            update relationship_type set destination_class = 'org.modelcatalogue.core.DataModel'
            where destination_class = 'org.modelcatalogue.core.Classification'
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-06") {
        sql """
             update batch set archived=1 where name like '% Model %' and not name like '% Data Model %';
             update batch set archived=1 where name like 'Deep Classify %';
        """
    }

    // without dbCreate=update
    changeSet(author: "Vladimir Orany", id: "1432717633383-07") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "model"
            not {
                tableExists tableName: "data_class"
            }
        }
        renameTable oldTableName: 'model', newTableName: 'data_class'
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-08") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "classification"
            not {
                tableExists tableName: "data_model"
            }
        }
        renameTable oldTableName: 'classification', newTableName: 'data_model'
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-09") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "model"
            not {
                columnExists tableName: "relationship", columnName: "data_model_id"
            }
        }
        renameColumn tableName: "relationship", columnDataType: "BIGINT", newColumnName: "data_model_id", oldColumnName: "classification_id"
    }

    // with dbCreate=update
    changeSet(author: "Vladimir Orany", id: "1432717633383-10") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "model"
            tableExists tableName: "data_class"
        }
        sql """
        insert into data_class (id)
        select id from model
        where id not in (select id from data_class)
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717633383-11") {
        preConditions(onFail: 'MARK_RAN') {
            tableExists tableName: "classification"
            tableExists tableName: "data_model"
        }
        sql """
        insert into data_model (id)
        select id from classification where id not in (select id from data_model)
        """
    }


    changeSet(author: "Vladimir Orany", id: "1432717633383-12") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from value_domain
                """
            }
        }
        grailsChange {
            change {
                sql.eachRow """
                        select vd.id, vd.unit_of_measure_id
                        from value_domain vd
                        where vd.data_type_id is null
                """, { row ->
                    def vdid = row[0]
                    def muid = row[1]
                    def newDataTypeId = sql.executeInsert("""
                        insert into catalogue_element
                        (version, date_created, description, last_updated, latest_version_id, model_catalogue_id, name, status, version_created, version_number)
                        select version, date_created, description, last_updated, latest_version_id, model_catalogue_id, name, status, version_created, version_number
                        from catalogue_element
                        where id = $vdid
                    """)[0][0]
                    sql.executeInsert "insert into data_type (id) values ($newDataTypeId)"

                    if (muid) {
                        sql.executeInsert "insert into primitive_type (id, measurement_unit_id) values ($newDataTypeId, $muid)"
                    }
                }

                // migrate rules

                sql.eachRow """
                        select vd.rule, dt.id
                        from value_domain vd
                        join data_type dt on vd.data_type_id = dt.id
                        join catalogue_element vdce on vdce.id = vd.id
                        join catalogue_element dtce on dtce.id = dt.id
                        where vd.rule is not null and vdce.name = dtce.name
                """, { row ->
                    def rule = row[0]
                    def dtid = row[1]
                    sql.executeUpdate """
                        update data_type set rule = $rule where id = $dtid
                    """
                }

                // migrate extensions

                sql.eachRow """
                    select vd.data_type_id, ex.id
                    from value_domain vd
                    join extension_value ex on vd.id = ex.element_id
                    where vd.data_type_id is not null
                """, { row ->

                    def dtid = row[0]
                    def exid = row[1]

                    try {
                        sql.executeUpdate """
                            update extension_value
                            set element_id = $dtid
                            where id = $exid
                        """
                    } catch (ignored) {
                        // duplicate entry - ignored
                    }
                }

                // migrate data elements

                sql.eachRow """
                        select de.id, dt.id
                        from data_element de
                        join value_domain vd on de.value_domain_id = vd.id
                        join data_type dt on vd.data_type_id = dt.id
                """, { row ->
                    def deid = row[0]
                    def dtid = row[1]
                    sql.executeUpdate """
                        update data_element
                        set value_domain_id = null, data_type_id = $dtid where id = $deid
                    """
                }

                // migrate relationships

                sql.eachRow """
                    select rel.id, dt.id, rel.destination_id
                    from relationship rel
                    join value_domain vd on rel.source_id = vd.id
                    join data_type dt on vd.data_type_id = dt.id
                    join catalogue_element vdce on vdce.id = vd.id
                    join catalogue_element dtce on dtce.id = dt.id
                    where dtce.name = vdce.name
                """, { row ->
                    def relid = row[0]
                    def dtid = row[1]
                    def destid = row[2]

                    def destDataType = sql.firstRow """
                        select data_type_id from value_domain where id = $destid
                    """

                    if (destDataType && destDataType[0]) {
                        println  "dest data type: $destDataType"
                        destid = destDataType[0]
                    }


                    try {
                        sql.executeUpdate """
                            update relationship
                            set source_id = $dtid,
                            destination_id = $destid
                            where id = $relid
                        """
                    } catch (ignored) {
                        // duplicate entry - ignored
                    }
                }

                sql.eachRow """
                    select rel.id, dt.id, rel.source_id
                    from relationship rel
                    join value_domain vd on rel.destination_id = vd.id
                    join data_type dt on vd.data_type_id = dt.id
                    join catalogue_element vdce on vdce.id = vd.id
                    join catalogue_element dtce on dtce.id = dt.id
                    where dtce.name = vdce.name
                """, { row ->
                    def relid = row[0]
                    def dtid = row[1]
                    def sourceid = row[2]

                    def sourceDataType = sql.firstRow """
                        select data_type_id from value_domain where id = $sourceid
                    """

                    if (sourceDataType && sourceDataType[0]) {
                        sourceid = sourceDataType[0]
                    }


                    try {
                        sql.executeUpdate """
                            update relationship
                            set destination_id = $dtid,
                            source_id = $sourceid
                            where id = $relid
                        """
                    } catch (ignored) {
                        // duplicate entry - it's fine
                    }
                }

                sql.executeUpdate """
                    update data_element set value_domain_id = null where value_domain_id is not null
                """

                def ids = []

                sql.eachRow "select id from value_domain", { row ->
                    ids << row[0]
                }

                for (id in ids) {
                    def vdResult = sql.executeUpdate """
                        delete from value_domain where id = $id
                    """

                    def exResult = sql.executeUpdate """
                        delete from extension_value where element_id = $id
                    """

                    def relResult = sql.executeUpdate """
                        delete from relationship where source_id = $id or destination_id = $id
                    """

                    def ceResult = sql.executeUpdate """
                        delete from catalogue_element where id = $id
                    """
                }

            }
        }
    }


}

