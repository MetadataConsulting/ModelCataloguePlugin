databaseChangeLog = {

	changeSet(author: "Vladimir Orany", id: "1412847974032-01") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'favourite'
            """
        }


        // language=SQL
        sql """
          insert into relationship_type (name, version, system, bidirectional, source_class, source_to_destination, destination_class, destination_to_source)
          value ('favourite', 1, 1, 0, 'org.modelcatalogue.core.security.User', 'favourites', 'org.modelcatalogue.core.CatalogueElement', 'is favourite of')
        """
    }

}

