import uk.co.mc.core.ValueDomain

load "measurementUnits/MU_degree_C"
load "dataTypes/DT_integer"

fixture{
    VD_degree_C(ValueDomain, unitOfMeasure: MU_degree_C, name: "value domain Celsius", dataType: DT_integer)
}
