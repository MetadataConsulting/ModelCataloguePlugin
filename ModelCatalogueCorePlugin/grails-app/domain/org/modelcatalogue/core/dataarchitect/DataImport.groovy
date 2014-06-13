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

    //TODO replace this with a call to the published element domain class constraint for model catalogue id directly
    private static final REGEX = '(?i)MC_([A-Z0-9]{8}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{12})_\\d+'

    Collection<Model> models = []
    Collection<String> messages = []

    static hasMany = [pendingAction: ImportRow, importQueue: ImportRow, imported: ImportRow]

    static constraints = {
        imported nullable: true
        pendingAction nullable: true
        importQueue nullable: true
    }

}
