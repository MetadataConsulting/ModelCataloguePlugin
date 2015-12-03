import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus

databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1432717677788-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "catalogue_element", columnName: "data_model_id"
            }
        }

        addColumn(tableName: 'catalogue_element') {
            column name: 'data_model_id', type: 'BIGINT'
        }

        addForeignKeyConstraint(baseColumnNames: "data_model_id", baseTableName: "catalogue_element", constraintName: "FK_DATA_MODEL", deferrable: "false", initiallyDeferred: "false", onDelete: "NO ACTION", onUpdate: "NO ACTION", referencedColumnNames: "id", referencedTableName: "data_model", referencesUniqueColumn: "false")
    }

    changeSet(author: "Vladimir Orany", id: "1432717677788-02") {
        sql """
            /* ensure every catalogue element does have latest version id */
            update catalogue_element
              set latest_version_id = id
              where latest_version_id is null;

            /* ensure temporary table does not exist */
            drop temporary table if exists _first_data_models;

            /* crate temporary table which links catalogue elements to first data models assigned */
            create temporary table _first_data_models as

            (
                select
                    src_root.id as first_data_model_id,
                    -- group_concat(distinct concat(src_root.id, ': ', src_root.name)) as data_model_names,
                    -- group_concat(distinct concat(dest.id, ': ', dest.name)) as catalogue_element_names,
                    dest_root.id as catalogue_element_root_id
                from relationship rel
                join catalogue_element src on src.id = rel.source_id
                join catalogue_element dest on dest.id = rel.destination_id
                join catalogue_element src_root on src.latest_version_id = src_root.id
                join catalogue_element dest_root on dest.latest_version_id = dest_root.id

                where rel.relationship_type_id = (select id from relationship_type where name = 'declaration')

                group by dest_root.id
                order by rel.id
            );

            /* assign the data model to the data model with the same latest version id and status as the first one */
            update catalogue_element ce
            join _first_data_models models
                on ce.latest_version_id = models.catalogue_element_root_id
            join relationship rel
                on rel.destination_id = ce.id
                and rel.relationship_type_id = (select id from relationship_type where name = 'declaration')
            join catalogue_element data_model
                on data_model.id = rel.source_id
                and data_model.latest_version_id = models.first_data_model_id
                and data_model.status = ce.status
            set ce.data_model_id = data_model.id
            where ce.data_model_id is null;

            /**
             * assign the data model to the data model with the same latest version id as the first one
             * (if the status is not matching)
             */
            update catalogue_element ce
            join _first_data_models models
                on ce.latest_version_id = models.catalogue_element_root_id
            join relationship rel
                on rel.destination_id = ce.id
                and rel.relationship_type_id = (select id from relationship_type where name = 'declaration')
            join catalogue_element data_model
                on data_model.id = rel.source_id
                and data_model.latest_version_id = models.first_data_model_id
            set ce.data_model_id = data_model.id
            where ce.data_model_id is null;

            /**
             * assign the latest data model to the data model with the same latest version id as the first one
             * (if the status is not matching and the latest data model is no longer used - probably a bug)
             */
            update catalogue_element ce
            join _first_data_models models
                on ce.latest_version_id = models.catalogue_element_root_id
            set ce.data_model_id = models.first_data_model_id
            where ce.data_model_id is null;

            /* drop temporary table */
            drop temporary table if exists _first_data_models;
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717677788-03") {
        grailsChange {
            change {
                int result = Integer.MAX_VALUE
                while (result != 0) {
                    result = sql.executeUpdate('''
                        /* run until result is zero */
                        update relationship rel
                        join catalogue_element src on rel.source_id = src.id
                        join catalogue_element dest on rel.destination_id = dest.id
                        join relationship_type rt on rel.relationship_type_id = rt.id
                        set dest.data_model_id = src.data_model_id
                        where dest.data_model_id is null
                        and rt.version_specific = true
                        and src.data_model_id is not null;
                    ''')
                }
            }
        }
    }

    changeSet(author: "Vladimir Orany", id: "1432717677788-04") {
        sql """
            /* propagate data element data model to the data type without data model */
            update catalogue_element de_ce
            join data_element de on de_ce.id = de.id
            join data_type dt on dt.id = de.data_type_id
            join catalogue_element dt_ce on dt.id = dt_ce.id
            set dt_ce.data_model_id = de_ce.data_model_id
            where dt_ce.data_model_id is null;

            /* classify assets */
            update relationship rel
            join catalogue_element src on src.id = rel.source_id
            join catalogue_element dest on dest.id = rel.destination_id
            set src.data_model_id = dest.id
            where src.data_model_id is null
            and rel.relationship_type_id = (select id from relationship_type where name = 'relatedTo')
            and rel.source_id in (select id from asset)
            and rel.destination_id in (select id from data_model);

            /* classify from imports */
            update relationship rel
            join catalogue_element src on src.id = rel.source_id
            join catalogue_element dest on dest.id = rel.destination_id
            set dest.data_model_id = src.data_model_id
            where dest.data_model_id is null
            and src.data_model_id is not null
            and rel.relationship_type_id = (select id from relationship_type where name = 'relatedTo')
            and rel.source_id in (select id from asset);
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717677788-05") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'import'
            """
        }


        // language=SQL
        sql """
          insert into relationship_type (name, version, system, bidirectional, source_class, source_to_destination, destination_class, destination_to_source)
          value ('import', 1, 0, 0, 'org.modelcatalogue.core.DataModel', 'imports', 'org.modelcatalogue.core.DataModel', 'is imported by')
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717677788-06") {


        sql """
            /* import hints */
            insert ignore into relationship
            (source_id, relationship_type_id, destination_id, version, archived)
            select dm.id, (select id from relationship_type where name = 'import'), other_dm.id, 1, false
            from catalogue_element dm
            join data_model dm_table on dm_table.id = dm.id
            join catalogue_element declared on dm.id = declared.data_model_id
            join relationship rel on declared.id = rel.source_id
            join relationship_type rt on rel.relationship_type_id = rt.id and rt.version_specific = true
            join catalogue_element dest on rel.destination_id = dest.id
            join catalogue_element other_dm on dest.data_model_id = other_dm.id and other_dm.latest_version_id != dm.latest_version_id
        """
    }

    changeSet(author: "Vladimir Orany", id: "1432717677788-07") {
        preConditions(onFail: 'MARK_RAN') {
            foreignKeyConstraintExists foreignKeyName: 'version_number'
        }
        dropForeignKeyConstraint baseTableName: 'catalogue_element', constraintName: 'version_number'
    }


    changeSet(author: "Vladimir Orany", id: "1432717677788-08") {
        grailsChange {
            change {
                // if an exception happens here you have to
                // 1) first - export the current database migrated so far data only, always use 'complete-insert' option (see e.g. Advanced Options in the Workbench)
                // 2) create new database
                // 3) run the application against the database
                // 4) export the structure of the database and reimport or simply truncate all tables
                // 5) load the data into the database - you may need to update the import script as some of the tables or columns no longer exist
                // 6) run the migraiton again
                Map<Number, Set<Number>> dataModelToDataClasses = [:].withDefault { new LinkedHashSet<Number>() }
                sql.eachRow('''
                    /* find which top level classes are not declared with the particular model */
                    select distinct dm.id, m.id
                    from catalogue_element as m
                    join data_class m_classifier on m_classifier.id = m.id
                    join relationship rel
                        on rel.destination_id = m.id
                    join catalogue_element dm
                        on rel.source_id = dm.id
                        and rel.relationship_type_id = (select id from relationship_type where name = 'declaration')
                    join data_model dm_classifier on dm_classifier.id = dm.id
                    where m.id not in (
                            select distinct r.destination_id
                            from relationship r
                            where r.relationship_type_id = (select id from relationship_type where name = 'hierarchy')
                            and r.source_id in (
                                select distinct d.destination_id
                                from relationship d
                                where d.relationship_type_id = (select id from relationship_type where name = 'declaration')
                                and d.source_id = dm.id
                            )
                        )
                    and m.data_model_id != dm.id
                    order by dm.id, m.id
                ''') { row ->
                    dataModelToDataClasses[row[0]].add(row[1])
                }

                dataModelToDataClasses.each { dataModelId, dataClassIds ->
                    DataModel dataModel = DataModel.get(dataModelId)

                    DataClass dataClass = DataClass.findByDataModelAndName(dataModel, "$dataModel.name Classes")

                    if (!dataClass) {
                        dataClass = new DataClass(dataModel: dataModel, name: "$dataModel.name Classes")
                        dataClass.save(failOnError: true, deepValidate: false)
                    }

                    for (Number id in dataClassIds) {
                        DataClass dc = DataClass.get(id)
                        if (dc.status == ElementStatus.DEPRECATED) {
                            if (dataModel.status == ElementStatus.DRAFT) {
                                continue
                            }
                            dataClass.createLinkTo(dc, RelationshipType.hierarchyType, ignoreRules: true, skipUniqueChecking: true, archived: true)
                        } else {
                            dataClass.createLinkTo(dc, RelationshipType.hierarchyType, ignoreRules: true, skipUniqueChecking: true)
                        }

                    }
                }


            }
        }
    }

}

