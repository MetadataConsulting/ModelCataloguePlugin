package org.modelcatalogue.core.api.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.modelcatalogue.core.CatalogueElement;

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
    void to(Catalogizable element, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #to(Catalogizable, Closure)
     */
    void to(Catalogizable element);

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
    <T extends CatalogueElement> void from(Catalogizable element, @DelegatesTo(RelationshipConfiguration.class) Closure extensions);

    /**
     * @see #from(Catalogizable, Closure)
     */
    <T extends CatalogueElement> void from(Catalogizable element);

    /**
     * Specifies type hint for the destination. Continue with #called(String, String) or #called(String) to
     * create the relationship.
     *
     * @param domain expected type of the destination
     * @return self
     */
    RelationshipBuilder to(Class domain);

    /**
     * Specifies type hint for the source. Continue with #called(String, String) or #called(String) to
     * create the relationship.
     *
     * @param domain expected type of the source
     * @return self
     */
    RelationshipBuilder from(Class domain);

    /**
     * Specifies the source or destination of the relationship created by given name. The metadata for this
     * relationship can be specified inside the extensions closure. #from(Class) or #to(Class) must be called before)
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
     * relationship can be specified inside the extensions closure. #from(Class) or #to(Class) must be called before)
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
