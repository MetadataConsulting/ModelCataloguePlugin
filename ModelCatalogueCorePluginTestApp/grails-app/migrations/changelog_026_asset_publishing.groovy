databaseChangeLog = {


	changeSet(author: "adam (generated)", id: "1516820130876-8") {

		addColumn(tableName: 'asset') {

			column(name: "published_status", type: "VARCHAR(255)")

		}

	}


}
