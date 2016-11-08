package org.modelcatalogue.core.util.js

class FrontendConfigurationProviderRegistry {

    List<FrontendConfigurationProvider> providers = []

    String getFrontendConfiguration() {
        StringBuilder sb = new StringBuilder()

        for (FrontendConfigurationProvider provider in providers) {
            sb << provider.javascriptConfiguration << '\n'
        }

        sb.toString()
    }

}
