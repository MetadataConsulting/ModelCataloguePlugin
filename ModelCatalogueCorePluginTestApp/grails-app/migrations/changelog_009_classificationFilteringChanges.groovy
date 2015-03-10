databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1412847974091-01") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'classification' and system = false
            """
        }


        // language=SQL
        sql """
          update relationship_type set system = false where name = 'classification'
        """
    }

    changeSet(author: "Vladimir Orany", id: "1412847974091-02") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from relationship_type where name = 'classificationFilter'
            """
        }

        //[name: "classificationFilter", sourceToDestination: "used as filter by", destinationToSource: "filtered by", sourceClass: Classification, destinationClass: User, system: true],
        // language=SQL
        sql """
          insert into relationship_type (name, version, system, bidirectional, version_specific, source_class, source_to_destination, destination_class, destination_to_source)
          value ('classificationFilter', 1, 1, 0, 0, 'org.modelcatalogue.core.Classification', 'used as filter by', 'org.modelcatalogue.core.security.User', 'filtered by')
        """
    }
}