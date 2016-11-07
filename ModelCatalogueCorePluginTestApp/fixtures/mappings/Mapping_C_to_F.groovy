import org.modelcatalogue.core.Mapping

fixture {
    Mapping_C_to_F(Mapping, source: DT_test1, destination: DT_test2, mapping: "(x as Double) * 9 / 5 + 32")
}