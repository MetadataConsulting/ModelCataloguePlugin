package org.modelcatalogue.core.publishing

import spock.lang.Specification
import spock.lang.Unroll

class DraftContextSpec extends Specification {

    @Unroll
    def "for version #original is the next version #suggested"() {
        expect:
        PublishingContext.nextPatchVersion(original) == suggested

        where:
        original    | suggested
        null        | '0.0.2'    // threat the original as 0.0.1
        '0.0.1'     | '0.0.2'
        '5'         | '5.0.1'
        '6.0'       | '6.0.1'
        '6.1'       | '6.1.1'
        '7.4.8'     | '7.4.9'
        '7.4.8-rc1' | '7.4.8-rc2'
        '7.4.8-a01' | '7.4.8-a02'
        'foo'       | 'foo-01'
        'foo-99'    | 'foo-100'
    }

}
