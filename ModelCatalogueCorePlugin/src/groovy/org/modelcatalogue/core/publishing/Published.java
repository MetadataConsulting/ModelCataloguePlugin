package org.modelcatalogue.core.publishing;

/**
 * Simple interface to decouple element service and domain classes
 */
public interface Published<T extends Published<T>> {

    T publish(Publisher<T> publisher);
    T createDraftVersion(Publisher<T> publisher, DraftContext strategy);

    boolean isPublished();

}
