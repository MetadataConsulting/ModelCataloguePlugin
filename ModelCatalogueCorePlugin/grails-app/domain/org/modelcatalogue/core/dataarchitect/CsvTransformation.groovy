package org.modelcatalogue.core.dataarchitect

class CsvTransformation {

    static searchable = {
        name boost: 10
        description boost: 5

        except = ['separator', 'columnDefinitions']
    }

    String name
    String description
    String separator = ";"

    List<ColumnTransformationDefinition> columnDefinitions

    // time stamping
    Date dateCreated
    Date lastUpdated

    static hasMany = [columnDefinitions: ColumnTransformationDefinition]

    static constraints = {
        name size: 1..255
        separator size: 1..1
        description nullable: true
    }

    static mapping = {
        columnDefinitions cascade: "all-delete-orphan"
    }

    def beforeDelete() {
//        if (columnDefinitions) {
//            final definitions = new ArrayList(columnDefinitions)
//            for (ColumnTransformationDefinition definition in definitions) {
//                definition.transformation = null
//                removeFromColumnDefinitions(definition)
//                definition.delete()
//            }
//            columnDefinitions.clear()
//        }
    }
}
