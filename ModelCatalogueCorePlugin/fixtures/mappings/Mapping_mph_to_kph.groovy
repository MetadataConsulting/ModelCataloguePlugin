import org.modelcatalogue.core.Mapping

fixture {
    Mapping_mph_to_kph(Mapping, source: DT_test4, destination: DT_test3, mapping: "(x as Double) * 1.609344")
}