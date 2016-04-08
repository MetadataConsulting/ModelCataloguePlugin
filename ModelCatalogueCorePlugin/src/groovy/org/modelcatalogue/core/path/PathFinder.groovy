package org.modelcatalogue.core.path

import org.modelcatalogue.core.CatalogueElement

class PathFinder {
    List<String> findPath(CatalogueElement element, List<String> ret = [element.link]) {
        Long parentId = element.getFirstParentId()
        if (parentId) {
            CatalogueElement parent = CatalogueElement.get(parentId)
            ret.add(0, parent.link)
            return findPath(parent, ret)
        }
        return ret
    }
}
