package org.modelcatalogue.core.publishing;

/**
 * Simple interface to decouple element service and domain classes
 * Vlad: This is only implemented by ElementService. Used in Finalization/Draft Chain.
 */
public interface Publisher<T extends Published>  {
    T archive(T published, boolean archiveRelationships);
}
