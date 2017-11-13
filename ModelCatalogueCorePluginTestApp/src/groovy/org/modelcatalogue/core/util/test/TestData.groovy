package org.modelcatalogue.core.util.test

import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelPolicy
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Mapping
import org.modelcatalogue.core.MeasurementUnit
import org.modelcatalogue.core.PrimitiveType
import org.modelcatalogue.core.ReferenceType
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.modelcatalogue.core.actions.Batch
import org.modelcatalogue.core.actions.CreateCatalogueElement
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation
import org.modelcatalogue.core.security.User

class TestData {

    static void createTestData() {
        Batch B_archived = new Batch(name: "Archived Batch").save(failOnError: true)
        Batch B_generic = new Batch(name: "Generic Batch").save(failOnError: true)
        Batch B_one = new Batch(name: "One").save(failOnError: true)
        Batch B_three = new Batch(name: "Three").save(failOnError: true)
        Batch B_two = new Batch(name: "Two").save(failOnError: true)
        Batch B_upgrade = new Batch(name: "Model Catalogue Upgrade 1.x").save(failOnError: true)

        Action A_create_model_5 = new Action(batch: B_generic, type: CreateCatalogueElement).save(failOnError: true)
        ActionParameter A_create_model_5_param_type = new ActionParameter(action: A_create_model_5, name: 'type', extensionValue: DataClass.name).save(failOnError: true)
        ActionParameter A_create_model_5_param_name = new ActionParameter(action: A_create_model_5, name: 'name', extensionValue: 'Test Model 5').save(failOnError: true)
        Action A_create_model_4 = new Action(batch: B_generic, type: CreateCatalogueElement).save(failOnError: true)
        ActionParameter A_create_model_4_param_type = new ActionParameter(action: A_create_model_4, name: 'type', extensionValue: DataClass.name).save(failOnError: true)
        ActionParameter A_create_model_4_param_name = new ActionParameter(action: A_create_model_4, name: 'name', extensionValue: 'Test Model 4').save(failOnError: true)
        Action A_create_model_1 = new Action(batch: B_generic, type: CreateCatalogueElement).save(failOnError: true)
        ActionParameter A_create_model_1_param_type = new ActionParameter(action: A_create_model_1, name: 'type', extensionValue: DataClass.name).save(failOnError: true)
        ActionParameter A_create_model_1_param_name = new ActionParameter(action: A_create_model_1, name: 'name', extensionValue: 'Test Model 1').save(failOnError: true)
        Action A_create_model_6 = new Action(batch: B_generic, type: CreateCatalogueElement).save(failOnError: true)
        ActionParameter A_create_model_6_param_type = new ActionParameter(action: A_create_model_6, name: 'type', extensionValue: DataClass.name).save(failOnError: true)
        ActionParameter A_create_model_6_param_name = new ActionParameter(action: A_create_model_6, name: 'name', extensionValue: 'Test Model 6').save(failOnError: true)
        Action A_create_model_3 = new Action(batch: B_generic, type: CreateCatalogueElement).save(failOnError: true)
        ActionParameter A_create_model_3_param_type = new ActionParameter(action: A_create_model_3, name: 'type', extensionValue: DataClass.name).save(failOnError: true)
        ActionParameter A_create_model_3_param_name = new ActionParameter(action: A_create_model_3, name: 'name', extensionValue: 'Test Model 3').save(failOnError: true)
        Action A_create_model_2 = new Action(batch: B_generic, type: CreateCatalogueElement).save(failOnError: true)
        ActionParameter A_create_model_2_param_type = new ActionParameter(action: A_create_model_2, name: 'type', extensionValue: DataClass.name).save(failOnError: true)
        ActionParameter A_create_model_2_param_name = new ActionParameter(action: A_create_model_2, name: 'name', extensionValue: 'Test Model 2').save(failOnError: true)

        Asset A_directory = new Asset(name:"directory", description: "random directory").save(failOnError: true)
        Asset A_file = new Asset(name:"file", description: "random file", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file1 = new Asset(name:"file1", description: "some random file 1", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file2 = new Asset(name:"ASSET", description: "some random file 2", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file3 = new Asset(name:"A_file3", description: "the random name 3", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file4 = new Asset(name:"A_file4", description: "some random file 4", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file5 = new Asset(name:"A_file5", description: "random file 5", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file6 = new Asset(name:"A_file6", description: "random file 6", status: ElementStatus.FINALIZED).save(failOnError: true)
        Asset A_file7 = new Asset(name:"A_file7", description: "random file 7").save(failOnError: true)
        Asset A_file8 = new Asset(name:"A_file8", description: "random file 8").save(failOnError: true)
        Asset A_file9 = new Asset(name:"A_file9", description: "random file 9").save(failOnError: true)
        Asset A_image = new Asset(name:"image", description: "random image").save(failOnError: true)

        for (int i = 1 ; i <= 12 ; i++) {
            new DataModelPolicy(name:"Policy $i", policyText: 'check dataType property "name" is unique').save(failOnError: true)
        }

        for (int i = 1 ; i <= 12 ; i++) {
            new ValidationRule(name:"rule$i", description: "some random rule $i", status: i <=6 ? ElementStatus.FINALIZED : ElementStatus.DRAFT).save(failOnError: true)
        }

        DataModel CL_dataSet1 = new DataModel(name: "data set a", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet10 = new DataModel(name: "data set 10", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet11 = new DataModel(name: "data set 11", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet12 = new DataModel(name: "data set 12", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet2 = new DataModel(name: "data set 2", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet3 = new DataModel(name: "data set 3", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet4 = new DataModel(name: "data set 4", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet5 = new DataModel(name: "data set 5", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet6 = new DataModel(name: "data set 6", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet7 = new DataModel(name: "data set 7", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet8 = new DataModel(name: "data set 8", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        DataModel CL_dataSet9 = new DataModel(name: "data set 9", status: ElementStatus.FINALIZED, description: "test data set").save(failOnError: true)
        CsvTransformation CT_example = new CsvTransformation(name: "Example").save(failOnError: true)
        DataType DT_boolean = new DataType(status: ElementStatus.FINALIZED, name: "boolean", description: "a boolean xdfxdf").save(failOnError: true)
        DataType DT_double = new DataType(tatus: ElementStatus.FINALIZED,  name: "double", description: "a double").save(failOnError: true)
        DataType DT_integer = new DataType(status: ElementStatus.FINALIZED, name: "integer", description: "an integer").save(failOnError: true)
        DataType DT_string = new DataType(status: ElementStatus.FINALIZED, name: "String", description: "a string").save(failOnError: true)
        DataType DT_test1 = new DataType(status: ElementStatus.FINALIZED, name: "test1", description: "test data type 1").save(failOnError: true)
        DataType DT_test2 = new DataType(status: ElementStatus.FINALIZED, name: "test2", description: "test data type 2").save(failOnError: true)
        DataType DT_test3 = new DataType(status: ElementStatus.FINALIZED, name: "test3", description: "test data type 3").save(failOnError: true)
        DataType DT_test4 = new DataType(status: ElementStatus.FINALIZED, name: "test4", description: "test data type 4").save(failOnError: true)
        DataType DT_test5 = new DataType(status: ElementStatus.FINALIZED, name: "test5", description: "test data type 5").save(failOnError: true)
        DataType DT_test6 = new DataType(status: ElementStatus.FINALIZED, name: "test6", description: "test data type 6").save(failOnError: true)
        DataType DT_test7 = new DataType(status: ElementStatus.FINALIZED, name: "xs:string", description: "xml string type").save(failOnError: true)
        DataType DT_test8 = new DataType(status: ElementStatus.FINALIZED,  name: "test8", description: "test data type 8").save(failOnError: true)
        DataElement DE_author = new DataElement(name:"DE_author", description: "the DE_author of the book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataElement DE_author1 = new DataElement(name:"DE_author1", description: "the DE_author of the book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataElement DE_author2 = new DataElement(name:"AUTHOR", description: "the DE_author of the book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataElement DE_author3 = new DataElement(name:"auth", description: "the DE_author of the book", status: ElementStatus.FINALIZED, dataModel: CL_dataSet2).save(failOnError: true)
        DataElement DE_author4 = new DataElement(name:"auth4", description: "the DE_author of the book", status: ElementStatus.FINALIZED, dataModel: CL_dataSet1).save(failOnError: true)
        DataElement DE_author5 = new DataElement(name:"auth5", description: "the DE_author of the book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataElement DE_opel = new DataElement(name:"speed of Opel", description: "speed of your Opel car", dataType: DT_test3).save(failOnError: true)
        DataElement DE_patient_temperature_uk = new DataElement(name:"patient temperature uk", description: "Patient's Temperature in the UK", status: ElementStatus.FINALIZED, dataType: DT_test1).save(failOnError: true)
        DataElement DE_patient_temperature_us = new DataElement(name:"patient temperature us", description: "Patient's Temperature in the US", dataType: DT_test2).save(failOnError: true)
        DataElement DE_title = new DataElement(name:"title", description: "the title of the book").save(failOnError: true)
        DataElement DE_vauxhall = new DataElement(name:"speed of Vauxhall", description: "speed of your Vauxhall car", dataType: DT_test4).save(failOnError: true)
        DataElement DE_writer = new DataElement(name:"writer", description: "the writer of the book").save(failOnError: true)
        ColumnTransformationDefinition CTD_cars = new ColumnTransformationDefinition(transformation: CT_example, source: DE_opel, destination: DE_vauxhall).save(failOnError: true)
        ColumnTransformationDefinition CTD_author = new ColumnTransformationDefinition(transformation: CT_example, source: DE_writer, destination: DE_author, header: "creator").save(failOnError: true)
        ColumnTransformationDefinition CTD_degrees = new ColumnTransformationDefinition(transformation: CT_example, source: DE_patient_temperature_uk, destination: DE_patient_temperature_us).save(failOnError: true)
        ColumnTransformationDefinition CTD_coauthor = new ColumnTransformationDefinition(transformation: CT_example, source: DE_author5, header: "co-author").save(failOnError: true)
        EnumeratedType ET_gender = new EnumeratedType(status: ElementStatus.FINALIZED, name: "gender", enumerations:['m':'male', 'f':'female', 'u':'unknown', 'ns':'not specified']).save(failOnError: true)
        EnumeratedType ET_schoolSubjects = new EnumeratedType(status: ElementStatus.FINALIZED, name: "sub1", enumerations:['H':'history', 'P':'politics', 'SCI':'science', 'GEO':'geography']).save(failOnError: true)
        EnumeratedType ET_test1 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest1", enumerations:['m1':'test1', 'm2':'test2']).save(failOnError: true)
        EnumeratedType ET_test2 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest2", enumerations:['m2m':'test2', 'm3m':'test3']).save(failOnError: true)
        EnumeratedType ET_test3 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest3", enumerations:['m3m':'test3', 'm22m':'test22']).save(failOnError: true)
        EnumeratedType ET_test4 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest4", enumerations:['m4m':'test4', 'm2m':'test2']).save(failOnError: true)
        EnumeratedType ET_test5 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest5", enumerations:['m5m':'test5', 'm2m':'test2']).save(failOnError: true)
        EnumeratedType ET_test6 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest6", enumerations:['m6m':'test6', 'm2m':'test2']).save(failOnError: true)
        EnumeratedType ET_test7 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest7", enumerations:['m7m':'test7', 'm2m':'test2']).save(failOnError: true)
        EnumeratedType ET_test8 = new EnumeratedType(status: ElementStatus.FINALIZED, name: "etTest8", enumerations:['m8m':'test8', 'm2m':'test2']).save(failOnError: true)
        EnumeratedType ET_uni2Subjects = new EnumeratedType(status: ElementStatus.FINALIZED, name: "sub2", enumerations:['HISTORY':'history', 'POLITICS':'politics', 'SCIENCE':'science']).save(failOnError: true)
        EnumeratedType ET_uniSubjects = new EnumeratedType(status: ElementStatus.FINALIZED, name: "sub3", enumerations:['h':'history', 'p':'politics', 'sci':'science']).save(failOnError: true)
        ExtensionValue Ex_first = new ExtensionValue(name: "metadata", extensionValue: "metadata value", element: DE_author1).save(failOnError: true)
        Mapping Mapping_C_to_F = new Mapping(source: DT_test1, destination: DT_test2, mapping: "(x as Double) * 9 / 5 + 32").save(failOnError: true)
        Mapping Mapping_F_to_C = new Mapping(source: DT_test2, destination: DT_test1, mapping: "((x as Double) - 32) * 5 / 9").save(failOnError: true)
        Mapping Mapping_kph_to_mph = new Mapping(source: DT_test3, destination: DT_test4, mapping: "(x as Double) * 0.621371192").save(failOnError: true)
        Mapping Mapping_mph_to_kph = new Mapping(source: DT_test4, destination: DT_test3, mapping: "(x as Double) * 1.609344").save(failOnError: true)
        MeasurementUnit MU_degree_C = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°C", name: "Degrees Celsius", description: """Celsius, also known as centigrade,[1] is a scale and unit of measurement for temperature. It is named after the Swedish astronomer Anders Celsius (1701–1744), who developed a similar temperature scale. The degree Celsius (°C) can refer to a specific temperature on the Celsius scale as well as a unit to indicate a temperature interval, a difference between two temperatures or an uncertainty. The unit was known until 1948 as "centigrade" from the Latin centum translated as 100 and gradus translated as "steps".""").save(failOnError: true)
        MeasurementUnit MU_degree_F = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°F", name: "Degrees of Fahrenheit", description: """Fahrenheit (symbol °F) is a temperature scale based on one proposed in 1724 by the physicist Daniel Gabriel Fahrenheit (1686–1736), after whom the scale is named.[1] On Fahrenheit's original scale the lower defining point was the lowest temperature to which he could reproducibly cool brine (defining 0 degrees), while the highest was that of the average human core body temperature (defining 100 degrees). There exist several stories on the exact original definition of his scale; however, some of the specifics have been presumed lost or exaggerated with time. The scale is now usually defined by two fixed points: the temperature at which water freezes into ice is defined as 32 degrees, and the boiling point of water is defined to be 212 degrees, a 180 degree separation, as defined at sea level and standard atmospheric pressure.""").save(failOnError: true)
        MeasurementUnit MU_kph = new MeasurementUnit(status: ElementStatus.FINALIZED, name:"Kilometers per hour", symbol: "KPH").save(failOnError: true)
        MeasurementUnit MU_milesPerHour = new MeasurementUnit(status: ElementStatus.FINALIZED, name:"Miles per hour", symbol: "MPH").save(failOnError: true)
        MeasurementUnit MU_test1 = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°1", name: "test mu1", description: "test1 mu").save(failOnError: true)
        MeasurementUnit MU_test2 = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°2", name: "test mu2", description: "test2 mu").save(failOnError: true)
        MeasurementUnit MU_test3 = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°3", name: "test mu3", description: "test3 mu").save(failOnError: true)
        MeasurementUnit MU_test4 = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°4", name: "test mu4", description: "test4 mu").save(failOnError: true)
        MeasurementUnit MU_test5 = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°5", name: "test mu5", description: "test5 mu").save(failOnError: true)
        MeasurementUnit MU_test6 = new MeasurementUnit(tatus: ElementStatus.FINALIZED,  symbol: "°6", name: "test mu6", description: "test6 mu").save(failOnError: true)
        MeasurementUnit MU_test7 = new MeasurementUnit(tatus: ElementStatus.FINALIZED,  symbol: "°7", name: "test mu7", description: "test7 mu").save(failOnError: true)
        MeasurementUnit MU_test8 = new MeasurementUnit(status: ElementStatus.FINALIZED, symbol: "°8", name: "test mu8", description: "test8 mu").save(failOnError: true)
        DataClass M_book = new DataClass(name: "book", description: "this is a model of a book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataClass M_chapter1 = new DataClass(name: "chapter1", description: "The Jabberwocky chapter for a book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataClass M_chapter2 = new DataClass(name: "chapter2", description: "this is a second chapter for a book", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataClass M_test1 = new DataClass(name: "mTest1", description: "this is a model test1", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataClass M_test2 = new DataClass(name: "mTest2", description: "this is a model test2", status: ElementStatus.FINALIZED).save(failOnError: true)
        DataClass M_test3 = new DataClass(name: "mTest3", description: "this is a model test3").save(failOnError: true)
        DataClass M_test4 = new DataClass(name: "mTest4", description: "this is a model test4").save(failOnError: true)
        DataClass M_test5 = new DataClass(name: "mTest5", description: "this is a model test5").save(failOnError: true)
        DataClass M_test6 = new DataClass(name: "mTest6", description: "this is a model test6").save(failOnError: true)
        DataClass M_test7 = new DataClass(name: "mTest7", description: "this is a model test7").save(failOnError: true)
        DataClass M_test8 = new DataClass(name: "mTest8", description: "this is a model test8").save(failOnError: true)
        DataClass M_test9 = new DataClass(name: "mTest9", description: "this is a model test9").save(failOnError: true)
        PrimitiveType PT_test1 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 1", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test10 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 10", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test11 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 11", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test12 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 12", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test2 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 2", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test3 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 3", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test4 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 4", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test5 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 5", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test6 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 6", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test7 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 7", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test8 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 8", measurementUnit: MU_test1).save(failOnError: true)
        PrimitiveType PT_test9 = new PrimitiveType(status: ElementStatus.FINALIZED, name: "Primitive Test 9", measurementUnit: MU_test1).save(failOnError: true)
        ReferenceType RT_test1 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 1", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test10 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 10", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test11 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 11", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test12 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 12", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test2 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 2", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test3 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 3", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test4 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 4", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test5 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 5", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test6 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 6", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test7 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 7", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test8 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 8", dataClass: M_test1).save(failOnError: true)
        ReferenceType RT_test9 = new ReferenceType(status: ElementStatus.FINALIZED, name: "Reference Test 9", dataClass: M_test1).save(failOnError: true)
        RelationshipType RT_antonym = new RelationshipType(name:"Antonym", sourceToDestination: "AntonymousWith", destinationToSource: "AntonymousWith", sourceClass: DataElement, destinationClass: DataElement).save(failOnError: true)
        RelationshipType RT_pubRelationship = new RelationshipType(name: "pubRelationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is relationship of").save(failOnError: true)
        RelationshipType RT_relatedTerm = new RelationshipType(name:"RelatedTerm", sourceToDestination: "relatedTo", destinationToSource: "relatedTo", sourceClass: DataElement, destinationClass: DataElement).save(failOnError: true)
        RelationshipType RT_relationship = new RelationshipType(name: "relationship", sourceClass: CatalogueElement, destinationClass: CatalogueElement, sourceToDestination: "relates to", destinationToSource: "is relationship of").save(failOnError: true)
        RelationshipType RT_ruleReturnFalse = new RelationshipType(name: "falseRuleReturn", sourceClass: DataElement, destinationClass: DataElement, destinationToSource: "narrower term for", sourceToDestination: "broader term for", rule: "return false").save(failOnError: true)
        RelationshipType RT_synonym = new RelationshipType(name:"Synonym", sourceToDestination: "SynonymousWith", destinationToSource: "SynonymousWith", sourceClass: DataElement, destinationClass: DataElement).save(failOnError: true)
        User U_Adam = new User(name: "Adam", username: "Adam", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Bella = new User(name: "Bella", username: "Bella", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Chris = new User(name: "Chris", username: "Chris", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_David = new User(name: "David", username: "David", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Emily = new User(name: "Emily", username: "Emily", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Frank = new User(name: "Frank", username: "Frank", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Gina = new User(name: "Gina", username: "Gina", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Henry = new User(name: "Henry", username: "Henry", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Ian = new User(name: "Ian", username: "Ian", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_John = new User(name: "John", username: "John", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Kris = new User(name: "Kris", username: "Kris", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)
        User U_Lily = new User(name: "Lily", username: "Lily", password: "password", status: ElementStatus.FINALIZED).save(failOnError: true)

        12.times {
            new Tag(name: "Tag #${it}").save(failOnError: true)
        }
    }

}
