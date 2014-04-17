package org.modelcatalogue.core.dataarchitect

class Import {

    Collection<ImportRow, RowAction> errorRows
    Collection<ImportRow, RowAction> pendingRows
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

    static void importAll(){
    }

    public static void ingestRow(ImportRow row){


//        if (name.isEmpty()) {
//            valid = false
//            messages.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
//        } else if (conceptualDomain.isEmpty()) {
//            valid = false
//            messages.put(metadataColumns.get("NHIC_Identifier"), "no name for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
//        } else if (categories.isEmpty()) {
//            valid = false
//            messages.put(metadataColumns.get("NHIC_Identifier"), "no models specified for the given data element: ${metadataColumns.get("NHIC_Identifier")}")
//        }



    }

}
