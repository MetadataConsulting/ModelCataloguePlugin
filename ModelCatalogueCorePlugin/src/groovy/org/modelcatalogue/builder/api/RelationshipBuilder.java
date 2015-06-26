package org.modelcatalogue.builder.api;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.modelcatalogue.core.api.CatalogueElement;

/**
 * RelationshipBuilder is supplementary class to CatalogueBuilder handling part of the DSL dealing with creating
 * relationships.
 */
public interface RelationshipBuilder {
    /**
     * Specifies the destination of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param classification classification of the destination
     * @param name           name of the destination
     * @param extensions     closure defining the metadata
     */
    void to(String classification, String name, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #to(String, String, Closure)
     */
    void to(String classification, String name);

    /**
     * Specifies the destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param name       name of the destination
     * @param extensions closure defining the metadata
     */
    void to(String name, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #to(String, Closure)
     */
    void to(String name);

    /**
     * Specifies the destination of the relationship created by given proxy. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param element      proxy of the destination
     * @param extensions closure defining the metadata
     */
    void to(CatalogueElement element, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #to(CatalogueElement, Closure)
     */
    void to(CatalogueElement element);

    /**
     * Specifies the source of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param classification classification of the source
     * @param name           name of the source
     * @param extensions     closure defining the metadata
     */
    void from(String classification, String name, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #from(String, String, Closure)
     */
    void from(String classification, String name);

    /**
     * Specifies the source of the relationship created by given classification and name. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param name           name of the source
     * @param extensions     closure defining the metadata
     */
    void from(String name, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #from(String, Closure)
     */
    void from(String name);

    /**
     * Specifies the source of the relationship created by given proxy. The metadata for this
     * relationship can be specified inside the extensions closure.
     *
     * @param element      proxy of the source
     * @param extensions closure defining the metadata
     */
    void from(CatalogueElement element, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #from(CatalogueElement, Closure)
     */
    void from(CatalogueElement element);

    /**
     * Specifies type hint for the destination. Continue with #called(String, String) or #called(String) to
     * create the relationship.
     *
     * @param domain expected type of the destination
     * @return self
     */
    RelationshipBuilder to(BuilderKeyword domain);

    /**
     * Specifies type hint for the source. Continue with #called(String, String) or #called(String) to
     * create the relationship.
     *
     * @param domain expected type of the source
     * @return self
     */
    RelationshipBuilder from(BuilderKeyword domain);

    /**
     * Specifies the source or destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure. #from(BuilderKeyword) or #to(BuilderKeyword) must be called before)
     * calling this method.
     *
     * @param name       name of the source or destination
     * @param extensions closure defining the metadata
     */
    void called(String name, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #called(String, Closure)
     */
    void called(String name);

    /**
     * Specifies the source or destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure. #from(BuilderKeyword) or #to(BuilderKeyword) must be called before)
     * calling this method.
     *
     * @param classification classification of the source or destination
     * @param name           name of the source or destination
     * @param extensions     closure defining the metadata
     */
    void called(String classification, String name, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #called(String, String, Closure)
     */
    void called(String classification, String name);
}
