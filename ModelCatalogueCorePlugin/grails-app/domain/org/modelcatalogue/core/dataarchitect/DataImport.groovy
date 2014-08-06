package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.marshalling.EnumeratedTypeMarshaller

class DataImport {

    private static final QUOTED_CHARS = ["\\": "&#92;", ":": "&#58;", "|": "&#124;", "%": "&#37;"]
    String name
    Collection<Model> models = []
    Collection<DataElement> updatedDataElements = []
    Collection<String> messages = []
    Set pendingAction = []
    Set importQueue = []
    Set imported = []

    static hasMany = [pendingAction: ImportRow, importQueue: ImportRow, imported: ImportRow]

    static constraints = {
        name nullable: false
        imported nullable: true
        pendingAction nullable: true
        importQueue nullable: true
    }

}
