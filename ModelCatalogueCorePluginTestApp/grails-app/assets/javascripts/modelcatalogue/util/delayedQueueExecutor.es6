
angular.module('mc.util.delayedQueueExecutor', ['mc.util.delayedQueueExecutor'])
    .factory('delayedQueueExecutor', function($q, $timeout){
        class DelayedQueueExecutor {
            constructor(delayBetweenCalls) {
                this.delayBetweenCalls = delayBetweenCalls;
                this.nextStart = new Date().getTime() + delayBetweenCalls;
                this.promises  = []
            }

            submit(fn) {
                const now = new Date().getTime();
                let currentDelay = now - this.nextStart;
                currentDelay = Math.min(currentDelay, this.delayBetweenCalls);
                let nextStart = now + currentDelay + this.delayBetweenCalls;
                let promise = $timeout(fn, currentDelay);
                this.promises.push(promise);
                return promise
            }
        }

        function delayedQueueExecutor(delayBetweenCalls) {
            return new DelayedQueueExecutor(delayBetweenCalls)
        }

        return delayedQueueExecutor
    });
