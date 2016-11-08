package org.modelcatalogue.core.publishing;

import rx.Observer;

/**
 * Simple interface to decouple element service and domain classes
 */
public interface Published<T extends Published<T>> {

    T publish(Publisher<T> publisher, Observer<String> monitor);
    boolean isPublished();

}
