package uk.co.mc.core

/*
* Measurement units are units such as MPH, cm3, cm2, m etc.
* They are used by value domains to instantiate a data element
*
* */

class MeasurementUnit extends CatalogueElement {

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

}
