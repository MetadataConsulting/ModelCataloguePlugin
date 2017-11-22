package org.modelcatalogue.core.actions


class IntegrationTestActionRunner extends AbstractActionRunner {

    def elementService

    @Override
    String getMessage() {
        "Integration FirstTestSpec Action Runner"
    }

    @Override
    void run() {
        out << "ElementService is $elementService"
    }
}

