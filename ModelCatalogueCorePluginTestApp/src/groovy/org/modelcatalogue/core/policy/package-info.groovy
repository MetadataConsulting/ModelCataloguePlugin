/** Suite of checkers that catalogue conforms to certain conventions or policies.
 * Notes from Vlad:
 * The checkers (including UniqueChecker) are called from a couple of places:
 * > from AbstractRestfulController and AbstractCatalogueElementController on save or update (see calls of validatePolicies method)
 * > from ElementService when the data model is finalized they are called from within checkFinalizeEligibility method of DataModel*/
package org.modelcatalogue.core.policy
