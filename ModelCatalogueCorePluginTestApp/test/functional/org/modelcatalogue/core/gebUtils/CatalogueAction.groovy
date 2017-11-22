package org.modelcatalogue.core.gebUtils

import geb.navigator.Navigator

class CatalogueAction {

    private final String role
    private final String id
    private final String child
    private final boolean first

    static CatalogueAction runLast(String id) {
        return new CatalogueAction('', id, null, false)
    }

    static CatalogueAction runLast(String role, String id) {
        return new CatalogueAction(role, id, null, false)
    }

    static CatalogueAction runLast(String parentRole, String parentId, String id) {
        return new CatalogueAction(parentRole, parentId, id, false)
    }

    static CatalogueAction runFirst(String id) {
        return new CatalogueAction('', id, null, true)
    }

    static CatalogueAction runFirst(String role, String id) {
        return new CatalogueAction(role, id, null, true)
    }

    static CatalogueAction runFirst(String parentRole, String parentId, String id) {
        return new CatalogueAction(parentRole, parentId, id, true)
    }

    public CatalogueAction first() {
        if (first) {
            return this
        }
        return new CatalogueAction(role, id, child, true)
    }

    public CatalogueAction last() {
        if (!first) {
            return this
        }
        return new CatalogueAction(role, id, child, false)
    }

    private CatalogueAction(String role, String id, String child, boolean first) {
        this.role = role
        this.id = id
        this.child = child
        this.first = first
    }

    private firstOrLast(Navigator nav) {
        if (first) {
            return nav.first()
        }
        return nav.last()
    }

    void perform(AbstractModelCatalogueGebSpec spec) {
        if (role) {
            spec.click {
                firstOrLast spec.$("#role_${role}_${id}-menu-item-link, #role_${role}_${id}Btn")

            }
        } else {
            spec.click {
                firstOrLast spec.$("#${id}-menu-item-link, #${id}Btn, #role_null_${id}Btn")
            }
        }
        if (child) {
            spec.click {
                firstOrLast spec.$("#${child}-menu-item-link, #${child}Btn, #role_null_${child}Btn")
            }
        }
    }

    String toSelector() {
        if (child) {
            throw new IllegalStateException("Child actions cannot be converted into selectors")
        }
        if (role) {
            return "#role_${role}_${id}-menu-item-link, #role_${role}_${id}Btn"
        }
        "#${id}-menu-item-link, #${id}Btn, #role_null_${id}Btn"
    }

    String toParentSelector() {
        if (role) {
            return "#role_${role}_${id}-menu-item-link, #role_${role}_${id}Btn"
        }
        "#${id}-menu-item-link, #${id}Btn, #role_null_${id}Btn"
    }


}
