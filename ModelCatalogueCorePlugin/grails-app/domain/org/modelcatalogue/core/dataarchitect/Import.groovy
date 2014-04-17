package org.modelcatalogue.core.dataarchitect

class Import {

    private Collection<ImportRow> errorRows
    private Collection<ImportRow> pendingRows
    private Collection<ImportRow> noUpdateRows
    private Collection<ImportRow> updateRows
    private Collection<ImportRow> createRows
    private Collection<ImportRow> deleteRows

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
