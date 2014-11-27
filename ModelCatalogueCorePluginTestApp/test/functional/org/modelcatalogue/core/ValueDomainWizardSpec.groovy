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
            viewTitle.text().trim() == 'Value Domain List'

            when:
            loginAdmin()

            then:
            waitFor {
                actionButton('create-catalogue-element', 'list').displayed
            }
        }


        def "Add new data element"(){
            when: 'I click the add value domain button'
            actionButton('create-catalogue-element', 'list').click()


            then: 'the value domain dialog opens'
            waitFor {
                basicEditDialog.displayed
            }

            when: 'the value domain details are filled in'

            classification      = "Java"
            selectCepItemIfExists()

            name                = "NewVD5"
            modelCatalogueId    = "http://www.example.com/" + UUID.randomUUID().toString()
            description         = "NewVD5 Description"

            dataType            = "xs:double"
            selectCepItemIfExists()

            unitOfMeasure       = "meter"
            selectCepItemIfExists()

            expandRuleButton.click()

            rule                = "is number"

            and: 'save button clicked'
            actionButton('modal-save-element', 'modal').click()

            then: 'the value domain  is saved and displayed at the top of the table'
            waitFor {
                infTableCell(1, 2, text: "NewVD5").displayed
            }

        }


        def "Check the value domain shows up with own details"(){
            when: 'Value domain is located'

            waitFor {
                infTableCell(1, 2, text: "NewVD5").displayed
            }

            then: 'Click the domain'

            infTableCell(1, 2).find('a').click()

            waitFor(60) {
                subviewTitle.displayed
            }

            subviewTitle.text().trim() == 'NewVD5 DRAFT'

        }

        def "Edit the value domain"() {
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
                $('td', 'data-value-for': "Unit Of Measure").text().contains('celsius')
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
            }

        }

        def "deprecate the domain"() {
            when: "depracete action is clicked"
            actionButton('archive').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "the element is now deprecated"
            waitFor {
                subviewStatus.text() == 'DEPRECATED'
            }

        }

        def "hard delete the domain"() {
            when: "delete action is clicked"
            actionButton('delete').click()

            then: "modal prompt is displayed"
            waitFor {
                confirmDialog.displayed
            }

            when: "ok is clicked"
            confirmOk.click()

            then: "you are redirected to the list page"
            waitFor {
                at ValueDomainPage
            }

        }
    }
