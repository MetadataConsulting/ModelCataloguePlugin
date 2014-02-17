package uk.co.mc.core

class MeasurementUnitController extends CatalogueElementController<MeasurementUnit> {

    static responseFormats = ['json', 'xml']

    MeasurementUnitController() {
        super(MeasurementUnit)
    }


}
