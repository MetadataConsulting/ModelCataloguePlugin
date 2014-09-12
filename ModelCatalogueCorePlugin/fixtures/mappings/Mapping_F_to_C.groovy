import org.modelcatalogue.core.Mapping

fixture {
    Mapping_C_to_F(Mapping, source: VD_degree_F, destination: VD_degree_C, mapping: "(x as Double) * 9 / 5 + 32")
}