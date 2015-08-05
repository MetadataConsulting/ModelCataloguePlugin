import org.modelcatalogue.core.util.marshalling.*

beans = {
    customObjectMarshallers( ModelCatalogueCorePluginCustomObjectMarshallers ) {
        marshallers = [
                            new DataModelMarshaller(),
                            new DataElementMarshaller(),
                            new DataTypeMarshaller(),
                            new ElementsMarshaller(),
                            new EnumeratedTypeMarshaller(),
                            new ReferenceTypeMarshaller(),
                            new PrimitiveTypeMarshaller(),
                            new MeasurementUnitMarshaller(),
                            new DataClassMarshaller(),
                            new RelationshipTypeMarshaller(),
                            new RelationshipMarshallers(),
                            new RelationshipsMarshaller(),
                            new ValueDomainMarshaller(),
                            new MappingMarshaller(),
                            new MappingsMarshaller()
                ]
    }
}