package uk.co.mc.core

import grails.rest.RestfulController

class MeasurementUnitController extends RestfulController<MeasurementUnit> {

    static responseFormats = ['json', 'xml']

    MeasurementUnitController() {
        super(MeasurementUnit)
    }


}
