package org.modelcatalogue.integration.excel.nt.uclh

import org.custommonkey.xmlunit.XMLUnit
import org.modelcatalogue.builder.xml.XmlCatalogueBuilder
import org.modelcatalogue.core.dataimport.excel.ExcelLoaderSpec

/**
 * Created by david on 04/08/2017.
 */
class UCLHExcelLoaderSpec extends ExcelLoaderSpec {


    def setup() {
        XMLUnit.ignoreWhitespace = true
        XMLUnit.ignoreComments = true
        XMLUnit.ignoreAttributeOrder = true
        stringWriter = new StringWriter()
        builder = new XmlCatalogueBuilder(stringWriter, true)
        loader = new UCLHExcelLoader(builder)
    }
    List<String> uclhHeaders = ['L2',	'L3',	'L4',	'L5',	'Lowest level ID',	'Idno',	'Name',	'Description',	'Multiplicity',	'Value Domain / Data Type',	'Related To',	'Current Paper Document  or system name',	'Semantic Matching',	'Known issue',	'Immediate solution', 'Immediate solution Owner',	'Long term solution',	'Long term solution owner',	'Data Item', 'Unique Code',	'Related To',	'Part of standard data set',	'Data Completeness',	'Estimated quality',	'Timely?', 'Comments']

    /**
     * There are two relatedTo columns!!!
     */
    def "test expected output for UCLH #file"() {
        expect:
        similar excelLoaderXmlResult(file),//, uclhHeaders),
            getClass().getResourceAsStream('UCLHAriaTestExpected.xml').text
        where:
        file << ['UCLHAriaTest.xlsx']

    }



}
