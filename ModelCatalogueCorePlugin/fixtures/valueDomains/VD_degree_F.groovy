import uk.co.mc.core.ValueDomain

load "measurementUnits/MU_degree_F"
load "dataTypes/DT_double"

fixture{
    VD_degree_F(ValueDomain, unitOfMeasure: MU_degree_F, name: "value domain Fahrenheit", dataType: DT_double)
}
