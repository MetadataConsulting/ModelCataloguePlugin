import org.modelcatalogue.core.enumeration.Enumerations

databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1612847974399-01") {
        preConditions (onFail: 'MARK_RAN') {
            columnExists tableName: "enumerated_type", columnName: "enum_as_string"
        }
        dropNotNullConstraint(tableName: "enumerated_type", columnName: "enum_as_string", columnDataType: "LONGTEXT")
    }

    changeSet(author: "Vladimir Orany", id: "1612847974399-04") {
        grailsChange {
            change {
                Map<Long, String> migratedEnums = [:]
                sql.eachRow "select id, enum_as_string from enumerated_type where enum_as_string not like '%orderedMap%'", { row ->
                    Long id = (row[0] as Number).longValue()
                    String enumAsString = row[1]
                    migratedEnums[id] = Enumerations.from(enumAsString).toJsonString()
                }

                println "Migrating ${migratedEnums.size()} enums"

                migratedEnums.each { Long id, String enumAsString ->
                    sql.executeUpdate "update enumerated_type set enum_as_string = $enumAsString where id = $id"
                }
            }
        }
    }

}

