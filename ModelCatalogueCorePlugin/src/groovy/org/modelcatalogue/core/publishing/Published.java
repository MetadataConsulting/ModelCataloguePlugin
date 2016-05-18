package org.modelcatalogue.core.publishing;

import org.modelcatalogue.core.util.builder.ProgressMonitor;

/**
 * Simple interface to decouple element service and domain classes
 */
public interface Published<T extends Published<T>> {

    T publish(Publisher<T> publisher, ProgressMonitor monitor);
    boolean isPublished();

}
