databaseChangeLog = {

    changeSet(author: "Rick Rees", id: "20160511112830") {

        createIndex(indexName: "change_idx", tableName: "change") {
            column(name: "changed_id")
            column(name: "system")
            column(name: "undone")
            column(name: "parent_id")
            column(name: "type")
            column(name: "date_created")
        }
    }

}
