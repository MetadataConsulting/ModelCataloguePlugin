package org.modelcatalogue.core.geb

class CatalogueAction {

    private final String role
    private final String id

    private final String child

    static CatalogueAction create(String id) {
        return new CatalogueAction('', id, null)
    }

    static CatalogueAction create(String role, String id) {
        return new CatalogueAction(role, id, null)
    }

    static CatalogueAction create(String parentRole, String parentId, String id) {
        return new CatalogueAction(parentRole, parentId, id)
    }


    private CatalogueAction(String role, String id, String child) {
        this.role = role
        this.id = id
        this.child = child
    }

    void perform(AbstractModelCatalogueGebSpec spec) {
        if (role) {
            spec.click {
                spec.$("#role_${role}_${id}-menu-item-link, #role_${role}_${id}Btn").last()
            }
        } else {
            spec.click {
                spec.$("#${id}-menu-item-link, #${id}Btn, #role_null_${id}Btn").last()
            }
        }
        if (child) {
            spec.click {
                spec.$("#${child}-menu-item-link, #${child}Btn, #role_null_${child}Btn").last()
            }
        }
    }


}
