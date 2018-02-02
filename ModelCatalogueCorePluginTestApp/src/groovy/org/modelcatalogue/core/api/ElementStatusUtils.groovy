package org.modelcatalogue.core.api

import groovy.transform.CompileStatic
import org.modelcatalogue.core.dashboard.DashboardDropdown

@CompileStatic
class ElementStatusUtils {
    static List<ElementStatus> of(DashboardDropdown dashboardDropdown) {
        switch ( dashboardDropdown ) {
            case DashboardDropdown.ACTIVE:
                return [ElementStatus.FINALIZED, ElementStatus.DRAFT, ElementStatus.PENDING]

            case DashboardDropdown.FINALIZED:
                return [ElementStatus.FINALIZED]

            case DashboardDropdown.DRAFT:
                return [ElementStatus.DRAFT]

            case DashboardDropdown.DEPRECATED:
                return [ElementStatus.DEPRECATED]
        }
    }
}