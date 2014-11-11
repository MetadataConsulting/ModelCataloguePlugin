package org.modelcatalogue.core;

/**
 * Simple interface to decouple element service and domain classes
 */
public interface Published<T extends Published<T>> {

    T publish(Archiver<T> archiver);

    boolean isPublished();

}
