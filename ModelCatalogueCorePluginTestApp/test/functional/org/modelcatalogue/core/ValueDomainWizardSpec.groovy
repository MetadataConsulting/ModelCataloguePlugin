    package org.modelcatalogue.core

    import geb.spock.GebReportingSpec
    import org.modelcatalogue.core.pages.ValueDomainPage
    import spock.lang.Stepwise

    @Stepwise
    class ValueDomainWizardSpec extends GebReportingSpec  {

        def "login and select Value Domain"() {
            when:
            go "#/catalogue/valueDomain/all"

            then:
            at ValueDomainPage
            waitFor(120) {
                viewTitle.displayed
            }
            waitFor {
                viewTitle.text().trim() == 'Value Domain List'
            }

            when:
            loginAdmin()

            then:
            waitFor {
                actionButton('create-catalogue-element', 'list').displayed
            }
        }


        def "filter by name in header and expand enumerations"() {
            when: "the header is expanded"
            $('.inf-table thead .inf-cell-expand').click()

            then: "the name filter is displayed"
            waitFor {
                $('input.form-control', placeholder: 'Filter Name').displayed
            }

            when: "we filter by anatomical side"
            $('input.form-control', placeholder: 'Filter Name').value('anatomical_side')

            then: "only one row will be shown"
            waitFor {
                $('.inf-table tbody .inf-table-item-row').size() == 1
            }

            when: "we expand the row"
            toggleInfTableRow(1)

            then: "we expand the data type"
            waitFor {
                dataTypeHeader.displayed
            }

            when:
            dataTypeHeader.find('span.fa-plus-square-o').click()
            driver.executeScript "scroll(0,250);"

            then: "we see enumerated values"
            waitFor {
                dataTypeHeader.find('div.preserve-new-lines')?.text()?.contains('Midline')
            }

            when: "the filter is reset"
            $('input.form-control', placeholder: 'Filter Name').value('')

            then: "we see many rows again"
            waitFor {
                $('.inf-table tbody .inf-table-item-row').size() > 1
            }
        }



        def "Add new value domain"(){
            when: 'I click the add value domain button'
            actionButton('create-catalogue-element', 'list').click()


            then: 'the value domain dialog opens'
            waitFor {
                basicEditDialog.displayed
            }

            when: 'the value domain details are filled in'

            classification      = "Java"
            selectCepItemIfExists()

            name                = "New"
            modelCatalogueId    = "http://www.example.com/" + UUID.randomUUID().toString()
            description         = "New Description"

            dataType            = "xs:double"
            selectCepItemIfExists()

            unitOfMeasure       = "meter"
            selectCepItemIfExists()

            expandRuleButton.click()

            rule                = "is(number)"

            and: 'save button clicked'
            actionButton('modal-save-element', 'modal').click()

            then: 'the value domain  is saved and displayed at the top of the table'
            waitFor {
                infTableCell(1, 2, text: "New").displayed
            }

        }

        def "Check the value domain shows up with own details"(){
            when: 'Value domain is located'

            waitFor {
                infTableCell(1, 2, text: "New").displayed
            }

            then: 'Click the domain'

            infTableCell(1, 2).find('a').click()

            waitFor(60) {
                subviewTitle.displayed
            }

            subviewTitle.text().trim() == 'New DRAFT'

        }

        def "update metadata"() {
            when:
            selectTab('ext')

            then:
            waitFor {
                $('button.btn-primary.update-object').disabled
            }

            when:
            fillMetadata(foo: 'bar', one: 'two', free: 'for')

            then:
            waitFor {
                !$('button.btn-primary.update-object').disabled
            }

            when:
            $('button.btn-primary.update-object').click()

            then:
            waitFor {
                $('button.btn-primary.update-object').disabled
            }

        }

        def "validate value"() {
            when: "validate action is clicked"
            actionButton('validate-value').click()


            then: "validate value dialog opens"
            waitFor {
                modalDialog.displayed
                modalHeader.text() == 'Validate Value by New domain'
                modalDialog.find('.alert.alert-warning').displayed
            }

            when: "valid value is entered"
            value = '10'

            then: "success is shown"
            waitFor {
                modalDialog.find('.alert.alert-success').displayed
            }

            when: "invalid value is entered"
            value = 'foo'

            then: "error is shown"
            waitFor {
                modalDialog.find('.alert.alert-danger').displayed
            }

            when:
            modalCloseButton.click()

            then:
            waitFor {
                !modalDialog
            }
        }



        def "create new mapping"() {
            when: "create new mapping action is clicked"
            actionButton('create-new-mapping').click()


            then: "crate new mapping dialog opens"
            waitFor {
                modalDialog.displayed
                modalHeader.text() == 'Create new mapping for New'
            }

            when: "value domain is selected"
            valueDomain = 'xs:boolean'
            selectCepItemIfExists()

            and: "new mapping rule is created"
            mapping = "number(x).asType(Boolean)"

            and: "the create mapping button is clicked"
            modalPrimaryButton.click()

            then: "there is exactly one mapping"
            waitFor {
                totalOf('mappings') == 1
            }
        }

        def "convert value"() {
            when: "convert action is clicked"
            actionButton('convert').click()

            then: "modal is shown"
            waitFor {
                modalDialog.displayed
                modalHeader.text() == 'Convert Value from New'
            }

            when: "truthy value is entered"
            value = '10'

            then: "true is shown"
            waitFor {
                modalDialog.find('pre').text().trim() == 'true'
            }

            when: "falsy value is entered"
            value = '0'

            then: "false is shown"
            waitFor {
                modalDialog.find('pre').text().trim() == 'false'
            }

            when: "invalid value is entered"
            value = 'foo'

            then: "INVALID is shown"
            waitFor {
                modalDialog.find('pre').text().trim().contains('INVALID')
            }

            when:
            modalCloseButton.click()

            then:
            waitFor {
                !modalDialog
            }
        }

        def "edit mapping"() {
            when: "mappings tab selected"
            selectTab('mappings')


            then: "mappings tab is active"
            waitFor {
                tabActive('mappings')
            }

            when:
            toggleInfTableRow(1)
            actionButton('edit-mapping').click()

            then:
            waitFor {
                modalDialog.displayed
            }

            when:
            mapping = 'x'
            modalPrimaryButton.click()

            then:
            waitFor {
                infTableCell(1, 2).text().trim() == 'x'
            }
        }


        def "create relationship"() {
            when: "create relationship action is clicked"
            actionButton('create-new-relationship').click()

            then: "modal is shown"
            waitFor {
                modalDialog.displayed
                modalHeader.text() == 'Create Relationship'
            }

            when:
            type    = 'related to'
            element = 'xs:boolean'
            selectCepItemIfExists()

            fillMetadata(modalDialog, foo: 'bar', one: 'two')

            modalPrimaryButton.click()

            then:
            waitFor {
                totalOf('relatedTo') == 1
            }

        }

        def "create relationship from footer action"() {
            when: "related to tab selected"
            selectTab('relatedTo')


            then: "related to tab is active"
            waitFor {
                tabActive('relatedTo')
            }

            when: "click the footer action"
            tableFooterAction.click()

            then: "modal is shown"
            waitFor {
                modalDialog.displayed
                modalHeader.text() == 'Create Relationship'
            }

            when:
            element = 'xs:string'
            selectCepItemIfExists()

            modalPrimaryButton.click()

            then:
            waitFor {
                totalOf('relatedTo') == 2
            }
        }

        def "remove relationship"() {
            when:
            toggleInfTableRow(1)
            actionButton('remove-relationship').click()

            then:
            waitFor {
                confirmDialog.displayed
            }

            when:
            confirmOk.click()

            then:
            waitFor {
                totalOf('relatedTo') == 1
            }
        }

        def "remove mapping"() {
            when:
            selectTab('mappings')
            toggleInfTableRow(1)
            actionButton('remove-mapping').click()

            then:
            waitFor {
                confirmDialog.displayed
            }

            when:
            confirmOk.click()

            then:
            waitFor {
                totalOf('mappings') == 0
            }
        }

        def "Edit the value domain"() {
            selectTab('properties')

            when: "edit action is clicked"
            actionButton('edit-catalogue-element').click()

            then: "edit dialog is shown"
            waitFor {
                basicEditDialog.displayed
            }

            when: "new value domain is changed"
            unitOfMeasure = "celsius"
            selectCepItemIfExists()

            dataType = ""

            and: 'save button clicked'
            actionButton('modal-save-element', 'modal').click()

            then: 'the value domain is saved and and different measurement unit is shown'
            waitFor {
                $('td', 'data-value-for': "Unit Of Measure")?.text()?.contains('celsius')
            }
        }

        // following is just copy-pasted until we find better way how to run feature methods stepwise
        def "finalize domain"() {
            when: "finalize is clicked"
            actionButton('finalize').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "the element is finalized"
            waitFor {
                subviewStatus.text() == 'FINALIZED'
            }

        }

        def "create new version of the domain"() {
            when: "new version is clicked"
            actionButton('create-new-version').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "the element new draft version is created"
            waitFor {
                subviewStatus.text() == 'DRAFT'
                totalOf('history') == 2
            }

        }

        def "merge domain"() {
            when:
            actionButton('merge').click()

            then:
            waitFor {
                modalDialog.displayed
                modalHeader.text() == 'Merge Value Domain New to another Value Domain'
            }

            when: "langauge is select and confirmed"
            value = 'xs:language'
            selectCepItemIfExists()

            modalPrimaryButton().click()

            then: "the item is merged and we are redirected to destination domain"
            waitFor {
                subviewTitle.text().trim() == 'xs:language FINALIZED'
            }

            and: "the relationships are copied to the destination domain"
            waitFor {
                totalOf('relatedTo') == 1
            }

        }
    }
