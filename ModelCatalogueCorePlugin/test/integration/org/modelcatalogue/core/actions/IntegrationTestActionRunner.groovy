package org.modelcatalogue.core.actions


class IntegrationTestActionRunner extends AbstractActionRunner {

    def publishedElementService

    @Override
    String getMessage() {
        "Integration Test Action Runner"
    }

    @Override
    void run() {
        out << "PublishedElementService is $publishedElementService"
    }
}

