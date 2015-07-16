databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1432717633383-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                sqlCheck expectedResult: '0', """
                    select count(id) from relationship_type
                    where source_class = 'org.modelcatalogue.core.Classification'
                    and name = 'classification'
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
        preConditions (onFail: 'MARK_RAN') {
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
        preConditions (onFail: 'MARK_RAN') {
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
        preConditions (onFail: 'MARK_RAN') {
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
        preConditions (onFail: 'MARK_RAN') {
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


}

