package org.modelcatalogue.core.dataarchitect

class Import {

    Collection<ImportRow> errorRows
    Collection<ImportRow> pendingRows
    Collection<ImportRow> noUpdateRows
    Collection<ImportRow> updateRows
    Collection<ImportRow> createRows
    Collection<ImportRow> deleteRows

    Boolean imported = false

    static constraints = {
        errorRows nullable:true
        pendingRows nullable:true
        noUpdateRows nullable:true
        updateRows nullable:true
        createRows nullable:true
        deleteRows nullable:true
    }



}
