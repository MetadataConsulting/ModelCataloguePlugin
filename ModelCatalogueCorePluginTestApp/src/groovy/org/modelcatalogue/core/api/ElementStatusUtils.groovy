package org.modelcatalogue.core.api

import com.google.common.collect.ImmutableList
import groovy.transform.CompileStatic
import org.modelcatalogue.core.dashboard.DashboardStatusDropdown

@CompileStatic
class ElementStatusUtils {
    static List<ElementStatus> of(DashboardStatusDropdown dashboardDropdown) {
        switch ( dashboardDropdown ) {
            case DashboardStatusDropdown.ACTIVE:
                return [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]

            case DashboardStatusDropdown.FINALIZED:
                return [ElementStatus.FINALIZED]

            case DashboardStatusDropdown.DRAFT:
                return [ElementStatus.DRAFT]

            case DashboardStatusDropdown.DEPRECATED:
                return [ElementStatus.DEPRECATED]
        }
    }

    static List<ElementStatus> findAllElementStatus(String status) {
        if (!status) {
            return ImmutableList.copyOf(ElementStatus.values().toList())
        }
        if (status.toLowerCase() == 'active') {
            return ImmutableList.of(ElementStatus.FINALIZED, ElementStatus.DRAFT)
        }
        ImmutableList.of(ElementStatus.valueOf(status.toUpperCase()))
    }
}