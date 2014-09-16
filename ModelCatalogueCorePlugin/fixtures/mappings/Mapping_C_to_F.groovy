import org.modelcatalogue.core.Mapping

fixture {
    Mapping_C_to_F(Mapping, source: VD_degree_C, destination: VD_degree_F, mapping: "(x as Double) * 9 / 5 + 32")
}