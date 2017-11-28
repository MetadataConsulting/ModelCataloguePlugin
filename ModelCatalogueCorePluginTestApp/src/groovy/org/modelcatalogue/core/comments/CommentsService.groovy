package org.modelcatalogue.core.comments

import org.modelcatalogue.core.CatalogueElement

interface CommentsService {
    List<Comment> getComments(CatalogueElement element)
    boolean isForumEnabled()
}
