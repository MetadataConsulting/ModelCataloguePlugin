package org.modelcatalogue.core.util.builder

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

import java.util.concurrent.TimeUnit

class BuildProgressMonitor implements Serializable, ProgressMonitor {

    static BuildProgressMonitor create(Serializable key, int stackSize = 5) {
        BuildProgressMonitor monitor = new BuildProgressMonitor(stackSize)
        MONITORS_CACHE.put(key, monitor)
        monitor
    }

    static BuildProgressMonitor get(Serializable key) {
        MONITORS_CACHE.getIfPresent(key)
    }

    static BuildProgressMonitor remove(Serializable key) {
        BuildProgressMonitor monitor = MONITORS_CACHE.getIfPresent(key)
        MONITORS_CACHE.invalidate(key)
        return monitor
    }

    private static final Cache<Serializable, BuildProgressMonitor> MONITORS_CACHE = CacheBuilder.newBuilder().initialCapacity(20).expireAfterAccess(30, TimeUnit.MINUTES).build()

    private final int stackSize
    private final String[] stack
    private int pointer = 0

    BuildProgressMonitor(int stackSize) {
        this.stackSize = stackSize
        this.stack = new String[stackSize]
    }


    void log(String message) {
        stack[pointer++] = message

        if (pointer == stackSize) {
            pointer = 0
        }
    }

    String getLastMessages() {
        StringBuilder builder = new StringBuilder()
        int currentPointer = pointer

        for (int i = 0; i < stackSize ; i++) {
            String message = stack[(i + currentPointer) % stackSize]
            if (message) {
                builder.append(message).append('\n')
            }
        }
        String result = builder.toString()

        if (!result) {
            return "Waiting for new progress to be logged"
        }
        return result
    }

}


