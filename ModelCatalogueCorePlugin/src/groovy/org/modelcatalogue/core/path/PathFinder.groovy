package org.modelcatalogue.core.path

import org.modelcatalogue.core.CatalogueElement

class PathFinder {
    List<Long> findPath(CatalogueElement element, List<Long> ret = []) {
        Long parentId = element.getFirstParentId()
        if (parentId) {
            ret.add(0, parentId)
            return findPath(CatalogueElement.get(parentId), ret)
        }
        return ret
    }
}
