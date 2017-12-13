package org.modelcatalogue.core.util.test

import grails.transaction.Transactional
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.actions.CreateCatalogueElement
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.persistence.ActionGormService
import org.modelcatalogue.core.persistence.ActionParameterGormService
import org.modelcatalogue.core.persistence.AssetGormService
import org.modelcatalogue.core.persistence.BatchGormService
import org.modelcatalogue.core.persistence.ColumnTransformationDefinitionGormService
import org.modelcatalogue.core.persistence.CsvTransformationGormService
import org.modelcatalogue.core.persistence.DataClassGormService
import org.modelcatalogue.core.persistence.DataElementGormService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.persistence.DataModelPolicyGormService
import org.modelcatalogue.core.persistence.DataTypeGormService
import org.modelcatalogue.core.persistence.EnumeratedTypeGormService
import org.modelcatalogue.core.persistence.ExtensionValueGormService
import org.modelcatalogue.core.persistence.MappingGormService
import org.modelcatalogue.core.persistence.MeasurementUnitGormService
import org.modelcatalogue.core.persistence.PrimitiveTypeGormService
import org.modelcatalogue.core.persistence.ReferenceTypeGormService
import org.modelcatalogue.core.persistence.RelationshipTypeGormService
import org.modelcatalogue.core.persistence.TagGormService
import org.modelcatalogue.core.persistence.UserGormService
import org.modelcatalogue.core.persistence.ValidationRuleGormService
import org.modelcatalogue.core.security.User

class TestDataService {

    BatchGormService batchGormService
    ActionGormService actionGormService
    ActionParameterGormService actionParameterGormService
    AssetGormService assetGormService
    DataModelPolicyGormService dataModelPolicyGormService
    ValidationRuleGormService ValidationRuleGormService
    DataModelGormService dataModelGormService
    CsvTransformationGormService csvTransformationGormService
    DataTypeGormService dataTypeGormService
    DataElementGormService dataElementGormService
    ColumnTransformationDefinitionGormService columnTransformationDefinitionGormService
    EnumeratedTypeGormService enumeratedTypeGormService
    UserGormService userGormService
    RelationshipTypeGormService relationshipTypeGormService
    ReferenceTypeGormService referenceTypeGormService
    MeasurementUnitGormService measurementUnitGormService
    MappingGormService mappingGormService
    PrimitiveTypeGormService primitiveTypeGormService
    DataClassGormService dataClassGormService
    ExtensionValueGormService extensionValueGormService
    TagGormService tagGormService

    @Transactional
    void createTestData() {

        Batch B_archived = batchGormService.saveWithName("Archived Batch")
        Batch B_generic = batchGormService.saveWithName("Generic Batch")
        Batch B_one = batchGormService.saveWithName("One")
        Batch B_three = batchGormService.saveWithName("Three")
        Batch B_two = batchGormService.saveWithName("Two")
        Batch B_upgrade = batchGormService.saveWithName("Model Catalogue Upgrade 1.x")

        Action A_create_model_5 = actionGormService.saveWithBatchAndType(B_generic, CreateCatalogueElement)
        ActionParameter A_create_model_5_param_type = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_5, 'type', DataClass.name)
        ActionParameter A_create_model_5_param_name = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_5, 'name', 'Test Model 5')
        Action A_create_model_4 = actionGormService.saveWithBatchAndType(B_generic, CreateCatalogueElement)
        ActionParameter A_create_model_4_param_type = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_4, 'type', DataClass.name)
        ActionParameter A_create_model_4_param_name = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_4, 'name', 'Test Model 4')
        Action A_create_model_1 = actionGormService.saveWithBatchAndType(B_generic, CreateCatalogueElement)
        ActionParameter A_create_model_1_param_type = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_1, 'type', DataClass.name)
        ActionParameter A_create_model_1_param_name = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_1, 'name', 'Test Model 1')
        Action A_create_model_6 = actionGormService.saveWithBatchAndType(B_generic, CreateCatalogueElement)
        ActionParameter A_create_model_6_param_type = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_6, 'type', DataClass.name)
        ActionParameter A_create_model_6_param_name = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_6, 'name', 'Test Model 6')
        Action A_create_model_3 = actionGormService.saveWithBatchAndType(B_generic, CreateCatalogueElement)
        ActionParameter A_create_model_3_param_type = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_3, 'type', DataClass.name)
        ActionParameter A_create_model_3_param_name = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_3, 'name', 'Test Model 3')
        Action A_create_model_2 = actionGormService.saveWithBatchAndType(B_generic, CreateCatalogueElement)
        ActionParameter A_create_model_2_param_type = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_2, 'type', DataClass.name)
        ActionParameter A_create_model_2_param_name = actionParameterGormService.saveWithActionAndNameAndExtensionValue(A_create_model_2, 'name', 'Test Model 2')

        Asset A_directory = assetGormService.saveWithNameAndDescription("directory", "random directory")
        Asset A_file = assetGormService.saveWithNameAndDescriptionAndStatus("file", "random file", ElementStatus.FINALIZED)
        Asset A_file1 = assetGormService.saveWithNameAndDescriptionAndStatus("file1", "some random file 1", ElementStatus.FINALIZED)
        Asset A_file2 = assetGormService.saveWithNameAndDescriptionAndStatus("ASSET", "some random file 2", ElementStatus.FINALIZED)
        Asset A_file3 = assetGormService.saveWithNameAndDescriptionAndStatus("A_file3", "the random name 3", ElementStatus.FINALIZED)
        Asset A_file4 = assetGormService.saveWithNameAndDescriptionAndStatus("A_file4", "some random file 4", ElementStatus.FINALIZED)
        Asset A_file5 = assetGormService.saveWithNameAndDescriptionAndStatus("A_file5", "random file 5", ElementStatus.FINALIZED)
        Asset A_file6 = assetGormService.saveWithNameAndDescriptionAndStatus("A_file6", "random file 6", ElementStatus.FINALIZED)
        Asset A_file7 = assetGormService.saveWithNameAndDescription("A_file7", "random file 7")
        Asset A_file8 = assetGormService.saveWithNameAndDescription("A_file8", "random file 8")
        Asset A_file9 = assetGormService.saveWithNameAndDescription("A_file9", "random file 9")
        Asset A_image = assetGormService.saveWithNameAndDescription("image", "random image")

        for (int i = 1 ; i <= 12 ; i++) {
            dataModelPolicyGormService.saveWithNameAndPolicyText("Policy $i", 'check dataType property "name" is unique')
        }

        for (int i = 1 ; i <= 12 ; i++) {
            ValidationRuleGormService.saveWithNameAndDescriptionAndStatus("rule$i", "some random rule $i", i <=6 ? ElementStatus.FINALIZED : ElementStatus.DRAFT)
        }

        DataModel CL_dataSet1 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set a", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet10 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 10", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet11 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 11", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet12 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 12", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet2 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 2", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet3 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 3", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet4 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 4", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet5 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 5", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet6 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 6", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet7 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 7", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet8 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 8", "test data set", ElementStatus.FINALIZED)
        DataModel CL_dataSet9 = dataModelGormService.saveWithNameAndDescriptonAndStatus("data set 9", "test data set", ElementStatus.FINALIZED)
        CsvTransformation CT_example = csvTransformationGormService.saveByName("Example")
        DataType DT_boolean = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "boolean", "a boolean xdfxdf")
        DataType DT_double = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED,  "double", "a double")
        DataType DT_integer = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "integer", "an integer")
        DataType DT_string = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "String", "a string")
        DataType DT_test1 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "test1", "test data type 1")
        DataType DT_test2 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "test2", "test data type 2")
        DataType DT_test3 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "test3", "test data type 3")
        DataType DT_test4 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "test4", "test data type 4")
        DataType DT_test5 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "test5", "test data type 5")
        DataType DT_test6 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "test6", "test data type 6")
        DataType DT_test7 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED, "xs:string", "xml string type")
        DataType DT_test8 = dataTypeGormService.saveWithStatusAndNameAndDescription(ElementStatus.FINALIZED,  "test8", "test data type 8")
        DataElement DE_author = dataElementGormService.saveWithNameAndDescriptionAndStatus("DE_author", "the DE_author of the book", ElementStatus.FINALIZED)
        DataElement DE_author1 = dataElementGormService.saveWithNameAndDescriptionAndStatus("DE_author1", "the DE_author of the book", ElementStatus.FINALIZED)
        DataElement DE_author2 = dataElementGormService.saveWithNameAndDescriptionAndStatus("AUTHOR", "the DE_author of the book", ElementStatus.FINALIZED)
        DataElement DE_author3 = dataElementGormService.saveWithNameAndDescriptionAndStatusAndDataModel("auth", "the DE_author of the book", ElementStatus.FINALIZED, CL_dataSet2)
        DataElement DE_author4 = dataElementGormService.saveWithNameAndDescriptionAndStatusAndDataModel("auth4", "the DE_author of the book", ElementStatus.FINALIZED, CL_dataSet1)
        DataElement DE_author5 = dataElementGormService.saveWithNameAndDescriptionAndStatus("auth5", "the DE_author of the book", ElementStatus.FINALIZED)
        DataElement DE_opel = dataElementGormService.saveWithNameAndDescriptionAndDataType("speed of Opel", "speed of your Opel car", DT_test3)
        DataElement DE_patient_temperature_uk = dataElementGormService.saveWithNameAndDescriptionAndStatusAndDataType("patient temperature uk", "Patient's Temperature in the UK", ElementStatus.FINALIZED, DT_test1)
        DataElement DE_patient_temperature_us = dataElementGormService.saveWithNameAndDescriptionAndDataType("patient temperature us", "Patient's Temperature in the US", DT_test2)
        DataElement DE_title = dataElementGormService.saveWithNameAndDescription("title", "the title of the book")
        DataElement DE_vauxhall = dataElementGormService.saveWithNameAndDescriptionAndDataType("speed of Vauxhall", "speed of your Vauxhall car",  DT_test4)
        DataElement DE_writer = dataElementGormService.saveWithNameAndDescription("writer", "the writer of the book")
        ColumnTransformationDefinition CTD_cars = columnTransformationDefinitionGormService.saveByTransformationAndSourceAndDestination(CT_example, DE_opel, DE_vauxhall)
        ColumnTransformationDefinition CTD_author = columnTransformationDefinitionGormService.saveByTransformationAndSourceAndDestinationAndHeader(CT_example, DE_writer, DE_author, "creator")
        ColumnTransformationDefinition CTD_degrees = columnTransformationDefinitionGormService.saveByTransformationAndSourceAndDestination(CT_example, DE_patient_temperature_uk, DE_patient_temperature_us)
        ColumnTransformationDefinition CTD_coauthor = columnTransformationDefinitionGormService.saveByTransformationAndSourceAndHeader(CT_example, DE_author5, "co-author")
        EnumeratedType ET_gender = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "gender", ['m':'male', 'f':'female', 'u':'unknown', 'ns':'not specified'])
        EnumeratedType ET_schoolSubjects = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "sub1", ['H':'history', 'P':'politics', 'SCI':'science', 'GEO':'geography'])
        EnumeratedType ET_test1 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest1", ['m1':'test1', 'm2':'test2'])
        EnumeratedType ET_test2 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest2", ['m2m':'test2', 'm3m':'test3'])
        EnumeratedType ET_test3 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest3", ['m3m':'test3', 'm22m':'test22'])
        EnumeratedType ET_test4 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest4", ['m4m':'test4', 'm2m':'test2'])
        EnumeratedType ET_test5 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest5", ['m5m':'test5', 'm2m':'test2'])
        EnumeratedType ET_test6 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest6", ['m6m':'test6', 'm2m':'test2'])
        EnumeratedType ET_test7 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest7", ['m7m':'test7', 'm2m':'test2'])
        EnumeratedType ET_test8 = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "etTest8", ['m8m':'test8', 'm2m':'test2'])
        EnumeratedType ET_uni2Subjects = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "sub2", ['HISTORY':'history', 'POLITICS':'politics', 'SCIENCE':'science'])
        EnumeratedType ET_uniSubjects = enumeratedTypeGormService.saveWithStatusAndNameAndEnumerations(ElementStatus.FINALIZED, "sub3", ['h':'history', 'p':'politics', 'sci':'science'])
        ExtensionValue Ex_first = extensionValueGormService.saveWithNameAndExtensionValueAndDataElement( "metadata", "metadata value", DE_author1)

        Mapping Mapping_C_to_F = mappingGormService.saveWithSourceAndDestinationAndMapping(DT_test1, DT_test2, "(x as Double) * 9 / 5 + 32")
        Mapping Mapping_F_to_C = mappingGormService.saveWithSourceAndDestinationAndMapping(DT_test2, DT_test1, "((x as Double) - 32) * 5 / 9")
        Mapping Mapping_kph_to_mph = mappingGormService.saveWithSourceAndDestinationAndMapping(DT_test3, DT_test4, "(x as Double) * 0.621371192")
        Mapping Mapping_mph_to_kph = mappingGormService.saveWithSourceAndDestinationAndMapping(DT_test4, DT_test3, "(x as Double) * 1.609344")
        MeasurementUnit MU_degree_C = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°C", "Degrees Celsius", """Celsius, also known as centigrade,[1] is a scale and unit of measurement for temperature. It is named after the Swedish astronomer Anders Celsius (1701–1744), who developed a similar temperature scale. The degree Celsius (°C) can refer to a specific temperature on the Celsius scale as well as a unit to indicate a temperature interval, a difference between two temperatures or an uncertainty. The unit was known until 1948 as "centigrade" from the Latin centum translated as 100 and gradus translated as "steps".""")
        MeasurementUnit MU_degree_F = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°F", "Degrees of Fahrenheit", """Fahrenheit (symbol °F) is a temperature scale based on one proposed in 1724 by the physicist Daniel Gabriel Fahrenheit (1686–1736), after whom the scale is named.[1] On Fahrenheit's original scale the lower defining point was the lowest temperature to which he could reproducibly cool brine (defining 0 degrees), while the highest was that of the average human core body temperature (defining 100 degrees). There exist several stories on the exact original definition of his scale; however, some of the specifics have been presumed lost or exaggerated with time. The scale is now usually defined by two fixed points: the temperature at which water freezes into ice is defined as 32 degrees, and the boiling point of water is defined to be 212 degrees, a 180 degree separation, as defined at sea level and standard atmospheric pressure.""")
        MeasurementUnit MU_kph = measurementUnitGormService.saveWithStatusAndSymbolAndName(ElementStatus.FINALIZED, "KPH", "Kilometers per hour")
        MeasurementUnit MU_milesPerHour = measurementUnitGormService.saveWithStatusAndSymbolAndName(ElementStatus.FINALIZED, "MPH", "Miles per hour")
        MeasurementUnit MU_test1 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°1", "test mu1", "test1 mu")
        MeasurementUnit MU_test2 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°2", "test mu2", "test2 mu")
        MeasurementUnit MU_test3 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°3", "test mu3", "test3 mu")
        MeasurementUnit MU_test4 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°4", "test mu4", "test4 mu")
        MeasurementUnit MU_test5 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°5", "test mu5", "test5 mu")
        MeasurementUnit MU_test6 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED,  "°6", "test mu6", "test6 mu")
        MeasurementUnit MU_test7 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED,  "°7", "test mu7", "test7 mu")
        MeasurementUnit MU_test8 = measurementUnitGormService.saveWithStatusAndSymbolAndNameAndDescription(ElementStatus.FINALIZED, "°8", "test mu8", "test8 mu")
        DataClass M_book = dataClassGormService.saveWithNameAndDescriptionAndStatus("book", "this is a model of a book", ElementStatus.FINALIZED)
        DataClass M_chapter1 = dataClassGormService.saveWithNameAndDescriptionAndStatus("chapter1", "The Jabberwocky chapter for a book", ElementStatus.FINALIZED)
        DataClass M_chapter2 = dataClassGormService.saveWithNameAndDescriptionAndStatus("chapter2", "this is a second chapter for a book", ElementStatus.FINALIZED)
        DataClass M_test1 = dataClassGormService.saveWithNameAndDescriptionAndStatus("mTest1", "this is a model test1", ElementStatus.FINALIZED)
        DataClass M_test2 = dataClassGormService.saveWithNameAndDescriptionAndStatus("mTest2", "this is a model test2", ElementStatus.FINALIZED)
        DataClass M_test3 = dataClassGormService.saveWithNameAndDescription("mTest3", "this is a model test3")
        DataClass M_test4 = dataClassGormService.saveWithNameAndDescription("mTest4", "this is a model test4")
        DataClass M_test5 = dataClassGormService.saveWithNameAndDescription("mTest5", "this is a model test5")
        DataClass M_test6 = dataClassGormService.saveWithNameAndDescription("mTest6", "this is a model test6")
        DataClass M_test7 = dataClassGormService.saveWithNameAndDescription("mTest7", "this is a model test7")
        DataClass M_test8 = dataClassGormService.saveWithNameAndDescription("mTest8", "this is a model test8")
        DataClass M_test9 = dataClassGormService.saveWithNameAndDescription("mTest9", "this is a model test9")

        PrimitiveType PT_test1 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 1", MU_test1)
        PrimitiveType PT_test10 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 10", MU_test1)
        PrimitiveType PT_test11 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 11", MU_test1)
        PrimitiveType PT_test12 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 12", MU_test1)
        PrimitiveType PT_test2 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 2", MU_test1)
        PrimitiveType PT_test3 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 3", MU_test1)
        PrimitiveType PT_test4 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 4", MU_test1)
        PrimitiveType PT_test5 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 5", MU_test1)
        PrimitiveType PT_test6 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 6", MU_test1)
        PrimitiveType PT_test7 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 7", MU_test1)
        PrimitiveType PT_test8 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 8", MU_test1)
        PrimitiveType PT_test9 = primitiveTypeGormService.saveWithStatusAndNameAndMeasurementUnit(ElementStatus.FINALIZED, "Primitive Test 9", MU_test1)
        ReferenceType RT_test1 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 1", M_test1)
        ReferenceType RT_test10 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 10", M_test1)
        ReferenceType RT_test11 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 11", M_test1)
        ReferenceType RT_test12 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 12", M_test1)
        ReferenceType RT_test2 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 2", M_test1)
        ReferenceType RT_test3 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 3", M_test1)
        ReferenceType RT_test4 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 4", M_test1)
        ReferenceType RT_test5 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 5", M_test1)
        ReferenceType RT_test6 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 6", M_test1)
        ReferenceType RT_test7 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 7", M_test1)
        ReferenceType RT_test8 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 8", M_test1)
        ReferenceType RT_test9 = referenceTypeGormService.saveWithStatusAndNameAndDataClass(ElementStatus.FINALIZED, "Reference Test 9", M_test1)

        RelationshipType RT_antonym = relationshipTypeGormService.saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClass("Antonym", "AntonymousWith", "AntonymousWith", DataElement, DataElement)
        RelationshipType RT_pubRelationship = relationshipTypeGormService.saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClass( "pubRelationship", "relates to", "is relationship of", CatalogueElement, CatalogueElement)
        RelationshipType RT_relatedTerm = relationshipTypeGormService.saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClass("RelatedTerm", "relatedTo", "relatedTo", DataElement, DataElement)
        RelationshipType RT_relationship = relationshipTypeGormService.saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClass( "relationship", "relates to", "is relationship of", CatalogueElement, CatalogueElement, )
        RelationshipType RT_ruleReturnFalse = relationshipTypeGormService.saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClassAndRule( "falseRuleReturn", "broader term for",  "narrower term for", DataElement, DataElement, "return false")
        RelationshipType RT_synonym = relationshipTypeGormService.saveWithNameAndSourceDestinationAndDestinationToSourceAndSourceClassAndDestinationClass("Synonym", "SynonymousWith", "SynonymousWith", DataElement, DataElement)

        User U_Adam = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Adam", "Adam", "password", ElementStatus.FINALIZED)
        User U_Bella = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Bella", "Bella", "password", ElementStatus.FINALIZED)
        User U_Chris = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Chris", "Chris", "password", ElementStatus.FINALIZED)
        User U_David = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("David", "David", "password", ElementStatus.FINALIZED)
        User U_Emily = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Emily", "Emily", "password", ElementStatus.FINALIZED)
        User U_Frank = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Frank", "Frank", "password", ElementStatus.FINALIZED)
        User U_Gina = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Gina", "Gina", "password", ElementStatus.FINALIZED)
        User U_Henry = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Henry", "Henry", "password", ElementStatus.FINALIZED)
        User U_Ian = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Ian", "Ian", "password", ElementStatus.FINALIZED)
        User U_John = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("John", "John", "password", ElementStatus.FINALIZED)
        User U_Kris = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Kris", "Kris", "password", ElementStatus.FINALIZED)
        User U_Lily = userGormService.saveWithNameAndUsernameAndPasswordAndStatus("Lily", "Lily", "password", ElementStatus.FINALIZED)

        12.times {
            tagGormService.saveWithName("Tag #${it}")
        }
    }
}
