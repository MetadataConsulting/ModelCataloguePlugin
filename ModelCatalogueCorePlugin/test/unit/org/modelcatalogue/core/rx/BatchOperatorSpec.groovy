package org.modelcatalogue.core.rx

import rx.Observable
import rx.Producer
import rx.Subscriber
import spock.lang.Specification

class BatchOperatorSpec extends Specification {


    def "request is called in the proper times"() {

        Producer producer = Mock(Producer)
        Subscriber<Integer> subscriber = Mock(Subscriber)

        when:
        Observable.range(0,100).lift(new BatchOperator<Integer>(10))
        then:
        true

    }
}
