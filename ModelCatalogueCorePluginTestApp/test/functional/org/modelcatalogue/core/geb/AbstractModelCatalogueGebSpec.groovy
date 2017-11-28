package org.modelcatalogue.core.geb

import geb.Browser
import geb.navigator.Navigator
import geb.spock.GebReportingSpec
import geb.waiting.WaitTimeoutException
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import org.openqa.selenium.logging.LogEntries
import org.openqa.selenium.logging.LogEntry
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.Keys

import static org.modelcatalogue.core.geb.Common.*
abstract class AbstractModelCatalogueGebSpec extends GebReportingSpec {

    // keep the passwords simply stupid, they are only for dev/test or very first setup
    // sauce labs connector for some reason fails with the six in the input
    def loginAdmin() { loginUser("admin", "admin") }
    def loginViewer() { loginUser("viewer", "viewer") }
    def loginCurator() { loginUser("curator", "curator") }

    def login(ApplicationUser user) {
        loginUser(user.username, user.password)
    }

    private void refreshIfConnectionLost() {
        noStale(1, true, {$('strong', text: contains('Connection lost'))}, {
            if (it.first().displayed) {
                refresh browser
            }
        }, false)
    }

    DataModelNavigator select(String dataModelName, boolean latest = false) {

        for (int i = 0; i < 10; i++) {
            try {
                go "#/dataModels?type=catalogue&q=${URLEncoder.encode(dataModelName, 'UTF-8')}"

                waitFor {
                    title == 'Data Models'
                }
                waitFor {
                    $("h3.panel-title", title: dataModelName).first().displayed
                }
                break
            } catch (e) {
                if (i == 9) {
                    throw e
                }
            }
        }


        for (int i = 0; i < 10; i++) {
            try {
                noStale({ latest ? $("h3.panel-title", title: dataModelName).last() : $("h3.panel-title", title: dataModelName).first() }) {
                    it.find('a.full-width-link').first().click()
                }
                check rightSideTitle contains dataModelName
                break
            } catch (e) {
                if (i == 9) {
                    throw e
                }
            }

        }

        return new DataModelNavigator(this)
    }

    def loginUser(String user, String pwd) {
        browser.driver.manage().window().maximize()
        go "logout"

        waitFor (120) {
            $("i.glyphicon.glyphicon-log-in").first().displayed
        }

        go "login/auth"

        $("#username").value(user)
        $("#password").value(pwd)
        $("input[name=_spring_security_remember_me]").value(true)

        $("#loginForm").find("button.btn-primary").first().click()

        go "#/dataModels"

        waitFor {
            $("#role_navigation-right_user-menu-menu-item-link").first().displayed
        }
        check "#my-models" displayed
    }

    boolean waitUntilModalClosed(int timeout = 10) {
        waitFor(timeout){
            !$('.modal-backdrop').first().displayed
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
                $('.cep-item:not(.show-more-cep-item), .item-found').first().displayed
            }
            $('.cep-item:not(.show-more-cep-item), .item-found').first().click()
            return true
        } catch (ignored) {
            return false
        }

    }

    FormFiller fill(CatalogueContent content) {
        fill { content.select(this) }
    }


    FormFiller fill(String nameOrId) {
        remove messages
        Closure<Navigator> navigator =  { $("input[name=$nameOrId], #$nameOrId") }
        if (nameOrId.contains('#') || nameOrId.startsWith('.')) {
            navigator = { $(nameOrId) }
        }
        new FormFiller(this, navigator)
    }

    boolean remove(Keywords keyword) {
        if (keyword == Keywords.MESSAGES) {
            (driver as JavascriptExecutor).executeScript('angular.element(\'.messages-panel.growl\').html(\'\')')
            return true
        }
        throw new IllegalArgumentException("Only 'messages' supported")
    }

    FormFiller fill(Closure<Navigator> navigatorClosure) {
        new FormFiller(this, navigatorClosure)
    }

    NavigatorCondition check(Map<String, Object> attributes, String selector) {
        check({$(attributes, selector)})
    }

    NavigatorCondition check(CatalogueAction action) {
        check(action.toSelector())
    }

    NavigatorCondition check(String selector) {
        check({$(selector)})
    }

    /**
     * @deprecated may produce stale element reference exceptions
     */
    NavigatorCondition check(Navigator navigator) {
        new NavigatorCondition(this, { navigator })
    }

    NavigatorCondition check(Closure<Navigator> navigator) {
        new NavigatorCondition(this, navigator)
    }

    NavigatorCondition check(CatalogueContent content) {
        if (!content.beforeSelect) {
            return check({ content.select(this) })
        }

        AbstractModelCatalogueGebSpec self = this

        check({
            self.with content.beforeSelect
            content.select(this)
        })
    }

    boolean no(Navigator navigator) {
        waitFor {
            !navigator.first().displayed
        }
    }

    /**
     * @deprecated may produce stale references
     */
    @Deprecated
    void click(Navigator navigator) {
        click { navigator }
    }

    PositionalClick click(Keywords first) {
        if (first == Keywords.FIRST) {
            return new PositionalClick(true, this)
        }
        if (first == Keywords.LAST) {
            return new PositionalClick(false, this)
        }
        throw new IllegalArgumentException("Only 'first' or 'last keyword supported")
    }

    void click(CatalogueAction action) {
        scroll action.toParentSelector()
        action.perform(this)
    }

    void click(CatalogueContent content) {
        scroll content.selector
        click { content.select(this) }
    }

    void click(Closure<Navigator> navigatorClosure) {
        try {
            noStale(navigatorClosure) {
                it.first().click()
            }
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("${navigatorClosure()} didn't return any results after a 10 attempts", iae)
        }
    }

    void click(String idOrSelector) {
        scroll idOrSelector
        try {
            click {
                $(idOrSelector)
            }
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Cannot click action element $idOrSelector", iae)
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
        check { $('div.inf-table-body tbody tr:nth-child(' + row +') a.inf-cell-expand span.fa-plus-square-o') } displayed
        click { $('div.inf-table-body tbody tr:nth-child(' + row +') a.inf-cell-expand') }
        check { $('div.inf-table-body tbody tr:nth-child(' + row +') a.inf-cell-expand span.fa-minus-square-o') } displayed
    }

    int totalOf(String name) {
        int result = noStale(5, -1, { tab(name).find('span.badge.tab-value-total') }) {
            if (!it.first().displayed) {
                // cannot be null, otherwise the wait block will fail
                return -1
            }
            it.first().text() to Integer
        }
        if (result == -1) {
            return 0
        }
        return result
    }

    Navigator tab(String name) {
        $('li', 'data-tab-name': name)
    }

    String tabTotal(String name) {
        "li[data-tab-name=$name] span.badge.tab-value-total"
    }

    void selectTab(String name) {
        remove messages
        5.times {
            try {
                noStale({ $("li[data-tab-name='$name'] a") }) {
                    it.first().click()
                }
                waitFor { tabActive(name) }
            } catch (e) {
                if (it >= 4) {
                    throw e
                }
            }

        }

    }


    public <R> R noStale(int maxAttempts = 5, R defaultValue = null, Closure<Navigator> navigatorClosure, Closure<R> resultClosure, boolean refreshOnConnectionLost = true) {
        int attempt = 0
        Throwable error = null
        Navigator navigator = null
        while (attempt < maxAttempts) {
            attempt++
            try {
                navigator = navigatorClosure()

                if (navigator == null) {
                    if (defaultValue != null) {
                        return defaultValue
                    }
                    System.err.println("Navigator is null!")
                    try {
                        throw new RuntimeException()
                    } catch (RuntimeException re) {
                        System.err.println ' > ' + re.stackTrace.findAll {
                            it.className.startsWith('org.modelcatalogue') && !it.methodName?.contains('noStale') && it.lineNumber && it.fileName
                        }.join('\n > ')
                    }
                    return null
                }

                if (!navigator.first().displayed && defaultValue == null) {
                    waitFor(attempt ** 2) {
                        navigator.first().displayed
                    }
                    return (R) waitFor(1) {
                        return resultClosure(navigator)
                    }
                }
                return (R) waitFor(attempt ** 2) {
                    return resultClosure(navigator)
                }
            } catch (StaleElementReferenceException | WaitTimeoutException e) {
                if (refreshOnConnectionLost && attempt == maxAttempts - 1) {
                    // on last but one attempt try to refresh
                    refreshIfConnectionLost()
                }
                println "Condition not met for after ${attempt ** 2} seconds, next waiting ${(attempt + 1) ** 2} seconds - elements: ${navigator?.allElements()}"

                try {
                    throw new RuntimeException()
                } catch (RuntimeException re) {
                    println ' > ' + re.stackTrace.findAll {
                        it.className.startsWith('org.modelcatalogue') && !it.methodName?.contains('noStale') && it.lineNumber && it.fileName
                    }.join('\n > ')
                }

                if (defaultValue != null) {
                    return defaultValue
                }

                error = e
            }
        }
        throw new IllegalArgumentException("Cannot evaluate expression $navigator after $maxAttempts attempts", error)
    }


    boolean tabActive(String name) {
        $("li[data-tab-name='$name'].active").first().displayed
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
            parent.find('.soe-table-property-row:first-child .soe-table-property-actions .soe-remove-row').first().click()
        }
        // last value might not be deleted
        parent.find('.soe-table-property-row:first-child .soe-table-property-actions .soe-remove-row').first().click()

        newMetadata.each { key, value ->
            // fill value first as key might disable both input
            parent.find('.soe-table-property-row:last-child .soe-table-property-value input').value(value?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-key input').value(key?.toString() ?: '')
            parent.find('.soe-table-property-row:last-child .soe-table-property-actions .soe-add-row').first().click()
        }
    }


    File download(String name, String url) {
        File sampleXsd = tmp.newFile(name)
        def out = new BufferedOutputStream(new FileOutputStream(sampleXsd))
        out << new URL(url).openStream()
        out.close()
        sampleXsd
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
            item.parent().first().click()
        }
    }


    void selectInTree(String name, boolean ensureExpanded = false) {
        if (ensureExpanded) {
            noStale({
                $('.catalogue-element-treeview-name', text: name).parent().find('.catalogue-element-treeview-icon')
            }) { Navigator it ->
                if (it.find('.fa-caret-down').first().displayed) {
                    return true
                }
                click { $('.catalogue-element-treeview-name', text: name) }
                return false
            }
            return
        }

        noStale({ $('.catalogue-element-treeview-name', text: name) }) {
            it.first().click()
        }
    }

    void refresh(Browser browser) {
        browser.driver.navigate().refresh()
    }

    void scroll(ScrollDirection direction) {
        direction.scroll(driver as JavascriptExecutor)
    }

    void scroll(String selector, int offset = -150) {
        //language=JavaScript
        js.exec """
            var selector = '$selector', offset = $offset;
            if (jQuery && jQuery(selector).length > 0) {
              jQuery(selector)[0].scrollIntoView(true);
              if (offset) {
                window.scrollBy(0, offset)
              }
            }
        """
    }

    void scrollTop() {
        js.exec("document.body.scrollTop = document.documentElement.scrollTop = 0;")
    }

    void addDataModelImport(String... imported) {
        click CatalogueAction.runFirst('item', 'catalogue-element', 'add-import')
        for (String dataModel in imported) {
            fill 'div.modal #elements' with dataModel and prefer first existing
        }
        click 'div.modal .btn-primary'
        check backdrop gone
    }

    void pressKey(String selector = "body", Keys keys = Keys.ENTER) {
        $(selector) << keys
    }

    void selectInSearch (int element){
        $("div.list-group>a:nth-child($element)").click()
    }
    void selectTreeView (String names){
        $("ul.catalogue-element-treeview-list-root>li>ul>li:nth-child($names)>div>span:nth-child(2)>span").click()
    }
}
