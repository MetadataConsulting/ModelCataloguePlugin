import uk.co.mc.core.util.marshalling.CustomObjectMarshallers
import uk.co.mc.core.util.marshalling.DataElementMarshaller
import uk.co.mc.core.util.marshalling.ListMarshaller

beans = {
    customObjectMarshallers( CustomObjectMarshallers ) {
        marshallers = [
                new DataElementMarshaller()
        ]
    }
}