package org.modelcatalogue.core.util.delayable

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

/**
 * Object wrapper with ability to delay and rollback method calls.
 *
 * This class is designed to be used with various builders where delayed method does not return any value.
 *
 * @param < T > the type of the delegate
 */
class Delayable<T> implements Runnable {

    private final T delegate
    private final List<List<DelayableQueueItem>> queues = [].withDefault { [] }
    private final List<Boolean> runRequests = [].withDefault { Boolean.FALSE }

    private int pauseLevel = -1

    Delayable(T delegate) {
        this.delegate = delegate
    }

    /**
     * All method call will from this call return null and will be queued for later execution using the #run() method.
     */
    void pauseAndRecord() {
        pauseLevel++
    }

    /**
     * @return <code>true</code> if the execution is currently paused
     */
    boolean isPaused() {
        pauseLevel >= 0
    }

    /**
     * @return <code>true</code> if the run is requested
     */
    boolean isRunRequested() {
        runRequests[pauseLevel]
    }

    /**
     * Resets the waiting queue so none of the queued method is invoked on the delegate regardless the run requested state.
     *
     * Do not call this method from within #whilePaused(boolean, Closure) method.
     *
     * @return number of methods queued
     */
    int resetAndUnpause(boolean ignoreNotPaused = false) {
        if (!paused && !ignoreNotPaused) {
            throw new IllegalStateException("Execution is not paused. Run pauseAndRecord() before to start queuing method calls.")
        }
        List<DelayableQueueItem> queue = queues[pauseLevel]
        int ret = queue.size()
        queue.clear()
        runRequests[pauseLevel] = Boolean.FALSE
        pauseLevel--
        return ret
    }

    /**
     * Resets the waiting queue so none of the queued method is invoked on the delegate regardless the run requested state
     * but does not unpause the execution.
     * @return number of methods queued
     */
    int reset(boolean ignoreNotPaused = false) {
        if (!paused && !ignoreNotPaused) {
            throw new IllegalStateException("Execution is not paused. Run pauseAndRecord() before to start queuing method calls.")
        }
        List<DelayableQueueItem> queue = queues[pauseLevel]
        int ret = queue.size()
        queue.clear()
        return ret
    }

    /**
     * Invokes all the queued methods on the delegate regardless the run requested state.
     */
    void run() {
        List<DelayableQueueItem> queue = queues[pauseLevel]
        if (pauseLevel > 0) {
            queues[pauseLevel - 1].addAll(queue)
        } else {
            for (DelayableQueueItem item in queue) {
                delegate.invokeMethod(item.methodName, item.args)
            }
        }
        queue.clear()
        runRequests[pauseLevel] = Boolean.FALSE
        pauseLevel--
    }

    void requestRun() {
        runRequests[pauseLevel] = Boolean.TRUE
    }

    /**
     * Runs only if the run was requested from the last call to the #pauseAndRecord().
     */
    void runIfRequested() {
        if (runRequested) {
            run()
        } else {
            resetAndUnpause(true)
        }
    }

    /**
     * Invokes the method on the delegate if not paused or queues the method for later execution with the #run() method.
     *
     * While paused, you can remove all the queued methods later using #resetAndUnpause().
     *
     * @param name name of the method to be invoked or queued
     * @param args arguments of the method to be invoked or queued
     * @return <code>null</code> if this object is in paused state or the result of method invocation otherwise.
     */
    @Override
    Object invokeMethod(String name, Object args) {
        if (paused) {
            queues[pauseLevel] << new DelayableQueueItem(name, args)
            return null
        }
        delegate.invokeMethod(name, args)
    }

    /**
     * Runs code inside the closure in the paused mode and runs all the pending methods when the closure execution is
     * finished.
     *
     * @param onlyRunWhenRequested only runs the pending methods if requested
     * @param closure closure which will be executed in context of this object with single parameters which is this object
     */
    void whilePaused(boolean onlyRunWhenRequested = false, @ClosureParams(value = FromString, options = 'org.modelcatalogue.core.util.delayable.Delayable') Closure closure) {
        pauseAndRecord()
        with closure
        if (onlyRunWhenRequested) {
            runIfRequested()
        } else {
            run()
        }

    }
}
