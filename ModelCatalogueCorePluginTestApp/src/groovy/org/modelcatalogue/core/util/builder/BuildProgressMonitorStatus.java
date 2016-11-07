package org.modelcatalogue.core.util.builder;

import org.modelcatalogue.core.api.ElementStatus;

public enum BuildProgressMonitorStatus {
    RUNNING {
        @Override
        public ElementStatus toElementStatusEquivalent() {
            return ElementStatus.PENDING;
        }
    },
    FINISHED {
        @Override
        public ElementStatus toElementStatusEquivalent() {
            return ElementStatus.FINALIZED;
        }
    },
    FAILED {
        @Override
        public ElementStatus toElementStatusEquivalent() {
            return ElementStatus.DEPRECATED;
        }
    };


    public abstract ElementStatus toElementStatusEquivalent();
}
