package org.modelcatalogue.core

import geb.navigator.Navigator
import geb.spock.GebReportingSpec
import geb.waiting.WaitTimeoutException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.logging.LogEntries
import org.openqa.selenium.logging.LogEntry
import org.openqa.selenium.logging.LogType

abstract class AbstractModelCatalogueGebSpec extends GebReportingSpec {

    // keep the passwords simply stupid, they are only for dev/test or very first setup
    // sauce labs connector for some reason fails with the six in the input
    def loginAdmin() { loginUser("admin", "admin") }
    def loginViewer() { loginUser("viewer", "viewer") }
    def loginCurator() { loginUser("curator", "creator") }

    def cleanup() {
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            println "${new Date(entry.getTimestamp())} ${entry.getLevel()} ${entry.getMessage()}"
        }
    }

    def openDataModel(String dataModelName) {
        go "#/dataModels"

        waitFor {
            title == 'Data Models'
        }

        waitFor {
            $("h3.panel-title", title: dataModelName).displayed
        }

        noStale({$("h3.panel-title", title: dataModelName)}) {
            it.find('a').click()
        }

        waitFor {
            title == dataModelName
        }
    }

    def loginUser(String user, String pwd) {
        go "login/auth"

        $("#username").value(user)
        $("#password").value(pwd)

        $("#loginForm").find("button.btn-primary").click()

        waitFor {
            $("#role_navigation-right_user-menu-menu-item-link").displayed
        }
    }

    boolean waitUntilModalClosed(int timeout = 10) {
        waitFor(timeout){
            !$('.modal-backdrop').displayed
        }
        return true
    }

    /**
     * Selects the first item from catalogue element picker if any element is found.
     * returns true if the element was selected, false otherwise
     */
    boolean selectCepItemIfExists(long waitTime = 3) {
        try {
            waitFor(waitTime) {
                $('.cep-item, .item-found').displayed
            }
            $('.cep-item, .item-found').click()
            return true
        } catch (ignored) {
            return false
        }

    }

    Navigator actionButton(String id, String role = "item") {
        $('#role_' + role + '_' + id + 'Btn, #' + id + 'Btn').last()
    }

    Navigator menuItem(String id, String role = "navigation") {
        if (role == '') {
            return $('#' + id + '-menu-item-link').last()
        }
        return $('#role_' + role + '_' + id + '-menu-item-link').last()
    }

    /**
     * @param row number of row starting 1
     * @param column number of column starting 1
     * @return given cell
     */
    Navigator infTableCell(Map attrs = [:], int row, int column) {
        $(attrs, 'div.inf-table-body tbody tr:nth-child(' + row +') td:nth-child(' + column + ')')
    }


    void toggleInfTableRow(int row) {
        waitFor {
            $('div.inf-table-body tbody tr:nth-child(' + row +') a.inf-cell-expand')
        }
        $('div.inf-table-body tbody tr:nth-child(' + row +') a.inf-cell-expand').click()
    }

    int totalOf(String name) {
        Navigator totalSpan = tab(name).find('span.badge.tab-value-total')
        if (!totalSpan.displayed) {
            return 0
        }
        return totalSpan.text() as Integer
    }

    Navigator tab(String name) {
        $('li', 'data-tab-name': name)
    }

    void selectTab(String name) {
        noStale({ $("li[data-tab-name='$name'] a") }) {
            it.click()
        }
    }


    public <R> R noStale(int maxAttempts = 10, Closure<Navigator> navigatorClosure, Closure<R> resultClosure) {
        int attempt = 0
        Throwable error = null
        Navigator navigator = null
        while (attempt < maxAttempts) {
            attempt++
            try {
                navigator = navigatorClosure()
                waitFor(1) {
                    navigator.displayed
                }
                return resultClosure(navigator)
            } catch (StaleElementReferenceException | WaitTimeoutException e) {
                Thread.sleep(Math.round(Math.pow(2, attempt)))
                error = e
            }
        }
        throw new IllegalArgumentException("Cannot evaluate expression $navigator after $maxAttempts attempts", error)
    }


    boolean tabActive(String name) {
        $("li[data-tab-name='$name'].active").displayed
    }

    /**
     * Fills the metadata with the new values
     * @param newMetadata
     */
    void fillMetadata(Map newMetadata, Navigator parent = null) {
        if (!parent) {
            parent = $('table.soe-table')
        }

        while (parent.find('.soe-table-property-row').size() > 1) {
            parent.find('.soe-table-property-row:first-child .soe-table-property-actions .soe-remove-row').click()
        }

        newMetadata.each { key, value ->
            parent.find('.soe-table-property-row:last-child .soe-table-property-key input').value(key?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-value input').value(value?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-actions .soe-add-row').click()
        }
    }


    File download(String name, String url) {
        File sampleXsd = tmp.newFile(name)
        def out = new BufferedOutputStream(new FileOutputStream(sampleXsd))
        out << new URL(url).openStream()
        out.close()
        sampleXsd
    }

    void goToDetailUsingSearch(String name, String dataModel = null) {
        String qualifiedName = dataModel ? "$name ($dataModel)" : name

        waitFor {
            menuItem('search-menu', 'navigation-right').displayed
        }

        menuItem('search-menu', 'navigation-right').click()

        noStale({ $('.search-lg input#value')}) { searchTerm ->
            searchTerm.value(name)
        }

        waitFor {
            $('a.list-group-item.with-pointer .classified-name', text: qualifiedName).displayed
        }
        noStale({ $('a.list-group-item.with-pointer .classified-name', text: qualifiedName).parent().parent() }) { resultLink ->
            resultLink.click()
        }
    }

    String getCurrentId() {
        def matcher = currentUrl =~ /\/(\d+)(\/)?/

        if (matcher) {
            return matcher[0][1]
        }
        return null
    }

    void fastAction(String name) {
        js.exec('''
            var press = jQuery.Event("keypress");
            press.ctrlKey = true;
            press.which = 0;
            $(document).trigger(press);
        ''')

        noStale({ $('#value')}) { Navigator value ->
            value.value(name)
        }

        noStale({$('a.item-found h4', text: name)}) { Navigator item ->
            item.parent().click()
        }
    }

    def selectInTree(String name) {
        noStale({ $('.catalogue-element-treeview-name', text: name) }) {
            it.click()
        }
    }

}
