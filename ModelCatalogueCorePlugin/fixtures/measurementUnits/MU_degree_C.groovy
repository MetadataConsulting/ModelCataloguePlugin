import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.ElementStatus

fixture{
    MU_degree_C(MeasurementUnit, status: ElementStatus.FINALIZED, symbol: "°C", name: "Degrees Celsius", description: """Celsius, also known as centigrade,[1] is a scale and unit of measurement for temperature. It is named after the Swedish astronomer Anders Celsius (1701–1744), who developed a similar temperature scale. The degree Celsius (°C) can refer to a specific temperature on the Celsius scale as well as a unit to indicate a temperature interval, a difference between two temperatures or an uncertainty. The unit was known until 1948 as "centigrade" from the Latin centum translated as 100 and gradus translated as "steps".""")
}