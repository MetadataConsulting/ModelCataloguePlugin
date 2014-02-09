package uk.co.mc.core

/*
*
* Data Elements and Models are extendible elements. This allows them to add additional metadata as properties
*
* */

abstract class ExtendibleElement extends PublishedElement{

    //storage is used with propertyMissing to allow the user to extend any catalogue element properties
    Map extension = [:]

    static constraints = {

    }

    /******************************************************************************************************************/
    /**** allows you to add properties to all of the catalogue elements using the extension map************************************************/
    /******************************************************************************************************************/
    def propertyMissing(String name, value) {
        extension[name] = value
    }


    def propertyMissing(String name) {
        extension[name]
    }
}
