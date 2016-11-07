import org.modelcatalogue.core.Mapping

fixture {
    Mapping_F_to_C(Mapping, source: DT_test2, destination: DT_test1, mapping: "((x as Double) - 32) * 5 / 9")
}