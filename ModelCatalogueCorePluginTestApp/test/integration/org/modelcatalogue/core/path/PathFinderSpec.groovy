package org.modelcatalogue.core.path

import org.modelcatalogue.core.AbstractIntegrationSpec
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelService
import org.modelcatalogue.core.ElementService
import spock.lang.IgnoreIf
import spock.lang.Requires

@IgnoreIf( { System.getProperty('spock.ignore.slow') })
class PathFinderSpec extends AbstractIntegrationSpec {

    DataModelService dataModelService
    ElementService elementService

    def setup() {

        buildComplexModel(dataModelService, elementService)

    }

    @Requires({ !System.getenv('TRAVIS') })
    def "find the path for particular data type"() {
        DataElement dataElement = DataElement.findByName('C4CTDE Model 2 Child Model 2 Data Element 2')
        DataModel dataModel = DataModel.findByName(COMPLEX_MODEL_NAME)

        expect:
        dataElement
        dataModel

        when:
        PathFinder finder = new PathFinder()

        List<String> path = finder.findPath(dataElement)

        then:
        path.size() == 5
        path[0] == dataModel.link
        path[1] == DataClass.findByName(COMPLEX_MODEL_ROOT_DATA_CLASS_NAME)?.link
        path[2] == DataClass.findByName('C4CTDE Model 2')?.link
        path[3] == DataClass.findByName('C4CTDE Model 2 Child Model 2')?.link
        path[4] == dataElement.link
    }

}
