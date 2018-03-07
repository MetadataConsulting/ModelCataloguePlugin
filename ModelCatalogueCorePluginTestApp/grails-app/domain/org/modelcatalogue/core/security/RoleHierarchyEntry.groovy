package org.modelcatalogue.core.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='entry')
@ToString(includes='entry', includeNames=true, includePackage=false)
class RoleHierarchyEntry implements Serializable {

	private static final long serialVersionUID = 1

	String entry

	RoleHierarchyEntry(String entry) {
		this()
		this.entry = entry
	}

	static constraints = {
		entry blank: false, unique: true
	}

	static mapping = {
		cache true
	}
}
