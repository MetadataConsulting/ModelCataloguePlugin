import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipDefinition
import org.modelcatalogue.core.RelationshipService
import org.modelcatalogue.core.RelationshipType

databaseChangeLog = {

    changeSet(author: "Vladimir Orany", id: "1432717688899-01") {
        preConditions (onFail: 'MARK_RAN') {
            columnExists tableName: "relationship", columnName: "combined_index"
        }

        dropColumn(tableName: 'relationship', columnName: 'combined_index')
    }


    changeSet(author: "Vladimir Orany", id: "1432717688899-02") {
        grailsChange {
            change {
                RelationshipService relationshipService = ctx.getBean(RelationshipService)

                //language=SQL
                sql.eachRow("""
                    select rel.id from relationship rel
                    join relationship_type rt on rt.id = rel.relationship_type_id

                    where rt.bidirectional = true
                """) { row ->
                    RelationshipDefinition definition = RelationshipDefinition.from(Relationship.get(row[0]))

                    println "Relinking $definition ..."

                    relationshipService.link(definition)

                    println "... relinking $definition finished"
                }
            }
        }
    }

}

