package org.modelcatalogue.core.elasticsearch

import org.modelcatalogue.core.security.User

class UserDocumentSerializer extends CatalogueElementDocumentSerializer implements DocumentSerializer<User> {

    Map getDocument(User user) {
        Map ret = super.getDocument(user)

        ret.username = user.username
        ret.email = user.email
        
        return ret
    }

}
