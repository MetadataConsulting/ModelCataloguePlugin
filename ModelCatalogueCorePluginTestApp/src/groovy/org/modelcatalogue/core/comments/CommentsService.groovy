package org.modelcatalogue.core.comments

import org.modelcatalogue.core.CatalogueElement

public interface CommentsService {
    List<Comment> getComments(CatalogueElement element)
    boolean isForumEnabled()
}
