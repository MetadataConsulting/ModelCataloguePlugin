package org.modelcatalogue.core.rx

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataElement
import rx.Observable
import spock.lang.IgnoreIf

@IgnoreIf( { System.getProperty('spock.ignore.slow') })
class ObservablesSpec extends AbstractIntegrationSpec {

    RxService rxService

    def setup() {
        loadFixtures()
    }

    def "create observable from criteria"() {
        Observable<DataElement> elements = rxService.from(DataElement.where {})
        expect:
        elements.count().toBlocking().toFuture().get() == DataElement.count()
    }


}
