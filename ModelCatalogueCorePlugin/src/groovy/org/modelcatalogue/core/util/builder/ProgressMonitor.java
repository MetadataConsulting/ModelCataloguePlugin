package org.modelcatalogue.core.util.builder;

public interface ProgressMonitor {

    void log(String message);

    ProgressMonitor NOOP = new ProgressMonitor() {
        @Override public void log(String message) {}

    };
}
