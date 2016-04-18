package org.modelcatalogue.core.util.delayable

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import groovy.util.logging.Log4j

/**
 * Object wrapper with ability to delay and rollback method calls.
 *
 * This class is designed to be used with various builders where delayed method does not return any value.
 *
 * @param < T > the type of the delegate
 */
@Log4j
class Delayable<T>  {

    private final T delegate
    private final List<List<DelayableQueueItem>> queues = [].withDefault { [] }
    private final List<Boolean> runRequests = [].withDefault { Boolean.FALSE }
    private final List<Boolean> runInProgress = [].withDefault { Boolean.FALSE }

    private int pauseLevel = -1

    Delayable(T delegate) {
        this.delegate = delegate
    }

    /**
     * All method call will from this call return null and will be queued for later execution using the #run() method.
     */
    protected void pauseAndRecord() {
        pauseLevel++
    }

    /**
     * @return <code>true</code> if the execution is currently paused
     */
    protected boolean isPaused() {
        pauseLevel >= 0
    }

    /**
     * @return <code>true</code> if the run is requested
     */
    protected boolean isRunRequested() {
        runRequests[pauseLevel]
    }

    /**
     * Resets the waiting queue so none of the queued method is invoked on the delegate regardless the run requested state.
     *
     * Do not call this method from within #whilePaused(boolean, Closure) method.
     *
     * @return number of methods queued
     */
    protected int resetAndUnpause(boolean ignoreNotPaused = false) {
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
    protected int reset(boolean ignoreNotPaused = false) {
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
    protected void run() {
        List<DelayableQueueItem> queue = queues[pauseLevel]
        if (pauseLevel > 0 && !runInProgress[pauseLevel - 1]) {
            queues[pauseLevel - 1].addAll(queue)
        } else {
            runInProgress[pauseLevel] = Boolean.TRUE
            for (DelayableQueueItem item in queue) {
                delegate.invokeMethod(item.methodName, item.args)
            }
            runInProgress[pauseLevel] = Boolean.FALSE
        }
        queue.clear()
        runRequests[pauseLevel] = Boolean.FALSE
        pauseLevel--
    }

    void requestRun() {
        for (int i in 0..pauseLevel) {
            runRequests[i] = Boolean.TRUE
        }
    }

    /**
     * Runs only if the run was requested from the last call to the #pauseAndRecord().
     */
    protected void runIfRequested() {
        if (runRequested) {
            run()
        } else {
            resetAndUnpause(true)
        }
    }

    /**
     * Invokes the method on the delegate if not paused or if the name does not match any factory or queues the method
     * for later execution with the #run() method.
     *
     * While paused, you can remove all the queued methods later using #resetAndUnpause().
     *
     * @param name name of the method to be invoked or queued
     * @param args arguments of the method to be invoked or queued
     * @return <code>null</code> if this object is in paused state or the result of method invocation otherwise.
     */
    @Override
    Object invokeMethod(String name, Object args) {
        if (paused && !runInProgress[pauseLevel]) {
            queues[pauseLevel] << new DelayableQueueItem(name, args)
            return null
        }
        delegate.invokeMethod(name, args)
    }


    void whilePaused(@ClosureParams(value = FromString, options = 'Delayable<T>') Closure closure) {
        pauseAndRecord()
        try {
            closure this
        } catch(Exception e) {
            reset(true)
            throw e
        }
        runIfRequested()
    }

    // TODO: publish more API


}
