package org.modelcatalogue.core.util.marshalling

import org.modelcatalogue.core.util.builder.ProgressMonitor

class ProgressMonitorMarshaller extends AbstractMarshaller {


    ProgressMonitorMarshaller() {
        super(ProgressMonitor)
    }

    protected Map<String, Object> prepareJsonMap(el) {
        [log: el.lastMessages, name: el.name]
    }

}




