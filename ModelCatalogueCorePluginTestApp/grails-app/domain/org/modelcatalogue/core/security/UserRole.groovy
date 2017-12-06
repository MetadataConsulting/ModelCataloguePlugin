package org.modelcatalogue.core.security

import org.apache.commons.lang.builder.HashCodeBuilder
import org.modelcatalogue.core.util.FriendlyErrors

class UserRole implements Serializable {

    private static final long serialVersionUID = 1

    User user
    Role role

    boolean equals(other) {
        if (!(other instanceof UserRole)) {
            return false
        }

        other.user?.id == user?.id &&
                other.role?.id == role?.id
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        if (user) builder.append(user.id)
        if (role) builder.append(role.id)
        builder.toHashCode()
    }

    static UserRole get(long userId, long roleId) {
        find 'from UserRole where user.id=:userId and role.id=:roleId',
                [userId: userId, roleId: roleId]
    }

    static UserRole create(User user, Role role, boolean flush = false) {
        if (!user.readyForQueries) {
            if (!user.name) {
                user.name = user.username
            }
            FriendlyErrors.failFriendlySave(user)
        }
        for (OAuthID authID in user.oAuthIDs) {
            if (!authID.getId()) {
                FriendlyErrors.failFriendlySave(authID)
            }
        }
        UserRole existing = findByUserAndRole(user, role)
        if (existing) {
            return existing
        }
        new UserRole(user: user, role: role).save(flush: flush, insert: true)
    }

    static boolean remove(User user, Role role, boolean flush = false) {
        UserRole instance = findByUserAndRole(user, role)
        if (!instance) {
            return false
        }

        instance.delete(flush: flush)
        true
    }

    static void removeAll(User user) {
        executeUpdate 'DELETE FROM UserRole WHERE user=:user', [user: user]
    }

    static void removeAll(Role role) {
        executeUpdate 'DELETE FROM UserRole WHERE role=:role', [role: role]
    }

    static mapping = {
        version false
    }
}
