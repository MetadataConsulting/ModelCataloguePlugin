package org.modelcatalogue.core.xml

import groovy.xml.XmlUtil
import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.core.*
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import spock.lang.Ignore

class CatalogueXmlPrinterSpec extends AbstractIntegrationSpec {

    CatalogueXmlPrinter printer

    def dataModelService
    def elementService
    def dataClassService


    def setup() {
        XMLUnit.ignoreWhitespace = true

        initCatalogue()

        printer = new CatalogueXmlPrinter(dataModelService, dataClassService)
    }


    def "replace special chars"() {
        Writable writable = printer.bind(new DataType(name: 'Test', description: "diagnosis.ƒ‚ƒ‚ƒ‚'‚“ e", modelCatalogueId: 'http://example.com/specialchars').save()) {
            noHref = true
        }
        StringWriter writer = new StringWriter()
        writable.writeTo(writer)
        expect:
        writer.toString() == '''<catalogue xmlns="http://www.metadataregistry.org.uk/assets/schema/2.2/metadataregistry.xsd">
  <dataType name="Test" id="http://example.com/specialchars">
    <description>diagnosis.&#402;&#8218;&#402;&#8218;&#402;&#8218;'&#8218;&#8220; e</description>
  </dataType>
</catalogue>'''
    }


    def "write simple measurement unit"() {
        expect:
        similar newton, 'newton.catalogue.xml'

    }

    def "write simple data type"() {
        expect:
        similar integer, 'integer.catalogue.xml'
    }

    def "write enumerated type"() {
        def type = gender
        expect:
        type instanceof EnumeratedType
        similar type, 'gender.catalogue.xml'
    }

    def "write reference type"() {
        def type = user
        expect:
        type instanceof ReferenceType
        similar type, 'user.catalogue.xml'
    }

    def "write simple primitive type"() {
        expect:
        similar force, 'force.catalogue.xml'
    }

    // TODO: sometimes is needed to print the type as well if not obvious

    def "write simple data element"() {
        expect:
        similar adhesion, 'adhesion.catalogue.xml'
    }

    def "write simple model with validation"() {
        expect:
        similar locomotive, 'locomotive.catalogue.xml'
    }

    def "write simple classification"() {
        expect:
        similar transportation, 'transportation.catalogue.xml'
    }


    boolean similar(CatalogueElement input, String sampleFile) {
        String xml = XmlUtil.serialize(printer.bind(input){
            noHref = true
        })

        println "ACTUAL:"
        println xml

        String expected = getClass().classLoader.getResourceAsStream("resources/xml/$sampleFile").text

        println "EXPECTED"
        println expected

        Diff diff = new Diff(xml.replaceAll(/[ \t]+/, " "), expected.replaceAll(/[ \t]+/, " "))
        DetailedDiff detailedDiff = new DetailedDiff(diff)

        assert detailedDiff.similar(), detailedDiff.toString()
        return true
    }

    private <E extends CatalogueElement> E build(@DelegatesTo(CatalogueBuilder) Closure cl) {
        DefaultCatalogueBuilder defaultCatalogueBuilder = new DefaultCatalogueBuilder(dataModelService, elementService)
        defaultCatalogueBuilder.build cl
        defaultCatalogueBuilder.created.first() as E
    }

    private DataType getPressure() {
        build {
            dataType(name: 'Pressure', id: "http://www.example.com/domains/Pressure")
        }
    }

    private DataModel getTransportation() {
        build {
            dataModel (name: "Transportation", id: "http://www.example.com/datasets/Transportation") {
                policy 'CD Policy'
                dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion") {
                    dataType(name: 'Force', id: "http://www.example.com/domains/Force") {
                        description "A force is a push or pull upon an object resulting from the object's interaction with another object."
                        regex "\\d+"
                        dataType(name: "Decimal", id: "http://www.example.com/types/Decimal") {
                            description "A number that uses a decimal point followed by digits that show a value smaller than one."
                        }
                        measurementUnit(name: "Newton", symbol: "N", id: "http://www.example.com/units/Newton") {
                            description "The newton (symbol: N) is the International System of Units (SI) derived unit of force."
                            ext "From", "SI"
                        }
                    }
                }
                dataClass(name: "Locomotive", id: "http://www.example.com/models/Locomotive") {
                    dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion")
                }
                dataClass(name: 'Engine', id: "http://www.example.com/models/Engine") {
                    dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion")
                }
                dataModelPolicy(name: 'CD Policy') {
                    check dataClass property 'name' is 'required'
                }
            }

        }
    }

    private DataClass getLocomotive() {
        build {
            dataClass(name: "Locomotive", id: "http://www.example.com/models/Locomotive") {
                dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion") {
                    dataType(name: 'Force', id: "http://www.example.com/domains/Force") {
                        description "A force is a push or pull upon an object resulting from the object's interaction with another object."
                        regex "\\d+"
                        dataType(name: "Decimal", id: "http://www.example.com/types/Decimal") {
                            description "A number that uses a decimal point followed by digits that show a value smaller than one."
                        }
                        measurementUnit(name: "Newton", symbol: "N", id: "http://www.example.com/units/Newton") {
                            description "The newton (symbol: N) is the International System of Units (SI) derived unit of force."
                            ext "From", "SI"
                        }
                    }
                }
                dataClass(name: 'Engine', id: "http://www.example.com/models/Engine") {
                    dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion") {
                        relationship {
                            ext 'Min. Occurs', '0'
                        }
                    }
                    validationRule(name: 'Locomotives Rules', id: 'http://www.example.com/rules/LocomotivesRules') {
                        component 'Southeast Lines'
                        ruleFocus 'Focus on trains not cars'
                        trigger  'When the new route is planned'
                        rule  'IF something THEN something else'
                        errorCondition  'stop doing everytihng'
                        issueRecord 'MET-XXXX'
                        notification 'trains should go cars should stop'
                        notificationTarget 'the conductor'
                        dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion")
                    }
                }
            }
        }
    }

    private DataElement getAdhesion() {
        build {
            dataElement(name: "Factor of Adhesion", id: "http://www.example.com/elements/Adhesion") {
                dataType(name: 'Force', id: "http://www.example.com/domains/Force") {
                    description "A force is a push or pull upon an object resulting from the object's interaction with another object."
                    regex "\\d+"
                    dataType(name: "Decimal", id: "http://www.example.com/types/Decimal") {
                        description "A number that uses a decimal point followed by digits that show a value smaller than one."
                    }
                    measurementUnit(name: "Newton", symbol: "N", id: "http://www.example.com/units/Newton") {
                        description "The newton (symbol: N) is the International System of Units (SI) derived unit of force."
                        ext "From", "SI"
                    }
                }
            }
        }
    }

    private DataType getForce() {
        pressure

        build {
            dataType(name: 'Force', id: "http://www.example.com/domains/Force") {
                description "A force is a push or pull upon an object resulting from the object's interaction with another object."
                regex "\\d+"
                dataType(name: "Decimal", id: "http://www.example.com/types/Decimal") {
                    description "A number that uses a decimal point followed by digits that show a value smaller than one."
                }
                measurementUnit(name: "Newton", symbol: "N", id: "http://www.example.com/units/Newton") {
                    description "The newton (symbol: N) is the International System of Units (SI) derived unit of force."
                    ext "From", "SI"
                }
//FIXME: why doesn't this seem to be working in the catalogue builder
//                rel "relatedTo" to 'Pressure', {
//                    ext 'Relation', 'Derived From'
//                }
            }
        }

    }

    private MeasurementUnit getNewton() {
        build {
            measurementUnit(name: "Newton", symbol: "N", id: "http://www.example.com/units/Newton") {
                description "The newton (symbol: N) is the International System of Units (SI) derived unit of force."
                ext "From", "SI"
                rel 'relatedTo' to 'SI', 'kilogram'
            }
        }
    }

    private DataType getInteger() {
        decimal

        DataType dataType = build {
            dataType(name: "Integer", id: "http://www.example.com/types/Integer") {
                basedOn 'Decimal'
                description "A number with no fractional part."
            }
        }

        assert dataType.isBasedOn
        assert dataType.isBasedOn.any { it.name == 'Decimal'}

        dataType
    }

    private DataType getGender() {
        build {
            dataType(name: "Gender", id: "http://www.example.com/types/Gender", enumerations: [M: 'Male', F: 'Female']) {
                description "The state of being male or female (typically used with reference to social and cultural differences rather than biological ones)"
            }
        }
    }

    private DataType getUser() {
        build {
            dataType(name: "User", id: "http://www.example.com/types/User") {
                description "Reference to the user"
                dataClass(name: "User", id: "http://www.example.com/classes/User") {
                    description "The user of the system"
                }
            }
        }
    }

    private DataType getDecimal() {
        build {
            dataType(name: "Decimal", id: "http://www.example.com/types/Decimal")
        }
    }

}
