databaseChangeLog = {

    changeSet(author: "Rick Rees", id: "20160511112830-1") {
        preConditions(onFail: 'MARK_RAN') {
            not {
                indexExists(tableName: 'change', indexName: 'change_idx')
            }
        }

        createIndex(indexName: "change_idx", tableName: "change") {
            column(name: "changed_id")
            column(name: "system")
            column(name: "undone")
            column(name: "parent_id")
            column(name: "type")
            column(name: "date_created")
        }

    }


    changeSet(author: "Vladimir Orany", id: "20160511112830-2") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                indexExists(tableName: 'change', indexName: 'change_idx_2')
            }
        }

        createIndex(indexName: "change_idx_2", tableName: "change") {
            column(name: "parent_id")
            column(name: "system")
            column(name: "other_side")
            column(name: "changed_id")
        }
    }

}
