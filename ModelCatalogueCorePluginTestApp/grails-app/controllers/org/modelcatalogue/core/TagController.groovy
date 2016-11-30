package org.modelcatalogue.core

import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.RelationshipDirection
import org.modelcatalogue.core.util.lists.Lists
import org.modelcatalogue.core.util.lists.Relationships

class TagController extends AbstractCatalogueElementController<Tag> {

    TagController() {
        super(Tag, false)
    }

    @Override
    protected boolean hasUniqueName() {
        true
    }

}
