import uk.co.mc.core.EnumeratedType
import uk.co.mc.core.MeasurementUnit
import uk.co.mc.core.ValueDomain

    subjects(ValueDomain) {
        name: "ground_speed"
        unitOfMeasure: new MeasurementUnit(name:"MPH")
        regexDef: "[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?"
        description: "the ground speed of the moving vehicle"
        dataType: new EnumeratedType(name:'test', enumerations: ['male','female','unknown'])
    }
