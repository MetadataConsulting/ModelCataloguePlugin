package uk.co.mc.core

import grails.converters.JSON
import grails.rest.RestfulController

class MeasurementUnitController extends RestfulController<MeasurementUnit> {

    static responseFormats = ['json', 'xml']

    MeasurementUnitController() {
        super(MeasurementUnit)
        JSON.use('deep')
    }


}
