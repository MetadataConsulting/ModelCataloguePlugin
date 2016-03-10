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

    changeSet(author: "Vladimir Orany", id: "1432717677799-03-03") {
        // language=SQL
        sql """
          update data_model dm
          join catalogue_element ce on dm.id = ce.id
          set dm.semantic_version = concat('1.0.', ce.version_number)
          where dm.semantic_version is null
        """
    }

}

