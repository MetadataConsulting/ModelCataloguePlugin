package org.modelcatalogue.core.regression.nexthoughts

import spock.lang.Stepwise
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import org.modelcatalogue.core.geb.Common
import static org.modelcatalogue.core.geb.Common.create
import static org.modelcatalogue.core.geb.Common.delete
import static org.modelcatalogue.core.geb.Common.description
import static org.modelcatalogue.core.geb.Common.getItem
import static org.modelcatalogue.core.geb.Common.getPick
import static org.modelcatalogue.core.geb.Common.modalHeader
import static org.modelcatalogue.core.geb.Common.modalPrimaryButton
import static org.modelcatalogue.core.geb.Common.modelCatalogueId
import static org.modelcatalogue.core.geb.Common.nameLabel
import static org.modelcatalogue.core.geb.Common.rightSideTitle
import static org.modelcatalogue.core.geb.Common.modalSuccessButton
import spock.lang.Ignore

@Stepwise
@Ignore
class FinalizedDataModelSpec extends AbstractModelCatalogueGebSpec {
    private static final String createDataClass = "catalogue-element-create-dataClass-menu-item-link>span:nth-child(3)"

    def "login and navigate to the model "() {
        when:
        loginCurator()
        select 'Cancer Model'
        selectInTree 'Data Classes'

        then:
        check Common.rightSideTitle contains 'Active Data Classes'
        $("#role_list_create-catalogue-element-menu-item-link").displayed == false
    }
}