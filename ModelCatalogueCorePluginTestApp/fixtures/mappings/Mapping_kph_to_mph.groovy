import org.modelcatalogue.core.Mapping

fixture {
    Mapping_kph_to_mph(Mapping, source: DT_test3, destination: DT_test4, mapping: "(x as Double) * 0.621371192")
}