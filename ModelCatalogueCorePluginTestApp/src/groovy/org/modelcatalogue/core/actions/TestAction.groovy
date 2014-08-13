package org.modelcatalogue.core.actions

/**
 * Created by ladin on 13.08.14.
 */
class TestAction extends AbstractActionRunner {
    static String description = """
        Test actions to mimic waiting and failures

        Parameters:
            fail: if set to 'true' the action will fail
            timeout: timeout to wait before finishing the execution
    """

    @Override
    String getMessage() {
        """Run test action with parameters $parameters"""
    }

    @Override
    void run() {
        if (parameters.timeout) {
            Long timeout = parameters.timeout as Long
            out << "Waiting for $parameters.timeout as set in parameters\n"
            Thread.sleep(timeout)
        }
        if (parameters.fail == Boolean.TRUE.toString()) {
            fail("Action failed as desired")
        } else {
            out << "Test action finished successfully for $parameters"
        }
    }
}
