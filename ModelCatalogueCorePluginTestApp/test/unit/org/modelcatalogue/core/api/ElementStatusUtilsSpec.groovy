package org.modelcatalogue.core.api

import spock.lang.Specification
import spock.lang.Unroll

class ElementStatusUtilsSpec extends Specification {

    @Unroll
    def "for #status => #statuses "(String status, List<ElementStatus> statuses) {
        expect:
        statuses == ElementStatusUtils.findAllElementStatus(status)

        where:
        status   | statuses
        null     | [ElementStatus.DRAFT, ElementStatus.UPDATED, ElementStatus.PENDING, ElementStatus.FINALIZED, ElementStatus.REMOVED, ElementStatus.DEPRECATED]
        'active' | [ElementStatus.FINALIZED, ElementStatus.DRAFT]
        'ACTIVE' | [ElementStatus.FINALIZED, ElementStatus.DRAFT]
        'draft'  | [ElementStatus.DRAFT]
    }
}
