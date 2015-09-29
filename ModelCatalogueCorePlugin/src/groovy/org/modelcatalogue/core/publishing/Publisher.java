package org.modelcatalogue.core.publishing;

/**
 * Simple interface to decouple element service and domain classes
 */
public interface Publisher<T extends Published>  {
    T archive(T published, boolean archiveRelationships);
}
