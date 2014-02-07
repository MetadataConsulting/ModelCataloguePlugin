import uk.co.mc.core.util.marshalling.CustomObjectMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller

beans = {
    customObjectMarshallers( CustomObjectMarshallers ) {
        marshallers = [
                new DataElementMarshaller()
        ]
    }
}