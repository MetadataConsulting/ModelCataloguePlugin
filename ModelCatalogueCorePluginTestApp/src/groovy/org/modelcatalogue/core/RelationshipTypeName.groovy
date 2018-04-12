package org.modelcatalogue.core

import groovy.transform.CompileStatic

@CompileStatic
enum RelationshipTypeName {
    CONTAINEMENT("containment"),
    INVOLVEDNESS("involvedness"),
    RULE_CONTEXT("ruleContext"),
    DECLARATION("declaration"),
    FAVOURITE("favourite"),
    SYNONYM("synonym"),
    RELATED_TO("relatedTo"),
    HIERARCHY("hierarchy"),
    SUPERSESSION("supersession"),
    ORIGIN("origin"),
    BASE("base"),
    IMPORT("import"),
    TAG("tag"),

    final String name

    private RelationshipTypeName(String name) {
        this.name = name
    }

    String toString() {
        this.name
    }
}
