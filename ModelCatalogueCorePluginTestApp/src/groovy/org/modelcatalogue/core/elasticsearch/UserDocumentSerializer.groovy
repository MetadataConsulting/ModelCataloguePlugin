package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.security.User

class UserDocumentSerializer extends CatalogueElementDocumentSerializer<User> {

    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, User user, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, user, builder)

        safePut(builder, 'username',  user.username)
        safePut(builder, 'email',  user.email)
        
        return builder
    }
}
