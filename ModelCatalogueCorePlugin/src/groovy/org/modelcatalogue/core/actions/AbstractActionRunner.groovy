package org.modelcatalogue.core.actions

/**
 * Created by ladin on 04.08.14.
 */
abstract class AbstractActionRunner implements ActionRunner {
    PrintWriter out
    protected Map<String,String> parameters

    @Override
    void initWith(Map<String, String> params) {
        parameters = params
    }
}
