package org.modelcatalogue.core.util

import org.modelcatalogue.core.PublishedElement

/**
 * Created by ladin on 25.02.14.
 */
class PublishedElements implements ListWrapper<PublishedElement>, HasListWrapper<PublishedElement>{

    @Delegate ListWrapper<PublishedElement> list

    Class<PublishedElement> getItemType() { PublishedElement }

}
