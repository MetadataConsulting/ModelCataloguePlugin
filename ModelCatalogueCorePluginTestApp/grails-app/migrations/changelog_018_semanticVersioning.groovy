import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.api.ElementStatus

databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1432717677799-01") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "data_model", columnName: "semantic_version"
            }
        }

        addColumn(tableName: 'data_model') {
            column name: 'semantic_version', type: 'VARCHAR(20)'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1432717677799-02") {
        preConditions (onFail: 'MARK_RAN') {
            not {
                columnExists tableName: "data_model", columnName: "revision_notes"
            }
        }

        addColumn(tableName: 'data_model') {
            column name: 'revision_notes', type: 'LONGTEXT'
        }
    }

    changeSet(author: "Vladimir Orany", id: "1432717677799-03") {
        preConditions (onFail: 'MARK_RAN') {
            // language=SQL
            sqlCheck expectedResult: '0', """
            select count(id) from data_model where semantic_version is null
            """
        }


        // language=SQL
        sql """
          update data_model
          set semantic_version = concat('1.0.', version_number)
          where semantic_version is null
        """
    }

}

