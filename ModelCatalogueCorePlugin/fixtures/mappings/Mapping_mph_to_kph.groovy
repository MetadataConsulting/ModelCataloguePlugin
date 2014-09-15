import org.modelcatalogue.core.Mapping

fixture {
    Mapping_mph_to_kph(Mapping, source: VD_speed_uk, destination: VD_speed_continental, mapping: "(x as Double) * 1.609344")
}