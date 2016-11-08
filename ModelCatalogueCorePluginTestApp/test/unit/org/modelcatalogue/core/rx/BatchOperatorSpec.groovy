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

        Observable<Integer> observable = createObservable(99, producer)
        Subscriber<Integer> subscriber = createSubscriber(vars)

        when: "the observable emits 100 items"
        observable.lift(new BatchOperator<Integer>(10)).concatWith(Observable.range(1, 200)).subscribe(subscriber)
        then: "the request is called on the beginning and than after each 10 items"
        vars.completed
        vars.last == 200
        11 * producer.request(10)

    }


    def "concat two observables with batch"() {
        BlockingVariables vars = new BlockingVariables()

        Producer producer1 = Mock(Producer)
        Producer producer2 = Mock(Producer)

        Observable<Integer> observable1 = createObservable(99, producer1).lift(new BatchOperator<Integer>(10)).onBackpressureBuffer()
        Observable<Integer> observable2 = createObservable(76, producer2).lift(new BatchOperator<Integer>(7)).onBackpressureBuffer()
        Subscriber<Integer> subscriber = createSubscriber(vars)

        when: "the observable emits 100 items"
        observable1.concatWith(observable2).subscribe(subscriber)
        then: "the request is called on the beginning and than after each 10 items"
        vars.completed
        vars.last == 75
        11 * producer1.request(10)
        12 * producer2.request(7)
    }

    private static Subscriber<Integer> createSubscriber(BlockingVariables vars) {
        new Subscriber<Integer>() {
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
                Thread.sleep(10) // to trigger backpressure
            }

        }
    }

    private static Observable<Integer> createObservable(int count, Producer producer) {
        Observable<Integer> observable = Observable.create { subscriber ->
            subscriber.setProducer(producer)
            subscriber.onStart()
            count.times { Integer number ->
                subscriber.onNext(number)
            }
            subscriber.onCompleted()
        }
        observable
    }

}
