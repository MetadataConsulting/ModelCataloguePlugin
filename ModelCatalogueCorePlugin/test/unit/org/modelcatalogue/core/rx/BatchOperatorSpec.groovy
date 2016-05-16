package org.modelcatalogue.core.rx

import rx.Observable
import rx.Producer
import rx.Subscriber
import spock.lang.Specification
import spock.util.concurrent.BlockingVariables

class BatchOperatorSpec extends Specification {


    def "request is called in the proper times"() {
        BlockingVariables vars = new BlockingVariables()

        Producer producer = Mock(Producer)

        Observable<Integer> observable = Observable.create{ subscriber ->
            subscriber.setProducer(producer)
            subscriber.onStart()
            99.times { Integer number ->
                subscriber.onNext(number)
            }
            subscriber.onCompleted()
        }

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            void onCompleted() {
                vars.completed = true
            }

            @Override
            void onError(Throwable e) {
                vars.error = e
            }

            @Override
            void onNext(Integer integer) {
                vars.last = integer
            }

        }

        when: "the observable emits 100 items"
        observable.lift(new BatchOperator<Integer>(10)).concatWith(Observable.range(1, 200)).subscribe(subscriber)
        then: "the request is called on the beginning and than after each 10 items"
        vars.completed
        11 * producer.request(10)

    }
}
