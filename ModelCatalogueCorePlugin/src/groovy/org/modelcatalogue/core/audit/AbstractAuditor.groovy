package org.modelcatalogue.core.audit

/**
 * Default auditor auditing the changes using the change table/entity.
 */
abstract class AbstractAuditor implements Auditor {

    Long defaultAuthorId
    Long parentChangeId
    Boolean system

}
