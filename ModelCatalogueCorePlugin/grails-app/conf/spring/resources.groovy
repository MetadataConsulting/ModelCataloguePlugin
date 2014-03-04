import uk.co.mc.core.util.marshalling.ConceptualDomainMarshaller
import uk.co.mc.core.util.marshalling.CustomObjectMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller
import uk.co.mc.core.util.marshalling.DataTypeMarshaller
import uk.co.mc.core.util.marshalling.ElementsMarshaller
import uk.co.mc.core.util.marshalling.EnumeratedTypeMarshaller
import uk.co.mc.core.util.marshalling.MeasurementUnitMarshallers
import uk.co.mc.core.util.marshalling.ModelMarshaller
import uk.co.mc.core.util.marshalling.RelationshipMarshallers
import uk.co.mc.core.util.marshalling.RelationshipsMarshaller
import uk.co.mc.core.util.marshalling.ValueDomainMarshaller

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