import org.modelcatalogue.core.util.marshalling.*

beans = {
    customObjectMarshallers( ModelCatalogueCorePluginCustomObjectMarshallers ) {
        marshallers = [
                            new DataModelMarshaller(),
                            new DataElementMarshaller(),
                            new DataTypeMarshaller(),
                            new ElementsMarshaller(),
                            new EnumeratedTypeMarshaller(),
                            new MeasurementUnitMarshaller(),
                            new ModelMarshaller(),
                            new RelationshipTypeMarshaller(),
                            new RelationshipMarshallers(),
                            new RelationshipsMarshaller(),
                            new ValueDomainMarshaller(),
                            new MappingMarshaller(),
                            new MappingsMarshaller()
                ]
    }
}