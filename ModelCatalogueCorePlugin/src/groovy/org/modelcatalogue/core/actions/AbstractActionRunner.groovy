package org.modelcatalogue.core.actions

/**
 * Created by ladin on 04.08.14.
 */
abstract class AbstractActionRunner implements ActionRunner {
    PrintWriter out
    protected Map<String,String> parameters = [:]

    private boolean failed = false


    @Override
    void initWith(Map<String, String> params) {
        parameters = params
    }

    @Override
    Map<String, String> validate(Map<String, String> params) { [:] }

    @Override
    boolean isFailed() {
        return failed
    }

    protected void fail(String message = null) {
        if (message && out) {
            out << message
            out << '\n\n'
        }
        failed = true
    }
}
