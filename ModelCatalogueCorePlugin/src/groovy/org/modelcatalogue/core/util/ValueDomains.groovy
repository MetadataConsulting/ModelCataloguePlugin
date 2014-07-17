package org.modelcatalogue.core.util

import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.ValueDomain

/**
 * Created by ladin on 25.02.14.
 */
class ValueDomains implements ListWrapper<ValueDomain> {

    @Delegate ListWrapper<ValueDomain> list

    Class<ValueDomain> getItemType() { ValueDomain }

}
