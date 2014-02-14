package uk.co.mc.core

import grails.converters.JSON
import grails.converters.XML
import grails.rest.RestfulController

class MeasurementUnitController extends RestfulController<MeasurementUnit> {

    static responseFormats = ['json', 'xml']

    MeasurementUnitController() {
        super(MeasurementUnit)
        // using deep we get relationships rendered
        // the relationships are not rendered directly but just the id, link and name is returned
        // for source, destination and type
        // TODO: the right render from the unit tests must be registrered
        JSON.use('deep')
        XML.use('deep')
    }


}
