import org.modelcatalogue.core.Mapping

fixture {
    Mapping_kph_to_mph(Mapping, source: VD_speed_continental, destination: VD_speed_uk, mapping: "x * (5 / 8)")
}