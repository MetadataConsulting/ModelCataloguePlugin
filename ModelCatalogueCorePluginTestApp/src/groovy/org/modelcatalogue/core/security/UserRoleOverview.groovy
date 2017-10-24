package org.modelcatalogue.core.security

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
@CompileStatic
class UserRoleOverview {
    Set<String> generalAuthorities = []
    Set<DataModelAuthorities> dataModelAuthorities = []

    static UserRoleOverview of(List<UserRole> userRoleList) {
        UserRoleOverview userRoleOverview = new UserRoleOverview()
        Map<Long, DataModelAuthorities> m = [:]
        for (UserRole userRole : userRoleList ) {
            if ( userRole.dataModel ) {
                if ( !m[userRole.dataModel.id] ) {
                    DataModelAuthorities dma = new DataModelAuthorities(id: userRole.dataModel.id as Long,
                                                                        authorities: [userRole.role.authority] as Set<String>)
                    m[userRole.dataModel.id] = dma
                } else {
                    m[userRole.dataModel.id].authorities << userRole.role.authority
                }
            } else {
                userRoleOverview.generalAuthorities << userRole.role.authority
            }
        }
        userRoleOverview.dataModelAuthorities = m.values() as Set<DataModelAuthorities>
        userRoleOverview
    }


}
