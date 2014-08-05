package org.modelcatalogue.core.dataarchitect

import org.modelcatalogue.core.Model

class DataImport {

    String name

    Collection<Model> models = []
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
