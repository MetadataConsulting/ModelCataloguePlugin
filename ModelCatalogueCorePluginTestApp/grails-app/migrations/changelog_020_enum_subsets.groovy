databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1612847974399-01") {
        preConditions (onFail: 'MARK_RAN') {
            columnExists tableName: "enumerated_type", columnName: "enum_as_string"
        }
        dropNotNullConstraint(tableName: "enumerated_type", columnName: "enum_as_string", columnDataType: "LONGTEXT")
    }

}

