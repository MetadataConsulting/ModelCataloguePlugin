# Notes on RxJava

With new ElasticSearch support, [RxJava](https://github.com/ReactiveX/RxJava/wiki) was added to the core project to make working with asynchronous code bearable. The documentation about [ReactiveX](http://reactivex.io/) is very comprehensive but you may miss couple of things on the first sight. Here's some of them I've struggled while creating the ElasticSearchService.

---

### Pretend you care

There are _hot_ and _cold_ observables. _Cold_ doesn't start to emit notifications unless something is subscribed. Observables from iterables are **cold**! Always call `.subscribe()` in case you don't care about the results otherwise no emission may happen.

Following won't call the `someAsyncFunction` method...
```
Observable o = Observables.from(['foo', 'bar']).map { text ->
   return Observables.from(someAsyncFunction(text))
}
```

... unless you call

```
o.subscribe()
```


---

### Transforming emitted results to something useful

In [RxJava](https://github.com/ReactiveX/RxJava/wiki) there are two useful method to transform the emission from the `Observable`.

If you want to transform the value synchronously use `map`:

```
Observables.from(['foo', 'bar']).map { text ->
   return text.toUpperCase()
}
```

If you want to transform the value asynchronously use `flatMap`:

```
Observables.from(['foo', 'bar']).flatMap { text ->
   return Observables.from(someAsyncFunction(text))
}
```

---
### Grails does not play well with Groovy yet

Sadly, I wasn't able to use RxGroovy as all the extension module additions are not present in the tests in Grails 2.3.4 (or any 2.x version later).
I was able to use RxJava with closures but if you use typed parameters the compilation starts to fail.

Following won't compile ...

```
Observables.from(['foo', 'bar']).map { String text ->
   ...
}
```

... but following does!


```
Observables.from(['foo', 'bar']).map { text ->
   ...
}
```

### Don't forget to inform the Subject about any exception thrown

If you create your own `Observable` with any subtype of `Subject` you need to take care of proper exception handling. If an exception is thrown in a way that `subject.onComplete()` is never called the application hangs. Wrap the emitting code to `try ... catch` blocks to propagate the exception properly.


```
ReplaySubject<Document> subject = ReplaySubject.create()
executorService.submit {
     try {
         subject.onNext(dangeoursMethod())
         subject.onCompleted()
     } catch (Exception e) {
         subject.onError(e)
     }

}
return subject

```
