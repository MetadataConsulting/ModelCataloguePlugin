package org.modelcatalogue.core.geb

import geb.Page
import geb.navigator.Navigator

class CreateDataClassMetadataPage extends Page implements InputUtils, MetadataUtils {
    static at = { $('.modal-body').text().contains('Metadata') }

    static content = {
        topNavigator { $('ul.tutorial-steps').module(CreateDataClassTopNavigatorModule) }
    }

}
