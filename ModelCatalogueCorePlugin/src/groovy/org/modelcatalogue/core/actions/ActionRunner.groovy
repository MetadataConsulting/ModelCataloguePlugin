package org.modelcatalogue.core.actions

public interface ActionRunner extends Runnable {

    PrintWriter getOut()
    void setOut(PrintWriter writer)

    void initWith(Map<String, String> params)

}