package org.modelcatalogue.core.publishing;

import rx.Observer;

/**
 * Simple interface to decouple element service and domain classes
 * ?? This is totally uninformative. Must ask Vlad.
 * Vlad: Used in finalization/draft chain.
 */
public interface Published<T extends Published<T>> {

    T publish(Publisher<T> publisher, Observer<String> monitor);
    boolean isPublished();

}
