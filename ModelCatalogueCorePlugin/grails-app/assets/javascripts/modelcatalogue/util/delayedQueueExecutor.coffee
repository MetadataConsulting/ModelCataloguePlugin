angular.module('mc.util.delayedQueueExecutor', ['mc.util.delayedQueueExecutor']).factory 'delayedQueueExecutor',  [ '$timeout', ($timeout) ->
  class DelayedQueueExecutor


    constructor: (delayBetweenCalls) ->
      nextStart = new Date().getTime() + delayBetweenCalls
      promises  = []

      @submit = (fn) ->

        now          = new Date().getTime()
        currentDelay = now - nextStart
        currentDelay = Math.min(currentDelay, delayBetweenCalls)
        nextStart    = now + currentDelay + delayBetweenCalls
        promise      = $timeout(fn, currentDelay)
        promises.push(promise)
        promise


  delayedQueueExecutor = (delayBetweenCalls) -> new DelayedQueueExecutor(delayBetweenCalls)
  # code completion works better when assigned to variable
  delayedQueueExecutor
]