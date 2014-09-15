import org.modelcatalogue.core.Mapping

fixture {
    Mapping_F_to_C(Mapping, source: VD_degree_F, destination: VD_degree_C, mapping: "((x as Double) - 32) * 5 / 9")
}