package org.modelcatalogue.core.api

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
}