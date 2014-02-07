package uk.co.mc.core.util.marshalling

class CustomObjectMarshallers {
 
List marshallers = []
 
def register() {
	marshallers.each{ it.register() }
}

}
