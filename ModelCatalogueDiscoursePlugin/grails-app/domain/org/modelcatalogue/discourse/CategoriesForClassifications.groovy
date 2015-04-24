package org.modelcatalogue.discourse

class CategoriesForClassifications {

    Long classificationId
    String discourseCategoryName

    static constraints = {
        discourseCategoryName maxSize: 255
    }
}
