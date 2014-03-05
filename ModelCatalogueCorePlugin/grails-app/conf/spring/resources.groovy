import org.modelcatalogue.core.util.marshalling.*

beans = {
    customObjectMarshallers( CustomObjectMarshallers ) {
        marshallers = [
                            new ConceptualDomainMarshaller(),
                            new DataElementMarshaller(),
                            new DataTypeMarshaller(),
                            new ElementsMarshaller(),
                            new EnumeratedTypeMarshaller(),
                            new MeasurementUnitMarshallers(),
                            new ModelMarshaller(),
                            new RelationshipMarshallers(),
                            new RelationshipsMarshaller(),
                            new ValueDomainMarshaller()
                ]
    }
}