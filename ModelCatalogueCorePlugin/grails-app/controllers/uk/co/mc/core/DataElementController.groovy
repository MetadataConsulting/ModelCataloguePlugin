package uk.co.mc.core

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional

class DataElementController extends CatalogueElementController<DataElement>{

    static responseFormats = ['json', 'xml']

    DataElementController() {
        super(DataElement)
    }

}
