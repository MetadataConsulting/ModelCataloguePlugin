package uk.co.mc.core.util.marshalling;

/**
 * Interface for the classes providing custom marshallers.
 * <p/>
 * The marshallers are registered in the {@link #register()} method.
 */
public interface MarshallersProvider {

    /**
     * Registers the marshallers
     */
    void register();

}
