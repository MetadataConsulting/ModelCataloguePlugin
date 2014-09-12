import org.modelcatalogue.core.Mapping

fixture {
    Mapping_kph_to_mph(Mapping, source: VD_speed_uk, destination: VD_speed_continental, mapping: "x * (8 / 5)")
}