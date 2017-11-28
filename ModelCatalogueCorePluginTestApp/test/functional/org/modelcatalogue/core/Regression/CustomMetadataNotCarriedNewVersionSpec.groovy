package org.modelcatalogue.core.Regression

import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

@IgnoreIf({ !System.getProperty('geb.env') })
@Stepwise
class CustomMetadataNotCarriedNewVersionSpec extends AbstractModelCatalogueGebSpec {

    private static final String first_row ='tr.inf-table-item-row>td:nth-child(1)'
    private static final String version ='div.active>span:nth-child(2)>span'

    def "login to model catalogue and select version"() {
        when:
        loginAdmin()
        select 'Clinical Tags' open 'Versions'

        then:
        check rightSideTitle contains 'Clinical Tags History'

//       and:
//        Thread.sleep(1000L)
//        check version contains '0.0.1'

        and:
        check first_row contains '0.0.1'
    }
}
