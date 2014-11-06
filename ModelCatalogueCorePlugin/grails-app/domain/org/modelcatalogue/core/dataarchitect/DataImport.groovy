package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model

class DataImport {

    String name

    Collection<Model> models = []
    Collection<DataElement> updatedDataElements = []
    Collection<String> messages = []
    Collection<Classification> classifications = []
    Asset asset

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
