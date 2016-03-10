# Working with Executor Service

When you want to execute asynchronous code you have to use [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)
provided by [Grails Executor Plugin](https://github.com/basejump/grails-executor). This brings some constraints for what can be run
inside the asynchronous task.

## Working with any entity
You cannot work with the entity reference directly, you have to store it's `id` and get it by that `id` inside the task closure

```
Long id = dataModel.getId()

executorService.submit {
    DataModel theDataModel = DataModel.get(id)
    // do something with data model
}
```

## Working with objects related to request and response
In similar way you have to store all the information from the `request`, `params`, `headers` or `response` needed 
inside the task closure

```
String description = params.description
executorService.submit {
    // do something with description
}
```
