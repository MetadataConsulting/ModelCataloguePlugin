package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.DataElement

class ColumnTransformationDefinition {

    DataElement source
    DataElement destination

    String header

    static belongsTo = [transformation: CsvTransformation]

    static constraints = {
        header nullable: true
        destination nullable: true
    }

    String getHeader() {
        if (header)         return header
        if (destination)    return destination.name
        if (source)         return source.name
                            return null
    }
}
