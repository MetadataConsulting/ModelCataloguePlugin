databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974029-01") {
		update tableName: 'published_element', {
            column name: 'status', value: 'DEPRECATED'
            where "status = 'ARCHIVED'"
        }
	}
}
