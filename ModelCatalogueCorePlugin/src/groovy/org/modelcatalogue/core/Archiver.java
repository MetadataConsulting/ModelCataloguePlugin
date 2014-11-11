package org.modelcatalogue.core;

/**
 * Simple interface to decouple element service and domain classes
 */
public interface Archiver<T extends Published>  {

    T archive(T published);

}
