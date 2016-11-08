package org.modelcatalogue.core.actions

import groovy.transform.Canonical

/**
 * Created by ladin on 07.08.14.
 */
@Canonical class ActionResult {
    /**
     * The message printed to the user describing the result of the execution or failure.
     */
    String outcome

    /**
     * Whether the action failed or not.
     */
    boolean failed

    /**
     * Optional String result which is consumed by dependant actions if there are any.
     */
    String result
}
