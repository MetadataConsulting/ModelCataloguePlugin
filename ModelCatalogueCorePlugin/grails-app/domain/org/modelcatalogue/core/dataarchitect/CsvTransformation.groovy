package org.modelcatalogue.core.dataarchitect

class CsvTransformation {

    String name
    String separator = ";"

    List<ColumnTransformationDefinition> columnDefinitions

    static hasMany = [columnDefinitions: ColumnTransformationDefinition]

    static constraints = {
        name size: 1..255
        separator size: 1..1
    }

    static mapping = {
        columnDefinitions cascade: "all-delete-orphan"
    }
}
