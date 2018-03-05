package org.modelcatalogue.core.Regression

import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.geb.DashboardPage
import org.modelcatalogue.core.geb.DataModelPage
import org.modelcatalogue.core.geb.LoginPage
import org.modelcatalogue.core.geb.VersionsPage
import org.modelcatalogue.core.util.MetadataDomain
import org.modelcatalogue.core.util.MetadataDomainEntity
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Ignore

import static org.modelcatalogue.core.geb.Common.getRightSideTitle
import org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
import spock.lang.IgnoreIf
import spock.lang.Stepwise

//@IgnoreIf({ !System.getProperty('geb.env') || System.getProperty('spock.ignore.suiteB')  })
@Stepwise
@Ignore
class CustomMetadataNotCarriedNewVersionSpec extends AbstractModelCatalogueGebSpec {

    private static final String first_row ='tr.inf-table-item-row>td:nth-child(1)'
    private static final String version ='div.active>span:nth-child(2)>span'

    def "login to model catalogue and select version"() {
        when:
        LoginPage loginPage = to LoginPage
        loginPage.login('supervisor', 'supervisor')

        then:
        at DashboardPage

        when:
        DashboardPage dashboardPage = browser.page DashboardPage
        dashboardPage.select('Clinical Tags')

        then:
        at DataModelPage

        when:
        DataModelPage dataModelPage = browser.page DataModelPage
        dataModelPage.treeView.select('Versions')

        then:
        at VersionsPage

        and:
        check rightSideTitle contains 'Clinical Tags History'

        and:
        check first_row contains '0.0.1'
    }
}
