package org.modelcatalogue.core

import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.persistence.ColumnTransformationDefinitionGormService
import org.modelcatalogue.core.persistence.CsvTransformationGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService

class SetupSimpleCsvTransformationService {
    MeasurementUnitGormService measurementUnitGormService
    DataTypeGormService dataTypeGormService
    PrimitiveTypeGormService primitiveTypeGormService
    DataElementGormService dataElementGormService
    CsvTransformationGormService csvTransformationGormService
    ColumnTransformationDefinitionGormService columnTransformationDefinitionGormService
    MappingService mappingService

    def setupSimpleCsvTransformation() {
        MeasurementUnit c = measurementUnitGormService.findByName("celsius")
        MeasurementUnit f = measurementUnitGormService.findByName("fahrenheit")

        DataType doubleType = dataTypeGormService.findByName("Double")

        assert c
        assert f
        assert doubleType

        PrimitiveType temperatureUS = new PrimitiveType(name: "temperature US", measurementUnit: f, regexDef: /\d+(\.\d+)?/)
        primitiveTypeGormService.save(temperatureUS)
        PrimitiveType temperature   = new PrimitiveType(name: "temperature",    measurementUnit: c, regexDef: /\d+(\.\d+)?/)
        primitiveTypeGormService.save(temperature)


        assert mappingService.map(temperature, temperatureUS, "(x as Double) * 9 / 5 + 32")
        assert mappingService.map(temperatureUS, temperature, "((x as Double) - 32) * 5 / 9")

        DataElement patientTemperature   = dataElementGormService.saveByNameAndPrimitiveType("patient temperature", temperature)
        DataElement patientTemperatureUS = dataElementGormService.saveByNameAndPrimitiveType("patient temperature US", temperatureUS)

        CsvTransformation transformation = csvTransformationGormService.saveByName("UK to US records")

        columnTransformationDefinitionGormService.saveByTransformationAndSourceAndHeader(transformation, dataElementGormService.findByName("PERSON GIVEN NAME"), "FIRST NAME")
        columnTransformationDefinitionGormService.saveByTransformationAndSourceAndHeader(transformation, dataElementGormService.findByName("PERSON FAMILY NAME"), "SURNAME")
        columnTransformationDefinitionGormService.saveByTransformationAndSourceAndDestinationAndHeader(transformation, patientTemperature, patientTemperatureUS, "PATIENT TEMPERATURE")
    }
}
