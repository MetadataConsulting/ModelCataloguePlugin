import org.modelcatalogue.core.util.marshalling.*

beans = {
    customObjectMarshallers( ModelCatalogueCorePluginCustomObjectMarshallers ) {
        marshallers = [
                            new ConceptualDomainMarshaller(),
                            new ClassificationMarshaller(),
                            new DataElementMarshaller(),
                            new DataTypeMarshaller(),
                            new ElementsMarshaller(),
                            new ValueDomainsMarshaller(),
                            new EnumeratedTypeMarshaller(),
                            new MeasurementUnitMarshallers(),
                            new ModelMarshaller(),
                            new RelationshipTypeMarshaller(),
                            new RelationshipMarshallers(),
                            new RelationshipsMarshaller(),
                            new ValueDomainMarshaller(),
                            new MappingMarshallers(),
                            new MappingsMarshaller()
                ]
    }
}