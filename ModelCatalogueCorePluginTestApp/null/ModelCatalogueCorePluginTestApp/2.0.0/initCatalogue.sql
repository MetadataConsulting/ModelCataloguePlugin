;              
CREATE USER IF NOT EXISTS SA SALT '2a5ab4392720bf4e' HASH '4542f7143fbd46546c348c1feca1ef861930d46b43e17aff5bc72999423d5989' ADMIN;            
DROP TABLE IF EXISTS PUBLIC."change" CASCADE;  
DROP TABLE IF EXISTS PUBLIC.ACL_CLASS CASCADE; 
DROP TABLE IF EXISTS PUBLIC.ACL_ENTRY CASCADE; 
DROP TABLE IF EXISTS PUBLIC.ACL_OBJECT_IDENTITY CASCADE;       
DROP TABLE IF EXISTS PUBLIC.ACL_SID CASCADE;   
DROP TABLE IF EXISTS PUBLIC.ACTION CASCADE;    
DROP TABLE IF EXISTS PUBLIC.ACTION_DEPENDENCY CASCADE;         
DROP TABLE IF EXISTS PUBLIC.ACTION_PARAMETER CASCADE;          
DROP TABLE IF EXISTS PUBLIC.ASSET CASCADE;     
DROP TABLE IF EXISTS PUBLIC.ASSET_FILE CASCADE;
DROP TABLE IF EXISTS PUBLIC.BATCH CASCADE;     
DROP TABLE IF EXISTS PUBLIC.CATALOGUE_ELEMENT CASCADE;         
DROP TABLE IF EXISTS PUBLIC.COLUMN_TRANSFORMATION_DEFINITION CASCADE;          
DROP TABLE IF EXISTS PUBLIC.CSV_TRANSFORMATION CASCADE;        
DROP TABLE IF EXISTS PUBLIC.DATA_CLASS CASCADE;
DROP TABLE IF EXISTS PUBLIC.DATA_ELEMENT CASCADE;              
DROP TABLE IF EXISTS PUBLIC.DATA_MODEL CASCADE;
DROP TABLE IF EXISTS PUBLIC.DATA_MODEL_DATA_MODEL_POLICY CASCADE;              
DROP TABLE IF EXISTS PUBLIC.DATA_MODEL_POLICY CASCADE;         
DROP TABLE IF EXISTS PUBLIC.DATA_TYPE CASCADE; 
DROP TABLE IF EXISTS PUBLIC.ENUMERATED_TYPE CASCADE;           
DROP TABLE IF EXISTS PUBLIC.EXTENSION_VALUE CASCADE;           
DROP TABLE IF EXISTS PUBLIC.MAPPING CASCADE;   
DROP TABLE IF EXISTS PUBLIC.MEASUREMENT_UNIT CASCADE;          
DROP TABLE IF EXISTS PUBLIC.OAUTHID CASCADE;   
DROP TABLE IF EXISTS PUBLIC.PRIMITIVE_TYPE CASCADE;            
DROP TABLE IF EXISTS PUBLIC.REFERENCE_TYPE CASCADE;            
DROP TABLE IF EXISTS PUBLIC.REGISTRATION_CODE CASCADE;         
DROP TABLE IF EXISTS PUBLIC.RELATIONSHIP CASCADE;              
DROP TABLE IF EXISTS PUBLIC.RELATIONSHIP_METADATA CASCADE;     
DROP TABLE IF EXISTS PUBLIC.RELATIONSHIP_TYPE CASCADE;         
DROP TABLE IF EXISTS PUBLIC.REQUESTMAP CASCADE;
DROP TABLE IF EXISTS PUBLIC.ROLE CASCADE;      
DROP TABLE IF EXISTS PUBLIC.TAG CASCADE;       
DROP TABLE IF EXISTS PUBLIC.USER CASCADE;      
DROP TABLE IF EXISTS PUBLIC.USER_ROLE CASCADE; 
DROP TABLE IF EXISTS PUBLIC.VALIDATION_RULE CASCADE;           
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_B73D2D48_B9EB_408C_90B3_6B5BBD20A79C START WITH 137 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A30922A4_033F_4C06_AC65_CDDA6C684D18 START WITH 1 BELONGS_TO_TABLE;     
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_FF10B2D8_8075_42FC_9B50_3B03281A4B70 START WITH 764 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_5C09A253_1193_42F7_8A18_0E5D39EF5E5F START WITH 65 BELONGS_TO_TABLE;    
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_EE1CCD12_5ABA_44B9_841B_EB946A1AF8E4 START WITH 1 BELONGS_TO_TABLE;     
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_986BF911_1ED6_48FD_8111_65FBF1F31C4B START WITH 9397 BELONGS_TO_TABLE;  
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_28AD9A6B_6861_4FEB_B19E_1C5E409E419E START WITH 396 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_02C1CBE2_AC91_4491_BBDD_52943DB39F47 START WITH 71 BELONGS_TO_TABLE;    
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_58A3A240_EE94_4CB6_A3AA_44C7A1DED7B3 START WITH 1 BELONGS_TO_TABLE;     
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_FA1F68A8_BD29_4CC2_884B_0CE6B5D946F4 START WITH 1206 BELONGS_TO_TABLE;  
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_CEF088DF_D7E8_4876_9297_5A82B37AA2D0 START WITH 136 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_AB101B45_C113_477C_864A_0A77F1AA9C42 START WITH 701 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_9DB6221B_ADD8_42AD_84F2_D2AB5ECF8565 START WITH 30993 BELONGS_TO_TABLE; 
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_1F290E42_6C97_4A53_B1A2_266886AC63E0 START WITH 61 BELONGS_TO_TABLE;    
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_62E61E11_5898_4FBA_92E2_90CF81DDDF5A START WITH 2903 BELONGS_TO_TABLE;  
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_AFD576D2_5FA8_4B88_949D_0E8E8EB21120 START WITH 17 BELONGS_TO_TABLE;    
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_48CEAAAF_2BEA_4F58_B74D_43F8F7F66F39 START WITH 12604 BELONGS_TO_TABLE; 
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A99F4BEF_4333_49FF_AB4B_73A6036EFBC1 START WITH 2954 BELONGS_TO_TABLE;  
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_928C5C84_0376_43E0_9024_0058AD5666D3 START WITH 1122 BELONGS_TO_TABLE;  
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_F84A0490_6AE9_4FDB_9712_87B6144B0C60 START WITH 363 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_D2222819_E18D_4DE8_A51B_6FF4F1051220 START WITH 240 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A026FE1B_84B2_4EDF_8553_641B5DABD8BA START WITH 280 BELONGS_TO_TABLE;   
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_1B50A1AE_487D_4735_A2C9_322B1D071584 START WITH 67 BELONGS_TO_TABLE;    
CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_546D4B5E_1C91_4B99_8DE6_E9279EB9FD70 START WITH 726 BELONGS_TO_TABLE;   
CREATE MEMORY TABLE PUBLIC."change"(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_9DB6221B_ADD8_42AD_84F2_D2AB5ECF8565) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_9DB6221B_ADD8_42AD_84F2_D2AB5ECF8565,
    AUTHOR_ID BIGINT,
    CHANGED_ID BIGINT NOT NULL,
    DATE_CREATED TIMESTAMP NOT NULL,
    LATEST_VERSION_ID BIGINT NOT NULL,
    NEW_VALUE LONGVARCHAR,
    OLD_VALUE LONGVARCHAR,
    OTHER_SIDE BOOLEAN NOT NULL,
    PARENT_ID BIGINT,
    PROPERTY VARCHAR(255),
    SYSTEM BOOLEAN,
    TYPE VARCHAR(255) NOT NULL,
    UNDONE BOOLEAN
);     
ALTER TABLE PUBLIC."change" ADD CONSTRAINT PUBLIC.CONSTRAINT_A PRIMARY KEY(ID);
-- 797 +/- SELECT COUNT(*) FROM PUBLIC."change";               
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30196, NULL, 12478, TIMESTAMP '2018-01-03 17:25:00.2', 12478, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30197, NULL, 12479, TIMESTAMP '2018-01-03 17:25:00.207', 12479, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30198, NULL, 12480, TIMESTAMP '2018-01-03 17:25:00.247', 12480, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30199, NULL, 12481, TIMESTAMP '2018-01-03 17:25:00.288', 12481, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30200, NULL, 12482, TIMESTAMP '2018-01-03 17:25:00.324', 12482, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30201, NULL, 12483, TIMESTAMP '2018-01-03 17:25:00.365', 12483, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30202, NULL, 12484, TIMESTAMP '2018-01-03 17:25:00.402', 12484, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30203, NULL, 12485, TIMESTAMP '2018-01-03 17:25:00.435', 12485, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30204, NULL, 12486, TIMESTAMP '2018-01-03 17:25:00.476', 12486, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30205, NULL, 12487, TIMESTAMP '2018-01-03 17:25:01.095', 12487, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30206, NULL, 12488, TIMESTAMP '2018-01-03 17:25:01.145', 12488, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30207, NULL, 12489, TIMESTAMP '2018-01-03 17:25:01.209', 12489, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30208, NULL, 12490, TIMESTAMP '2018-01-03 17:25:01.267', 12490, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30209, NULL, 12491, TIMESTAMP '2018-01-03 17:25:01.319', 12491, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30210, NULL, 12492, TIMESTAMP '2018-01-03 17:25:01.368', 12492, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30211, NULL, 12493, TIMESTAMP '2018-01-03 17:25:01.417', 12493, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30212, NULL, 12494, TIMESTAMP '2018-01-03 17:25:01.463', 12494, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30213, NULL, 12495, TIMESTAMP '2018-01-03 17:25:01.501', 12495, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30214, NULL, 12496, TIMESTAMP '2018-01-03 17:25:01.532', 12496, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30215, NULL, 12497, TIMESTAMP '2018-01-03 17:25:01.564', 12497, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30216, NULL, 12498, TIMESTAMP '2018-01-03 17:25:02.404', 12498, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30217, NULL, 12499, TIMESTAMP '2018-01-03 17:25:02.45', 12499, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30218, NULL, 12500, TIMESTAMP '2018-01-03 17:25:02.489', 12500, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30219, NULL, 12501, TIMESTAMP '2018-01-03 17:25:02.524', 12501, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30220, NULL, 12502, TIMESTAMP '2018-01-03 17:25:02.559', 12502, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30221, NULL, 12503, TIMESTAMP '2018-01-03 17:25:02.617', 12503, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30222, NULL, 12504, TIMESTAMP '2018-01-03 17:25:02.678', 12504, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30223, NULL, 12505, TIMESTAMP '2018-01-03 17:25:02.73', 12505, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30224, NULL, 12506, TIMESTAMP '2018-01-03 17:25:02.769', 12506, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30225, NULL, 12507, TIMESTAMP '2018-01-03 17:25:02.811', 12507, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE);    
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30226, NULL, 12508, TIMESTAMP '2018-01-03 17:25:02.855', 12508, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30227, NULL, 12509, TIMESTAMP '2018-01-03 17:25:02.906', 12509, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30228, NULL, 12510, TIMESTAMP '2018-01-03 17:25:03.426', 12510, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30229, NULL, 12511, TIMESTAMP '2018-01-03 17:25:03.471', 12511, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30230, NULL, 12512, TIMESTAMP '2018-01-03 17:25:03.479', 12512, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30231, NULL, 12513, TIMESTAMP '2018-01-03 17:25:03.485', 12513, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30232, NULL, 12483, TIMESTAMP '2018-01-03 17:25:03.503', 12483, '{"id":2895,"source":{"semanticVersion":"0.0.1","name":"xs:normalizedString","id":12484,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12484","versionNumber":1,"latestVersionId":12484,"classifiedName":"xs:normalizedString (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:string","id":12483,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12483","versionNumber":1,"latestVersionId":12483,"classifiedName":"xs:string (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30233, NULL, 12484, TIMESTAMP '2018-01-03 17:25:03.506', 12484, '{"id":2895,"source":{"semanticVersion":"0.0.1","name":"xs:normalizedString","id":12484,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12484","versionNumber":1,"latestVersionId":12484,"classifiedName":"xs:normalizedString (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:string","id":12483,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12483","versionNumber":1,"latestVersionId":12483,"classifiedName":"xs:string (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30234, NULL, 12484, TIMESTAMP '2018-01-03 17:25:03.53', 12484, '{"id":2896,"source":{"semanticVersion":"0.0.1","name":"xs:token","id":12485,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12485","versionNumber":1,"latestVersionId":12485,"classifiedName":"xs:token (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:normalizedString","id":12484,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12484","versionNumber":1,"latestVersionId":12484,"classifiedName":"xs:normalizedString (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30235, NULL, 12485, TIMESTAMP '2018-01-03 17:25:03.53', 12485, '{"id":2896,"source":{"semanticVersion":"0.0.1","name":"xs:token","id":12485,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12485","versionNumber":1,"latestVersionId":12485,"classifiedName":"xs:token (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:normalizedString","id":12484,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12484","versionNumber":1,"latestVersionId":12484,"classifiedName":"xs:normalizedString (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30236, NULL, 12485, TIMESTAMP '2018-01-03 17:25:03.558', 12485, '{"id":2897,"source":{"semanticVersion":"0.0.1","name":"xs:language","id":12486,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12486","versionNumber":1,"latestVersionId":12486,"classifiedName":"xs:language (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:token","id":12485,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12485","versionNumber":1,"latestVersionId":12485,"classifiedName":"xs:token (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE);       
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30237, NULL, 12486, TIMESTAMP '2018-01-03 17:25:03.558', 12486, '{"id":2897,"source":{"semanticVersion":"0.0.1","name":"xs:language","id":12486,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12486","versionNumber":1,"latestVersionId":12486,"classifiedName":"xs:language (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:token","id":12485,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12485","versionNumber":1,"latestVersionId":12485,"classifiedName":"xs:token (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30238, NULL, 12490, TIMESTAMP '2018-01-03 17:25:03.585', 12490, '{"id":2898,"source":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:decimal","id":12487,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12487","versionNumber":1,"latestVersionId":12487,"classifiedName":"xs:decimal (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30239, NULL, 12487, TIMESTAMP '2018-01-03 17:25:03.585', 12487, '{"id":2898,"source":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:decimal","id":12487,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12487","versionNumber":1,"latestVersionId":12487,"classifiedName":"xs:decimal (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30240, NULL, 12490, TIMESTAMP '2018-01-03 17:25:03.617', 12490, '{"id":2899,"source":{"semanticVersion":"0.0.1","name":"xs:long","id":12491,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12491","versionNumber":1,"latestVersionId":12491,"classifiedName":"xs:long (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30241, NULL, 12491, TIMESTAMP '2018-01-03 17:25:03.617', 12491, '{"id":2899,"source":{"semanticVersion":"0.0.1","name":"xs:long","id":12491,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12491","versionNumber":1,"latestVersionId":12491,"classifiedName":"xs:long (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30242, NULL, 12495, TIMESTAMP '2018-01-03 17:25:03.646', 12495, '{"id":2900,"source":{"semanticVersion":"0.0.1","name":"xs:nonNegativeInteger","id":12495,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12495","versionNumber":1,"latestVersionId":12495,"classifiedName":"xs:nonNegativeInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE);      
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30243, NULL, 12490, TIMESTAMP '2018-01-03 17:25:03.646', 12490, '{"id":2900,"source":{"semanticVersion":"0.0.1","name":"xs:nonNegativeInteger","id":12495,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12495","versionNumber":1,"latestVersionId":12495,"classifiedName":"xs:nonNegativeInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30244, NULL, 12490, TIMESTAMP '2018-01-03 17:25:03.67', 12490, '{"id":2901,"source":{"semanticVersion":"0.0.1","name":"xs:nonPositiveInteger","id":12496,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12496","versionNumber":1,"latestVersionId":12496,"classifiedName":"xs:nonPositiveInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30245, NULL, 12496, TIMESTAMP '2018-01-03 17:25:03.67', 12496, '{"id":2901,"source":{"semanticVersion":"0.0.1","name":"xs:nonPositiveInteger","id":12496,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12496","versionNumber":1,"latestVersionId":12496,"classifiedName":"xs:nonPositiveInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:integer","id":12490,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12490","versionNumber":1,"latestVersionId":12490,"classifiedName":"xs:integer (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30246, NULL, 12492, TIMESTAMP '2018-01-03 17:25:03.694', 12492, '{"id":2902,"source":{"semanticVersion":"0.0.1","name":"xs:int","id":12492,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12492","versionNumber":1,"latestVersionId":12492,"classifiedName":"xs:int (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:long","id":12491,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12491","versionNumber":1,"latestVersionId":12491,"classifiedName":"xs:long (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30247, NULL, 12491, TIMESTAMP '2018-01-03 17:25:03.694', 12491, '{"id":2902,"source":{"semanticVersion":"0.0.1","name":"xs:int","id":12492,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12492","versionNumber":1,"latestVersionId":12492,"classifiedName":"xs:int (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:long","id":12491,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12491","versionNumber":1,"latestVersionId":12491,"classifiedName":"xs:long (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30248, NULL, 12492, TIMESTAMP '2018-01-03 17:25:03.723', 12492, '{"id":2903,"source":{"semanticVersion":"0.0.1","name":"xs:short","id":12493,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12493","versionNumber":1,"latestVersionId":12493,"classifiedName":"xs:short (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:int","id":12492,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12492","versionNumber":1,"latestVersionId":12492,"classifiedName":"xs:int (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE);        
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30249, NULL, 12493, TIMESTAMP '2018-01-03 17:25:03.723', 12493, '{"id":2903,"source":{"semanticVersion":"0.0.1","name":"xs:short","id":12493,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12493","versionNumber":1,"latestVersionId":12493,"classifiedName":"xs:short (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:int","id":12492,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12492","versionNumber":1,"latestVersionId":12492,"classifiedName":"xs:int (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30250, NULL, 12494, TIMESTAMP '2018-01-03 17:25:03.756', 12494, '{"id":2904,"source":{"semanticVersion":"0.0.1","name":"xs:byte","id":12494,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12494","versionNumber":1,"latestVersionId":12494,"classifiedName":"xs:byte (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:short","id":12493,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12493","versionNumber":1,"latestVersionId":12493,"classifiedName":"xs:short (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30251, NULL, 12493, TIMESTAMP '2018-01-03 17:25:03.756', 12493, '{"id":2904,"source":{"semanticVersion":"0.0.1","name":"xs:byte","id":12494,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12494","versionNumber":1,"latestVersionId":12494,"classifiedName":"xs:byte (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:short","id":12493,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12493","versionNumber":1,"latestVersionId":12493,"classifiedName":"xs:short (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30252, NULL, 12497, TIMESTAMP '2018-01-03 17:25:03.788', 12497, '{"id":2905,"source":{"semanticVersion":"0.0.1","name":"xs:negativeInteger","id":12497,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12497","versionNumber":1,"latestVersionId":12497,"classifiedName":"xs:negativeInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:nonPositiveInteger","id":12496,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12496","versionNumber":1,"latestVersionId":12496,"classifiedName":"xs:nonPositiveInteger (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30253, NULL, 12496, TIMESTAMP '2018-01-03 17:25:03.788', 12496, '{"id":2905,"source":{"semanticVersion":"0.0.1","name":"xs:negativeInteger","id":12497,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12497","versionNumber":1,"latestVersionId":12497,"classifiedName":"xs:negativeInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:nonPositiveInteger","id":12496,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12496","versionNumber":1,"latestVersionId":12496,"classifiedName":"xs:nonPositiveInteger (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30254, NULL, 12495, TIMESTAMP '2018-01-03 17:25:03.811', 12495, '{"id":2906,"source":{"semanticVersion":"0.0.1","name":"xs:positiveInteger","id":12498,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12498","versionNumber":1,"latestVersionId":12498,"classifiedName":"xs:positiveInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:nonNegativeInteger","id":12495,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12495","versionNumber":1,"latestVersionId":12495,"classifiedName":"xs:nonNegativeInteger (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE);             
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30255, NULL, 12498, TIMESTAMP '2018-01-03 17:25:03.811', 12498, '{"id":2906,"source":{"semanticVersion":"0.0.1","name":"xs:positiveInteger","id":12498,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12498","versionNumber":1,"latestVersionId":12498,"classifiedName":"xs:positiveInteger (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:nonNegativeInteger","id":12495,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12495","versionNumber":1,"latestVersionId":12495,"classifiedName":"xs:nonNegativeInteger (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30256, NULL, 12499, TIMESTAMP '2018-01-03 17:25:03.832', 12499, '{"id":2907,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedLong","id":12499,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12499","versionNumber":1,"latestVersionId":12499,"classifiedName":"xs:unsignedLong (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:nonNegativeInteger","id":12495,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12495","versionNumber":1,"latestVersionId":12495,"classifiedName":"xs:nonNegativeInteger (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30257, NULL, 12495, TIMESTAMP '2018-01-03 17:25:03.831', 12495, '{"id":2907,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedLong","id":12499,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12499","versionNumber":1,"latestVersionId":12499,"classifiedName":"xs:unsignedLong (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:nonNegativeInteger","id":12495,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12495","versionNumber":1,"latestVersionId":12495,"classifiedName":"xs:nonNegativeInteger (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30258, NULL, 12500, TIMESTAMP '2018-01-03 17:25:03.853', 12500, '{"id":2908,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedInt","id":12500,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12500","versionNumber":1,"latestVersionId":12500,"classifiedName":"xs:unsignedInt (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:unsignedLong","id":12499,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12499","versionNumber":1,"latestVersionId":12499,"classifiedName":"xs:unsignedLong (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30259, NULL, 12499, TIMESTAMP '2018-01-03 17:25:03.853', 12499, '{"id":2908,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedInt","id":12500,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12500","versionNumber":1,"latestVersionId":12500,"classifiedName":"xs:unsignedInt (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:unsignedLong","id":12499,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12499","versionNumber":1,"latestVersionId":12499,"classifiedName":"xs:unsignedLong (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30260, NULL, 12500, TIMESTAMP '2018-01-03 17:25:03.877', 12500, '{"id":2909,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedShort","id":12501,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12501","versionNumber":1,"latestVersionId":12501,"classifiedName":"xs:unsignedShort (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:unsignedInt","id":12500,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12500","versionNumber":1,"latestVersionId":12500,"classifiedName":"xs:unsignedInt (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE); 
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30261, NULL, 12501, TIMESTAMP '2018-01-03 17:25:03.877', 12501, '{"id":2909,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedShort","id":12501,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12501","versionNumber":1,"latestVersionId":12501,"classifiedName":"xs:unsignedShort (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:unsignedInt","id":12500,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12500","versionNumber":1,"latestVersionId":12500,"classifiedName":"xs:unsignedInt (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30262, NULL, 12502, TIMESTAMP '2018-01-03 17:25:03.899', 12502, '{"id":2910,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedByte","id":12502,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12502","versionNumber":1,"latestVersionId":12502,"classifiedName":"xs:unsignedByte (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:unsignedShort","id":12501,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12501","versionNumber":1,"latestVersionId":12501,"classifiedName":"xs:unsignedShort (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'is based on', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30263, NULL, 12501, TIMESTAMP '2018-01-03 17:25:03.899', 12501, '{"id":2910,"source":{"semanticVersion":"0.0.1","name":"xs:unsignedByte","id":12502,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12502","versionNumber":1,"latestVersionId":12502,"classifiedName":"xs:unsignedByte (XMLSchema 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"xs:unsignedShort","id":12501,"elementType":"org.modelcatalogue.core.DataType","link":"/dataType/12501","versionNumber":1,"latestVersionId":12501,"classifiedName":"xs:unsignedShort (XMLSchema 0.0.1)"},"type":{"id":2889,"name":"base","link":"/relationshipType/2889"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'is base for', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30264, NULL, 12514, TIMESTAMP '2018-01-03 17:25:03.957', 12514, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30265, NULL, 12515, TIMESTAMP '2018-01-03 17:25:03.963', 12515, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30266, NULL, 12516, TIMESTAMP '2018-01-03 17:25:03.97', 12516, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30267, NULL, 12517, TIMESTAMP '2018-01-03 17:25:03.975', 12517, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30268, NULL, 12518, TIMESTAMP '2018-01-03 17:25:03.979', 12518, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30269, NULL, 12519, TIMESTAMP '2018-01-03 17:25:03.984', 12519, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30270, NULL, 12520, TIMESTAMP '2018-01-03 17:25:03.99', 12520, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30271, NULL, 12521, TIMESTAMP '2018-01-03 17:25:03.996', 12521, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30272, NULL, 12522, TIMESTAMP '2018-01-03 17:25:04.001', 12522, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30273, NULL, 12514, TIMESTAMP '2018-01-03 17:25:04.004', 12514, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, NULL, 'status', FALSE, 'ELEMENT_FINALIZED', FALSE),
(30274, NULL, 12514, TIMESTAMP '2018-01-03 17:25:04.006', 12514, '{"value":"PENDING"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30275, NULL, 12514, TIMESTAMP '2018-01-03 17:25:04.009', 12514, '{"value":"FINALIZED"}', '{"value":"PENDING"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE);         
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30276, NULL, 12515, TIMESTAMP '2018-01-03 17:25:04.01', 12515, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30277, NULL, 12516, TIMESTAMP '2018-01-03 17:25:04.012', 12516, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30278, NULL, 12517, TIMESTAMP '2018-01-03 17:25:04.014', 12517, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30279, NULL, 12518, TIMESTAMP '2018-01-03 17:25:04.015', 12518, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30280, NULL, 12519, TIMESTAMP '2018-01-03 17:25:04.017', 12519, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30281, NULL, 12520, TIMESTAMP '2018-01-03 17:25:04.019', 12520, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30282, NULL, 12521, TIMESTAMP '2018-01-03 17:25:04.021', 12521, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30283, NULL, 12522, TIMESTAMP '2018-01-03 17:25:04.023', 12522, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30273, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30284, NULL, 12523, TIMESTAMP '2018-01-03 17:25:04.071', 12523, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30285, NULL, 12523, TIMESTAMP '2018-01-03 17:25:04.073', 12523, '{"value":"true"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/genomics/#cancer-types-export', FALSE, 'METADATA_CREATED', FALSE),
(30286, NULL, 12523, TIMESTAMP '2018-01-03 17:25:04.074', 12523, '{"value":"true"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/genomics/#all-cancer-reports', FALSE, 'METADATA_CREATED', FALSE),
(30287, NULL, 12524, TIMESTAMP '2018-01-03 17:25:04.078', 12524, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30288, NULL, 12523, TIMESTAMP '2018-01-03 17:25:04.084', 12523, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, NULL, 'status', FALSE, 'ELEMENT_FINALIZED', FALSE),
(30289, NULL, 12523, TIMESTAMP '2018-01-03 17:25:04.734', 12523, '{"value":"PENDING"}', '{"value":"DRAFT"}', FALSE, 30288, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30290, NULL, 12523, TIMESTAMP '2018-01-03 17:25:04.741', 12523, '{"value":"FINALIZED"}', '{"value":"PENDING"}', FALSE, 30288, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30291, NULL, 12524, TIMESTAMP '2018-01-03 17:25:04.743', 12524, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30288, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30292, NULL, 12525, TIMESTAMP '2018-01-03 17:25:04.834', 12525, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30293, NULL, 12526, TIMESTAMP '2018-01-03 17:25:04.843', 12526, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30294, NULL, 12527, TIMESTAMP '2018-01-03 17:25:04.852', 12527, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30295, NULL, 12528, TIMESTAMP '2018-01-03 17:25:04.861', 12528, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30296, NULL, 12529, TIMESTAMP '2018-01-03 17:25:04.871', 12529, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30297, NULL, 12530, TIMESTAMP '2018-01-03 17:25:04.88', 12530, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30298, NULL, 12531, TIMESTAMP '2018-01-03 17:25:04.89', 12531, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30299, NULL, 12532, TIMESTAMP '2018-01-03 17:25:04.899', 12532, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30300, NULL, 12533, TIMESTAMP '2018-01-03 17:25:04.905', 12533, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30301, NULL, 12534, TIMESTAMP '2018-01-03 17:25:04.91', 12534, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE);              
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30302, NULL, 12535, TIMESTAMP '2018-01-03 17:25:04.915', 12535, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30303, NULL, 12536, TIMESTAMP '2018-01-03 17:25:04.92', 12536, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30304, NULL, 12537, TIMESTAMP '2018-01-03 17:25:04.925', 12537, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30305, NULL, 12538, TIMESTAMP '2018-01-03 17:25:04.93', 12538, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30306, NULL, 12539, TIMESTAMP '2018-01-03 17:25:04.936', 12539, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30307, NULL, 12540, TIMESTAMP '2018-01-03 17:25:04.941', 12540, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30308, NULL, 12541, TIMESTAMP '2018-01-03 17:25:04.946', 12541, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30309, NULL, 12542, TIMESTAMP '2018-01-03 17:25:04.951', 12542, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30310, NULL, 12543, TIMESTAMP '2018-01-03 17:25:04.957', 12543, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30311, NULL, 12544, TIMESTAMP '2018-01-03 17:25:04.962', 12544, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30312, NULL, 12545, TIMESTAMP '2018-01-03 17:25:04.967', 12545, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30313, NULL, 12546, TIMESTAMP '2018-01-03 17:25:04.973', 12546, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30314, NULL, 12547, TIMESTAMP '2018-01-03 17:25:04.978', 12547, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30315, NULL, 12525, TIMESTAMP '2018-01-03 17:25:04.981', 12525, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, NULL, 'status', FALSE, 'ELEMENT_FINALIZED', FALSE),
(30316, NULL, 12525, TIMESTAMP '2018-01-03 17:25:04.985', 12525, '{"value":"PENDING"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30317, NULL, 12525, TIMESTAMP '2018-01-03 17:25:04.991', 12525, '{"value":"FINALIZED"}', '{"value":"PENDING"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30318, NULL, 12526, TIMESTAMP '2018-01-03 17:25:04.993', 12526, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30319, NULL, 12527, TIMESTAMP '2018-01-03 17:25:04.995', 12527, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30320, NULL, 12528, TIMESTAMP '2018-01-03 17:25:04.997', 12528, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30321, NULL, 12529, TIMESTAMP '2018-01-03 17:25:04.999', 12529, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30322, NULL, 12530, TIMESTAMP '2018-01-03 17:25:05.001', 12530, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30323, NULL, 12531, TIMESTAMP '2018-01-03 17:25:05.004', 12531, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30324, NULL, 12532, TIMESTAMP '2018-01-03 17:25:05.006', 12532, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30325, NULL, 12533, TIMESTAMP '2018-01-03 17:25:05.008', 12533, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30326, NULL, 12534, TIMESTAMP '2018-01-03 17:25:05.01', 12534, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30327, NULL, 12535, TIMESTAMP '2018-01-03 17:25:05.012', 12535, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE);   
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30328, NULL, 12536, TIMESTAMP '2018-01-03 17:25:05.014', 12536, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30329, NULL, 12537, TIMESTAMP '2018-01-03 17:25:05.016', 12537, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30330, NULL, 12538, TIMESTAMP '2018-01-03 17:25:05.018', 12538, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30331, NULL, 12539, TIMESTAMP '2018-01-03 17:25:05.023', 12539, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30332, NULL, 12540, TIMESTAMP '2018-01-03 17:25:05.023', 12540, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30333, NULL, 12541, TIMESTAMP '2018-01-03 17:25:05.025', 12541, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30334, NULL, 12542, TIMESTAMP '2018-01-03 17:25:05.027', 12542, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30335, NULL, 12543, TIMESTAMP '2018-01-03 17:25:05.029', 12543, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30336, NULL, 12544, TIMESTAMP '2018-01-03 17:25:05.032', 12544, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30337, NULL, 12545, TIMESTAMP '2018-01-03 17:25:05.034', 12545, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30338, NULL, 12546, TIMESTAMP '2018-01-03 17:25:05.035', 12546, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30339, NULL, 12547, TIMESTAMP '2018-01-03 17:25:05.037', 12547, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30315, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30340, NULL, 12548, TIMESTAMP '2018-01-03 17:25:05.074', 12548, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30341, NULL, 12548, TIMESTAMP '2018-01-03 17:25:05.076', 12548, '{"value":"true"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/genomics/#all-rd-reports', FALSE, 'METADATA_CREATED', FALSE),
(30342, NULL, 12548, TIMESTAMP '2018-01-03 17:25:05.076', 12548, '{"value":"true"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/genomics/#rare-disease-reports', FALSE, 'METADATA_CREATED', FALSE),
(30343, NULL, 12549, TIMESTAMP '2018-01-03 17:25:05.08', 12549, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30344, NULL, 12548, TIMESTAMP '2018-01-03 17:25:05.084', 12548, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, NULL, 'status', FALSE, 'ELEMENT_FINALIZED', FALSE),
(30345, NULL, 12548, TIMESTAMP '2018-01-03 17:25:05.088', 12548, '{"value":"PENDING"}', '{"value":"DRAFT"}', FALSE, 30344, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30346, NULL, 12548, TIMESTAMP '2018-01-03 17:25:05.094', 12548, '{"value":"FINALIZED"}', '{"value":"PENDING"}', FALSE, 30344, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30347, NULL, 12549, TIMESTAMP '2018-01-03 17:25:05.096', 12549, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30344, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30348, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.206', 12550, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30349, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.211', 12550, '{"value":"Matous Kucera"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#authors', FALSE, 'METADATA_CREATED', FALSE),
(30350, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.213', 12550, '{"value":"Adam Milward"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#reviewers', FALSE, 'METADATA_CREATED', FALSE),
(30351, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.214', 12550, '{"value":"Vladimir Orany"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#owner', FALSE, 'METADATA_CREATED', FALSE);           
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30352, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.215', 12550, '{"value":"2016-03-30T11:46:30Z"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#reviewed', FALSE, 'METADATA_CREATED', FALSE),
(30353, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.216', 12550, '{"value":"2016-03-30T11:46:30Z"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#approved', FALSE, 'METADATA_CREATED', FALSE),
(30354, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.217', 12550, '{"value":"Global Namespace"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#namespace', FALSE, 'METADATA_CREATED', FALSE),
(30355, NULL, 12550, TIMESTAMP '2018-01-03 17:25:08.218', 12550, '{"value":"Metadata Consulting"}', NULL, FALSE, NULL, 'http://www.modelcatalogue.org/metadata/#organization', FALSE, 'METADATA_CREATED', FALSE),
(30356, NULL, 12551, TIMESTAMP '2018-01-03 17:25:08.224', 12551, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30357, NULL, 12552, TIMESTAMP '2018-01-03 17:25:08.232', 12552, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30358, NULL, 12553, TIMESTAMP '2018-01-03 17:25:08.238', 12553, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30359, NULL, 12554, TIMESTAMP '2018-01-03 17:25:08.243', 12554, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30360, NULL, 12555, TIMESTAMP '2018-01-03 17:25:08.249', 12555, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30361, NULL, 12556, TIMESTAMP '2018-01-03 17:25:08.255', 12556, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30362, NULL, 12557, TIMESTAMP '2018-01-03 17:25:08.263', 12557, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30363, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.267', 12558, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30364, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.271', 12558, '{"value":"TEST_1"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30365, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.274', 12558, '{"value":"NHS NUMBER"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30366, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.276', 12558, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30367, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.279', 12558, '{"value":"XXXXXXX0010"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30368, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.281', 12558, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30369, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.283', 12558, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30370, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.285', 12558, '{"value":"3"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30371, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.287', 12558, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30372, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.288', 12558, NULL, NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30373, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.29', 12558, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE);       
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30374, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.292', 12558, '{"value":"GC 123"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30375, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.294', 12558, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30376, NULL, 12558, TIMESTAMP '2018-01-03 17:25:08.296', 12558, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30377, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.3', 12559, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30378, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.303', 12559, '{"value":"TEST_2"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30379, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.305', 12559, '{"value":"LOCAL PATIENT IDENTIFIER"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30380, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.306', 12559, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30381, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.308', 12559, '{"value":"XXXXXXX0020"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30382, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.31', 12559, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30383, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.312', 12559, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30384, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.313', 12559, '{"value":"3"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30385, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.315', 12559, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30386, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.317', 12559, '{"value":"hard"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30387, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.319', 12559, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30388, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.321', 12559, '{"value":"GC 124"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30389, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.323', 12559, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30390, NULL, 12559, TIMESTAMP '2018-01-03 17:25:08.324', 12559, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30391, NULL, 12560, TIMESTAMP '2018-01-03 17:25:08.33', 12560, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30392, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.334', 12561, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30393, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.336', 12561, '{"value":"TEST_3"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30394, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.338', 12561, '{"value":"NHS NUMBER STATUS INDICATOR CODE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30395, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.34', 12561, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE);
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30396, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.342', 12561, '{"value":"XXXXXXX1350"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30397, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.344', 12561, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30398, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.346', 12561, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30399, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.348', 12561, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30400, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.35', 12561, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30401, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.352', 12561, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30402, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.354', 12561, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30403, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.356', 12561, '{"value":"GC 125"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30404, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.358', 12561, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30405, NULL, 12561, TIMESTAMP '2018-01-03 17:25:08.36', 12561, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30406, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.368', 12562, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30407, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.369', 12562, '{"value":"TEST_4"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30408, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.371', 12562, '{"value":"PERSON BIRTH DATE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30409, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.373', 12562, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30410, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.375', 12562, '{"value":"XXXXXXX0100"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30411, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.378', 12562, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30412, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.38', 12562, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30413, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.382', 12562, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30414, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.384', 12562, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30415, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.386', 12562, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30416, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.388', 12562, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30417, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.389', 12562, '{"value":"GC 126"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30418, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.391', 12562, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30419, NULL, 12562, TIMESTAMP '2018-01-03 17:25:08.393', 12562, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30420, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.397', 12563, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30421, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.4', 12563, '{"value":"TEST_5"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30422, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.402', 12563, '{"value":"ORGANISATION CODE (CODE OF PROVIDER)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE);               
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30423, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.404', 12563, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30424, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.405', 12563, '{"value":"XXXXXXX0030"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30425, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.407', 12563, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30426, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.409', 12563, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30427, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.411', 12563, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30428, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.412', 12563, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30429, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.414', 12563, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30430, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.416', 12563, '{"value":"When referral details entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30431, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.418', 12563, '{"value":"GC 127"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30432, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.42', 12563, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30433, NULL, 12563, TIMESTAMP '2018-01-03 17:25:08.422', 12563, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30434, NULL, 12564, TIMESTAMP '2018-01-03 17:25:08.426', 12564, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30435, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.431', 12565, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30436, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.433', 12565, '{"value":"TEST_6"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30437, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.435', 12565, '{"value":"PRIMARY DIAGNOSIS (ICD)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30438, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.437', 12565, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30439, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.439', 12565, '{"value":"XXXXXXX0370"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30440, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.44', 12565, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30441, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.442', 12565, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30442, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.444', 12565, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30443, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.446', 12565, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30444, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.448', 12565, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30445, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.449', 12565, '{"value":"MDT Meeting"}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30446, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.451', 12565, '{"value":"GC 128"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30447, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.453', 12565, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30448, NULL, 12565, TIMESTAMP '2018-01-03 17:25:08.455', 12565, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30449, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.459', 12566, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE);    
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30450, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.462', 12566, '{"value":"TEST_7"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30451, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.464', 12566, '{"value":"DATE OF DIAGNOSIS (TESTCER CLINICALLY AGREED)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30452, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.466', 12566, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30453, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.467', 12566, '{"value":"XXXXXXX2030"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30454, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.469', 12566, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30455, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.471', 12566, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30456, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.473', 12566, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30457, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.475', 12566, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30458, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.477', 12566, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30459, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.479', 12566, '{"value":"MDT Meeting - From MDT against which definitive diagnosis recorded, or final pre-treatment diagnosis recorded."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30460, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.48', 12566, '{"value":"GC 129"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30461, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.483', 12566, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30462, NULL, 12566, TIMESTAMP '2018-01-03 17:25:08.485', 12566, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30463, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.491', 12567, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30464, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.495', 12567, '{"value":"TEST_8"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30465, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.498', 12567, '{"value":"DATE OF RECURRENCE (TESTCER CLINICALLY AGREED)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30466, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.499', 12567, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30467, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.501', 12567, '{"value":"XXXXXXX0440"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE);      
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30468, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.503', 12567, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30469, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.505', 12567, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30470, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.507', 12567, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30471, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.508', 12567, '{"value":"3"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30472, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.51', 12567, '{"value":"hard"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30473, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.512', 12567, '{"value":"MDT Meeting - From MDT against which definitive diagnosis recorded, or final pre-treatment diagnosis recorded. Recurrence radio button needs to be selected on MDT meeting / Diagnosis / Diagnosis tab or MDT D&S tab"}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30474, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.514', 12567, '{"value":"GC 130"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30475, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.516', 12567, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30476, NULL, 12567, TIMESTAMP '2018-01-03 17:25:08.518', 12567, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30477, NULL, 12568, TIMESTAMP '2018-01-03 17:25:08.522', 12568, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30478, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.527', 12569, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30479, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.53', 12569, '{"value":"TEST_9"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30480, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.531', 12569, '{"value":"PERSON FAMILY NAME"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30481, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.533', 12569, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30482, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.535', 12569, '{"value":"XXXXXXX0050"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30483, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.537', 12569, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30484, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.539', 12569, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30485, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.54', 12569, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30486, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.542', 12569, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30487, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.544', 12569, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30488, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.546', 12569, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30489, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.548', 12569, '{"value":"GC 131"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30490, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.55', 12569, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30491, NULL, 12569, TIMESTAMP '2018-01-03 17:25:08.551', 12569, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30492, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.556', 12570, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30493, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.559', 12570, '{"value":"TEST_10"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE);            
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30494, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.56', 12570, '{"value":"PERSON GIVEN NAME"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30495, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.562', 12570, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30496, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.564', 12570, '{"value":"XXXXXXX0060"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30497, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.566', 12570, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30498, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.568', 12570, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30499, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.569', 12570, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30500, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.571', 12570, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30501, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.573', 12570, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30502, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.575', 12570, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30503, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.577', 12570, '{"value":"GC 132"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30504, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.579', 12570, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30505, NULL, 12570, TIMESTAMP '2018-01-03 17:25:08.58', 12570, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30506, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.586', 12571, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30507, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.59', 12571, '{"value":"TEST_11"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30508, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.592', 12571, '{"value":"PATIENT USUAL ADDRESS (AT DIAGNOSIS)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30509, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.595', 12571, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30510, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.597', 12571, '{"value":"XXXXXXX0070"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30511, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.599', 12571, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30512, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.601', 12571, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30513, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.603', 12571, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30514, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.605', 12571, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30515, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.607', 12571, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30516, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.608', 12571, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE);   
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30517, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.61', 12571, '{"value":"GC 133"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30518, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.612', 12571, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30519, NULL, 12571, TIMESTAMP '2018-01-03 17:25:08.614', 12571, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30520, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.619', 12572, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30521, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.621', 12572, '{"value":"TEST_12"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30522, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.623', 12572, '{"value":"POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30523, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.625', 12572, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30524, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.627', 12572, '{"value":"XXXXXXX0080"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30525, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.629', 12572, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30526, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.63', 12572, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30527, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.633', 12572, '{"value":"3"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30528, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.635', 12572, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30529, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.636', 12572, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30530, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.638', 12572, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30531, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.64', 12572, '{"value":"GC 134"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30532, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.642', 12572, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30533, NULL, 12572, TIMESTAMP '2018-01-03 17:25:08.645', 12572, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30534, NULL, 12573, TIMESTAMP '2018-01-03 17:25:08.652', 12573, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30535, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.657', 12574, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30536, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.661', 12574, '{"value":"TEST_13"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30537, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.664', 12574, '{"value":"PERSON GENDER CODE CURRENT"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30538, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.666', 12574, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE);             
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30539, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.669', 12574, '{"value":"XXXXXXX0090"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30540, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.671', 12574, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30541, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.674', 12574, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30542, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.677', 12574, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30543, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.679', 12574, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30544, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.682', 12574, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30545, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.685', 12574, '{"value":"When Patient demographics entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30546, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.687', 12574, '{"value":"GC 135"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30547, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.689', 12574, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30548, NULL, 12574, TIMESTAMP '2018-01-03 17:25:08.691', 12574, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30549, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.696', 12575, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30550, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.699', 12575, '{"value":"TEST_14"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30551, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.7', 12575, '{"value":"GENERAL MEDICAL PRACTITIONER (SPECIFIED)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30552, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.702', 12575, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30553, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.704', 12575, '{"value":"XXXXXXX0110"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30554, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.706', 12575, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30555, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.708', 12575, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30556, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.709', 12575, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30557, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.711', 12575, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30558, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.713', 12575, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30559, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.715', 12575, '{"value":"When Patient demographics entered. Name shown. Code available in JCIS tables."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30560, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.717', 12575, '{"value":"GC 136"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE);            
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30561, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.719', 12575, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30562, NULL, 12575, TIMESTAMP '2018-01-03 17:25:08.721', 12575, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30563, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.726', 12576, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30564, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.729', 12576, '{"value":"TEST_15"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30565, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.732', 12576, '{"value":"GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30566, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.734', 12576, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30567, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.737', 12576, '{"value":"XXXXXXX0120"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30568, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.739', 12576, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30569, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.741', 12576, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30570, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.744', 12576, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30571, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.747', 12576, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30572, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.749', 12576, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30573, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.753', 12576, '{"value":"When Patient demographics entered. Address shown. Code available in JCIS tables."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30574, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.755', 12576, '{"value":"GC 137"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30575, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.756', 12576, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30576, NULL, 12576, TIMESTAMP '2018-01-03 17:25:08.759', 12576, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30577, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.766', 12577, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30578, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.77', 12577, '{"value":"TEST_16"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30579, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.771', 12577, '{"value":"PERSON FAMILY NAME (AT BIRTH)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30580, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.773', 12577, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE);              
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30581, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.775', 12577, '{"value":"XXXXXXX0140"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30582, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.777', 12577, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30583, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.779', 12577, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30584, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.781', 12577, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30585, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.783', 12577, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30586, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.786', 12577, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30587, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.788', 12577, '{"value":"When Patient demographics entered. The HISS field is MaidenName (ie not strictly Surname at birth)."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30588, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.79', 12577, '{"value":"GC 138"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30589, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.793', 12577, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30590, NULL, 12577, TIMESTAMP '2018-01-03 17:25:08.795', 12577, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30591, NULL, 12578, TIMESTAMP '2018-01-03 17:25:08.802', 12578, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30592, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.806', 12579, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30593, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.809', 12579, '{"value":"TEST_17"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30594, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.811', 12579, '{"value":"ETHNIC CATEGORY"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30595, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.813', 12579, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30596, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.815', 12579, '{"value":"XXXXXXX0150"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30597, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.817', 12579, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30598, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.819', 12579, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30599, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.821', 12579, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30600, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.823', 12579, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30601, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.825', 12579, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30602, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.827', 12579, '{"value":"Imported from HISS but not displayed."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE);       
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30603, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.83', 12579, '{"value":"GC 139"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30604, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.832', 12579, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30605, NULL, 12579, TIMESTAMP '2018-01-03 17:25:08.834', 12579, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30606, NULL, 12580, TIMESTAMP '2018-01-03 17:25:08.839', 12580, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30607, NULL, 12581, TIMESTAMP '2018-01-03 17:25:08.846', 12581, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30608, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.85', 12582, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30609, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.853', 12582, '{"value":"TEST_18"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30610, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.855', 12582, '{"value":"SOURCE OF REFERRAL FOR OUT-PATIENTS"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30611, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.857', 12582, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30612, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.859', 12582, '{"value":"XXXXXXX1600"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30613, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.861', 12582, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30614, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.863', 12582, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30615, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.865', 12582, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30616, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.867', 12582, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30617, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.869', 12582, '{"value":"1"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30618, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.872', 12582, '{"value":"When referral details entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30619, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.874', 12582, '{"value":"GC 140"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30620, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.876', 12582, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30621, NULL, 12582, TIMESTAMP '2018-01-03 17:25:08.878', 12582, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30622, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.885', 12583, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30623, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.889', 12583, '{"value":"TEST_19"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30624, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.891', 12583, '{"value":"REFERRAL TO TREATMENT PERIOD START DATE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30625, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.893', 12583, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE);  
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30626, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.896', 12583, '{"value":"XXXXXXX1580"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30627, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.898', 12583, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30628, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.9', 12583, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30629, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.902', 12583, '{"value":"2"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30630, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.904', 12583, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30631, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.906', 12583, '{"value":"2"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30632, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.907', 12583, '{"value":"When referral details entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30633, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.909', 12583, '{"value":"GC 141"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30634, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.911', 12583, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30635, NULL, 12583, TIMESTAMP '2018-01-03 17:25:08.913', 12583, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30636, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.918', 12584, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30637, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.921', 12584, '{"value":"TEST_20"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30638, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.923', 12584, '{"value":"DATE FIRST SEEN"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30639, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.925', 12584, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30640, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.926', 12584, '{"value":"XXXXXXX0230"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30641, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.928', 12584, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30642, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.93', 12584, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30643, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.932', 12584, '{"value":"2"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30644, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.934', 12584, '{"value":"1"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30645, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.936', 12584, '{"value":"2"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30646, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.937', 12584, '{"value":"When referral details entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30647, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.939', 12584, '{"value":"GC 142"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30648, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.941', 12584, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30649, NULL, 12584, TIMESTAMP '2018-01-03 17:25:08.943', 12584, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30650, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.948', 12585, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30651, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.951', 12585, '{"value":"TEST_21"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30652, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.953', 12585, '{"value":"CONSULTANT CODE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE);          
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30653, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.955', 12585, '{"value":"Imported activities do not have this field completed, but it TEST be derived from the Clinic code, which is imported. Check with VT."}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30654, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.957', 12585, '{"value":"XXXXXXX0210"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30655, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.958', 12585, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30656, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.96', 12585, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30657, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.962', 12585, '{"value":"4"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30658, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.964', 12585, '{"value":"4"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30659, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.966', 12585, '{"value":"2 - Backlog of consultant codes will need to be entered"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30660, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.969', 12585, '{"value":"Visit\neg Initial visit or Radiological visit used as first seen in wait time table."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30661, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.971', 12585, '{"value":"GC 143"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30662, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.973', 12585, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30663, NULL, 12585, TIMESTAMP '2018-01-03 17:25:08.976', 12585, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30664, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.982', 12586, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30665, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.985', 12586, '{"value":"TEST_22"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30666, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.987', 12586, '{"value":"CARE PROFESSIONAL MAIN SPECIALTY CODE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30667, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.989', 12586, '{"value":"NOT SHOWN ON JCIS\n(A field is available in Consultants table, but often empty)"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30668, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.99', 12586, '{"value":"XXXXXXX0220"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30669, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.992', 12586, '{"value":"4"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30670, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.994', 12586, '{"value":"4"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30671, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.996', 12586, '{"value":"4"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30672, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.998', 12586, '{"value":"4"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30673, NULL, 12586, TIMESTAMP '2018-01-03 17:25:08.999', 12586, '{"value":"3"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30674, NULL, 12586, TIMESTAMP '2018-01-03 17:25:09.001', 12586, '{"value":"NOT SHOWN ON JCIS\n(A field is available in Consultants table, but often empty)"}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30675, NULL, 12586, TIMESTAMP '2018-01-03 17:25:09.003', 12586, '{"value":"GC 144"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30676, NULL, 12586, TIMESTAMP '2018-01-03 17:25:09.005', 12586, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE);  
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30677, NULL, 12586, TIMESTAMP '2018-01-03 17:25:09.007', 12586, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30678, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.012', 12587, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30679, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.015', 12587, '{"value":"TEST_23"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30680, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.017', 12587, '{"value":"SITE CODE (OF PROVIDER FIRST SEEN)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30681, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.018', 12587, '{"value":"Name shown. Code available in JCIS tables."}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30682, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.02', 12587, '{"value":"XXXXXXX1410"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30683, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.022', 12587, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30684, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.024', 12587, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30685, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.025', 12587, '{"value":"2"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30686, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.027', 12587, '{"value":"3"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30687, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.029', 12587, '{"value":"2"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30688, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.031', 12587, '{"value":"Visit\neg Initial visit or Radiological visit used as first seen in wait time table."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30689, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.033', 12587, '{"value":"GC 145"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30690, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.035', 12587, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30691, NULL, 12587, TIMESTAMP '2018-01-03 17:25:09.036', 12587, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30692, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.042', 12588, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30693, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.045', 12588, '{"value":"TEST_24"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30694, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.047', 12588, '{"value":"DATE FIRST SEEN (TESTCER SPECIALIST)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30695, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.049', 12588, '{"value":"How TEST this be pinned down?"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30696, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.05', 12588, '{"value":"XXXXXXX1360"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30697, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.052', 12588, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30698, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.054', 12588, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30699, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.056', 12588, '{"value":"2"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30700, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.058', 12588, '{"value":"3"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30701, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.06', 12588, '{"value":"2"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE);           
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30702, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.061', 12588, '{"value":"Visit\neg Initial visit or Radiological visit used as first seen in wait time table."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30703, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.631', 12588, '{"value":"GC 146"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30704, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.634', 12588, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30705, NULL, 12588, TIMESTAMP '2018-01-03 17:25:09.636', 12588, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30706, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.646', 12589, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30707, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.651', 12589, '{"value":"TEST_25"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30708, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.654', 12589, '{"value":"SITE CODE (OF PROVIDER FIRST TESTCER SPECIALIST)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30709, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.656', 12589, '{"value":"Name shown. Code available in JCIS tables."}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30710, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.659', 12589, '{"value":"XXXXXXX1400"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30711, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.66', 12589, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30712, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.662', 12589, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30713, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.664', 12589, '{"value":"2"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30714, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.666', 12589, '{"value":"3"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30715, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.668', 12589, '{"value":"2"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30716, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.67', 12589, '{"value":"Visit\neg Initial visit or Radiological visit used as first seen in wait time table."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30717, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.672', 12589, '{"value":"GC 147"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30718, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.674', 12589, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30719, NULL, 12589, TIMESTAMP '2018-01-03 17:25:09.675', 12589, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30720, NULL, 12590, TIMESTAMP '2018-01-03 17:25:09.685', 12590, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30721, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.689', 12591, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30722, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.693', 12591, '{"value":"TEST_26"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30723, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.696', 12591, '{"value":"TESTCER OR SYMPTOMATIC BREAST REFERRAL PATIENT STATUS"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30724, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.699', 12591, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30725, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.701', 12591, '{"value":"XXXXXXX0270"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30726, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.703', 12591, '{"value":"TBA"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE);           
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30727, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.705', 12591, '{"value":"TBA"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30728, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.707', 12591, '{"value":"TBA"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30729, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.709', 12591, '{"value":"TBA"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30730, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.711', 12591, '{"value":"3"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30731, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.713', 12591, '{"value":"When referral details entered."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30732, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.716', 12591, '{"value":"GC 148"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30733, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.718', 12591, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30734, NULL, 12591, TIMESTAMP '2018-01-03 17:25:09.72', 12591, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30735, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.726', 12592, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30736, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.729', 12592, '{"value":"TEST_27"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30737, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.734', 12592, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30738, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.736', 12592, '{"value":"TESTCER SYMPTOMS FIRST NOTED DATE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30739, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.737', 12592, '{"value":"XXXXXXX2000"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30740, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.739', 12592, '{"value":"TBA"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30741, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.742', 12592, '{"value":"TBA"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30742, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.745', 12592, '{"value":"TBA"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30743, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.747', 12592, '{"value":"TBA"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30744, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.75', 12592, '{"value":"4"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30745, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.752', 12592, '{"value":"Calculated from date of visit / duration of symptoms"}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30746, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.755', 12592, '{"value":"GC 149"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30747, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.758', 12592, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30748, NULL, 12592, TIMESTAMP '2018-01-03 17:25:09.761', 12592, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30749, NULL, 12593, TIMESTAMP '2018-01-03 17:25:09.768', 12593, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE); 
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30750, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.776', 12594, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30751, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.781', 12594, '{"value":"TEST_28"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30752, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.783', 12594, '{"value":"SITE CODE (OF IMAGING)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30753, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.785', 12594, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30754, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.787', 12594, '{"value":"XXXXXXX0310"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30755, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.789', 12594, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30756, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.791', 12594, '{"value":"1"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30757, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.793', 12594, '{"value":"1"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30758, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.795', 12594, '{"value":"2"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30759, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.796', 12594, '{"value":"5"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30760, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.798', 12594, '{"value":"Name shown. Code available in JCIS tables."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30761, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.8', 12594, '{"value":"GC 150"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30762, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.802', 12594, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30763, NULL, 12594, TIMESTAMP '2018-01-03 17:25:09.804', 12594, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30764, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.809', 12595, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30765, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.812', 12595, '{"value":"TEST_29"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30766, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.814', 12595, '{"value":"PROCEDURE DATE (TESTCER IMAGING)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30767, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.816', 12595, NULL, NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30768, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.818', 12595, '{"value":"XXXXXXX0320"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30769, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.819', 12595, '{"value":"1"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30770, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.821', 12595, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30771, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.823', 12595, '{"value":"2"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE);         
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30772, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.825', 12595, '{"value":"2"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30773, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.827', 12595, '{"value":"6"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30774, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.829', 12595, NULL, NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30775, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.83', 12595, '{"value":"GC 151"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30776, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.832', 12595, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30777, NULL, 12595, TIMESTAMP '2018-01-03 17:25:09.834', 12595, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30778, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.839', 12596, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30779, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.842', 12596, '{"value":"TEST_30"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30780, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.844', 12596, '{"value":"IMAGING CODE (NICIP)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30781, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.846', 12596, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30782, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.848', 12596, '{"value":"XXXXXXX1610"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30783, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.85', 12596, '{"value":"TBA"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30784, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.851', 12596, '{"value":"TBA"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30785, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.853', 12596, '{"value":"TBA"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30786, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.855', 12596, '{"value":"TBA"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30787, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.857', 12596, '{"value":"7"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30788, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.859', 12596, '{"value":"See also alternative  mandatory item for non-imported radiology activities\nIe  (TESTCER IMAGING MODALITY and IMAGING ANATOMICAL SITE and ANATOMICAL SIDE (IMAGING))"}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30789, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.861', 12596, '{"value":"GC 152"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30790, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.863', 12596, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30791, NULL, 12596, TIMESTAMP '2018-01-03 17:25:09.865', 12596, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30792, NULL, 12597, TIMESTAMP '2018-01-03 17:25:09.876', 12597, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30793, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.88', 12598, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE); 
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30794, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.884', 12598, '{"value":"TEST_31"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30795, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.886', 12598, '{"value":"TESTCER IMAGING MODALITY"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30796, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.888', 12598, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30797, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.89', 12598, '{"value":"XXXXXXX0330"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30798, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.892', 12598, '{"value":"2"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30799, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.894', 12598, '{"value":"2"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30800, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.896', 12598, '{"value":"3"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30801, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.898', 12598, '{"value":"3"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30802, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.9', 12598, '{"value":"8"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30803, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.902', 12598, '{"value":"Radiology - see title of activity"}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30804, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.904', 12598, '{"value":"GC 153"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30805, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.906', 12598, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30806, NULL, 12598, TIMESTAMP '2018-01-03 17:25:09.909', 12598, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30807, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.914', 12599, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30808, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.917', 12599, '{"value":"TEST_32"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30809, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.919', 12599, '{"value":"IMAGING ANATOMICAL SITE"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30810, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.921', 12599, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30811, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.923', 12599, '{"value":"XXXXXXX0340"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE);               
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30812, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.924', 12599, '{"value":"4"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30813, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.926', 12599, '{"value":"4"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30814, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.928', 12599, '{"value":"4"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30815, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.93', 12599, '{"value":"4"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30816, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.932', 12599, '{"value":"9"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30817, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.934', 12599, '{"value":"Typically not recorded.  Unless the ATM makes it clear what the site is, the site of imaging is not recorded. Exceptions are mammogram (site recorded as breast);BRT ultrasound - user has to record site.\n[Usage for UGI CT sTEST (recorded 4 times on Live patients) - is misleading usage of backend table as front end desXXXXXXXiption is ''Primary tumour location'' on CT sTEST.Findings tab].\n\n(Findings site is recordable)."}', NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30818, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.936', 12599, '{"value":"GC 154"}', NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30819, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.938', 12599, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30820, NULL, 12599, TIMESTAMP '2018-01-03 17:25:09.939', 12599, '{"value":"from source"}', NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30821, NULL, 12600, TIMESTAMP '2018-01-03 17:25:09.948', 12600, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30822, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.951', 12601, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30823, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.954', 12601, '{"value":"TEST_33"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30824, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.956', 12601, '{"value":"ANATOMICAL SIDE (IMAGING)"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30825, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.958', 12601, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30826, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.96', 12601, '{"value":"XXXXXXX3000"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30827, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.962', 12601, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30828, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.964', 12601, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30829, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.966', 12601, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30830, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.968', 12601, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30831, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.97', 12601, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE);           
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30832, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.972', 12601, NULL, NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30833, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.974', 12601, NULL, NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30834, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.976', 12601, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30835, NULL, 12601, TIMESTAMP '2018-01-03 17:25:09.978', 12601, NULL, NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30836, NULL, 12602, TIMESTAMP '2018-01-03 17:25:09.989', 12602, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30837, NULL, 12603, TIMESTAMP '2018-01-03 17:25:09.993', 12603, NULL, NULL, FALSE, NULL, NULL, FALSE, 'NEW_ELEMENT_CREATED', FALSE),
(30838, NULL, 12603, TIMESTAMP '2018-01-03 17:25:09.997', 12603, '{"value":"TEST_GENDER"}', NULL, FALSE, NULL, 'NHIC_Identifier:', FALSE, 'METADATA_CREATED', FALSE),
(30839, NULL, 12603, TIMESTAMP '2018-01-03 17:25:09.999', 12603, '{"value":"GENDER2"}', NULL, FALSE, NULL, 'Link_to_existing definition:', FALSE, 'METADATA_CREATED', FALSE),
(30840, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.001', 12603, '{"value":"orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu"}', NULL, FALSE, NULL, 'Notes_from_GD_JCIS', FALSE, 'METADATA_CREATED', FALSE),
(30841, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.003', 12603, '{"value":"asd"}', NULL, FALSE, NULL, '[Optional]_Local_Identifier', FALSE, 'METADATA_CREATED', FALSE),
(30842, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.005', 12603, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'A', FALSE, 'METADATA_CREATED', FALSE),
(30843, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.007', 12603, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'B', FALSE, 'METADATA_CREATED', FALSE),
(30844, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.009', 12603, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'C', FALSE, 'METADATA_CREATED', FALSE),
(30845, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.011', 12603, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'D', FALSE, 'METADATA_CREATED', FALSE),
(30846, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.013', 12603, '{"value":"MAY BE REMOVED"}', NULL, FALSE, NULL, 'E', FALSE, 'METADATA_CREATED', FALSE),
(30847, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.015', 12603, NULL, NULL, FALSE, NULL, 'F', FALSE, 'METADATA_CREATED', FALSE),
(30848, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.017', 12603, NULL, NULL, FALSE, NULL, 'G', FALSE, 'METADATA_CREATED', FALSE),
(30849, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.019', 12603, '{"value":"NEO4J"}', NULL, FALSE, NULL, 'H', FALSE, 'METADATA_CREATED', FALSE),
(30850, NULL, 12603, TIMESTAMP '2018-01-03 17:25:10.021', 12603, NULL, NULL, FALSE, NULL, 'E2', FALSE, 'METADATA_CREATED', FALSE),
(30851, NULL, 12551, TIMESTAMP '2018-01-03 17:25:10.024', 12551, '{"id":2911,"source":{"semanticVersion":"0.0.1","name":"NHIC Datasets","id":12551,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12551","versionNumber":1,"latestVersionId":12551,"classifiedName":"NHIC Datasets (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"Ovarian Cancer","id":12552,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12552","versionNumber":1,"latestVersionId":12552,"classifiedName":"Ovarian Cancer (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE);               
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30852, NULL, 12552, TIMESTAMP '2018-01-03 17:25:10.024', 12552, '{"id":2911,"source":{"semanticVersion":"0.0.1","name":"NHIC Datasets","id":12551,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12551","versionNumber":1,"latestVersionId":12551,"classifiedName":"NHIC Datasets (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"Ovarian Cancer","id":12552,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12552","versionNumber":1,"latestVersionId":12552,"classifiedName":"Ovarian Cancer (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30853, NULL, 12552, TIMESTAMP '2018-01-03 17:25:10.038', 12552, '{"id":2912,"source":{"semanticVersion":"0.0.1","name":"Ovarian Cancer","id":12552,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12552","versionNumber":1,"latestVersionId":12552,"classifiedName":"Ovarian Cancer (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"CUH","id":12553,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12553","versionNumber":1,"latestVersionId":12553,"classifiedName":"CUH (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30854, NULL, 12553, TIMESTAMP '2018-01-03 17:25:10.039', 12553, '{"id":2912,"source":{"semanticVersion":"0.0.1","name":"Ovarian Cancer","id":12552,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12552","versionNumber":1,"latestVersionId":12552,"classifiedName":"Ovarian Cancer (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"CUH","id":12553,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12553","versionNumber":1,"latestVersionId":12553,"classifiedName":"CUH (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30855, NULL, 12553, TIMESTAMP '2018-01-03 17:25:10.054', 12553, '{"id":2913,"source":{"semanticVersion":"0.0.1","name":"CUH","id":12553,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12553","versionNumber":1,"latestVersionId":12553,"classifiedName":"CUH (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"Round 1","id":12554,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12554","versionNumber":1,"latestVersionId":12554,"classifiedName":"Round 1 (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30856, NULL, 12554, TIMESTAMP '2018-01-03 17:25:10.054', 12554, '{"id":2913,"source":{"semanticVersion":"0.0.1","name":"CUH","id":12553,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12553","versionNumber":1,"latestVersionId":12553,"classifiedName":"CUH (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"Round 1","id":12554,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12554","versionNumber":1,"latestVersionId":12554,"classifiedName":"Round 1 (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30857, NULL, 12554, TIMESTAMP '2018-01-03 17:25:10.073', 12554, '{"id":2914,"source":{"semanticVersion":"0.0.1","name":"Round 1","id":12554,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12554","versionNumber":1,"latestVersionId":12554,"classifiedName":"Round 1 (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE);            
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30858, NULL, 12555, TIMESTAMP '2018-01-03 17:25:10.073', 12555, '{"id":2914,"source":{"semanticVersion":"0.0.1","name":"Round 1","id":12554,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12554","versionNumber":1,"latestVersionId":12554,"classifiedName":"Round 1 (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30859, NULL, 12556, TIMESTAMP '2018-01-03 17:25:10.088', 12556, '{"id":2915,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30860, NULL, 12555, TIMESTAMP '2018-01-03 17:25:10.089', 12555, '{"id":2915,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30861, NULL, 12555, TIMESTAMP '2018-01-03 17:25:10.105', 12555, '{"id":2916,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30862, NULL, 12564, TIMESTAMP '2018-01-03 17:25:10.105', 12564, '{"id":2916,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30863, NULL, 12555, TIMESTAMP '2018-01-03 17:25:10.117', 12555, '{"id":2917,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE);      
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30864, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.117', 12568, '{"id":2917,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30865, NULL, 12555, TIMESTAMP '2018-01-03 17:25:10.129', 12555, '{"id":2918,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30866, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.129', 12580, '{"id":2918,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30867, NULL, 12555, TIMESTAMP '2018-01-03 17:25:10.143', 12555, '{"id":2919,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'parent of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30868, NULL, 12593, TIMESTAMP '2018-01-03 17:25:10.143', 12593, '{"id":2919,"source":{"semanticVersion":"0.0.1","name":"MAIN","id":12555,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12555","versionNumber":1,"latestVersionId":12555,"classifiedName":"MAIN (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"type":{"id":2891,"name":"hierarchy","link":"/relationshipType/2891"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'child of', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30869, NULL, 12556, TIMESTAMP '2018-01-03 17:25:10.186', 12556, '{"id":2920,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"NHS NUMBER*","id":12558,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12558","versionNumber":1,"latestVersionId":12558,"classifiedName":"NHS NUMBER* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE);         
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30870, NULL, 12558, TIMESTAMP '2018-01-03 17:25:10.186', 12558, '{"id":2920,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"NHS NUMBER*","id":12558,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12558","versionNumber":1,"latestVersionId":12558,"classifiedName":"NHS NUMBER* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30871, NULL, 12559, TIMESTAMP '2018-01-03 17:25:10.201', 12559, '{"id":2921,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"LOCAL PATIENT IDENTIFIER*","id":12559,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12559","versionNumber":1,"latestVersionId":12559,"classifiedName":"LOCAL PATIENT IDENTIFIER* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30872, NULL, 12556, TIMESTAMP '2018-01-03 17:25:10.202', 12556, '{"id":2921,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"LOCAL PATIENT IDENTIFIER*","id":12559,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12559","versionNumber":1,"latestVersionId":12559,"classifiedName":"LOCAL PATIENT IDENTIFIER* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30873, NULL, 12556, TIMESTAMP '2018-01-03 17:25:10.218', 12556, '{"id":2922,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"nhsNumberStatusIndicatorCode","id":12561,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12561","versionNumber":1,"latestVersionId":12561,"classifiedName":"nhsNumberStatusIndicatorCode (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30874, NULL, 12561, TIMESTAMP '2018-01-03 17:25:10.218', 12561, '{"id":2922,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"nhsNumberStatusIndicatorCode","id":12561,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12561","versionNumber":1,"latestVersionId":12561,"classifiedName":"nhsNumberStatusIndicatorCode (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);           
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30875, NULL, 12562, TIMESTAMP '2018-01-03 17:25:10.235', 12562, '{"id":2923,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON BIRTH DATE","id":12562,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12562","versionNumber":1,"latestVersionId":12562,"classifiedName":"PERSON BIRTH DATE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30876, NULL, 12556, TIMESTAMP '2018-01-03 17:25:10.236', 12556, '{"id":2923,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON BIRTH DATE","id":12562,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12562","versionNumber":1,"latestVersionId":12562,"classifiedName":"PERSON BIRTH DATE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30877, NULL, 12556, TIMESTAMP '2018-01-03 17:25:10.253', 12556, '{"id":2924,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ORGANISATION CODE (CODE OF PROVIDER)","id":12563,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12563","versionNumber":1,"latestVersionId":12563,"classifiedName":"ORGANISATION CODE (CODE OF PROVIDER) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30878, NULL, 12563, TIMESTAMP '2018-01-03 17:25:10.253', 12563, '{"id":2924,"source":{"semanticVersion":"0.0.1","name":"PATIENT IDENTITY DETAILS","id":12556,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12556","versionNumber":1,"latestVersionId":12556,"classifiedName":"PATIENT IDENTITY DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ORGANISATION CODE (CODE OF PROVIDER)","id":12563,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12563","versionNumber":1,"latestVersionId":12563,"classifiedName":"ORGANISATION CODE (CODE OF PROVIDER) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30879, NULL, 12564, TIMESTAMP '2018-01-03 17:25:10.271', 12564, '{"id":2925,"source":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PRIMARY DIAGNOSIS (ICD)","id":12565,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12565","versionNumber":1,"latestVersionId":12565,"classifiedName":"PRIMARY DIAGNOSIS (ICD) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE);  
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30880, NULL, 12565, TIMESTAMP '2018-01-03 17:25:10.271', 12565, '{"id":2925,"source":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PRIMARY DIAGNOSIS (ICD)","id":12565,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12565","versionNumber":1,"latestVersionId":12565,"classifiedName":"PRIMARY DIAGNOSIS (ICD) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30881, NULL, 12564, TIMESTAMP '2018-01-03 17:25:10.285', 12564, '{"id":2926,"source":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE OF DIAGNOSIS (CLINICALLY AGREED)*","id":12566,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12566","versionNumber":1,"latestVersionId":12566,"classifiedName":"DATE OF DIAGNOSIS (CLINICALLY AGREED)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30882, NULL, 12566, TIMESTAMP '2018-01-03 17:25:10.285', 12566, '{"id":2926,"source":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE OF DIAGNOSIS (CLINICALLY AGREED)*","id":12566,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12566","versionNumber":1,"latestVersionId":12566,"classifiedName":"DATE OF DIAGNOSIS (CLINICALLY AGREED)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30883, NULL, 12564, TIMESTAMP '2018-01-03 17:25:10.301', 12564, '{"id":2927,"source":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE OF RECURRENCE (CLINICALLY AGREED)*","id":12567,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12567","versionNumber":1,"latestVersionId":12567,"classifiedName":"DATE OF RECURRENCE (CLINICALLY AGREED)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30884, NULL, 12567, TIMESTAMP '2018-01-03 17:25:10.301', 12567, '{"id":2927,"source":{"semanticVersion":"0.0.1","name":"DIAGNOSTIC DETAILS","id":12564,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12564","versionNumber":1,"latestVersionId":12564,"classifiedName":"DIAGNOSTIC DETAILS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE OF RECURRENCE (CLINICALLY AGREED)*","id":12567,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12567","versionNumber":1,"latestVersionId":12567,"classifiedName":"DATE OF RECURRENCE (CLINICALLY AGREED)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);               
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30885, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.313', 12568, '{"id":2928,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON FAMILY NAME","id":12569,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12569","versionNumber":1,"latestVersionId":12569,"classifiedName":"PERSON FAMILY NAME (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30886, NULL, 12569, TIMESTAMP '2018-01-03 17:25:10.313', 12569, '{"id":2928,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON FAMILY NAME","id":12569,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12569","versionNumber":1,"latestVersionId":12569,"classifiedName":"PERSON FAMILY NAME (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30887, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.324', 12568, '{"id":2929,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON GIVEN NAME","id":12570,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12570","versionNumber":1,"latestVersionId":12570,"classifiedName":"PERSON GIVEN NAME (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30888, NULL, 12570, TIMESTAMP '2018-01-03 17:25:10.324', 12570, '{"id":2929,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON GIVEN NAME","id":12570,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12570","versionNumber":1,"latestVersionId":12570,"classifiedName":"PERSON GIVEN NAME (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30889, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.336', 12568, '{"id":2930,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PATIENT USUAL ADDRESS (AT DIAGNOSIS)","id":12571,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12571","versionNumber":1,"latestVersionId":12571,"classifiedName":"PATIENT USUAL ADDRESS (AT DIAGNOSIS) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30890, NULL, 12571, TIMESTAMP '2018-01-03 17:25:10.336', 12571, '{"id":2930,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PATIENT USUAL ADDRESS (AT DIAGNOSIS)","id":12571,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12571","versionNumber":1,"latestVersionId":12571,"classifiedName":"PATIENT USUAL ADDRESS (AT DIAGNOSIS) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);           
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30891, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.348', 12568, '{"id":2931,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS)","id":12572,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12572","versionNumber":1,"latestVersionId":12572,"classifiedName":"POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30892, NULL, 12572, TIMESTAMP '2018-01-03 17:25:10.348', 12572, '{"id":2931,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS)","id":12572,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12572","versionNumber":1,"latestVersionId":12572,"classifiedName":"POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30893, NULL, 12574, TIMESTAMP '2018-01-03 17:25:10.36', 12574, '{"id":2932,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"personGenderCode(current)","id":12574,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12574","versionNumber":1,"latestVersionId":12574,"classifiedName":"personGenderCode(current) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30894, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.36', 12568, '{"id":2932,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"personGenderCode(current)","id":12574,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12574","versionNumber":1,"latestVersionId":12574,"classifiedName":"personGenderCode(current) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30895, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.372', 12568, '{"id":2933,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"GENERAL MEDICAL PRACTITIONER (SPECIFIED)","id":12575,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12575","versionNumber":1,"latestVersionId":12575,"classifiedName":"GENERAL MEDICAL PRACTITIONER (SPECIFIED) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE);              
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30896, NULL, 12575, TIMESTAMP '2018-01-03 17:25:10.372', 12575, '{"id":2933,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"GENERAL MEDICAL PRACTITIONER (SPECIFIED)","id":12575,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12575","versionNumber":1,"latestVersionId":12575,"classifiedName":"GENERAL MEDICAL PRACTITIONER (SPECIFIED) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30897, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.385', 12568, '{"id":2934,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION)","id":12576,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12576","versionNumber":1,"latestVersionId":12576,"classifiedName":"GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30898, NULL, 12576, TIMESTAMP '2018-01-03 17:25:10.385', 12576, '{"id":2934,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION)","id":12576,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12576","versionNumber":1,"latestVersionId":12576,"classifiedName":"GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30899, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.401', 12568, '{"id":2935,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON FAMILY NAME (AT BIRTH)","id":12577,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12577","versionNumber":1,"latestVersionId":12577,"classifiedName":"PERSON FAMILY NAME (AT BIRTH) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30900, NULL, 12577, TIMESTAMP '2018-01-03 17:25:10.401', 12577, '{"id":2935,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PERSON FAMILY NAME (AT BIRTH)","id":12577,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12577","versionNumber":1,"latestVersionId":12577,"classifiedName":"PERSON FAMILY NAME (AT BIRTH) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);         
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30901, NULL, 12568, TIMESTAMP '2018-01-03 17:25:10.414', 12568, '{"id":2936,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ethnicCategory","id":12579,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12579","versionNumber":1,"latestVersionId":12579,"classifiedName":"ethnicCategory (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30902, NULL, 12579, TIMESTAMP '2018-01-03 17:25:10.414', 12579, '{"id":2936,"source":{"semanticVersion":"0.0.1","name":"DEMOGRAPHICS","id":12568,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12568","versionNumber":1,"latestVersionId":12568,"classifiedName":"DEMOGRAPHICS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ethnicCategory","id":12579,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12579","versionNumber":1,"latestVersionId":12579,"classifiedName":"ethnicCategory (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30903, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.426', 12580, '{"id":2937,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"sourceOfReferralForOutPatients","id":12582,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12582","versionNumber":1,"latestVersionId":12582,"classifiedName":"sourceOfReferralForOutPatients (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30904, NULL, 12582, TIMESTAMP '2018-01-03 17:25:10.426', 12582, '{"id":2937,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"sourceOfReferralForOutPatients","id":12582,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12582","versionNumber":1,"latestVersionId":12582,"classifiedName":"sourceOfReferralForOutPatients (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30905, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.438', 12580, '{"id":2938,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"REFERRAL TO TREATMENT PERIOD START DATE","id":12583,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12583","versionNumber":1,"latestVersionId":12583,"classifiedName":"REFERRAL TO TREATMENT PERIOD START DATE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30906, NULL, 12583, TIMESTAMP '2018-01-03 17:25:10.438', 12583, '{"id":2938,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"REFERRAL TO TREATMENT PERIOD START DATE","id":12583,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12583","versionNumber":1,"latestVersionId":12583,"classifiedName":"REFERRAL TO TREATMENT PERIOD START DATE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);   
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30907, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.45', 12580, '{"id":2939,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE FIRST SEEN","id":12584,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12584","versionNumber":1,"latestVersionId":12584,"classifiedName":"DATE FIRST SEEN (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30908, NULL, 12584, TIMESTAMP '2018-01-03 17:25:10.45', 12584, '{"id":2939,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE FIRST SEEN","id":12584,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12584","versionNumber":1,"latestVersionId":12584,"classifiedName":"DATE FIRST SEEN (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30909, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.461', 12580, '{"id":2940,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"CONSULTANT CODE","id":12585,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12585","versionNumber":1,"latestVersionId":12585,"classifiedName":"CONSULTANT CODE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30910, NULL, 12585, TIMESTAMP '2018-01-03 17:25:10.461', 12585, '{"id":2940,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"CONSULTANT CODE","id":12585,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12585","versionNumber":1,"latestVersionId":12585,"classifiedName":"CONSULTANT CODE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30911, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.477', 12580, '{"id":2941,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"CARE PROFESSIONAL MAIN SPECIALTY CODE","id":12586,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12586","versionNumber":1,"latestVersionId":12586,"classifiedName":"CARE PROFESSIONAL MAIN SPECIALTY CODE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30912, NULL, 12586, TIMESTAMP '2018-01-03 17:25:10.477', 12586, '{"id":2941,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"CARE PROFESSIONAL MAIN SPECIALTY CODE","id":12586,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12586","versionNumber":1,"latestVersionId":12586,"classifiedName":"CARE PROFESSIONAL MAIN SPECIALTY CODE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE); 
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30913, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.492', 12580, '{"id":2942,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ORGANISATION SITE CODE (PROVIDER FIRST SEEN)","id":12587,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12587","versionNumber":1,"latestVersionId":12587,"classifiedName":"ORGANISATION SITE CODE (PROVIDER FIRST SEEN) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30914, NULL, 12587, TIMESTAMP '2018-01-03 17:25:10.492', 12587, '{"id":2942,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ORGANISATION SITE CODE (PROVIDER FIRST SEEN)","id":12587,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12587","versionNumber":1,"latestVersionId":12587,"classifiedName":"ORGANISATION SITE CODE (PROVIDER FIRST SEEN) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30915, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.504', 12580, '{"id":2943,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE FIRST SEEN (TESTCER SPECIALIST)","id":12588,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12588","versionNumber":1,"latestVersionId":12588,"classifiedName":"DATE FIRST SEEN (TESTCER SPECIALIST) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30916, NULL, 12588, TIMESTAMP '2018-01-03 17:25:10.504', 12588, '{"id":2943,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"DATE FIRST SEEN (TESTCER SPECIALIST)","id":12588,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12588","versionNumber":1,"latestVersionId":12588,"classifiedName":"DATE FIRST SEEN (TESTCER SPECIALIST) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30917, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.516', 12580, '{"id":2944,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ORGANISATION SITE CODE (PROVIDER FIRST TESTCER SPECIALIST)","id":12589,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12589","versionNumber":1,"latestVersionId":12589,"classifiedName":"ORGANISATION SITE CODE (PROVIDER FIRST TESTCER SPECIALIST) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE);          
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30918, NULL, 12589, TIMESTAMP '2018-01-03 17:25:10.516', 12589, '{"id":2944,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"ORGANISATION SITE CODE (PROVIDER FIRST TESTCER SPECIALIST)","id":12589,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12589","versionNumber":1,"latestVersionId":12589,"classifiedName":"ORGANISATION SITE CODE (PROVIDER FIRST TESTCER SPECIALIST) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30919, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.527', 12580, '{"id":2945,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"testcerOrSymptomaticBreastReferralPatientStatus","id":12591,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12591","versionNumber":1,"latestVersionId":12591,"classifiedName":"testcerOrSymptomaticBreastReferralPatientStatus (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30920, NULL, 12591, TIMESTAMP '2018-01-03 17:25:10.527', 12591, '{"id":2945,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"testcerOrSymptomaticBreastReferralPatientStatus","id":12591,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12591","versionNumber":1,"latestVersionId":12591,"classifiedName":"testcerOrSymptomaticBreastReferralPatientStatus (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30921, NULL, 12580, TIMESTAMP '2018-01-03 17:25:10.54', 12580, '{"id":2946,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"TESTCER SYMPTOMS FIRST NOTED DATE","id":12592,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12592","versionNumber":1,"latestVersionId":12592,"classifiedName":"TESTCER SYMPTOMS FIRST NOTED DATE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30922, NULL, 12592, TIMESTAMP '2018-01-03 17:25:10.54', 12592, '{"id":2946,"source":{"semanticVersion":"0.0.1","name":"REFERRALS","id":12580,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12580","versionNumber":1,"latestVersionId":12580,"classifiedName":"REFERRALS (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"TESTCER SYMPTOMS FIRST NOTED DATE","id":12592,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12592","versionNumber":1,"latestVersionId":12592,"classifiedName":"TESTCER SYMPTOMS FIRST NOTED DATE (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);         
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30923, NULL, 12593, TIMESTAMP '2018-01-03 17:25:10.551', 12593, '{"id":2947,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"SITE CODE (OF IMAGING)","id":12594,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12594","versionNumber":1,"latestVersionId":12594,"classifiedName":"SITE CODE (OF IMAGING) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30924, NULL, 12594, TIMESTAMP '2018-01-03 17:25:10.551', 12594, '{"id":2947,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"SITE CODE (OF IMAGING)","id":12594,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12594","versionNumber":1,"latestVersionId":12594,"classifiedName":"SITE CODE (OF IMAGING) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30925, NULL, 12593, TIMESTAMP '2018-01-03 17:25:10.563', 12593, '{"id":2948,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PROCEDURE DATE (TESTCER IMAGING)","id":12595,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12595","versionNumber":1,"latestVersionId":12595,"classifiedName":"PROCEDURE DATE (TESTCER IMAGING) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30926, NULL, 12595, TIMESTAMP '2018-01-03 17:25:10.563', 12595, '{"id":2948,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"PROCEDURE DATE (TESTCER IMAGING)","id":12595,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12595","versionNumber":1,"latestVersionId":12595,"classifiedName":"PROCEDURE DATE (TESTCER IMAGING) (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30927, NULL, 12593, TIMESTAMP '2018-01-03 17:25:10.576', 12593, '{"id":2949,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"IMAGING CODE (NICIP)*","id":12596,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12596","versionNumber":1,"latestVersionId":12596,"classifiedName":"IMAGING CODE (NICIP)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30928, NULL, 12596, TIMESTAMP '2018-01-03 17:25:10.576', 12596, '{"id":2949,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"IMAGING CODE (NICIP)*","id":12596,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12596","versionNumber":1,"latestVersionId":12596,"classifiedName":"IMAGING CODE (NICIP)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);       
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30929, NULL, 12598, TIMESTAMP '2018-01-03 17:25:11.33', 12598, '{"id":2950,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"testcerImagingModality*","id":12598,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12598","versionNumber":1,"latestVersionId":12598,"classifiedName":"testcerImagingModality* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30930, NULL, 12593, TIMESTAMP '2018-01-03 17:25:11.33', 12593, '{"id":2950,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"testcerImagingModality*","id":12598,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12598","versionNumber":1,"latestVersionId":12598,"classifiedName":"testcerImagingModality* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30931, NULL, 12599, TIMESTAMP '2018-01-03 17:25:11.345', 12599, '{"id":2951,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"IMAGING ANATOMICAL SITE*","id":12599,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12599","versionNumber":1,"latestVersionId":12599,"classifiedName":"IMAGING ANATOMICAL SITE* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30932, NULL, 12593, TIMESTAMP '2018-01-03 17:25:11.345', 12593, '{"id":2951,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"IMAGING ANATOMICAL SITE*","id":12599,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12599","versionNumber":1,"latestVersionId":12599,"classifiedName":"IMAGING ANATOMICAL SITE* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30933, NULL, 12593, TIMESTAMP '2018-01-03 17:25:11.358', 12593, '{"id":2952,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"anatomicalSide(imaging)*","id":12601,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12601","versionNumber":1,"latestVersionId":12601,"classifiedName":"anatomicalSide(imaging)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30934, NULL, 12601, TIMESTAMP '2018-01-03 17:25:11.358', 12601, '{"id":2952,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"anatomicalSide(imaging)*","id":12601,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12601","versionNumber":1,"latestVersionId":12601,"classifiedName":"anatomicalSide(imaging)* (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE);         
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30935, NULL, 12593, TIMESTAMP '2018-01-03 17:25:11.37', 12593, '{"id":2953,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"gender2","id":12603,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12603","versionNumber":1,"latestVersionId":12603,"classifiedName":"gender2 (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, FALSE, NULL, 'contains', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30936, NULL, 12603, TIMESTAMP '2018-01-03 17:25:11.37', 12603, '{"id":2953,"source":{"semanticVersion":"0.0.1","name":"IMAGING","id":12593,"elementType":"org.modelcatalogue.core.DataClass","link":"/dataClass/12593","versionNumber":1,"latestVersionId":12593,"classifiedName":"IMAGING (NHIC 0.0.1)"},"destination":{"semanticVersion":"0.0.1","name":"gender2","id":12603,"elementType":"org.modelcatalogue.core.DataElement","link":"/dataElement/12603","versionNumber":1,"latestVersionId":12603,"classifiedName":"gender2 (NHIC 0.0.1)"},"type":{"id":2888,"name":"containment","link":"/relationshipType/2888"},"elementType":"org.modelcatalogue.core.Relationship"}', NULL, TRUE, NULL, 'contained in', FALSE, 'RELATIONSHIP_CREATED', FALSE),
(30937, NULL, 12550, TIMESTAMP '2018-01-03 17:25:11.385', 12550, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, NULL, 'status', FALSE, 'ELEMENT_FINALIZED', FALSE),
(30938, NULL, 12550, TIMESTAMP '2018-01-03 17:25:11.391', 12550, '{"value":"PENDING"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30939, NULL, 12550, TIMESTAMP '2018-01-03 17:25:11.435', 12550, '{"value":"FINALIZED"}', '{"value":"PENDING"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30940, NULL, 12551, TIMESTAMP '2018-01-03 17:25:11.437', 12551, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30941, NULL, 12552, TIMESTAMP '2018-01-03 17:25:11.44', 12552, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30942, NULL, 12553, TIMESTAMP '2018-01-03 17:25:11.442', 12553, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30943, NULL, 12554, TIMESTAMP '2018-01-03 17:25:11.445', 12554, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30944, NULL, 12555, TIMESTAMP '2018-01-03 17:25:11.448', 12555, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30945, NULL, 12556, TIMESTAMP '2018-01-03 17:25:11.451', 12556, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30946, NULL, 12557, TIMESTAMP '2018-01-03 17:25:11.454', 12557, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30947, NULL, 12558, TIMESTAMP '2018-01-03 17:25:11.457', 12558, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30948, NULL, 12559, TIMESTAMP '2018-01-03 17:25:11.459', 12559, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30949, NULL, 12560, TIMESTAMP '2018-01-03 17:25:11.462', 12560, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30950, NULL, 12561, TIMESTAMP '2018-01-03 17:25:11.464', 12561, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30951, NULL, 12562, TIMESTAMP '2018-01-03 17:25:11.467', 12562, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE);        
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30952, NULL, 12563, TIMESTAMP '2018-01-03 17:25:11.469', 12563, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30953, NULL, 12564, TIMESTAMP '2018-01-03 17:25:11.471', 12564, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30954, NULL, 12565, TIMESTAMP '2018-01-03 17:25:11.473', 12565, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30955, NULL, 12566, TIMESTAMP '2018-01-03 17:25:11.475', 12566, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30956, NULL, 12567, TIMESTAMP '2018-01-03 17:25:11.478', 12567, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30957, NULL, 12568, TIMESTAMP '2018-01-03 17:25:11.481', 12568, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30958, NULL, 12569, TIMESTAMP '2018-01-03 17:25:11.483', 12569, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30959, NULL, 12570, TIMESTAMP '2018-01-03 17:25:11.486', 12570, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30960, NULL, 12571, TIMESTAMP '2018-01-03 17:25:11.489', 12571, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30961, NULL, 12572, TIMESTAMP '2018-01-03 17:25:11.492', 12572, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30962, NULL, 12573, TIMESTAMP '2018-01-03 17:25:11.495', 12573, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30963, NULL, 12574, TIMESTAMP '2018-01-03 17:25:11.497', 12574, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30964, NULL, 12575, TIMESTAMP '2018-01-03 17:25:11.5', 12575, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30965, NULL, 12576, TIMESTAMP '2018-01-03 17:25:11.503', 12576, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30966, NULL, 12577, TIMESTAMP '2018-01-03 17:25:11.506', 12577, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30967, NULL, 12578, TIMESTAMP '2018-01-03 17:25:11.509', 12578, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30968, NULL, 12579, TIMESTAMP '2018-01-03 17:25:11.511', 12579, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30969, NULL, 12580, TIMESTAMP '2018-01-03 17:25:11.514', 12580, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30970, NULL, 12581, TIMESTAMP '2018-01-03 17:25:11.516', 12581, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30971, NULL, 12582, TIMESTAMP '2018-01-03 17:25:11.518', 12582, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30972, NULL, 12583, TIMESTAMP '2018-01-03 17:25:11.521', 12583, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30973, NULL, 12584, TIMESTAMP '2018-01-03 17:25:11.523', 12584, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30974, NULL, 12585, TIMESTAMP '2018-01-03 17:25:11.525', 12585, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30975, NULL, 12586, TIMESTAMP '2018-01-03 17:25:11.527', 12586, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE);              
INSERT INTO PUBLIC."change"(ID, AUTHOR_ID, CHANGED_ID, DATE_CREATED, LATEST_VERSION_ID, NEW_VALUE, OLD_VALUE, OTHER_SIDE, PARENT_ID, PROPERTY, SYSTEM, TYPE, UNDONE) VALUES
(30976, NULL, 12587, TIMESTAMP '2018-01-03 17:25:11.529', 12587, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30977, NULL, 12588, TIMESTAMP '2018-01-03 17:25:11.532', 12588, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30978, NULL, 12589, TIMESTAMP '2018-01-03 17:25:11.534', 12589, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30979, NULL, 12590, TIMESTAMP '2018-01-03 17:25:11.536', 12590, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30980, NULL, 12591, TIMESTAMP '2018-01-03 17:25:11.538', 12591, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30981, NULL, 12592, TIMESTAMP '2018-01-03 17:25:11.541', 12592, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30982, NULL, 12593, TIMESTAMP '2018-01-03 17:25:11.543', 12593, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30983, NULL, 12594, TIMESTAMP '2018-01-03 17:25:11.545', 12594, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30984, NULL, 12595, TIMESTAMP '2018-01-03 17:25:11.547', 12595, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30985, NULL, 12596, TIMESTAMP '2018-01-03 17:25:11.549', 12596, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30986, NULL, 12597, TIMESTAMP '2018-01-03 17:25:11.551', 12597, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30987, NULL, 12598, TIMESTAMP '2018-01-03 17:25:11.554', 12598, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30988, NULL, 12599, TIMESTAMP '2018-01-03 17:25:11.556', 12599, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30989, NULL, 12600, TIMESTAMP '2018-01-03 17:25:11.558', 12600, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30990, NULL, 12601, TIMESTAMP '2018-01-03 17:25:11.56', 12601, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30991, NULL, 12602, TIMESTAMP '2018-01-03 17:25:11.563', 12602, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE),
(30992, NULL, 12603, TIMESTAMP '2018-01-03 17:25:11.565', 12603, '{"value":"FINALIZED"}', '{"value":"DRAFT"}', FALSE, 30937, 'status', TRUE, 'PROPERTY_CHANGED', FALSE);            
CREATE INDEX PUBLIC.CHANGE_IDX_4 ON PUBLIC."change"(LATEST_VERSION_ID);        
CREATE MEMORY TABLE PUBLIC.ACL_CLASS(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_CEF088DF_D7E8_4876_9297_5A82B37AA2D0) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_CEF088DF_D7E8_4876_9297_5A82B37AA2D0,
    CLASS VARCHAR(255) NOT NULL
);     
ALTER TABLE PUBLIC.ACL_CLASS ADD CONSTRAINT PUBLIC.CONSTRAINT_2 PRIMARY KEY(ID);               
-- 1 +/- SELECT COUNT(*) FROM PUBLIC.ACL_CLASS;
INSERT INTO PUBLIC.ACL_CLASS(ID, CLASS) VALUES
(135, 'org.modelcatalogue.core.DataModel');     
CREATE MEMORY TABLE PUBLIC.ACL_ENTRY(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_FA1F68A8_BD29_4CC2_884B_0CE6B5D946F4) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_FA1F68A8_BD29_4CC2_884B_0CE6B5D946F4,
    ACE_ORDER INTEGER NOT NULL,
    ACL_OBJECT_IDENTITY BIGINT NOT NULL,
    AUDIT_FAILURE BOOLEAN NOT NULL,
    AUDIT_SUCCESS BOOLEAN NOT NULL,
    GRANTING BOOLEAN NOT NULL,
    MASK INTEGER NOT NULL,
    SID BIGINT NOT NULL
);  
ALTER TABLE PUBLIC.ACL_ENTRY ADD CONSTRAINT PUBLIC.CONSTRAINT_2F PRIMARY KEY(ID);              
-- 6 +/- SELECT COUNT(*) FROM PUBLIC.ACL_ENTRY;
INSERT INTO PUBLIC.ACL_ENTRY(ID, ACE_ORDER, ACL_OBJECT_IDENTITY, AUDIT_FAILURE, AUDIT_SUCCESS, GRANTING, MASK, SID) VALUES
(1200, 0, 1116, FALSE, FALSE, TRUE, 16, 136),
(1201, 0, 1117, FALSE, FALSE, TRUE, 16, 136),
(1202, 0, 1118, FALSE, FALSE, TRUE, 16, 136),
(1203, 0, 1119, FALSE, FALSE, TRUE, 16, 136),
(1204, 0, 1120, FALSE, FALSE, TRUE, 16, 136),
(1205, 0, 1121, FALSE, FALSE, TRUE, 16, 136); 
CREATE MEMORY TABLE PUBLIC.ACL_OBJECT_IDENTITY(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_928C5C84_0376_43E0_9024_0058AD5666D3) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_928C5C84_0376_43E0_9024_0058AD5666D3,
    OBJECT_ID_CLASS BIGINT NOT NULL,
    ENTRIES_INHERITING BOOLEAN NOT NULL,
    OBJECT_ID_IDENTITY BIGINT NOT NULL,
    OWNER_SID BIGINT,
    PARENT_OBJECT BIGINT
);      
ALTER TABLE PUBLIC.ACL_OBJECT_IDENTITY ADD CONSTRAINT PUBLIC.CONSTRAINT_9 PRIMARY KEY(ID);     
-- 6 +/- SELECT COUNT(*) FROM PUBLIC.ACL_OBJECT_IDENTITY;      
INSERT INTO PUBLIC.ACL_OBJECT_IDENTITY(ID, OBJECT_ID_CLASS, ENTRIES_INHERITING, OBJECT_ID_IDENTITY, OWNER_SID, PARENT_OBJECT) VALUES
(1116, 135, TRUE, 12478, 136, NULL),
(1117, 135, TRUE, 12514, 136, NULL),
(1118, 135, TRUE, 12523, 136, NULL),
(1119, 135, TRUE, 12525, 136, NULL),
(1120, 135, TRUE, 12548, 136, NULL),
(1121, 135, TRUE, 12550, 136, NULL);             
CREATE MEMORY TABLE PUBLIC.ACL_SID(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_B73D2D48_B9EB_408C_90B3_6B5BBD20A79C) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_B73D2D48_B9EB_408C_90B3_6B5BBD20A79C,
    PRINCIPAL BOOLEAN NOT NULL,
    SID VARCHAR(255) NOT NULL
);         
ALTER TABLE PUBLIC.ACL_SID ADD CONSTRAINT PUBLIC.CONSTRAINT_E PRIMARY KEY(ID); 
-- 1 +/- SELECT COUNT(*) FROM PUBLIC.ACL_SID;  
INSERT INTO PUBLIC.ACL_SID(ID, PRINCIPAL, SID) VALUES
(136, TRUE, 'Adam');     
CREATE MEMORY TABLE PUBLIC.ACTION(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_28AD9A6B_6861_4FEB_B19E_1C5E409E419E) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_28AD9A6B_6861_4FEB_B19E_1C5E409E419E,
    BATCH_ID BIGINT NOT NULL,
    DATE_CREATED TIMESTAMP NOT NULL,
    LAST_UPDATED TIMESTAMP NOT NULL,
    OUTCOME VARCHAR(10000),
    RESULT VARCHAR(1000),
    STATE VARCHAR(255) NOT NULL,
    TYPE VARCHAR(255) NOT NULL
);          
ALTER TABLE PUBLIC.ACTION ADD CONSTRAINT PUBLIC.CONSTRAINT_7 PRIMARY KEY(ID);  
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.ACTION;   
CREATE MEMORY TABLE PUBLIC.ACTION_DEPENDENCY(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_AFD576D2_5FA8_4B88_949D_0E8E8EB21120) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_AFD576D2_5FA8_4B88_949D_0E8E8EB21120,
    VERSION BIGINT NOT NULL,
    DEPENDANT_ID BIGINT NOT NULL,
    PROVIDER_ID BIGINT NOT NULL,
    ROLE VARCHAR(100) NOT NULL
);              
ALTER TABLE PUBLIC.ACTION_DEPENDENCY ADD CONSTRAINT PUBLIC.CONSTRAINT_B PRIMARY KEY(ID);       
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.ACTION_DEPENDENCY;        
CREATE MEMORY TABLE PUBLIC.ACTION_PARAMETER(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_FF10B2D8_8075_42FC_9B50_3B03281A4B70) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_FF10B2D8_8075_42FC_9B50_3B03281A4B70,
    VERSION BIGINT NOT NULL,
    ACTION_ID BIGINT NOT NULL,
    EXTENSION_VALUE VARCHAR(1000),
    NAME VARCHAR(255) NOT NULL
);
ALTER TABLE PUBLIC.ACTION_PARAMETER ADD CONSTRAINT PUBLIC.CONSTRAINT_6 PRIMARY KEY(ID);        
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.ACTION_PARAMETER;         
CREATE MEMORY TABLE PUBLIC.ASSET(
    ID BIGINT NOT NULL,
    CONTENT_TYPE VARCHAR(255),
    MD5 VARCHAR(32),
    ORIGINAL_FILE_NAME VARCHAR(255),
    SIZE BIGINT
);          
ALTER TABLE PUBLIC.ASSET ADD CONSTRAINT PUBLIC.CONSTRAINT_3 PRIMARY KEY(ID);   
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.ASSET;    
CREATE MEMORY TABLE PUBLIC.ASSET_FILE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_A30922A4_033F_4C06_AC65_CDDA6C684D18) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A30922A4_033F_4C06_AC65_CDDA6C684D18,
    VERSION BIGINT NOT NULL,
    CONTENT BLOB NOT NULL,
    PATH VARCHAR(255) NOT NULL
);             
ALTER TABLE PUBLIC.ASSET_FILE ADD CONSTRAINT PUBLIC.CONSTRAINT_9D PRIMARY KEY(ID);             
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.ASSET_FILE;               
CREATE MEMORY TABLE PUBLIC.BATCH(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_F84A0490_6AE9_4FDB_9712_87B6144B0C60) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_F84A0490_6AE9_4FDB_9712_87B6144B0C60,
    VERSION BIGINT NOT NULL,
    ARCHIVED BOOLEAN NOT NULL,
    DATE_CREATED TIMESTAMP NOT NULL,
    DESCRIPTION VARCHAR(2000),
    LAST_UPDATED TIMESTAMP NOT NULL,
    NAME VARCHAR(255) NOT NULL
);     
ALTER TABLE PUBLIC.BATCH ADD CONSTRAINT PUBLIC.CONSTRAINT_3C PRIMARY KEY(ID);  
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.BATCH;    
CREATE MEMORY TABLE PUBLIC.CATALOGUE_ELEMENT(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_48CEAAAF_2BEA_4F58_B74D_43F8F7F66F39) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_48CEAAAF_2BEA_4F58_B74D_43F8F7F66F39 SELECTIVITY 100,
    VERSION BIGINT NOT NULL SELECTIVITY 10,
    DATA_MODEL_ID BIGINT SELECTIVITY 4,
    DATE_CREATED TIMESTAMP NOT NULL SELECTIVITY 100,
    DESCRIPTION LONGVARCHAR SELECTIVITY 37,
    LAST_UPDATED TIMESTAMP NOT NULL SELECTIVITY 100,
    LATEST_VERSION_ID BIGINT SELECTIVITY 45,
    MODEL_CATALOGUE_ID VARCHAR(255) SELECTIVITY 1,
    NAME VARCHAR(255) NOT NULL SELECTIVITY 50,
    STATUS VARCHAR(255) NOT NULL SELECTIVITY 2,
    VERSION_CREATED TIMESTAMP NOT NULL SELECTIVITY 100,
    VERSION_NUMBER INTEGER NOT NULL SELECTIVITY 2
);          
ALTER TABLE PUBLIC.CATALOGUE_ELEMENT ADD CONSTRAINT PUBLIC.CONSTRAINT_1 PRIMARY KEY(ID);       
-- 126 +/- SELECT COUNT(*) FROM PUBLIC.CATALOGUE_ELEMENT;      
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12478, 0, NULL, TIMESTAMP '2018-01-03 17:25:00.191', 'XML Schema provides standard types for describing your own XML formats', TIMESTAMP '2018-01-03 17:25:00.191', NULL, 'http://www.w3.org/2001/XMLSchema', 'XMLSchema', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.19', 1),
(12479, 0, 12478, TIMESTAMP '2018-01-03 17:25:00.206', 'Binary-valued logic legal literals', TIMESTAMP '2018-01-03 17:25:00.206', NULL, 'http://www.w3.org/2001/XMLSchema#boolean', 'xs:boolean', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.204', 1),
(12480, 0, 12478, TIMESTAMP '2018-01-03 17:25:00.229', 'Base64-encoded arbitrary binary data', TIMESTAMP '2018-01-03 17:25:00.229', NULL, 'http://www.w3.org/2001/XMLSchema#base64Binary', 'xs:base64Binary', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.212', 1),
(12481, 0, 12478, TIMESTAMP '2018-01-03 17:25:00.271', 'Arbitrary hex-encoded binary data. Example, "0FB7" is a hex encoding for 16-bit int 4023 (binary 111110110111).', TIMESTAMP '2018-01-03 17:25:00.271', NULL, 'http://www.w3.org/2001/XMLSchema#hexBinary', 'xs:hexBinary', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.253', 1),
(12482, 0, 12478, TIMESTAMP '2018-01-03 17:25:00.308', 'A Uniform Resource Identifier Reference (URI). Can be absolute or relative, and may have an optional fragment identifier.', TIMESTAMP '2018-01-03 17:25:00.308', NULL, 'http://www.w3.org/2001/XMLSchema#anyURI', 'xs:anyURI', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.292', 1),
(12483, 1, 12478, TIMESTAMP '2018-01-03 17:25:00.348', 'Character strings in XML.', TIMESTAMP '2018-01-03 17:25:03.505', NULL, 'http://www.w3.org/2001/XMLSchema#string', 'xs:string', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.328', 1),
(12484, 2, 12478, TIMESTAMP '2018-01-03 17:25:00.386', 'White space normalized strings', TIMESTAMP '2018-01-03 17:25:03.532', NULL, 'http://www.w3.org/2001/XMLSchema#normalizedString', 'xs:normalizedString', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.369', 1),
(12485, 2, 12478, TIMESTAMP '2018-01-03 17:25:00.422', 'Tokenized strings.', TIMESTAMP '2018-01-03 17:25:03.559', NULL, 'http://www.w3.org/2001/XMLSchema#token', 'xs:token', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.408', 1),
(12486, 1, 12478, TIMESTAMP '2018-01-03 17:25:00.459', 'Tokenized strings.', TIMESTAMP '2018-01-03 17:25:03.559', NULL, 'http://www.w3.org/2001/XMLSchema#language', 'xs:language', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.441', 1),
(12487, 1, 12478, TIMESTAMP '2018-01-03 17:25:00.51', STRINGDECODE('Arbitrary precision decimal numbers. Sign omitted, \u201c+\u201d is assumed. Leading and trailing zeroes are optional. If the fractional part is zero, the period and following zero(es) can be omitted.'), TIMESTAMP '2018-01-03 17:25:03.586', NULL, 'http://www.w3.org/2001/XMLSchema#decimal', 'xs:decimal', 'DRAFT', TIMESTAMP '2018-01-03 17:25:00.482', 1),
(12488, 0, 12478, TIMESTAMP '2018-01-03 17:25:01.122', 'Double-precision 64-bit floating point type legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF', TIMESTAMP '2018-01-03 17:25:01.122', NULL, 'http://www.w3.org/2001/XMLSchema#double', 'xs:double', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.1', 1),
(12489, 0, 12478, TIMESTAMP '2018-01-03 17:25:01.176', 'Double-precision 32-bit floating point type legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF', TIMESTAMP '2018-01-03 17:25:01.176', NULL, 'http://www.w3.org/2001/XMLSchema#float', 'xs:float', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.15', 1),
(12490, 4, 12478, TIMESTAMP '2018-01-03 17:25:01.239', STRINGDECODE('Integer or whole numbers - Sign omitted, \u201c+\u201d is assumed. Example: -1, 0, 12678967543233, +100000'), TIMESTAMP '2018-01-03 17:25:03.671', NULL, 'http://www.w3.org/2001/XMLSchema#integer', 'xs:integer', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.213', 1),
(12491, 2, 12478, TIMESTAMP '2018-01-03 17:25:01.298', STRINGDECODE('9223372036854775807 to -9223372036854775808. Sign omitted, \u201c+\u201d assumed. Example: -1, 0, 12678967543233, +100000.'), TIMESTAMP '2018-01-03 17:25:03.695', NULL, 'http://www.w3.org/2001/XMLSchema#long', 'xs:long', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.274', 1);       
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12492, 2, 12478, TIMESTAMP '2018-01-03 17:25:01.347', STRINGDECODE('2147483647 to -2147483648. Sign omitted, \u201c+\u201d is assumed. Example: -1, 0, 126789675, +100000.'), TIMESTAMP '2018-01-03 17:25:03.724', NULL, 'http://www.w3.org/2001/XMLSchema#int', 'xs:int', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.326', 1),
(12493, 2, 12478, TIMESTAMP '2018-01-03 17:25:01.398', STRINGDECODE('32767 to -32768. Sign omitted, \u201c+\u201d assumed. Example: -1, 0, 12678, +10000.'), TIMESTAMP '2018-01-03 17:25:03.758', NULL, 'http://www.w3.org/2001/XMLSchema#short', 'xs:short', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.376', 1),
(12494, 1, 12478, TIMESTAMP '2018-01-03 17:25:01.444', STRINGDECODE('127 to-128. Sign is omitted, \u201c+\u201d assumed. Example: -1, 0, 126, +100.'), TIMESTAMP '2018-01-03 17:25:03.758', NULL, 'http://www.w3.org/2001/XMLSchema#byte', 'xs:byte', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.424', 1),
(12495, 3, 12478, TIMESTAMP '2018-01-03 17:25:01.487', STRINGDECODE('Infinite set {0, 1, 2,...}. Sign omitted, \u201c+\u201d assumed. Example: 1, 0, 12678967543233, +100000.'), TIMESTAMP '2018-01-03 17:25:03.832', NULL, 'http://www.w3.org/2001/XMLSchema#nonNegativeInteger', 'xs:nonNegativeInteger', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.47', 1),
(12496, 2, 12478, TIMESTAMP '2018-01-03 17:25:01.519', 'Infinite set {...,-2,-1,0}. Example: -1, 0, -126733, -100000.', TIMESTAMP '2018-01-03 17:25:03.789', NULL, 'http://www.w3.org/2001/XMLSchema#nonPositiveInteger', 'xs:nonPositiveInteger', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.505', 1),
(12497, 1, 12478, TIMESTAMP '2018-01-03 17:25:01.551', 'Infinite set {...,-2,-1}. Example: -1, -12678967543233, -100000', TIMESTAMP '2018-01-03 17:25:03.789', NULL, 'http://www.w3.org/2001/XMLSchema#negativeInteger', 'xs:negativeInteger', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.536', 1),
(12498, 1, 12478, TIMESTAMP '2018-01-03 17:25:01.585', STRINGDECODE('Infinite set {1, 2,...}. Optional \u201c+\u201d sign,. Example: 1, 12678967543233, +100000.'), TIMESTAMP '2018-01-03 17:25:03.812', NULL, 'http://www.w3.org/2001/XMLSchema#positiveInteger', 'xs:positiveInteger', 'DRAFT', TIMESTAMP '2018-01-03 17:25:01.57', 1),
(12499, 2, 12478, TIMESTAMP '2018-01-03 17:25:02.436', '0 to 18446744073709551615. Example: 0, 12678967543233, 100000.', TIMESTAMP '2018-01-03 17:25:03.854', NULL, 'http://www.w3.org/2001/XMLSchema#unsignedLong', 'xs:unsignedLong', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.414', 1),
(12500, 2, 12478, TIMESTAMP '2018-01-03 17:25:02.47', '0 to 4294967295', TIMESTAMP '2018-01-03 17:25:03.878', NULL, 'http://www.w3.org/2001/XMLSchema#unsignedInt', 'xs:unsignedInt', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.455', 1),
(12501, 2, 12478, TIMESTAMP '2018-01-03 17:25:02.51', '0 to 65535. Example: 0, 12678, 10000.', TIMESTAMP '2018-01-03 17:25:03.9', NULL, 'http://www.w3.org/2001/XMLSchema#unsignedShort', 'xs:unsignedShort', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.495', 1),
(12502, 1, 12478, TIMESTAMP '2018-01-03 17:25:02.545', '0 to 255. a finite-length Example: 0, 126, 100.', TIMESTAMP '2018-01-03 17:25:03.9', NULL, 'http://www.w3.org/2001/XMLSchema#unsignedByte', 'xs:unsignedByte', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.531', 1),
(12503, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.592', 'Calendar date.Format YYYY-MM-DD. Example, May the 31st, 1999 is: 1999-05-31.', TIMESTAMP '2018-01-03 17:25:02.592', NULL, 'http://www.w3.org/2001/XMLSchema#date', 'xs:date', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.566', 1),
(12504, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.654', 'Specific instant of time. ISO 8601 extended format YYYY-MM-DDThh:mm:ss. Example, to indicate 1:20 pm on May the 31st, 1999 for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC): 1999-05-31T13:20:00-05:00.', TIMESTAMP '2018-01-03 17:25:02.654', NULL, 'http://www.w3.org/2001/XMLSchema#dateTime', 'xs:dateTime', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.622', 1);      
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12505, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.708', 'An instant of time that recurs every day. Example, 1:20 pm for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC), write: 13:20:00-05:00.', TIMESTAMP '2018-01-03 17:25:02.708', NULL, 'http://www.w3.org/2001/XMLSchema#time', 'xs:time', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.683', 1),
(12506, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.753', 'A duration of time. ISO 8601 extended format PnYnMnDTnHnMnS. Example, to indicate duration of 1 year, 2 months, 3 days, 10 hours, and 30 minutes: P1Y2M3DT10H30M. One could also indicate a duration of minus 120 days as: -P120D.', TIMESTAMP '2018-01-03 17:25:02.753', NULL, 'http://www.w3.org/2001/XMLSchema#duration', 'xs:duration', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.735', 1),
(12507, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.794', 'Gregorian day. Example a day such as the 5th of the month is 05.', TIMESTAMP '2018-01-03 17:25:02.794', NULL, 'http://www.w3.org/2001/XMLSchema#gDay', 'xs:gDay', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.775', 1),
(12508, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.834', 'Gregorian month. Example: May is 05.', TIMESTAMP '2018-01-03 17:25:02.834', NULL, 'http://www.w3.org/2001/XMLSchema#gMonth', 'xs:gMonth', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.816', 1),
(12509, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.886', 'Gregorian specific day in a month. Example: Feb 5 is 02-05.', TIMESTAMP '2018-01-03 17:25:02.886', NULL, 'http://www.w3.org/2001/XMLSchema#gMonthDay', 'xs:gMonthDay', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.86', 1),
(12510, 0, 12478, TIMESTAMP '2018-01-03 17:25:02.939', 'Gregorian calendar year. Example, year 1999, write: 1999.', TIMESTAMP '2018-01-03 17:25:02.939', NULL, 'http://www.w3.org/2001/XMLSchema#gYear', 'xs:gYear', 'DRAFT', TIMESTAMP '2018-01-03 17:25:02.912', 1),
(12511, 0, 12478, TIMESTAMP '2018-01-03 17:25:03.452', 'Specific gregorian month and year. Example, May 1999, write: 1999-05.', TIMESTAMP '2018-01-03 17:25:03.452', NULL, 'http://www.w3.org/2001/XMLSchema#gYearMonth', 'xs:gYearMonth', 'DRAFT', TIMESTAMP '2018-01-03 17:25:03.433', 1),
(12512, 0, 12478, TIMESTAMP '2018-01-03 17:25:03.478', 'QName represents XML qualified names. The value space of QName is the set of tuples {namespace name, local part}, where namespace name is an anyURI and local part is an NCName.', TIMESTAMP '2018-01-03 17:25:03.478', NULL, 'http://www.w3.org/2001/XMLSchema#QName', 'xs:QName', 'DRAFT', TIMESTAMP '2018-01-03 17:25:03.476', 1),
(12513, 0, 12478, TIMESTAMP '2018-01-03 17:25:03.485', 'NOTATION represents the NOTATION attribute type from [XML 1.0 (Second Edition)]. The value space of NOTATION is the set of QNames of notations declared in the current schema.', TIMESTAMP '2018-01-03 17:25:03.485', NULL, 'http://www.w3.org/2001/XMLSchema#NOTATION', 'xs:NOTATION', 'DRAFT', TIMESTAMP '2018-01-03 17:25:03.483', 1),
(12514, 2, NULL, TIMESTAMP '2018-01-03 17:25:03.947', NULL, TIMESTAMP '2018-01-03 17:25:04.01', NULL, NULL, 'Java', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.945', 1),
(12515, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.962', 'java.lang.String', TIMESTAMP '2018-01-03 17:25:04.012', NULL, NULL, 'String', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.961', 1),
(12516, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.97', 'java.lang.Integer', TIMESTAMP '2018-01-03 17:25:04.013', NULL, NULL, 'Integer', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.969', 1),
(12517, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.974', 'java.lang.Double', TIMESTAMP '2018-01-03 17:25:04.015', NULL, NULL, 'Double', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.973', 1),
(12518, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.978', 'java.lang.Boolean', TIMESTAMP '2018-01-03 17:25:04.017', NULL, NULL, 'Boolean', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.978', 1),
(12519, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.984', 'java.util.Date', TIMESTAMP '2018-01-03 17:25:04.019', NULL, NULL, 'Date', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.983', 1);         
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12520, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.989', 'java.sql.Time', TIMESTAMP '2018-01-03 17:25:04.021', NULL, NULL, 'Time', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.988', 1),
(12521, 1, 12514, TIMESTAMP '2018-01-03 17:25:03.996', 'java.util.Currency', TIMESTAMP '2018-01-03 17:25:04.023', NULL, NULL, 'Currency', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:03.995', 1),
(12522, 1, 12514, TIMESTAMP '2018-01-03 17:25:04.0', 'a text field', TIMESTAMP '2018-01-03 17:25:04.025', NULL, NULL, 'Text', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.0', 1),
(12523, 3, NULL, TIMESTAMP '2018-01-03 17:25:04.064', NULL, TIMESTAMP '2018-01-03 17:25:04.742', NULL, NULL, 'Cancer Model', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.063', 1),
(12524, 1, 12523, TIMESTAMP '2018-01-03 17:25:04.078', NULL, TIMESTAMP '2018-01-03 17:25:04.745', NULL, NULL, 'Cancer Models', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.077', 1),
(12525, 2, NULL, TIMESTAMP '2018-01-03 17:25:04.828', 'The International System of Units (SI)', TIMESTAMP '2018-01-03 17:25:04.992', NULL, 'http://www.bipm.org/en/publications/si-brochure/', 'SI', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.826', 1),
(12526, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.842', STRINGDECODE('The 1889 definition of the metre, based on the international prototype of platinum-iridium, was replaced by the 11th CGPM (1960) using a definition based on the wavelength of krypton 86 radiation. This change was adopted in order to improve the accuracy with which the definition of the metre could be realized, the realization being achieved using an interferometer with a travelling microscope to measure the optical path difference as the fringes were counted. In turn, this was replaced in 1983 by the 17th CGPM (1983, Resolution 1) that specified the current definition, as follows:\n\nThe metre is the length of the path travelled by light in vacuum during a time interval of 1/299 792 458 of a second.\n\nIt follows that the speed of light in vacuum is exactly 299 792 458 metres per second, c0 = 299 792 458 m/s.\n\nThe original international prototype of the metre, which was sanctioned by the 1st CGPM in 1889, is still kept at the BIPM under conditions specified in 1889.'), TIMESTAMP '2018-01-03 17:25:04.995', NULL, 'http://www.bipm.org/en/publications/si-brochure/metre.html', 'meter', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.839', 1),
(12527, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.852', STRINGDECODE('The international prototype of the kilogram, an artefact made of platinum-iridium, is kept at the BIPM under the conditions specified by the 1st CGPM in 1889 when it sanctioned the prototype and declared:\n\nThis prototype shall henceforth be considered to be the unit of mass.\nThe 3rd CGPM (1901), in a declaration intended to end the ambiguity in popular usage concerning the use of the word \"weight\", confirmed that:\n\nThe kilogram is the unit of mass; it is equal to the mass of the international prototype of the kilogram.\nThe complete declaration appears here.\n\nIt follows that the mass of the international prototype of the kilogram is always 1 kilogram exactly, m(grand K) = 1 kg. However, due to the inevitable accumulation of contaminants on surfaces, the international prototype is subject to reversible surface contamination that approaches 1 \u00b5g per year in mass. For this reason, the CIPM declared that, pending further research, the reference mass of the international prototype is that immediately after cleaning and washing by a specified method (PV, 1989, 57, 104-105 and PV, 1990, 58, 95-97). The reference mass thus defined is used to calibrate national standards of platinum-iridium alloy (Metrologia, 1994, 31, 317-336).'), TIMESTAMP '2018-01-03 17:25:04.997', NULL, 'http://www.bipm.org/en/publications/si-brochure/kilogram.html', 'kilogram', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.849', 1),
(12528, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.86', STRINGDECODE('The unit of time, the second, was at one time considered to be the fraction 1/86 400 of the mean solar day. The exact definition of \"mean solar day\" was left to the astronomers. However measurements showed that irregularities in the rotation of the Earth made this an unsatisfactory definition. In order to define the unit of time more precisely, the 11th CGPM (1960, Resolution 9) adopted a definition given by the International Astronomical Union based on the tropical year 1900. Experimental work, however, had already shown that an atomic standard of time, based on a transition between two energy levels of an atom or a molecule, could be realized and reproduced much more accurately. Considering that a very precise definition of the unit of time is indispensable for science and technology, the 13th CGPM (1967/68, Resolution 1) replaced the definition of the second by the following:\n\nThe second is the duration of 9 192 631 770 periods of the radiation corresponding to the transition between the two hyperfine levels of the ground state of the caesium 133 atom.\nIt follows that the hyperfine splitting in the ground state of the caesium 133 atom is exactly 9 192 631 770 hertz, nu(hfs Cs) = 9 192 631 770 Hz.\n\nAt its 1997 meeting the CIPM affirmed that:\n\nThis definition refers to a caesium atom at rest at a temperature of 0 K.\nThis note was intended to make it clear that the definition of the SI second is based on a caesium atom unperturbed by black body radiation, that is, in an environment whose thermodynamic temperature is 0 K. The frequencies of all primary frequency standards should therefore be corrected for the shift due to ambient radiation, as stated at the meeting of the Consultative Committee for Time and Frequency in 1999.'), TIMESTAMP '2018-01-03 17:25:04.999', NULL, 'http://www.bipm.org/en/publications/si-brochure/second.html', 'second', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.858', 1);    
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12529, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.871', STRINGDECODE('Electric units, called \"international units\", for current and resistance, were introduced by the International Electrical Congress held in Chicago in 1893, and definitions of the \"international ampere\" and \"international ohm\" were confirmed by the International Conference in London in 1908.\n\nAlthough it was already obvious on the occasion of the 8th CGPM (1933) that there was a unanimous desire to replace those \"international units\" by so-called \"absolute units\", the official decision to abolish them was only taken by the 9th CGPM (1948), which adopted the ampere for the unit of electric current, following a definition proposed by the CIPM (1946, Resolution 2):\n\nThe ampere is that constant current which, if maintained in two straight parallel conductors of infinite length, of negligible circular cross-section, and placed 1 metre apart in vacuum, would produce between these conductors a force equal to 2 x 10\u20137 newton per metre of length.\nIt follows that the magnetic constant, mu0, also known as the permeability of free space, is exactly 4 x 10\u20137 henries per metre, mu0 = 4 x 10\u20137 H/m.\n\nThe expression \"MKS unit of force\" which occurs in the original text of 1946 has been replaced here by \"newton\", a name adopted for this unit by the 9th CGPM (1948, Resolution 7).'), TIMESTAMP '2018-01-03 17:25:05.001', NULL, 'http://www.bipm.org/en/publications/si-brochure/ampere.html', 'ampere', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.868', 1),
(12530, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.88', STRINGDECODE('The kelvin, unit of thermodynamic temperature, is the fraction 1/273.16 of the thermodynamic temperature of the triple point of water.\nIt follows that the thermodynamic temperature of the triple point of water is exactly 273.16 kelvins, Ttpw = 273.16 K.\n\nThe symbol, Ttpw, is used to denote the thermodynamic temperature of the triple point of water.\nAt its 2005 meeting the CIPM affirmed that:\n\nThis definition refers to water having the isotopic composition defined exactly by the following amount of substance ratios: 0.000 155 76 mole of 2H per mole of 1H, 0.000 379 9 mole of 17O per mole of 16O, and 0.002 005 2 mole of 18O per mole of 16O.\nBecause of the manner in which temperature scales used to be defined, it remains common practice to express a thermodynamic temperature, symbol T, in terms of its difference from the reference temperature T0 = 273.15 K, the ice point. This difference is called the Celsius temperature, symbol t, which is defined by the quantity equation:\n\nt = T \u2013 T0.\nThe unit of Celsius temperature is the degree Celsius, symbol \u00b0C, which is by definition equal in magnitude to the kelvin. A difference or interval of temperature may be expressed in kelvins or in degrees Celsius (13th CGPM, 1967/68, Resolution 3, mentioned above), the numerical value of the temperature difference being the same. However, the numerical value of a Celsius temperature expressed in degrees Celsius is related to the numerical value of the thermodynamic temperature expressed in kelvins by the relation\n\nt/\u00b0C = T/K \u2013 273.15.'), TIMESTAMP '2018-01-03 17:25:05.003', NULL, 'http://www.bipm.org/en/publications/si-brochure/kelvin.html', 'kelvin', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.877', 1),
(12531, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.889', STRINGDECODE('The quantity used by chemists to specify the amount of chemical elements or compounds is now called \"amount of substance\". Amount of substance is defined to be proportional to the number of specified elementary entities in a sample, the proportionality constant being a universal constant which is the same for all samples. The unit of amount of substance is called the mole, symbol mol, and the mole is defined by specifying the mass of carbon 12 that constitutes one mole of carbon 12 atoms. By international agreement this was fixed at 0.012 kg, i.e. 12 g.\n\nFollowing proposals by the IUPAP, the IUPAC, and the ISO, the CIPM gave a definition of the mole in 1967 and confirmed it in 1969. This was adopted by the 14th CGPM (1971, Resolution 3):\n\nThe mole is the amount of substance of a system which contains as many elementary entities as there are atoms in 0.012 kilogram of carbon 12; its symbol is \"mol\".\nWhen the mole is used, the elementary entities must be specified and may be atoms, molecules, ions, electrons, other particles, or specified groups of such particles.\nIt follows that the molar mass of carbon 12 is exactly 12 grams per mole, M(12C) = 12 g/mol.\n\nIn 1980 the CIPM approved the report of the CCU (1980) which specified that\n\nIn this definition, it is understood that unbound atoms of carbon 12, at rest and in their ground state, are referred to.\nThe definition of the mole also determines the value of the universal constant that relates the number of entities to amount of substance for any sample. This constant is called the Avogadro constant, symbol NA or L. If N(X) denotes the number of entities X in a specified sample, and if n(X) denotes the amount of substance of entities X in the same sample, the relation is\n\nn(X) = N(X)/NA.\nNote that since N(X) is dimensionless, and n(X) has the SI unit mole, the Avogadro constant has the coherent SI unit reciprocal mole.'), TIMESTAMP '2018-01-03 17:25:05.005', NULL, 'http://www.bipm.org/en/publications/si-brochure/mole.html', 'mole', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.886', 1);   
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12532, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.898', STRINGDECODE('The units of luminous intensity based on flame or incandescent filament standards in use in various countries before 1948 were replaced initially by the \"new candle\" based on the luminance of a Planck radiator (a black body) at the temperature of freezing platinum. This modification had been prepared by the International Commission on Illumination (CIE) and by the CIPM before 1937, and the decision was promulgated by the CIPM in 1946. It was then ratified in 1948 by the 9th CGPM which adopted a new international name for this unit, the candela, symbol cd; in 1967 the 13th CGPM (Resolution 5) gave an amended version of this definition.\n\nIn 1979, because of the difficulties in realizing a Planck radiator at high temperatures, and the new possibilities offered by radiometry, i.e. the measurement of optical radiation power, the 16th CGPM (1979, Resolution 3) adopted a new definition of the candela:\n\nThe candela is the luminous intensity, in a given direction, of a source that emits monochromatic radiation of frequency 540 x 1012 hertz and that has a radiant intensity in that direction of 1/683 watt per steradian.\nIt follows that the spectral luminous efficacy for monochromatic radiation of frequency of 540 x 1012 hertz is exactly 683 lumens per watt, K = 683 lm/W = 683 cd sr/W.\nNote that since N(X) is dimensionless, and n(X) has the SI unit mole, the Avogadro constant has the coherent SI unit reciprocal mole.'), TIMESTAMP '2018-01-03 17:25:05.007', NULL, 'http://www.bipm.org/en/publications/si-brochure/candela.html', 'candela', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.896', 1),
(12533, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.904', 'degrees celsius', TIMESTAMP '2018-01-03 17:25:05.009', NULL, NULL, 'celsius', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.903', 1),
(12534, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.91', 'degrees fahrenheit', TIMESTAMP '2018-01-03 17:25:05.011', NULL, NULL, 'fahrenheit', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.908', 1),
(12535, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.915', 'measurement of force', TIMESTAMP '2018-01-03 17:25:05.014', NULL, NULL, 'newtons', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.913', 1),
(12536, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.92', 'square meter', TIMESTAMP '2018-01-03 17:25:05.016', NULL, NULL, 'area', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.919', 1),
(12537, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.925', 'cubic meter', TIMESTAMP '2018-01-03 17:25:05.018', NULL, NULL, 'volume', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.924', 1),
(12538, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.93', 'meter per second', TIMESTAMP '2018-01-03 17:25:05.02', NULL, NULL, 'speed, velocity', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.929', 1),
(12539, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.935', STRINGDECODE('meter per second squared\u00a0\u00a0'), TIMESTAMP '2018-01-03 17:25:05.022', NULL, NULL, 'acceleration', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.934', 1),
(12540, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.94', 'reciprocal meter', TIMESTAMP '2018-01-03 17:25:05.024', NULL, NULL, 'wave number', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.939', 1),
(12541, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.946', 'kilogram per cubic meter', TIMESTAMP '2018-01-03 17:25:05.027', NULL, NULL, 'mass density', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.944', 1),
(12542, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.951', 'cubic meter per kilogram', TIMESTAMP '2018-01-03 17:25:05.029', NULL, NULL, 'specific volume', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.95', 1),
(12543, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.956', 'ampere per square meter', TIMESTAMP '2018-01-03 17:25:05.031', NULL, NULL, 'current density', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.955', 1),
(12544, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.961', 'ampere per meter', TIMESTAMP '2018-01-03 17:25:05.033', NULL, NULL, STRINGDECODE('magnetic field strength\u00a0\u00a0'), 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.96', 1);               
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12545, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.967', 'mole per cubic meter', TIMESTAMP '2018-01-03 17:25:05.035', NULL, NULL, 'amount-of-substance concentration', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.965', 1),
(12546, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.972', 'candela per square meter', TIMESTAMP '2018-01-03 17:25:05.037', NULL, NULL, 'luminance', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.971', 1),
(12547, 1, 12525, TIMESTAMP '2018-01-03 17:25:04.977', 'kilogram per kilogram', TIMESTAMP '2018-01-03 17:25:05.038', NULL, NULL, 'mass fraction', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:04.976', 1),
(12548, 3, NULL, TIMESTAMP '2018-01-03 17:25:05.065', NULL, TIMESTAMP '2018-01-03 17:25:05.095', NULL, NULL, 'Rare Disease Conditions', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:05.064', 1),
(12549, 1, 12548, TIMESTAMP '2018-01-03 17:25:05.08', NULL, TIMESTAMP '2018-01-03 17:25:05.098', NULL, NULL, 'Rare Disease Conditions and Phenotypes', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:05.079', 1),
(12550, 3, NULL, TIMESTAMP '2018-01-03 17:25:08.187', 'NHIC conceptual domain i.e. value domains used the NHIC project', TIMESTAMP '2018-01-03 17:25:11.436', NULL, NULL, 'NHIC', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.186', 1),
(12551, 2, 12550, TIMESTAMP '2018-01-03 17:25:08.224', NULL, TIMESTAMP '2018-01-03 17:25:11.439', NULL, NULL, 'NHIC Datasets', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.223', 1),
(12552, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.231', NULL, TIMESTAMP '2018-01-03 17:25:11.442', NULL, NULL, 'Ovarian Cancer', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.231', 1),
(12553, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.238', NULL, TIMESTAMP '2018-01-03 17:25:11.445', NULL, NULL, 'CUH', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.237', 1),
(12554, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.243', NULL, TIMESTAMP '2018-01-03 17:25:11.448', NULL, NULL, 'Round 1', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.243', 1),
(12555, 7, 12550, TIMESTAMP '2018-01-03 17:25:08.248', NULL, TIMESTAMP '2018-01-03 17:25:11.451', NULL, NULL, 'MAIN', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.248', 1),
(12556, 7, 12550, TIMESTAMP '2018-01-03 17:25:08.254', NULL, TIMESTAMP '2018-01-03 17:25:11.453', NULL, NULL, 'PATIENT IDENTITY DETAILS', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.254', 1),
(12557, 1, 12550, TIMESTAMP '2018-01-03 17:25:08.262', NULL, TIMESTAMP '2018-01-03 17:25:11.456', NULL, NULL, 'String', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.262', 1),
(12558, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.266', STRINGDECODE('*For linkage purposes NHS NUMBER\nand/or\n LOCAL PATIENT IDENTIFIER\nis required.\n\nThe NHS NUMBER, the primary identifier of a PERSON, is a unique identifier for aPATIENTwithin the NHS in England and Wales. This will not vary by any ORGANISATION of which a PERSON is a PATIENT.'), TIMESTAMP '2018-01-03 17:25:11.459', NULL, NULL, 'NHS NUMBER*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.26', 1),
(12559, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.3', STRINGDECODE('*For linkage purposes NHS NUMBER\nand/or\n LOCAL PATIENT IDENTIFIER\nis required.\n\nThis is a number used to identify a PATIENT uniquely within a Health Care Provider. It may be different from the PATIENT''s casenote number and may be assigned automatically by the computer system.'), TIMESTAMP '2018-01-03 17:25:11.461', NULL, NULL, 'LOCAL PATIENT IDENTIFIER*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.3', 1),
(12560, 1, 12550, TIMESTAMP '2018-01-03 17:25:08.33', NULL, TIMESTAMP '2018-01-03 17:25:11.464', NULL, NULL, 'nhsNumberStatusIndicatorCode', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.329', 1),
(12561, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.333', 'The NHS NUMBER STATUS  INDICATOR CODE indicates the verification status of the NHS number provided.', TIMESTAMP '2018-01-03 17:25:11.466', NULL, NULL, 'nhsNumberStatusIndicatorCode', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.328', 1);             
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12562, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.364', 'The date on which a PERSON was born or is officially deemed to have been born.', TIMESTAMP '2018-01-03 17:25:11.468', NULL, NULL, 'PERSON BIRTH DATE', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.364', 1),
(12563, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.397', STRINGDECODE('ORGANISATION CODE (CODE OF PROVIDER) is the ORGANISATION CODE of the ORGANISATION acting as a Health Care Provider.\n(an6 not applicable to COSD)'), TIMESTAMP '2018-01-03 17:25:11.471', NULL, NULL, 'ORGANISATION CODE (CODE OF PROVIDER)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.397', 1),
(12564, 5, 12550, TIMESTAMP '2018-01-03 17:25:08.425', NULL, TIMESTAMP '2018-01-03 17:25:11.473', NULL, NULL, 'DIAGNOSTIC DETAILS', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.425', 1),
(12565, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.43', 'See DIAGNOSTIC CODING for details on coding and PRIMARY DIAGNOSES for the standardised definition of primary diagnosis.', TIMESTAMP '2018-01-03 17:25:11.475', NULL, NULL, 'PRIMARY DIAGNOSIS (ICD)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.43', 1),
(12566, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.459', STRINGDECODE('*For linkage purposes DATE OF DIAGNOSIS ((CLINICALLY AGREED)\nor\n DATE OF RECURRENCE (CLINICALLY AGREED)\nis required as mandatory.\n\nRecord the date  where TESTcer was confirmed or diagnosis agreed (This will normally be  the date of the authorised  pathology report which confirms the TESTcer or if this is not available at the time it will be the date of the Multidisciplinary Team Meeting when the diagnosis was agreed)\n(This is may not be the same as Date of Diagnosis which is used for TESTcer Registration.)'), TIMESTAMP '2018-01-03 17:25:11.477', NULL, NULL, 'DATE OF DIAGNOSIS (CLINICALLY AGREED)*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.459', 1),
(12567, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.49', STRINGDECODE('*For linkage purposes DATE OF DIAGNOSIS ((CLINICALLY AGREED)\nor\n DATE OF RECURRENCE (CLINICALLY AGREED)\nis required as mandatory.\n\n(Recurrences only.)\nRecord the date where TESTcer recurrence was confirmed or diagnosis of recurrence was agreed (This will normally be the  date of the authorised  pathology report which confirms the recurrence or if this is not available at the time it will be the date of the Multidisciplinary Team Meeting when the diagnosis of recurrence was agreed)\n(This is may not be the same as Date of Recurrence which is used for TESTcer Registration.)'), TIMESTAMP '2018-01-03 17:25:11.48', NULL, NULL, 'DATE OF RECURRENCE (CLINICALLY AGREED)*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.49', 1),
(12568, 11, 12550, TIMESTAMP '2018-01-03 17:25:08.522', NULL, TIMESTAMP '2018-01-03 17:25:11.483', NULL, NULL, 'DEMOGRAPHICS', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.522', 1),
(12569, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.527', 'That part of a PERSON''s name which is used to describe family, clan, tribal group, or marital association.', TIMESTAMP '2018-01-03 17:25:11.486', NULL, NULL, 'PERSON FAMILY NAME', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.526', 1),
(12570, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.556', 'The forename(s) or given name(s) of a PERSON.', TIMESTAMP '2018-01-03 17:25:11.488', NULL, NULL, 'PERSON GIVEN NAME', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.555', 1),
(12571, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.586', 'PATIENT USUAL ADDRESS (AT DIAGNOSIS) is the PATIENT USUAL ADDRESS of the PATIENT at the time of PATIENT DIAGNOSIS.', TIMESTAMP '2018-01-03 17:25:11.491', NULL, NULL, 'PATIENT USUAL ADDRESS (AT DIAGNOSIS)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.586', 1),
(12572, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.618', 'POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS) is the POSTCODE OF USUAL ADDRESS of the PATIENT at the time of PATIENT DIAGNOSIS.', TIMESTAMP '2018-01-03 17:25:11.494', NULL, NULL, 'POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.618', 1);
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12573, 1, 12550, TIMESTAMP '2018-01-03 17:25:08.652', NULL, TIMESTAMP '2018-01-03 17:25:11.497', NULL, NULL, 'personGenderCode(current)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.651', 1),
(12574, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.656', 'A PERSON''s gender currently.', TIMESTAMP '2018-01-03 17:25:11.5', NULL, NULL, 'personGenderCode(current)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.649', 1),
(12575, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.696', STRINGDECODE('GENERAL MEDICAL PRACTITIONER (SPECIFIED) is the code of the GENERAL MEDICAL PRACTITIONER specified by the PATIENT.\n\nThis GENERAL MEDICAL PRACTITIONER works within the General Medical Practitioner Practice with which the PATIENT is registered.'), TIMESTAMP '2018-01-03 17:25:11.503', NULL, NULL, 'GENERAL MEDICAL PRACTITIONER (SPECIFIED)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.695', 1),
(12576, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.725', 'The GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION) is an ORGANISATION CODE. This is the code of the GP Practice that the PATIENT is registered with.', TIMESTAMP '2018-01-03 17:25:11.506', NULL, NULL, 'GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.725', 1),
(12577, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.766', 'The PATIENT''s surname at birth.', TIMESTAMP '2018-01-03 17:25:11.508', NULL, NULL, 'PERSON FAMILY NAME (AT BIRTH)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.765', 1),
(12578, 1, 12550, TIMESTAMP '2018-01-03 17:25:08.802', NULL, TIMESTAMP '2018-01-03 17:25:11.511', NULL, NULL, 'ethnicCategory', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.802', 1),
(12579, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.805', STRINGDECODE('The ethnicity of a PERSON, as specified by the PERSON.. The 16+1 ethnic data categories defined in the 2001 census is the national mandatory standard for the collection and analysis of ethnicity.\n(The Office for National Statistics has developed a further breakdown of the group from that given, which may be used locally.)'), TIMESTAMP '2018-01-03 17:25:11.513', NULL, NULL, 'ethnicCategory', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.8', 1),
(12580, 12, 12550, TIMESTAMP '2018-01-03 17:25:08.839', NULL, TIMESTAMP '2018-01-03 17:25:11.516', NULL, NULL, 'REFERRALS', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.839', 1),
(12581, 1, 12550, TIMESTAMP '2018-01-03 17:25:08.846', NULL, TIMESTAMP '2018-01-03 17:25:11.518', NULL, NULL, 'sourceOfReferralForOutPatients', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.846', 1),
(12582, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.849', STRINGDECODE('This identifies the source of referral of each Consultant Out-Patient Episode.\n(See User Guide for further details)'), TIMESTAMP '2018-01-03 17:25:11.521', NULL, NULL, 'sourceOfReferralForOutPatients', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.844', 1),
(12583, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.884', 'The start date of a REFERRAL TO TREATMENT PERIOD. Date that the initial referral to Secondary care was received for this diagnosis. This may be different from TESTCER REFERRAL TO TREATMENT PERIOD START DATE if initial referral was not to the TESTcer services teams.', TIMESTAMP '2018-01-03 17:25:11.523', NULL, NULL, 'REFERRAL TO TREATMENT PERIOD START DATE', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.884', 1),
(12584, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.918', 'This is the date that the PATIENT is first seen in the Trust that receives the first referral.', TIMESTAMP '2018-01-03 17:25:11.525', NULL, NULL, 'DATE FIRST SEEN', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.918', 1),
(12585, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.948', 'A code uniquely identifying a CONSULTANT. (Referred to CONSULTANT CODE).', TIMESTAMP '2018-01-03 17:25:11.527', NULL, NULL, 'CONSULTANT CODE', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.948', 1),
(12586, 3, 12550, TIMESTAMP '2018-01-03 17:25:08.981', 'A unique code identifying each MAIN SPECIALTY designated by Royal Colleges. This is the same as the OCCUPATION CODES desXXXXXXXibing specialties. (TEST be derived from consultant code).', TIMESTAMP '2018-01-03 17:25:11.529', NULL, NULL, 'CARE PROFESSIONAL MAIN SPECIALTY CODE', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:08.981', 1);     
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12587, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.012', 'The  ORGANISATION SITE CODE of the Health Care Provider at the first contact with the PATIENT.', TIMESTAMP '2018-01-03 17:25:11.531', NULL, NULL, 'ORGANISATION SITE CODE (PROVIDER FIRST SEEN)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.012', 1),
(12588, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.042', 'This is the date that the PATIENT is first seen by the appropriate specialist for TESTcer care within a TESTcer Care Spell. This is the PERSON or PERSONS who are most able to progress the diagnosis of the primary tumour.', TIMESTAMP '2018-01-03 17:25:11.533', NULL, NULL, 'DATE FIRST SEEN (TESTCER SPECIALIST)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.041', 1),
(12589, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.645', 'The  ORGANISATION SITE CODE of the ORGANISATION acting as Health Care Provider where the PATIENT is first seen by an appropriate TESTcer specialist on the DATE FIRST SEEN (TESTCER SPECIALIST).', TIMESTAMP '2018-01-03 17:25:11.535', NULL, NULL, 'ORGANISATION SITE CODE (PROVIDER FIRST TESTCER SPECIALIST)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.645', 1),
(12590, 1, 12550, TIMESTAMP '2018-01-03 17:25:09.684', NULL, TIMESTAMP '2018-01-03 17:25:11.538', NULL, NULL, 'testcerOrSymptomaticBreastReferralPatientStatus', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.683', 1),
(12591, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.688', STRINGDECODE('TESTCER OR SYMPTOMATIC BREAST REFERRAL PATIENT STATUS is recorded to enable tracking of the status of REFERRAL REQUESTS for PATIENTS referred with a suspected TESTcer, or referred with breast symptoms with TESTcer not originally suspected.\nFor COSD this TEST be used for all patients regardless of referral route.'), TIMESTAMP '2018-01-03 17:25:11.54', NULL, NULL, 'testcerOrSymptomaticBreastReferralPatientStatus', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.68', 1),
(12592, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.725', STRINGDECODE('Record the time when the symptoms were first noted related to this diagnosis as agreed between the consultant and the patient.   Depending on the length of time this should normally include at least month and year. Day should also be included if known. If symptoms have been present for a long time then it may only be possible to record the year. This will normally be recorded by the consultant first seeing the patient in secondary care.\n\nIn these circumstances the Format/Length will be:\nDATE (including year, month and day) - CCYY-MM-DD\n    YEAR AND MONTH - YYYY-MM\n    Year only - YYYY.'), TIMESTAMP '2018-01-03 17:25:11.542', NULL, NULL, 'TESTCER SYMPTOMS FIRST NOTED DATE', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.725', 1),
(12593, 9, 12550, TIMESTAMP '2018-01-03 17:25:09.767', NULL, TIMESTAMP '2018-01-03 17:25:11.545', NULL, NULL, 'IMAGING', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.767', 1),
(12594, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.776', 'This is the ORGANISATION SITE CODE of the Organisation  where the imaging took place.', TIMESTAMP '2018-01-03 17:25:11.547', NULL, NULL, 'SITE CODE (OF IMAGING)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.775', 1),
(12595, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.809', 'The DATE the TESTcer Imaging was carried out.', TIMESTAMP '2018-01-03 17:25:11.549', NULL, NULL, 'PROCEDURE DATE (TESTCER IMAGING)', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.809', 1),
(12596, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.839', STRINGDECODE('*IMAGING CODE (NICIP)\nand/or\n (TESTCER IMAGING MODALITY and IMAGING ANATOMICAL SITE and ANATOMICAL SIDE (IMAGING))\nis required.\n\nIMAGING CODE (NICIP) is the National Interim Clinical Imaging Procedure Code Set code which is used to identify both the test modality and body site of the test.'), TIMESTAMP '2018-01-03 17:25:11.551', NULL, NULL, 'IMAGING CODE (NICIP)*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.839', 1),
(12597, 1, 12550, TIMESTAMP '2018-01-03 17:25:09.875', NULL, TIMESTAMP '2018-01-03 17:25:11.553', NULL, NULL, 'testcerImagingModality*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.875', 1);     
INSERT INTO PUBLIC.CATALOGUE_ELEMENT(ID, VERSION, DATA_MODEL_ID, DATE_CREATED, DESCRIPTION, LAST_UPDATED, LATEST_VERSION_ID, MODEL_CATALOGUE_ID, NAME, STATUS, VERSION_CREATED, VERSION_NUMBER) VALUES
(12598, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.88', STRINGDECODE('*IMAGING CODE (NICIP)\nand/or\n (TESTCER IMAGING MODALITY and IMAGING ANATOMICAL SITE and ANATOMICAL SIDE (IMAGING))\nis required.\n\nThe type of imaging procedure used during an Imaging or Radiodiagnostic Event for a TESTcer Care Spell.\nNB: PET STEST also includes PET-CT STEST.'), TIMESTAMP '2018-01-03 17:25:11.556', NULL, NULL, 'testcerImagingModality*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.872', 1),
(12599, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.914', STRINGDECODE('*IMAGING CODE (NICIP)\nand/or\n (TESTCER IMAGING MODALITY and IMAGING ANATOMICAL SITE and ANATOMICAL SIDE (IMAGING))\nis required.\n\nA classification of the part of the body that is the subject of an Imaging Or Radiodiagnostic Event.'), TIMESTAMP '2018-01-03 17:25:11.558', NULL, NULL, 'IMAGING ANATOMICAL SITE*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.914', 1),
(12600, 1, 12550, TIMESTAMP '2018-01-03 17:25:09.947', NULL, TIMESTAMP '2018-01-03 17:25:11.56', NULL, NULL, 'anatomicalSide(imaging)*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.947', 1),
(12601, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.951', STRINGDECODE('*IMAGING CODE (NICIP)\nand/or\n (TESTCER IMAGING MODALITY and IMAGING ANATOMICAL SITE and ANATOMICAL SIDE (IMAGING))\nis required.\n\nThe side of the body that is the subject of an Imaging or Radiodiagnostic Event.'), TIMESTAMP '2018-01-03 17:25:11.562', NULL, NULL, 'anatomicalSide(imaging)*', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.944', 1),
(12602, 1, 12550, TIMESTAMP '2018-01-03 17:25:09.988', NULL, TIMESTAMP '2018-01-03 17:25:11.565', NULL, NULL, 'gender2', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.988', 1),
(12603, 3, 12550, TIMESTAMP '2018-01-03 17:25:09.992', 'gender test stuff', TIMESTAMP '2018-01-03 17:25:11.567', NULL, NULL, 'gender2', 'FINALIZED', TIMESTAMP '2018-01-03 17:25:09.984', 1);   
CREATE INDEX PUBLIC.CTLGELEMENT_LATESTVERSIONID_IDX ON PUBLIC.CATALOGUE_ELEMENT(LATEST_VERSION_ID);            
CREATE INDEX PUBLIC.CTLGELEMENT_MODELCATALOGUEID_IDX ON PUBLIC.CATALOGUE_ELEMENT(MODEL_CATALOGUE_ID);          
CREATE INDEX PUBLIC.CTLGELEMENT_NAME_IDX ON PUBLIC.CATALOGUE_ELEMENT(NAME);    
CREATE MEMORY TABLE PUBLIC.COLUMN_TRANSFORMATION_DEFINITION(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_D2222819_E18D_4DE8_A51B_6FF4F1051220) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_D2222819_E18D_4DE8_A51B_6FF4F1051220,
    VERSION BIGINT NOT NULL,
    DESTINATION_ID BIGINT,
    HEADER VARCHAR(255),
    SOURCE_ID BIGINT NOT NULL,
    TRANSFORMATION_ID BIGINT NOT NULL,
    COLUMN_DEFINITIONS_IDX INTEGER
);    
ALTER TABLE PUBLIC.COLUMN_TRANSFORMATION_DEFINITION ADD CONSTRAINT PUBLIC.CONSTRAINT_D PRIMARY KEY(ID);        
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.COLUMN_TRANSFORMATION_DEFINITION;         
CREATE MEMORY TABLE PUBLIC.CSV_TRANSFORMATION(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_1F290E42_6C97_4A53_B1A2_266886AC63E0) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_1F290E42_6C97_4A53_B1A2_266886AC63E0,
    VERSION BIGINT NOT NULL,
    DATE_CREATED TIMESTAMP NOT NULL,
    DESCRIPTION VARCHAR(255),
    LAST_UPDATED TIMESTAMP NOT NULL,
    NAME VARCHAR(255) NOT NULL
);        
ALTER TABLE PUBLIC.CSV_TRANSFORMATION ADD CONSTRAINT PUBLIC.CONSTRAINT_D3 PRIMARY KEY(ID);     
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.CSV_TRANSFORMATION;       
CREATE MEMORY TABLE PUBLIC.DATA_CLASS(
    ID BIGINT NOT NULL
);               
ALTER TABLE PUBLIC.DATA_CLASS ADD CONSTRAINT PUBLIC.CONSTRAINT_B3 PRIMARY KEY(ID);             
-- 12 +/- SELECT COUNT(*) FROM PUBLIC.DATA_CLASS;              
INSERT INTO PUBLIC.DATA_CLASS(ID) VALUES
(12524),
(12549),
(12551),
(12552),
(12553),
(12554),
(12555),
(12556),
(12564),
(12568),
(12580),
(12593);           
CREATE MEMORY TABLE PUBLIC.DATA_ELEMENT(
    ID BIGINT NOT NULL,
    DATA_TYPE_ID BIGINT
);    
ALTER TABLE PUBLIC.DATA_ELEMENT ADD CONSTRAINT PUBLIC.CONSTRAINT_F PRIMARY KEY(ID);            
-- 34 +/- SELECT COUNT(*) FROM PUBLIC.DATA_ELEMENT;            
INSERT INTO PUBLIC.DATA_ELEMENT(ID, DATA_TYPE_ID) VALUES
(12558, 12557),
(12559, 12557),
(12561, 12560),
(12562, 12557),
(12563, 12557),
(12565, 12557),
(12566, 12557),
(12567, 12557),
(12569, 12557),
(12570, 12557),
(12571, 12557),
(12572, 12557),
(12574, 12573),
(12575, 12557),
(12576, 12557),
(12577, 12557),
(12579, 12578),
(12582, 12581),
(12583, 12557),
(12584, 12557),
(12585, 12557),
(12586, 12557),
(12587, 12557),
(12588, 12557),
(12589, 12557),
(12591, 12590),
(12592, 12557),
(12594, 12557),
(12595, 12557),
(12596, 12557),
(12598, 12597),
(12599, 12557),
(12601, 12600),
(12603, 12602);       
CREATE MEMORY TABLE PUBLIC.DATA_MODEL(
    ID BIGINT NOT NULL,
    REVISION_NOTES VARCHAR(2000),
    SEMANTIC_VERSION VARCHAR(20)
);           
ALTER TABLE PUBLIC.DATA_MODEL ADD CONSTRAINT PUBLIC.CONSTRAINT_B3B PRIMARY KEY(ID);            
-- 6 +/- SELECT COUNT(*) FROM PUBLIC.DATA_MODEL;               
INSERT INTO PUBLIC.DATA_MODEL(ID, REVISION_NOTES, SEMANTIC_VERSION) VALUES
(12478, NULL, '0.0.1'),
(12514, NULL, '0.0.1'),
(12523, NULL, '0.0.1'),
(12525, NULL, '0.0.1'),
(12548, NULL, '0.0.1'),
(12550, NULL, '0.0.1');     
CREATE MEMORY TABLE PUBLIC.DATA_MODEL_DATA_MODEL_POLICY(
    DATA_MODEL_POLICIES_ID BIGINT,
    DATA_MODEL_POLICY_ID BIGINT
); 
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.DATA_MODEL_DATA_MODEL_POLICY;             
CREATE MEMORY TABLE PUBLIC.DATA_MODEL_POLICY(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_AB101B45_C113_477C_864A_0A77F1AA9C42) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_AB101B45_C113_477C_864A_0A77F1AA9C42,
    VERSION BIGINT NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    POLICY_TEXT VARCHAR(10000) NOT NULL
);        
ALTER TABLE PUBLIC.DATA_MODEL_POLICY ADD CONSTRAINT PUBLIC.CONSTRAINT_2D PRIMARY KEY(ID);      
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.DATA_MODEL_POLICY;        
CREATE MEMORY TABLE PUBLIC.DATA_TYPE(
    ID BIGINT NOT NULL,
    RULE VARCHAR(10000)
);       
ALTER TABLE PUBLIC.DATA_TYPE ADD CONSTRAINT PUBLIC.CONSTRAINT_92 PRIMARY KEY(ID);              
-- 52 +/- SELECT COUNT(*) FROM PUBLIC.DATA_TYPE;               
INSERT INTO PUBLIC.DATA_TYPE(ID, RULE) VALUES
(12479, NULL),
(12480, 'x ==~ /[a-zA-Z0-9=]*/'),
(12481, 'x ==~ /[a-fA-F0-9]*/'),
(12482, 'is URI'),
(12483, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\ntrue && (x = parseString(string(x)))')),
(12484, '!(x =~ /[\r\n\t]/)'),
(12485, '!(x =~ /\s+/)'),
(12486, 'maxLength(2) && new Locale(x)'),
(12487, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseDecimal(string(x)) in BigDecimal')),
(12488, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseDouble(string(x)) in Double')),
(12489, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseFloat(string(x)) in Float')),
(12490, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseInteger(string(x)) in BigInteger')),
(12491, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseLong(string(x)) in Long')),
(12492, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseInt(string(x)) in Integer')),
(12493, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseShort(string(x)) in Short')),
(12494, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseByte(string(x)) in Byte')),
(12495, 'minInclusive(0)'),
(12496, 'maxInclusive(0)'),
(12497, 'maxExclusive(0)'),
(12498, 'minExclusive(0)'),
(12499, 'minInclusive(0) && maxInclusive(18446744073709551615)'),
(12500, 'minInclusive(0) && maxInclusive(4294967295)'),
(12501, 'minInclusive(0) && maxInclusive(65535)'),
(12502, 'minInclusive(0) && maxInclusive(255)'),
(12503, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseDateTime(string(x)) in Calendar')),
(12504, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseDateTime(string(x)) in Calendar')),
(12505, STRINGDECODE('import static javax.xml.bind.DatatypeConverter.*\n\nparseTime(string(x)) in Calendar')),
(12506, 'x ==~ /-?P\d+Y(\d+M(\d+D(T\d+H(\d+M(\d+S)?)?)?)?)?/'),
(12507, 'date("dd") in Date'),
(12508, 'date("MM") in Date'),
(12509, 'date("MM-dd") in Date'),
(12510, 'date("yyyy") in Date'),
(12511, 'date("yyyy-MM") in Date'),
(12512, NULL),
(12513, NULL),
(12515, NULL),
(12516, NULL),
(12517, NULL),
(12518, NULL),
(12519, NULL),
(12520, NULL),
(12521, NULL),
(12522, NULL),
(12557, NULL),
(12560, NULL),
(12573, NULL),
(12578, NULL),
(12581, NULL),
(12590, NULL),
(12597, NULL),
(12600, NULL),
(12602, NULL);              
CREATE MEMORY TABLE PUBLIC.ENUMERATED_TYPE(
    ID BIGINT NOT NULL,
    ENUM_AS_STRING LONGVARCHAR
);          
ALTER TABLE PUBLIC.ENUMERATED_TYPE ADD CONSTRAINT PUBLIC.CONSTRAINT_63 PRIMARY KEY(ID);        
-- 8 +/- SELECT COUNT(*) FROM PUBLIC.ENUMERATED_TYPE;          
INSERT INTO PUBLIC.ENUMERATED_TYPE(ID, ENUM_AS_STRING) VALUES
(12560, '{"type":"orderedMap","values":[{"id":1,"key":"01","value":"Number present and verified","deprecated":false},{"id":2,"key":"02","value":"Number present but not traced","deprecated":false},{"id":3,"key":"03","value":"Trace required","deprecated":false},{"id":4,"key":"04","value":"Trace attempted - No match or multiple match found","deprecated":false},{"id":5,"key":"05","value":"Trace needs to be resolved - (NHS Number or patient detail conflict)","deprecated":false},{"id":6,"key":"06","value":"Trace in progress","deprecated":false},{"id":7,"key":"07","value":"Number not present and trace not required","deprecated":false},{"id":8,"key":"08","value":"Trace postponed (baby under six weeks old)","deprecated":false}]}'),
(12573, '{"type":"orderedMap","values":[{"id":1,"key":"0","value":"Not Known","deprecated":false},{"id":2,"key":"1","value":"Male","deprecated":false},{"id":3,"key":"2","value":"Female","deprecated":false},{"id":4,"key":"9","value":"Not Specified","deprecated":false}]}'),
(12578, '{"type":"orderedMap","values":[{"id":1,"key":"99","value":"Not Known","deprecated":false},{"id":2,"key":"A","value":"(White) British","deprecated":false},{"id":3,"key":"B","value":"(White) Irish","deprecated":false},{"id":4,"key":"C","value":"Any other White background","deprecated":false},{"id":5,"key":"D","value":"White and Black Caribbean","deprecated":false},{"id":6,"key":"E","value":"White and Black AfriTEST","deprecated":false},{"id":7,"key":"F","value":"White and Asian","deprecated":false},{"id":8,"key":"G","value":"Any other mixed background","deprecated":false},{"id":9,"key":"H","value":"Indian","deprecated":false},{"id":10,"key":"J","value":"Pakistani","deprecated":false},{"id":11,"key":"K","value":"Bangladeshi","deprecated":false},{"id":12,"key":"L","value":"Any other Asian background","deprecated":false},{"id":13,"key":"M","value":"Caribbean","deprecated":false},{"id":14,"key":"N","value":"AfriTEST","deprecated":false},{"id":15,"key":"P","value":"Any other Black background","deprecated":false},{"id":16,"key":"R","value":"Chinese","deprecated":false},{"id":17,"key":"S","value":"Any other ethnic group","deprecated":false},{"id":18,"key":"Z","value":"Not stated","deprecated":false}]}'),
(12581, '{"type":"orderedMap","values":[{"id":1,"key":"11","value":"other - initiated by the CONSULTANT responsible for the Consultant Out-Patient Episode","deprecated":false},{"id":2,"key":"01","value":"following an emergency admission","deprecated":false},{"id":3,"key":"12","value":"referral from a GENERAL PRACTITIONER with a Special Interest (GPwSI) or dentist with a Special Interest (DwSI)","deprecated":false},{"id":4,"key":"02","value":"following a Domiciliary Consultation","deprecated":false},{"id":5,"key":"13","value":"referral from a Specialist NURSE (Secondary Care)","deprecated":false},{"id":6,"key":"03","value":"referral from a GENERAL MEDICAL PRACTITIONER","deprecated":false},{"id":7,"key":"14","value":"referral from an Allied Health Professional","deprecated":false},{"id":8,"key":"04","value":"referral from an Accident And Emergency Department (including Minor Injuries Units and Walk In Centres)","deprecated":false},{"id":9,"key":"15","value":"referral from an OPTOMETRIST","deprecated":false},{"id":10,"key":"05","value":"referral from a CONSULTANT, other than in an Accident And Emergency Department","deprecated":false},{"id":11,"key":"16","value":"referral from an Orthoptist","deprecated":false},{"id":12,"key":"06","value":"self-referral","deprecated":false},{"id":13,"key":"17","value":"referral from a National SXXXXXXXeening Programme","deprecated":false},{"id":14,"key":"07","value":"referral from a Prosthetist","deprecated":false},{"id":15,"key":"92","value":"referral from a GENERAL DENTAL PRACTITIONER","deprecated":false},{"id":16,"key":"93","value":"referral from a Community Dental Service","deprecated":false},{"id":17,"key":"97","value":"other - not initiated by the CONSULTANT responsible for the Consultant Out-Patient Episode","deprecated":false},{"id":18,"key":"10","value":"following an Accident And Emergency Attendance (including Minor Injuries Units and Walk In Centres)","deprecated":false}]}');             
INSERT INTO PUBLIC.ENUMERATED_TYPE(ID, ENUM_AS_STRING) VALUES
(12590, '{"type":"orderedMap","values":[{"id":1,"key":"11","value":"Diagnosis of new TESTcer confirmed - English NHS first treatment planned","deprecated":false},{"id":2,"key":"12","value":"Diagnosis of new TESTcer confirmed - subsequent treatment not yet planned","deprecated":false},{"id":3,"key":"13","value":"Diagnosis of new TESTcer confirmed - subsequent English NHS treatment planned","deprecated":false},{"id":4,"key":"14","value":"Suspected primary TESTcer","deprecated":false},{"id":5,"key":"03","value":"No new TESTcer diagnosis identified by the Healthcare Provider","deprecated":false},{"id":6,"key":"15","value":"Suspected recurrent TESTcer","deprecated":false},{"id":7,"key":"16","value":"Diagnosis of recurrent TESTcer confirmed - first treatment not yet planned","deprecated":false},{"id":8,"key":"17","value":"Diagnosis of recurrent TESTcer confirmed - English NHS first treatment planned","deprecated":false},{"id":9,"key":"07","value":"Diagnosis of TESTcer confirmed - no English NHS treatment planned","deprecated":false},{"id":10,"key":"18","value":"Diagnosis of recurrent TESTcer confirmed - no English NHS treatment planned","deprecated":false},{"id":11,"key":"08","value":"First treatment commenced (English NHS only)","deprecated":false},{"id":12,"key":"19","value":"Diagnosis of recurrent TESTcer confirmed - subsequent treatment not yet planned","deprecated":false},{"id":13,"key":"09","value":"Under investigation following symptomatic referral, TESTcer not suspected (breast referrals only) (see note 1)","deprecated":false},{"id":14,"key":"20","value":"Diagnosis of recurrent TESTcer confirmed - subsequent English NHS treatment planned","deprecated":false},{"id":15,"key":"10","value":"Diagnosis of new TESTcer confirmed - first treatment not yet planned","deprecated":false},{"id":16,"key":"21","value":"Subsequent treatment commenced (English NHS only)","deprecated":false}]}'),
(12597, '{"type":"orderedMap","values":[{"id":1,"key":"C02C","value":"Virtual colonoscopy","deprecated":false},{"id":2,"key":"C08B","value":"Barium","deprecated":false},{"id":3,"key":"C08A","value":"Angiography","deprecated":false},{"id":4,"key":"CXXX","value":"Other","deprecated":false},{"id":5,"key":"C01X","value":"Standard Radiography","deprecated":false},{"id":6,"key":"C02X","value":"CT STEST","deprecated":false},{"id":7,"key":"C03X","value":"MRI STEST","deprecated":false},{"id":8,"key":"C04X","value":"PET STEST","deprecated":false},{"id":9,"key":"C05X","value":"Ultrasound STEST","deprecated":false},{"id":10,"key":"C06X","value":"Nuclear Medicine imaging","deprecated":false},{"id":11,"key":"C09X","value":"Intervention radiography.","deprecated":false},{"id":12,"key":"C08U","value":"Urography (IV and retrograde)","deprecated":false},{"id":13,"key":"C01M","value":"Mammogram","deprecated":false}]}'),
(12600, '{"type":"orderedMap","values":[{"id":1,"key":"R","value":"Right","deprecated":false},{"id":2,"key":"B","value":"Bilateral","deprecated":false},{"id":3,"key":"8","value":"Not applicable","deprecated":false},{"id":4,"key":"9","value":"Not Known","deprecated":false},{"id":5,"key":"L","value":"Left","deprecated":false},{"id":6,"key":"M","value":"Midline","deprecated":false}]}'),
(12602, '{"type":"orderedMap","values":[{"id":1,"key":"0","value":"Not Known","deprecated":false},{"id":2,"key":"1","value":"Male","deprecated":false},{"id":3,"key":"2","value":"Female","deprecated":false},{"id":4,"key":"9","value":"Not Specified","deprecated":false}]}');  
CREATE MEMORY TABLE PUBLIC.EXTENSION_VALUE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_986BF911_1ED6_48FD_8111_65FBF1F31C4B) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_986BF911_1ED6_48FD_8111_65FBF1F31C4B,
    VERSION BIGINT NOT NULL,
    ELEMENT_ID BIGINT NOT NULL,
    EXTENSION_VALUE VARCHAR(10000),
    NAME VARCHAR(255) NOT NULL,
    ORDER_INDEX BIGINT NOT NULL
);              
ALTER TABLE PUBLIC.EXTENSION_VALUE ADD CONSTRAINT PUBLIC.CONSTRAINT_DE PRIMARY KEY(ID);        
-- 453 +/- SELECT COUNT(*) FROM PUBLIC.EXTENSION_VALUE;        
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(8944, 0, 12523, 'true', 'http://www.modelcatalogue.org/metadata/genomics/#cancer-types-export', 1515000304072),
(8945, 0, 12523, 'true', 'http://www.modelcatalogue.org/metadata/genomics/#all-cancer-reports', 1515000304074),
(8946, 0, 12548, 'true', 'http://www.modelcatalogue.org/metadata/genomics/#all-rd-reports', 1515000305075),
(8947, 0, 12548, 'true', 'http://www.modelcatalogue.org/metadata/genomics/#rare-disease-reports', 1515000305076),
(8948, 0, 12550, 'Matous Kucera', 'http://www.modelcatalogue.org/metadata/#authors', 1515000308210),
(8949, 0, 12550, 'Adam Milward', 'http://www.modelcatalogue.org/metadata/#reviewers', 1515000308212),
(8950, 0, 12550, 'Vladimir Orany', 'http://www.modelcatalogue.org/metadata/#owner', 1515000308214),
(8951, 0, 12550, '2016-03-30T11:46:30Z', 'http://www.modelcatalogue.org/metadata/#reviewed', 1515000308215),
(8952, 0, 12550, '2016-03-30T11:46:30Z', 'http://www.modelcatalogue.org/metadata/#approved', 1515000308216),
(8953, 0, 12550, 'Global Namespace', 'http://www.modelcatalogue.org/metadata/#namespace', 1515000308217),
(8954, 0, 12550, 'Metadata Consulting', 'http://www.modelcatalogue.org/metadata/#organization', 1515000308218),
(8955, 0, 12558, 'TEST_1', 'NHIC_Identifier:', 1515000308270),
(8956, 0, 12558, 'NHS NUMBER', 'Link_to_existing definition:', 1515000308273),
(8957, 0, 12558, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308276),
(8958, 0, 12558, 'XXXXXXX0010', '[Optional]_Local_Identifier', 1515000308278),
(8959, 0, 12558, '2', 'A', 1515000308281),
(8960, 0, 12558, '2', 'B', 1515000308283),
(8961, 0, 12558, '3', 'C', 1515000308285),
(8962, 0, 12558, '1', 'D', 1515000308286),
(8963, 0, 12558, NULL, 'E', 1515000308288),
(8964, 0, 12558, 'When Patient demographics entered.', 'F', 1515000308290),
(8965, 0, 12558, 'GC 123', 'G', 1515000308292),
(8966, 0, 12558, 'NEO4J', 'H', 1515000308294),
(8967, 0, 12558, 'from source', 'E2', 1515000308295),
(8968, 0, 12559, 'TEST_2', 'NHIC_Identifier:', 1515000308302),
(8969, 0, 12559, 'LOCAL PATIENT IDENTIFIER', 'Link_to_existing definition:', 1515000308304),
(8970, 0, 12559, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308306),
(8971, 0, 12559, 'XXXXXXX0020', '[Optional]_Local_Identifier', 1515000308308),
(8972, 0, 12559, '2', 'A', 1515000308309),
(8973, 0, 12559, '2', 'B', 1515000308311),
(8974, 0, 12559, '3', 'C', 1515000308313),
(8975, 0, 12559, '1', 'D', 1515000308315),
(8976, 0, 12559, 'hard', 'E', 1515000308317),
(8977, 0, 12559, 'When Patient demographics entered.', 'F', 1515000308319),
(8978, 0, 12559, 'GC 124', 'G', 1515000308320),
(8979, 0, 12559, 'NEO4J', 'H', 1515000308322),
(8980, 0, 12559, 'from source', 'E2', 1515000308324),
(8981, 0, 12561, 'TEST_3', 'NHIC_Identifier:', 1515000308336),
(8982, 0, 12561, 'NHS NUMBER STATUS INDICATOR CODE', 'Link_to_existing definition:', 1515000308338),
(8983, 0, 12561, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308340);            
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(8984, 0, 12561, 'XXXXXXX1350', '[Optional]_Local_Identifier', 1515000308342),
(8985, 0, 12561, '2', 'A', 1515000308344),
(8986, 0, 12561, '2', 'B', 1515000308346),
(8987, 0, 12561, '1', 'C', 1515000308347),
(8988, 0, 12561, '1', 'D', 1515000308349),
(8989, 0, 12561, '1', 'E', 1515000308351),
(8990, 0, 12561, 'When Patient demographics entered.', 'F', 1515000308353),
(8991, 0, 12561, 'GC 125', 'G', 1515000308355),
(8992, 0, 12561, 'NEO4J', 'H', 1515000308357),
(8993, 0, 12561, 'from source', 'E2', 1515000308359),
(8994, 0, 12562, 'TEST_4', 'NHIC_Identifier:', 1515000308368),
(8995, 0, 12562, 'PERSON BIRTH DATE', 'Link_to_existing definition:', 1515000308371),
(8996, 0, 12562, NULL, 'Notes_from_GD_JCIS', 1515000308373),
(8997, 0, 12562, 'XXXXXXX0100', '[Optional]_Local_Identifier', 1515000308375),
(8998, 0, 12562, '2', 'A', 1515000308377),
(8999, 0, 12562, '2', 'B', 1515000308379),
(9000, 0, 12562, '1', 'C', 1515000308382),
(9001, 0, 12562, '1', 'D', 1515000308384),
(9002, 0, 12562, '1', 'E', 1515000308385),
(9003, 0, 12562, 'When Patient demographics entered.', 'F', 1515000308387),
(9004, 0, 12562, 'GC 126', 'G', 1515000308389),
(9005, 0, 12562, 'NEO4J', 'H', 1515000308391),
(9006, 0, 12562, 'from source', 'E2', 1515000308393),
(9007, 0, 12563, 'TEST_5', 'NHIC_Identifier:', 1515000308400),
(9008, 0, 12563, 'ORGANISATION CODE (CODE OF PROVIDER)', 'Link_to_existing definition:', 1515000308402),
(9009, 0, 12563, NULL, 'Notes_from_GD_JCIS', 1515000308403),
(9010, 0, 12563, 'XXXXXXX0030', '[Optional]_Local_Identifier', 1515000308405),
(9011, 0, 12563, '2', 'A', 1515000308407),
(9012, 0, 12563, '1', 'B', 1515000308409),
(9013, 0, 12563, '1', 'C', 1515000308410),
(9014, 0, 12563, '1', 'D', 1515000308412),
(9015, 0, 12563, '1', 'E', 1515000308414),
(9016, 0, 12563, 'When referral details entered.', 'F', 1515000308416),
(9017, 0, 12563, 'GC 127', 'G', 1515000308418),
(9018, 0, 12563, 'NEO4J', 'H', 1515000308419),
(9019, 0, 12563, 'from source', 'E2', 1515000308421),
(9020, 0, 12565, 'TEST_6', 'NHIC_Identifier:', 1515000308433),
(9021, 0, 12565, 'PRIMARY DIAGNOSIS (ICD)', 'Link_to_existing definition:', 1515000308435),
(9022, 0, 12565, NULL, 'Notes_from_GD_JCIS', 1515000308436),
(9023, 0, 12565, 'XXXXXXX0370', '[Optional]_Local_Identifier', 1515000308438),
(9024, 0, 12565, '1', 'A', 1515000308440),
(9025, 0, 12565, '1', 'B', 1515000308442),
(9026, 0, 12565, '1', 'C', 1515000308444),
(9027, 0, 12565, '1', 'D', 1515000308445),
(9028, 0, 12565, '1', 'E', 1515000308447),
(9029, 0, 12565, 'MDT Meeting', 'F', 1515000308449),
(9030, 0, 12565, 'GC 128', 'G', 1515000308451),
(9031, 0, 12565, 'NEO4J', 'H', 1515000308453),
(9032, 0, 12565, 'from source', 'E2', 1515000308455),
(9033, 0, 12566, 'TEST_7', 'NHIC_Identifier:', 1515000308462),
(9034, 0, 12566, 'DATE OF DIAGNOSIS (TESTCER CLINICALLY AGREED)', 'Link_to_existing definition:', 1515000308463),
(9035, 0, 12566, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308465),
(9036, 0, 12566, 'XXXXXXX2030', '[Optional]_Local_Identifier', 1515000308467),
(9037, 0, 12566, '2', 'A', 1515000308469),
(9038, 0, 12566, '2', 'B', 1515000308471),
(9039, 0, 12566, '1', 'C', 1515000308473),
(9040, 0, 12566, '1', 'D', 1515000308474),
(9041, 0, 12566, '1', 'E', 1515000308476),
(9042, 0, 12566, 'MDT Meeting - From MDT against which definitive diagnosis recorded, or final pre-treatment diagnosis recorded.', 'F', 1515000308478),
(9043, 0, 12566, 'GC 129', 'G', 1515000308480),
(9044, 0, 12566, 'NEO4J', 'H', 1515000308482);  
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9045, 0, 12566, 'from source', 'E2', 1515000308485),
(9046, 0, 12567, 'TEST_8', 'NHIC_Identifier:', 1515000308494),
(9047, 0, 12567, 'DATE OF RECURRENCE (TESTCER CLINICALLY AGREED)', 'Link_to_existing definition:', 1515000308497),
(9048, 0, 12567, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308499),
(9049, 0, 12567, 'XXXXXXX0440', '[Optional]_Local_Identifier', 1515000308501),
(9050, 0, 12567, '2', 'A', 1515000308503),
(9051, 0, 12567, '2', 'B', 1515000308505),
(9052, 0, 12567, '1', 'C', 1515000308506),
(9053, 0, 12567, '3', 'D', 1515000308508),
(9054, 0, 12567, 'hard', 'E', 1515000308510),
(9055, 0, 12567, 'MDT Meeting - From MDT against which definitive diagnosis recorded, or final pre-treatment diagnosis recorded. Recurrence radio button needs to be selected on MDT meeting / Diagnosis / Diagnosis tab or MDT D&S tab', 'F', 1515000308512),
(9056, 0, 12567, 'GC 130', 'G', 1515000308514),
(9057, 0, 12567, 'NEO4J', 'H', 1515000308515),
(9058, 0, 12567, 'from source', 'E2', 1515000308517),
(9059, 0, 12569, 'TEST_9', 'NHIC_Identifier:', 1515000308529),
(9060, 0, 12569, 'PERSON FAMILY NAME', 'Link_to_existing definition:', 1515000308531),
(9061, 0, 12569, NULL, 'Notes_from_GD_JCIS', 1515000308533),
(9062, 0, 12569, 'XXXXXXX0050', '[Optional]_Local_Identifier', 1515000308535),
(9063, 0, 12569, '2', 'A', 1515000308536),
(9064, 0, 12569, '2', 'B', 1515000308538),
(9065, 0, 12569, '1', 'C', 1515000308540),
(9066, 0, 12569, '1', 'D', 1515000308542),
(9067, 0, 12569, '1', 'E', 1515000308544),
(9068, 0, 12569, 'When Patient demographics entered.', 'F', 1515000308545),
(9069, 0, 12569, 'GC 131', 'G', 1515000308547),
(9070, 0, 12569, 'NEO4J', 'H', 1515000308549),
(9071, 0, 12569, 'from source', 'E2', 1515000308551),
(9072, 0, 12570, 'TEST_10', 'NHIC_Identifier:', 1515000308558),
(9073, 0, 12570, 'PERSON GIVEN NAME', 'Link_to_existing definition:', 1515000308560),
(9074, 0, 12570, NULL, 'Notes_from_GD_JCIS', 1515000308562),
(9075, 0, 12570, 'XXXXXXX0060', '[Optional]_Local_Identifier', 1515000308564),
(9076, 0, 12570, '2', 'A', 1515000308565),
(9077, 0, 12570, '2', 'B', 1515000308567),
(9078, 0, 12570, '1', 'C', 1515000308569),
(9079, 0, 12570, '1', 'D', 1515000308571),
(9080, 0, 12570, '1', 'E', 1515000308573),
(9081, 0, 12570, 'When Patient demographics entered.', 'F', 1515000308575),
(9082, 0, 12570, 'GC 132', 'G', 1515000308576),
(9083, 0, 12570, 'NEO4J', 'H', 1515000308578),
(9084, 0, 12570, 'from source', 'E2', 1515000308580),
(9085, 0, 12571, 'TEST_11', 'NHIC_Identifier:', 1515000308589),
(9086, 0, 12571, 'PATIENT USUAL ADDRESS (AT DIAGNOSIS)', 'Link_to_existing definition:', 1515000308592),
(9087, 0, 12571, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308594),
(9088, 0, 12571, 'XXXXXXX0070', '[Optional]_Local_Identifier', 1515000308597),
(9089, 0, 12571, '2', 'A', 1515000308599),
(9090, 0, 12571, '2', 'B', 1515000308601),
(9091, 0, 12571, '1', 'C', 1515000308602);     
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9092, 0, 12571, '1', 'D', 1515000308604),
(9093, 0, 12571, '1', 'E', 1515000308606),
(9094, 0, 12571, 'When Patient demographics entered.', 'F', 1515000308608),
(9095, 0, 12571, 'GC 133', 'G', 1515000308610),
(9096, 0, 12571, 'NEO4J', 'H', 1515000308612),
(9097, 0, 12571, 'from source', 'E2', 1515000308614),
(9098, 0, 12572, 'TEST_12', 'NHIC_Identifier:', 1515000308621),
(9099, 0, 12572, 'POSTCODE OF USUAL ADDRESS (AT DIAGNOSIS)', 'Link_to_existing definition:', 1515000308623),
(9100, 0, 12572, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308625),
(9101, 0, 12572, 'XXXXXXX0080', '[Optional]_Local_Identifier', 1515000308626),
(9102, 0, 12572, '2', 'A', 1515000308628),
(9103, 0, 12572, '2', 'B', 1515000308630),
(9104, 0, 12572, '3', 'C', 1515000308632),
(9105, 0, 12572, '1', 'D', 1515000308634),
(9106, 0, 12572, '1', 'E', 1515000308636),
(9107, 0, 12572, 'When Patient demographics entered.', 'F', 1515000308638),
(9108, 0, 12572, 'GC 134', 'G', 1515000308640),
(9109, 0, 12572, 'NEO4J', 'H', 1515000308642),
(9110, 0, 12572, 'from source', 'E2', 1515000308644),
(9111, 0, 12574, 'TEST_13', 'NHIC_Identifier:', 1515000308660),
(9112, 0, 12574, 'PERSON GENDER CODE CURRENT', 'Link_to_existing definition:', 1515000308663),
(9113, 0, 12574, NULL, 'Notes_from_GD_JCIS', 1515000308666),
(9114, 0, 12574, 'XXXXXXX0090', '[Optional]_Local_Identifier', 1515000308668),
(9115, 0, 12574, '1', 'A', 1515000308671),
(9116, 0, 12574, '1', 'B', 1515000308673),
(9117, 0, 12574, '1', 'C', 1515000308676),
(9118, 0, 12574, '1', 'D', 1515000308679),
(9119, 0, 12574, '1', 'E', 1515000308681),
(9120, 0, 12574, 'When Patient demographics entered.', 'F', 1515000308684),
(9121, 0, 12574, 'GC 135', 'G', 1515000308686),
(9122, 0, 12574, 'NEO4J', 'H', 1515000308689),
(9123, 0, 12574, 'from source', 'E2', 1515000308691),
(9124, 0, 12575, 'TEST_14', 'NHIC_Identifier:', 1515000308698),
(9125, 0, 12575, 'GENERAL MEDICAL PRACTITIONER (SPECIFIED)', 'Link_to_existing definition:', 1515000308700),
(9126, 0, 12575, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308702),
(9127, 0, 12575, 'XXXXXXX0110', '[Optional]_Local_Identifier', 1515000308704),
(9128, 0, 12575, '2', 'A', 1515000308706),
(9129, 0, 12575, '2', 'B', 1515000308707),
(9130, 0, 12575, '1', 'C', 1515000308709),
(9131, 0, 12575, '1', 'D', 1515000308711),
(9132, 0, 12575, '1', 'E', 1515000308713),
(9133, 0, 12575, 'When Patient demographics entered. Name shown. Code available in JCIS tables.', 'F', 1515000308715),
(9134, 0, 12575, 'GC 136', 'G', 1515000308716),
(9135, 0, 12575, 'NEO4J', 'H', 1515000308718),
(9136, 0, 12575, 'from source', 'E2', 1515000308720),
(9137, 0, 12576, 'TEST_15', 'NHIC_Identifier:', 1515000308729),
(9138, 0, 12576, 'GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION)', 'Link_to_existing definition:', 1515000308731),
(9139, 0, 12576, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308734);        
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9140, 0, 12576, 'XXXXXXX0120', '[Optional]_Local_Identifier', 1515000308736),
(9141, 0, 12576, '1', 'A', 1515000308738),
(9142, 0, 12576, '1', 'B', 1515000308741),
(9143, 0, 12576, '1', 'C', 1515000308743),
(9144, 0, 12576, '1', 'D', 1515000308746),
(9145, 0, 12576, '1', 'E', 1515000308748),
(9146, 0, 12576, 'When Patient demographics entered. Address shown. Code available in JCIS tables.', 'F', 1515000308751),
(9147, 0, 12576, 'GC 137', 'G', 1515000308753),
(9148, 0, 12576, 'NEO4J', 'H', 1515000308756),
(9149, 0, 12576, 'from source', 'E2', 1515000308758),
(9150, 0, 12577, 'TEST_16', 'NHIC_Identifier:', 1515000308769),
(9151, 0, 12577, 'PERSON FAMILY NAME (AT BIRTH)', 'Link_to_existing definition:', 1515000308771),
(9152, 0, 12577, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308773),
(9153, 0, 12577, 'XXXXXXX0140', '[Optional]_Local_Identifier', 1515000308775),
(9154, 0, 12577, '2', 'A', 1515000308777),
(9155, 0, 12577, '2', 'B', 1515000308778),
(9156, 0, 12577, '1', 'C', 1515000308780),
(9157, 0, 12577, '1', 'D', 1515000308783),
(9158, 0, 12577, '1', 'E', 1515000308785),
(9159, 0, 12577, 'When Patient demographics entered. The HISS field is MaidenName (ie not strictly Surname at birth).', 'F', 1515000308787),
(9160, 0, 12577, 'GC 138', 'G', 1515000308790),
(9161, 0, 12577, 'NEO4J', 'H', 1515000308792),
(9162, 0, 12577, 'from source', 'E2', 1515000308794),
(9163, 0, 12579, 'TEST_17', 'NHIC_Identifier:', 1515000308808),
(9164, 0, 12579, 'ETHNIC CATEGORY', 'Link_to_existing definition:', 1515000308810),
(9165, 0, 12579, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308812),
(9166, 0, 12579, 'XXXXXXX0150', '[Optional]_Local_Identifier', 1515000308815),
(9167, 0, 12579, '1', 'A', 1515000308817),
(9168, 0, 12579, '1', 'B', 1515000308819),
(9169, 0, 12579, '1', 'C', 1515000308821),
(9170, 0, 12579, '1', 'D', 1515000308823),
(9171, 0, 12579, '1', 'E', 1515000308825),
(9172, 0, 12579, 'Imported from HISS but not displayed.', 'F', 1515000308827),
(9173, 0, 12579, 'GC 139', 'G', 1515000308829),
(9174, 0, 12579, 'NEO4J', 'H', 1515000308831),
(9175, 0, 12579, 'from source', 'E2', 1515000308834),
(9176, 0, 12582, 'TEST_18', 'NHIC_Identifier:', 1515000308852),
(9177, 0, 12582, 'SOURCE OF REFERRAL FOR OUT-PATIENTS', 'Link_to_existing definition:', 1515000308855),
(9178, 0, 12582, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000308857);         
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9179, 0, 12582, 'XXXXXXX1600', '[Optional]_Local_Identifier', 1515000308859),
(9180, 0, 12582, '1', 'A', 1515000308861),
(9181, 0, 12582, '1', 'B', 1515000308863),
(9182, 0, 12582, '1', 'C', 1515000308865),
(9183, 0, 12582, '1', 'D', 1515000308867),
(9184, 0, 12582, '1', 'E', 1515000308869),
(9185, 0, 12582, 'When referral details entered.', 'F', 1515000308871),
(9186, 0, 12582, 'GC 140', 'G', 1515000308873),
(9187, 0, 12582, 'NEO4J', 'H', 1515000308876),
(9188, 0, 12582, 'from source', 'E2', 1515000308878),
(9189, 0, 12583, 'TEST_19', 'NHIC_Identifier:', 1515000308888),
(9190, 0, 12583, 'REFERRAL TO TREATMENT PERIOD START DATE', 'Link_to_existing definition:', 1515000308890),
(9191, 0, 12583, NULL, 'Notes_from_GD_JCIS', 1515000308893),
(9192, 0, 12583, 'XXXXXXX1580', '[Optional]_Local_Identifier', 1515000308895),
(9193, 0, 12583, '2', 'A', 1515000308898),
(9194, 0, 12583, '2', 'B', 1515000308900),
(9195, 0, 12583, '2', 'C', 1515000308902),
(9196, 0, 12583, '1', 'D', 1515000308903),
(9197, 0, 12583, '2', 'E', 1515000308905),
(9198, 0, 12583, 'When referral details entered.', 'F', 1515000308907),
(9199, 0, 12583, 'GC 141', 'G', 1515000308909),
(9200, 0, 12583, 'NEO4J', 'H', 1515000308911),
(9201, 0, 12583, 'from source', 'E2', 1515000308913),
(9202, 0, 12584, 'TEST_20', 'NHIC_Identifier:', 1515000308921),
(9203, 0, 12584, 'DATE FIRST SEEN', 'Link_to_existing definition:', 1515000308923),
(9204, 0, 12584, NULL, 'Notes_from_GD_JCIS', 1515000308924),
(9205, 0, 12584, 'XXXXXXX0230', '[Optional]_Local_Identifier', 1515000308926),
(9206, 0, 12584, '2', 'A', 1515000308928),
(9207, 0, 12584, '2', 'B', 1515000308930),
(9208, 0, 12584, '2', 'C', 1515000308932),
(9209, 0, 12584, '1', 'D', 1515000308933),
(9210, 0, 12584, '2', 'E', 1515000308935),
(9211, 0, 12584, 'When referral details entered.', 'F', 1515000308937),
(9212, 0, 12584, 'GC 142', 'G', 1515000308939),
(9213, 0, 12584, 'NEO4J', 'H', 1515000308941),
(9214, 0, 12584, 'from source', 'E2', 1515000308943),
(9215, 0, 12585, 'TEST_21', 'NHIC_Identifier:', 1515000308951),
(9216, 0, 12585, 'CONSULTANT CODE', 'Link_to_existing definition:', 1515000308953),
(9217, 0, 12585, 'Imported activities do not have this field completed, but it TEST be derived from the Clinic code, which is imported. Check with VT.', 'Notes_from_GD_JCIS', 1515000308954),
(9218, 0, 12585, 'XXXXXXX0210', '[Optional]_Local_Identifier', 1515000308956),
(9219, 0, 12585, '2', 'A', 1515000308958),
(9220, 0, 12585, '2', 'B', 1515000308960),
(9221, 0, 12585, '4', 'C', 1515000308962),
(9222, 0, 12585, '4', 'D', 1515000308963),
(9223, 0, 12585, '2 - Backlog of consultant codes will need to be entered', 'E', 1515000308966),
(9224, 0, 12585, STRINGDECODE('Visit\neg Initial visit or Radiological visit used as first seen in wait time table.'), 'F', 1515000308968),
(9225, 0, 12585, 'GC 143', 'G', 1515000308971),
(9226, 0, 12585, 'NEO4J', 'H', 1515000308973),
(9227, 0, 12585, 'from source', 'E2', 1515000308975),
(9228, 0, 12586, 'TEST_22', 'NHIC_Identifier:', 1515000308985),
(9229, 0, 12586, 'CARE PROFESSIONAL MAIN SPECIALTY CODE', 'Link_to_existing definition:', 1515000308987),
(9230, 0, 12586, STRINGDECODE('NOT SHOWN ON JCIS\n(A field is available in Consultants table, but often empty)'), 'Notes_from_GD_JCIS', 1515000308988),
(9231, 0, 12586, 'XXXXXXX0220', '[Optional]_Local_Identifier', 1515000308990),
(9232, 0, 12586, '4', 'A', 1515000308992),
(9233, 0, 12586, '4', 'B', 1515000308994),
(9234, 0, 12586, '4', 'C', 1515000308996),
(9235, 0, 12586, '4', 'D', 1515000308997),
(9236, 0, 12586, '3', 'E', 1515000308999),
(9237, 0, 12586, STRINGDECODE('NOT SHOWN ON JCIS\n(A field is available in Consultants table, but often empty)'), 'F', 1515000309001),
(9238, 0, 12586, 'GC 144', 'G', 1515000309003),
(9239, 0, 12586, 'NEO4J', 'H', 1515000309005),
(9240, 0, 12586, 'from source', 'E2', 1515000309007),
(9241, 0, 12587, 'TEST_23', 'NHIC_Identifier:', 1515000309014),
(9242, 0, 12587, 'SITE CODE (OF PROVIDER FIRST SEEN)', 'Link_to_existing definition:', 1515000309016);           
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9243, 0, 12587, 'Name shown. Code available in JCIS tables.', 'Notes_from_GD_JCIS', 1515000309018),
(9244, 0, 12587, 'XXXXXXX1410', '[Optional]_Local_Identifier', 1515000309020),
(9245, 0, 12587, '2', 'A', 1515000309022),
(9246, 0, 12587, '2', 'B', 1515000309023),
(9247, 0, 12587, '2', 'C', 1515000309025),
(9248, 0, 12587, '3', 'D', 1515000309027),
(9249, 0, 12587, '2', 'E', 1515000309029),
(9250, 0, 12587, STRINGDECODE('Visit\neg Initial visit or Radiological visit used as first seen in wait time table.'), 'F', 1515000309031),
(9251, 0, 12587, 'GC 145', 'G', 1515000309032),
(9252, 0, 12587, 'NEO4J', 'H', 1515000309034),
(9253, 0, 12587, 'from source', 'E2', 1515000309036),
(9254, 0, 12588, 'TEST_24', 'NHIC_Identifier:', 1515000309044),
(9255, 0, 12588, 'DATE FIRST SEEN (TESTCER SPECIALIST)', 'Link_to_existing definition:', 1515000309046),
(9256, 0, 12588, 'How TEST this be pinned down?', 'Notes_from_GD_JCIS', 1515000309048),
(9257, 0, 12588, 'XXXXXXX1360', '[Optional]_Local_Identifier', 1515000309050),
(9258, 0, 12588, '2', 'A', 1515000309052),
(9259, 0, 12588, '2', 'B', 1515000309054),
(9260, 0, 12588, '2', 'C', 1515000309055),
(9261, 0, 12588, '3', 'D', 1515000309057),
(9262, 0, 12588, '2', 'E', 1515000309059),
(9263, 0, 12588, STRINGDECODE('Visit\neg Initial visit or Radiological visit used as first seen in wait time table.'), 'F', 1515000309061),
(9264, 0, 12588, 'GC 146', 'G', 1515000309630),
(9265, 0, 12588, 'NEO4J', 'H', 1515000309633),
(9266, 0, 12588, 'from source', 'E2', 1515000309636),
(9267, 0, 12589, 'TEST_25', 'NHIC_Identifier:', 1515000309650),
(9268, 0, 12589, 'SITE CODE (OF PROVIDER FIRST TESTCER SPECIALIST)', 'Link_to_existing definition:', 1515000309653),
(9269, 0, 12589, 'Name shown. Code available in JCIS tables.', 'Notes_from_GD_JCIS', 1515000309656),
(9270, 0, 12589, 'XXXXXXX1400', '[Optional]_Local_Identifier', 1515000309658),
(9271, 0, 12589, '2', 'A', 1515000309660),
(9272, 0, 12589, '2', 'B', 1515000309662),
(9273, 0, 12589, '2', 'C', 1515000309664),
(9274, 0, 12589, '3', 'D', 1515000309666),
(9275, 0, 12589, '2', 'E', 1515000309668),
(9276, 0, 12589, STRINGDECODE('Visit\neg Initial visit or Radiological visit used as first seen in wait time table.'), 'F', 1515000309669),
(9277, 0, 12589, 'GC 147', 'G', 1515000309671),
(9278, 0, 12589, 'NEO4J', 'H', 1515000309673),
(9279, 0, 12589, 'from source', 'E2', 1515000309675),
(9280, 0, 12591, 'TEST_26', 'NHIC_Identifier:', 1515000309692),
(9281, 0, 12591, 'TESTCER OR SYMPTOMATIC BREAST REFERRAL PATIENT STATUS', 'Link_to_existing definition:', 1515000309696),
(9282, 0, 12591, NULL, 'Notes_from_GD_JCIS', 1515000309698),
(9283, 0, 12591, 'XXXXXXX0270', '[Optional]_Local_Identifier', 1515000309700),
(9284, 0, 12591, 'TBA', 'A', 1515000309702),
(9285, 0, 12591, 'TBA', 'B', 1515000309705),
(9286, 0, 12591, 'TBA', 'C', 1515000309707),
(9287, 0, 12591, 'TBA', 'D', 1515000309709),
(9288, 0, 12591, '3', 'E', 1515000309711),
(9289, 0, 12591, 'When referral details entered.', 'F', 1515000309713),
(9290, 0, 12591, 'GC 148', 'G', 1515000309715),
(9291, 0, 12591, 'NEO4J', 'H', 1515000309717),
(9292, 0, 12591, 'from source', 'E2', 1515000309720),
(9293, 0, 12592, 'TEST_27', 'NHIC_Identifier:', 1515000309729),
(9294, 0, 12592, 'TESTCER SYMPTOMS FIRST NOTED DATE', 'Link_to_existing definition:', 1515000309731),
(9295, 0, 12592, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000309734),
(9296, 0, 12592, 'XXXXXXX2000', '[Optional]_Local_Identifier', 1515000309737);    
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9297, 0, 12592, 'TBA', 'A', 1515000309739),
(9298, 0, 12592, 'TBA', 'B', 1515000309741),
(9299, 0, 12592, 'TBA', 'C', 1515000309744),
(9300, 0, 12592, 'TBA', 'D', 1515000309747),
(9301, 0, 12592, '4', 'E', 1515000309749),
(9302, 0, 12592, 'Calculated from date of visit / duration of symptoms', 'F', 1515000309752),
(9303, 0, 12592, 'GC 149', 'G', 1515000309755),
(9304, 0, 12592, 'NEO4J', 'H', 1515000309757),
(9305, 0, 12592, 'from source', 'E2', 1515000309760),
(9306, 0, 12594, 'TEST_28', 'NHIC_Identifier:', 1515000309780),
(9307, 0, 12594, 'SITE CODE (OF IMAGING)', 'Link_to_existing definition:', 1515000309782),
(9308, 0, 12594, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000309785),
(9309, 0, 12594, 'XXXXXXX0310', '[Optional]_Local_Identifier', 1515000309787),
(9310, 0, 12594, '1', 'A', 1515000309789),
(9311, 0, 12594, '1', 'B', 1515000309791),
(9312, 0, 12594, '1', 'C', 1515000309792),
(9313, 0, 12594, '2', 'D', 1515000309794),
(9314, 0, 12594, '5', 'E', 1515000309796),
(9315, 0, 12594, 'Name shown. Code available in JCIS tables.', 'F', 1515000309798),
(9316, 0, 12594, 'GC 150', 'G', 1515000309800),
(9317, 0, 12594, 'NEO4J', 'H', 1515000309802),
(9318, 0, 12594, 'from source', 'E2', 1515000309804),
(9319, 0, 12595, 'TEST_29', 'NHIC_Identifier:', 1515000309812),
(9320, 0, 12595, 'PROCEDURE DATE (TESTCER IMAGING)', 'Link_to_existing definition:', 1515000309814),
(9321, 0, 12595, NULL, 'Notes_from_GD_JCIS', 1515000309815),
(9322, 0, 12595, 'XXXXXXX0320', '[Optional]_Local_Identifier', 1515000309817),
(9323, 0, 12595, '1', 'A', 1515000309819),
(9324, 0, 12595, '2', 'B', 1515000309821),
(9325, 0, 12595, '2', 'C', 1515000309823),
(9326, 0, 12595, '2', 'D', 1515000309825),
(9327, 0, 12595, '6', 'E', 1515000309826),
(9328, 0, 12595, NULL, 'F', 1515000309828),
(9329, 0, 12595, 'GC 151', 'G', 1515000309830),
(9330, 0, 12595, 'NEO4J', 'H', 1515000309832),
(9331, 0, 12595, 'from source', 'E2', 1515000309834),
(9332, 0, 12596, 'TEST_30', 'NHIC_Identifier:', 1515000309842),
(9333, 0, 12596, 'IMAGING CODE (NICIP)', 'Link_to_existing definition:', 1515000309844),
(9334, 0, 12596, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000309846),
(9335, 0, 12596, 'XXXXXXX1610', '[Optional]_Local_Identifier', 1515000309847),
(9336, 0, 12596, 'TBA', 'A', 1515000309849),
(9337, 0, 12596, 'TBA', 'B', 1515000309851),
(9338, 0, 12596, 'TBA', 'C', 1515000309853),
(9339, 0, 12596, 'TBA', 'D', 1515000309855),
(9340, 0, 12596, '7', 'E', 1515000309857),
(9341, 0, 12596, STRINGDECODE('See also alternative  mandatory item for non-imported radiology activities\nIe  (TESTCER IMAGING MODALITY and IMAGING ANATOMICAL SITE and ANATOMICAL SIDE (IMAGING))'), 'F', 1515000309859),
(9342, 0, 12596, 'GC 152', 'G', 1515000309860),
(9343, 0, 12596, 'NEO4J', 'H', 1515000309862),
(9344, 0, 12596, 'from source', 'E2', 1515000309865),
(9345, 0, 12598, 'TEST_31', 'NHIC_Identifier:', 1515000309883),
(9346, 0, 12598, 'TESTCER IMAGING MODALITY', 'Link_to_existing definition:', 1515000309885);             
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9347, 0, 12598, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000309887),
(9348, 0, 12598, 'XXXXXXX0330', '[Optional]_Local_Identifier', 1515000309890),
(9349, 0, 12598, '2', 'A', 1515000309892),
(9350, 0, 12598, '2', 'B', 1515000309894),
(9351, 0, 12598, '3', 'C', 1515000309896),
(9352, 0, 12598, '3', 'D', 1515000309898),
(9353, 0, 12598, '8', 'E', 1515000309900),
(9354, 0, 12598, 'Radiology - see title of activity', 'F', 1515000309902),
(9355, 0, 12598, 'GC 153', 'G', 1515000309904),
(9356, 0, 12598, 'NEO4J', 'H', 1515000309906),
(9357, 0, 12598, 'from source', 'E2', 1515000309908),
(9358, 0, 12599, 'TEST_32', 'NHIC_Identifier:', 1515000309917),
(9359, 0, 12599, 'IMAGING ANATOMICAL SITE', 'Link_to_existing definition:', 1515000309919),
(9360, 0, 12599, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000309920),
(9361, 0, 12599, 'XXXXXXX0340', '[Optional]_Local_Identifier', 1515000309922),
(9362, 0, 12599, '4', 'A', 1515000309924),
(9363, 0, 12599, '4', 'B', 1515000309926),
(9364, 0, 12599, '4', 'C', 1515000309928),
(9365, 0, 12599, '4', 'D', 1515000309930),
(9366, 0, 12599, '9', 'E', 1515000309931),
(9367, 0, 12599, STRINGDECODE('Typically not recorded.  Unless the ATM makes it clear what the site is, the site of imaging is not recorded. Exceptions are mammogram (site recorded as breast);BRT ultrasound - user has to record site.\n[Usage for UGI CT sTEST (recorded 4 times on Live patients) - is misleading usage of backend table as front end desXXXXXXXiption is ''Primary tumour location'' on CT sTEST.Findings tab].\n\n(Findings site is recordable).'), 'F', 1515000309933),
(9368, 0, 12599, 'GC 154', 'G', 1515000309935),
(9369, 0, 12599, 'NEO4J', 'H', 1515000309937),
(9370, 0, 12599, 'from source', 'E2', 1515000309939),
(9371, 0, 12601, 'TEST_33', 'NHIC_Identifier:', 1515000309954),
(9372, 0, 12601, 'ANATOMICAL SIDE (IMAGING)', 'Link_to_existing definition:', 1515000309956),
(9373, 0, 12601, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000309958),
(9374, 0, 12601, 'XXXXXXX3000', '[Optional]_Local_Identifier', 1515000309960),
(9375, 0, 12601, 'MAY BE REMOVED', 'A', 1515000309962),
(9376, 0, 12601, 'MAY BE REMOVED', 'B', 1515000309963),
(9377, 0, 12601, 'MAY BE REMOVED', 'C', 1515000309965),
(9378, 0, 12601, 'MAY BE REMOVED', 'D', 1515000309967),
(9379, 0, 12601, 'MAY BE REMOVED', 'E', 1515000309969),
(9380, 0, 12601, NULL, 'F', 1515000309971);
INSERT INTO PUBLIC.EXTENSION_VALUE(ID, VERSION, ELEMENT_ID, EXTENSION_VALUE, NAME, ORDER_INDEX) VALUES
(9381, 0, 12601, NULL, 'G', 1515000309973),
(9382, 0, 12601, 'NEO4J', 'H', 1515000309975),
(9383, 0, 12601, NULL, 'E2', 1515000309977),
(9384, 0, 12603, 'TEST_GENDER', 'NHIC_Identifier:', 1515000309996),
(9385, 0, 12603, 'GENDER2', 'Link_to_existing definition:', 1515000309999),
(9386, 0, 12603, 'orem ipsum dolor sit amet, consectetur adipiscing elit. Praesent blandit erat id euismod gravida. Mauris tincidunt hendrerit mauris, in ullamcorper libero commodo id. Suspendisse potenti. Ut malesuada id metus sit amet semper. Praesent fermentum nulla lacus, vel semper diam rhoncus bibendum. Nulla tincidunt risus accumsan ornare pretium. Pellentesque suscipit tellus at pellentesque fermentum. Proin posuere urna vitae odio elementum, ut mattis justo varius. Aenean id neque odio. Mauris lobortis nisl eros, sed scelerisque nunc gravida in. Aenean elementu', 'Notes_from_GD_JCIS', 1515000310001),
(9387, 0, 12603, 'asd', '[Optional]_Local_Identifier', 1515000310003),
(9388, 0, 12603, 'MAY BE REMOVED', 'A', 1515000310005),
(9389, 0, 12603, 'MAY BE REMOVED', 'B', 1515000310007),
(9390, 0, 12603, 'MAY BE REMOVED', 'C', 1515000310009),
(9391, 0, 12603, 'MAY BE REMOVED', 'D', 1515000310011),
(9392, 0, 12603, 'MAY BE REMOVED', 'E', 1515000310013),
(9393, 0, 12603, NULL, 'F', 1515000310015),
(9394, 0, 12603, NULL, 'G', 1515000310017),
(9395, 0, 12603, 'NEO4J', 'H', 1515000310019),
(9396, 0, 12603, NULL, 'E2', 1515000310021);     
CREATE MEMORY TABLE PUBLIC.MAPPING(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_A026FE1B_84B2_4EDF_8553_641B5DABD8BA) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A026FE1B_84B2_4EDF_8553_641B5DABD8BA,
    VERSION BIGINT NOT NULL,
    DESTINATION_ID BIGINT NOT NULL,
    MAPPING VARCHAR(10000) NOT NULL,
    SOURCE_ID BIGINT NOT NULL
);   
ALTER TABLE PUBLIC.MAPPING ADD CONSTRAINT PUBLIC.CONSTRAINT_5 PRIMARY KEY(ID); 
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.MAPPING;  
CREATE MEMORY TABLE PUBLIC.MEASUREMENT_UNIT(
    ID BIGINT NOT NULL,
    SYMBOL VARCHAR(100)
);
ALTER TABLE PUBLIC.MEASUREMENT_UNIT ADD CONSTRAINT PUBLIC.CONSTRAINT_20 PRIMARY KEY(ID);       
-- 22 +/- SELECT COUNT(*) FROM PUBLIC.MEASUREMENT_UNIT;        
INSERT INTO PUBLIC.MEASUREMENT_UNIT(ID, SYMBOL) VALUES
(12526, 'm'),
(12527, 'kg'),
(12528, 's'),
(12529, 'A'),
(12530, 'K'),
(12531, 'mol'),
(12532, 'cd'),
(12533, STRINGDECODE('\u00b0C')),
(12534, STRINGDECODE('\u00b0F')),
(12535, 'N'),
(12536, 'm2'),
(12537, 'm3'),
(12538, 'm/s'),
(12539, 'm/s2'),
(12540, 'm-1'),
(12541, 'kg/m3'),
(12542, 'm3/kg'),
(12543, 'A/m2'),
(12544, 'A/m'),
(12545, 'mol/m3'),
(12546, 'cd/m2'),
(12547, 'kg/kg = 1');  
CREATE MEMORY TABLE PUBLIC.OAUTHID(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_58A3A240_EE94_4CB6_A3AA_44C7A1DED7B3) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_58A3A240_EE94_4CB6_A3AA_44C7A1DED7B3,
    VERSION BIGINT NOT NULL,
    ACCESS_TOKEN VARCHAR(255) NOT NULL,
    PROVIDER VARCHAR(255) NOT NULL,
    USER_ID BIGINT NOT NULL
);  
ALTER TABLE PUBLIC.OAUTHID ADD CONSTRAINT PUBLIC.CONSTRAINT_C PRIMARY KEY(ID); 
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.OAUTHID;  
CREATE INDEX PUBLIC.IDENTITY_IDX ON PUBLIC.OAUTHID(ACCESS_TOKEN, PROVIDER);    
CREATE MEMORY TABLE PUBLIC.PRIMITIVE_TYPE(
    ID BIGINT NOT NULL,
    MEASUREMENT_UNIT_ID BIGINT
);           
ALTER TABLE PUBLIC.PRIMITIVE_TYPE ADD CONSTRAINT PUBLIC.CONSTRAINT_24 PRIMARY KEY(ID);         
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.PRIMITIVE_TYPE;           
CREATE MEMORY TABLE PUBLIC.REFERENCE_TYPE(
    ID BIGINT NOT NULL,
    DATA_CLASS_ID BIGINT
); 
ALTER TABLE PUBLIC.REFERENCE_TYPE ADD CONSTRAINT PUBLIC.CONSTRAINT_E8 PRIMARY KEY(ID);         
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.REFERENCE_TYPE;           
CREATE MEMORY TABLE PUBLIC.REGISTRATION_CODE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_EE1CCD12_5ABA_44B9_841B_EB946A1AF8E4) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_EE1CCD12_5ABA_44B9_841B_EB946A1AF8E4,
    DATE_CREATED TIMESTAMP NOT NULL,
    TOKEN VARCHAR(255) NOT NULL,
    USERNAME VARCHAR(255) NOT NULL
);    
ALTER TABLE PUBLIC.REGISTRATION_CODE ADD CONSTRAINT PUBLIC.CONSTRAINT_AC PRIMARY KEY(ID);      
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.REGISTRATION_CODE;        
CREATE MEMORY TABLE PUBLIC.RELATIONSHIP(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_A99F4BEF_4333_49FF_AB4B_73A6036EFBC1) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_A99F4BEF_4333_49FF_AB4B_73A6036EFBC1,
    VERSION BIGINT NOT NULL,
    ARCHIVED BOOLEAN NOT NULL,
    DATA_MODEL_ID BIGINT,
    DESTINATION_ID BIGINT NOT NULL,
    INCOMING_INDEX BIGINT NOT NULL,
    INHERITED BOOLEAN NOT NULL,
    OUTGOING_INDEX BIGINT NOT NULL,
    RELATIONSHIP_TYPE_ID BIGINT NOT NULL,
    SOURCE_ID BIGINT NOT NULL
);        
ALTER TABLE PUBLIC.RELATIONSHIP ADD CONSTRAINT PUBLIC.CONSTRAINT_7A PRIMARY KEY(ID);           
-- 59 +/- SELECT COUNT(*) FROM PUBLIC.RELATIONSHIP;            
INSERT INTO PUBLIC.RELATIONSHIP(ID, VERSION, ARCHIVED, DATA_MODEL_ID, DESTINATION_ID, INCOMING_INDEX, INHERITED, OUTGOING_INDEX, RELATIONSHIP_TYPE_ID, SOURCE_ID) VALUES
(2895, 1, FALSE, NULL, 12483, 1514999710464, FALSE, 1514999710464, 2889, 12484),
(2896, 1, FALSE, NULL, 12484, 1514999710465, FALSE, 1514999710465, 2889, 12485),
(2897, 1, FALSE, NULL, 12485, 1514999710466, FALSE, 1514999710466, 2889, 12486),
(2898, 1, FALSE, NULL, 12487, 1514999710467, FALSE, 1514999710467, 2889, 12490),
(2899, 1, FALSE, NULL, 12490, 1514999710468, FALSE, 1514999710468, 2889, 12491),
(2900, 1, FALSE, NULL, 12490, 1514999710469, FALSE, 1514999710469, 2889, 12495),
(2901, 1, FALSE, NULL, 12490, 1514999710470, FALSE, 1514999710470, 2889, 12496),
(2902, 1, FALSE, NULL, 12491, 1514999710471, FALSE, 1514999710471, 2889, 12492),
(2903, 1, FALSE, NULL, 12492, 1514999710472, FALSE, 1514999710472, 2889, 12493),
(2904, 1, FALSE, NULL, 12493, 1514999710473, FALSE, 1514999710473, 2889, 12494),
(2905, 1, FALSE, NULL, 12496, 1514999710474, FALSE, 1514999710474, 2889, 12497),
(2906, 1, FALSE, NULL, 12495, 1514999710475, FALSE, 1514999710475, 2889, 12498),
(2907, 1, FALSE, NULL, 12495, 1514999710476, FALSE, 1514999710476, 2889, 12499),
(2908, 1, FALSE, NULL, 12499, 1514999710477, FALSE, 1514999710477, 2889, 12500),
(2909, 1, FALSE, NULL, 12500, 1514999710478, FALSE, 1514999710478, 2889, 12501),
(2910, 1, FALSE, NULL, 12501, 1514999710479, FALSE, 1514999710479, 2889, 12502),
(2911, 1, FALSE, NULL, 12552, 1514999710523, FALSE, 1514999710523, 2891, 12551),
(2912, 1, FALSE, NULL, 12553, 1514999710524, FALSE, 1514999710524, 2891, 12552),
(2913, 1, FALSE, NULL, 12554, 1514999710525, FALSE, 1514999710525, 2891, 12553),
(2914, 1, FALSE, NULL, 12555, 1514999710526, FALSE, 1514999710526, 2891, 12554),
(2915, 1, FALSE, NULL, 12556, 1514999710527, FALSE, 1514999710527, 2891, 12555),
(2916, 1, FALSE, NULL, 12564, 1514999710528, FALSE, 1514999710528, 2891, 12555),
(2917, 1, FALSE, NULL, 12568, 1514999710529, FALSE, 1514999710529, 2891, 12555),
(2918, 1, FALSE, NULL, 12580, 1514999710530, FALSE, 1514999710530, 2891, 12555),
(2919, 1, FALSE, NULL, 12593, 1514999710531, FALSE, 1514999710531, 2891, 12555),
(2920, 1, FALSE, NULL, 12558, 1514999710532, FALSE, 1514999710532, 2888, 12556),
(2921, 1, FALSE, NULL, 12559, 1514999710533, FALSE, 1514999710533, 2888, 12556),
(2922, 1, FALSE, NULL, 12561, 1514999710534, FALSE, 1514999710534, 2888, 12556),
(2923, 1, FALSE, NULL, 12562, 1514999710535, FALSE, 1514999710535, 2888, 12556),
(2924, 1, FALSE, NULL, 12563, 1514999710536, FALSE, 1514999710536, 2888, 12556),
(2925, 1, FALSE, NULL, 12565, 1514999710537, FALSE, 1514999710537, 2888, 12564),
(2926, 1, FALSE, NULL, 12566, 1514999710538, FALSE, 1514999710538, 2888, 12564),
(2927, 1, FALSE, NULL, 12567, 1514999710539, FALSE, 1514999710539, 2888, 12564),
(2928, 1, FALSE, NULL, 12569, 1514999710540, FALSE, 1514999710540, 2888, 12568),
(2929, 1, FALSE, NULL, 12570, 1514999710541, FALSE, 1514999710541, 2888, 12568),
(2930, 1, FALSE, NULL, 12571, 1514999710542, FALSE, 1514999710542, 2888, 12568),
(2931, 1, FALSE, NULL, 12572, 1514999710543, FALSE, 1514999710543, 2888, 12568),
(2932, 1, FALSE, NULL, 12574, 1514999710544, FALSE, 1514999710544, 2888, 12568),
(2933, 1, FALSE, NULL, 12575, 1514999710545, FALSE, 1514999710545, 2888, 12568),
(2934, 1, FALSE, NULL, 12576, 1514999710546, FALSE, 1514999710546, 2888, 12568),
(2935, 1, FALSE, NULL, 12577, 1514999710547, FALSE, 1514999710547, 2888, 12568),
(2936, 1, FALSE, NULL, 12579, 1514999710548, FALSE, 1514999710548, 2888, 12568),
(2937, 1, FALSE, NULL, 12582, 1514999710549, FALSE, 1514999710549, 2888, 12580),
(2938, 1, FALSE, NULL, 12583, 1514999710550, FALSE, 1514999710550, 2888, 12580),
(2939, 1, FALSE, NULL, 12584, 1514999710551, FALSE, 1514999710551, 2888, 12580),
(2940, 1, FALSE, NULL, 12585, 1514999710552, FALSE, 1514999710552, 2888, 12580),
(2941, 1, FALSE, NULL, 12586, 1514999710553, FALSE, 1514999710553, 2888, 12580),
(2942, 1, FALSE, NULL, 12587, 1514999710554, FALSE, 1514999710554, 2888, 12580),
(2943, 1, FALSE, NULL, 12588, 1514999710555, FALSE, 1514999710555, 2888, 12580);      
INSERT INTO PUBLIC.RELATIONSHIP(ID, VERSION, ARCHIVED, DATA_MODEL_ID, DESTINATION_ID, INCOMING_INDEX, INHERITED, OUTGOING_INDEX, RELATIONSHIP_TYPE_ID, SOURCE_ID) VALUES
(2944, 1, FALSE, NULL, 12589, 1514999710556, FALSE, 1514999710556, 2888, 12580),
(2945, 1, FALSE, NULL, 12591, 1514999710557, FALSE, 1514999710557, 2888, 12580),
(2946, 1, FALSE, NULL, 12592, 1514999710558, FALSE, 1514999710558, 2888, 12580),
(2947, 1, FALSE, NULL, 12594, 1514999710559, FALSE, 1514999710559, 2888, 12593),
(2948, 1, FALSE, NULL, 12595, 1514999710560, FALSE, 1514999710560, 2888, 12593),
(2949, 1, FALSE, NULL, 12596, 1514999710561, FALSE, 1514999710561, 2888, 12593),
(2950, 1, FALSE, NULL, 12598, 1514999710562, FALSE, 1514999710562, 2888, 12593),
(2951, 1, FALSE, NULL, 12599, 1514999710563, FALSE, 1514999710563, 2888, 12593),
(2952, 1, FALSE, NULL, 12601, 1514999710564, FALSE, 1514999710564, 2888, 12593),
(2953, 1, FALSE, NULL, 12603, 1514999710565, FALSE, 1514999710565, 2888, 12593);             
CREATE MEMORY TABLE PUBLIC.RELATIONSHIP_METADATA(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_1B50A1AE_487D_4735_A2C9_322B1D071584) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_1B50A1AE_487D_4735_A2C9_322B1D071584,
    VERSION BIGINT NOT NULL,
    EXTENSION_VALUE VARCHAR(10000),
    NAME VARCHAR(255) NOT NULL,
    ORDER_INDEX BIGINT NOT NULL,
    RELATIONSHIP_ID BIGINT NOT NULL
);   
ALTER TABLE PUBLIC.RELATIONSHIP_METADATA ADD CONSTRAINT PUBLIC.CONSTRAINT_29 PRIMARY KEY(ID);  
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.RELATIONSHIP_METADATA;    
CREATE MEMORY TABLE PUBLIC.RELATIONSHIP_TYPE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_62E61E11_5898_4FBA_92E2_90CF81DDDF5A) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_62E61E11_5898_4FBA_92E2_90CF81DDDF5A,
    VERSION BIGINT NOT NULL,
    BIDIRECTIONAL BOOLEAN NOT NULL,
    DESTINATION_CLASS VARCHAR(255) NOT NULL,
    DESTINATION_TO_SOURCE VARCHAR(255) NOT NULL,
    DESTINATION_TO_SOURCE_DESCRIPTION VARCHAR(2000),
    METADATA_HINTS LONGVARCHAR,
    NAME VARCHAR(255) NOT NULL,
    RULE LONGVARCHAR,
    SEARCHABLE BOOLEAN DEFAULT FALSE NOT NULL,
    SOURCE_CLASS VARCHAR(255) NOT NULL,
    SOURCE_TO_DESTINATION VARCHAR(255) NOT NULL,
    SOURCE_TO_DESTINATION_DESCRIPTION VARCHAR(2000),
    SYSTEM BOOLEAN NOT NULL,
    VERSION_SPECIFIC BOOLEAN NOT NULL
);   
ALTER TABLE PUBLIC.RELATIONSHIP_TYPE ADD CONSTRAINT PUBLIC.CONSTRAINT_6B PRIMARY KEY(ID);      
-- 15 +/- SELECT COUNT(*) FROM PUBLIC.RELATIONSHIP_TYPE;       
INSERT INTO PUBLIC.RELATIONSHIP_TYPE(ID, VERSION, BIDIRECTIONAL, DESTINATION_CLASS, DESTINATION_TO_SOURCE, DESTINATION_TO_SOURCE_DESCRIPTION, METADATA_HINTS, NAME, RULE, SEARCHABLE, SOURCE_CLASS, SOURCE_TO_DESTINATION, SOURCE_TO_DESTINATION_DESCRIPTION, SYSTEM, VERSION_SPECIFIC) VALUES
(2888, 0, FALSE, 'org.modelcatalogue.core.DataElement', 'contained in', 'A Data Element can be contained in multiple Data Classes. When a new draft of a Data Element is created, then drafts for all containing Data Classes are created as well.', NULL, 'containment', STRINGDECODE('String minOccursString = ext[''Min Occurs'']\n            String maxOccursString = ext[''Max Occurs'']\n\n            Integer minOccurs = minOccursString in [''unbounded'', ''null'', ''*'', null, ''''] ? 0 : (minOccursString as Integer)\n            Integer maxOccurs = maxOccursString in [''unbounded'', ''null'', ''*'', null, ''''] ? Integer.MAX_VALUE : (maxOccursString as Integer)\n\n            if (minOccurs < 0) {\n                return [\"relationshipType.containment.min.occurs.less.than.zero\", \"''Max Occurs'' has to be greater than zero\"]\n            }\n            if (maxOccurs < minOccurs) {\n                return [\"relationshipType.containment.min.occurs.greater.than.max.occurs\", \"The metadata ''Min Occurs'' cannot be greater than ''Min Occurs''\"]\n            }\n            if (maxOccurs < 1) {\n                return [\"relationshipType.containment.max.occurs.zero\", \"The metadata ''Max Occurs'' must be greater than zero\"]\n            }\n\n            return true'), FALSE, 'org.modelcatalogue.core.DataClass', 'contains', 'A Data Class can contain multiple Data Elements. Contained Data Elements are finalized when the Class is finalized.', FALSE, TRUE),
(2889, 0, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'is base for', 'A Catalogue Element can be base for multiple Catalogue Elements of the same type.', NULL, 'base', 'isSameClass()', FALSE, 'org.modelcatalogue.core.CatalogueElement', 'is based on', 'A Catalogue Element can be based on multiple Catalogue Elements of the same type. Value domains will first use rules of the base value domains and then their own when validating input values.', FALSE, TRUE),
(2890, 0, FALSE, 'org.modelcatalogue.core.Asset', 'is attached to', 'An uploaded Asset can be attached to multiple Catalogue Elements.', NULL, 'attachment', NULL, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'has attachment of', 'A Catalogue Element can have multiple uploaded Assets attached to it.', FALSE, FALSE),
(2891, 0, FALSE, 'org.modelcatalogue.core.DataClass', 'child of', 'A Class can be contained (be child Class of) in multiple Classes. When a draft is created for child Class, drafts for parent Classes are created as well.', NULL, 'hierarchy', NULL, FALSE, 'org.modelcatalogue.core.DataClass', 'parent of', 'A Class can contain (be parent of) multiple Classes. Child Classes are finalized when parent Class is finalized.', FALSE, TRUE),
(2892, 0, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'supersedes', 'A Catalogue Element can be previous version (supersede) multiple Catalogue Elements of the same type.', NULL, 'supersession', 'isSameClass()', FALSE, 'org.modelcatalogue.core.CatalogueElement', 'superseded by', 'A Catalogue Element can have multiple previous versions which are Catalogue Elements of the same type.', TRUE, TRUE),
(2893, 0, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'is cloned from', 'A Catalogue Element can be origin for multiple cloned Catalogue Elements of the same type in different Data Models.', NULL, 'origin', 'isSameClass()', FALSE, 'org.modelcatalogue.core.CatalogueElement', 'is origin for', 'A Catalogue Element can be cloned from a single Catalogue Element of the same type.', TRUE, TRUE),
(2894, 0, TRUE, 'org.modelcatalogue.core.CatalogueElement', 'related to', NULL, NULL, 'relatedTo', NULL, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'related to', 'A Catalogue Element can be related to multiple Catalogue Elements. This relationship has no specific meaning, but may carry metadata to further specify it.', FALSE, FALSE);          
INSERT INTO PUBLIC.RELATIONSHIP_TYPE(ID, VERSION, BIDIRECTIONAL, DESTINATION_CLASS, DESTINATION_TO_SOURCE, DESTINATION_TO_SOURCE_DESCRIPTION, METADATA_HINTS, NAME, RULE, SEARCHABLE, SOURCE_CLASS, SOURCE_TO_DESTINATION, SOURCE_TO_DESTINATION_DESCRIPTION, SYSTEM, VERSION_SPECIFIC) VALUES
(2895, 0, TRUE, 'org.modelcatalogue.core.CatalogueElement', 'is synonym for', NULL, NULL, 'synonym', 'isSameClass()', FALSE, 'org.modelcatalogue.core.CatalogueElement', 'is synonym for', 'A Catalogue Element can be a synonym of multiple Catalogue Elements of the same type having similar meaning.', FALSE, FALSE),
(2896, 0, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'is favourite of', 'A Catalogue Element can be favourited by multiple users and appear in their Favourites page.', NULL, 'favourite', NULL, TRUE, 'org.modelcatalogue.core.security.User', 'favourites', 'A User can favourite multiple Catalogue Elements which will be displayed at the Favourites page.', TRUE, FALSE),
(2897, 0, FALSE, 'org.modelcatalogue.core.DataModel', 'is imported by', 'A Data Model can be imported by other Data Models so they can reuse the Catalogue Elements defined within.', NULL, 'import', NULL, FALSE, 'org.modelcatalogue.core.DataModel', 'imports', 'A Data Model can import other Data Models to reuse Catalogue Elements defined wtihin.', FALSE, FALSE),
(2898, 0, FALSE, 'org.modelcatalogue.core.CatalogueElement', 'declared within', 'A Catalogue Element can be declared within multiple Data Models. When new draft of the Catalogue Element is created then drafts for Data Models are created as well.', NULL, 'declaration', NULL, FALSE, 'org.modelcatalogue.core.DataModel', 'declares', 'Data Models can declare multiple Catalogue Elements. Based on this relationship you can narrow the Catalogue Elements shown in the Catalogue using the Data Model filter in the bottom left corner. When Data Model is finalized all defined Elements are finalized as well.', TRUE, TRUE),
(2899, 0, FALSE, 'org.modelcatalogue.core.security.User', 'filtered by', 'A User can filter by multiple classifications. To use exclusion filter instead of inclusion, set metadata $exclude to any non-null value.', NULL, 'classificationFilter', NULL, FALSE, 'org.modelcatalogue.core.DataModel', 'used as filter by', 'A Classification can be used as filter by multiple users. This is done using the classification filter in bottom left corner.', TRUE, FALSE),
(2900, 0, FALSE, 'org.modelcatalogue.core.DataClass', 'provides context for', 'A Data Class can provide the context for multiple validation rules', NULL, 'ruleContext', NULL, FALSE, 'org.modelcatalogue.core.ValidationRule', 'applied within context', 'A Validation rule is applied within the context of a Data Class.', FALSE, TRUE),
(2901, 0, FALSE, 'org.modelcatalogue.core.DataElement', 'is involved in', 'A Data Element can be involved in multiple Validation Rules', NULL, 'involvedness', NULL, FALSE, 'org.modelcatalogue.core.ValidationRule', 'involves', 'A Validation Rule can involve multiple Data Elements', FALSE, TRUE),
(2902, 0, FALSE, 'org.modelcatalogue.core.DataElement', 'is tagged by', 'Data Elements can be tagged by multiple Tags', NULL, 'tag', NULL, FALSE, 'org.modelcatalogue.core.Tag', 'tags', 'A Tag may tag multiple Data Elements', FALSE, FALSE);              
CREATE INDEX PUBLIC.RELATIONTYPE_DESTINATIONCLASS_IDX ON PUBLIC.RELATIONSHIP_TYPE(DESTINATION_CLASS);          
CREATE INDEX PUBLIC.RELATIONTYPE_NAME_IDX ON PUBLIC.RELATIONSHIP_TYPE(NAME);   
CREATE MEMORY TABLE PUBLIC.REQUESTMAP(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_546D4B5E_1C91_4B99_8DE6_E9279EB9FD70) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_546D4B5E_1C91_4B99_8DE6_E9279EB9FD70,
    VERSION BIGINT NOT NULL,
    CONFIG_ATTRIBUTE VARCHAR(255) NOT NULL,
    HTTP_METHOD VARCHAR(255),
    URL VARCHAR(255) NOT NULL
);               
ALTER TABLE PUBLIC.REQUESTMAP ADD CONSTRAINT PUBLIC.CONSTRAINT_1F PRIMARY KEY(ID);             
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.REQUESTMAP;               
CREATE MEMORY TABLE PUBLIC.ROLE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_5C09A253_1193_42F7_8A18_0E5D39EF5E5F) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_5C09A253_1193_42F7_8A18_0E5D39EF5E5F,
    VERSION BIGINT NOT NULL,
    AUTHORITY VARCHAR(255) NOT NULL
);         
ALTER TABLE PUBLIC.ROLE ADD CONSTRAINT PUBLIC.CONSTRAINT_26 PRIMARY KEY(ID);   
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.ROLE;     
CREATE MEMORY TABLE PUBLIC.TAG(
    ID BIGINT NOT NULL
);      
ALTER TABLE PUBLIC.TAG ADD CONSTRAINT PUBLIC.CONSTRAINT_14 PRIMARY KEY(ID);    
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.TAG;      
CREATE MEMORY TABLE PUBLIC.USER(
    ID BIGINT NOT NULL,
    ACCOUNT_EXPIRED BOOLEAN,
    ACCOUNT_LOCKED BOOLEAN,
    API_KEY VARCHAR(255),
    EMAIL VARCHAR(255),
    ENABLED BOOLEAN,
    "password" VARCHAR(255),
    PASSWORD_EXPIRED BOOLEAN,
    USERNAME VARCHAR(255)
);               
ALTER TABLE PUBLIC.USER ADD CONSTRAINT PUBLIC.CONSTRAINT_27 PRIMARY KEY(ID);   
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.USER;     
CREATE MEMORY TABLE PUBLIC.USER_ROLE(
    ID BIGINT DEFAULT (NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_02C1CBE2_AC91_4491_BBDD_52943DB39F47) NOT NULL NULL_TO_DEFAULT SEQUENCE PUBLIC.SYSTEM_SEQUENCE_02C1CBE2_AC91_4491_BBDD_52943DB39F47,
    ROLE_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL
);            
ALTER TABLE PUBLIC.USER_ROLE ADD CONSTRAINT PUBLIC.CONSTRAINT_BC PRIMARY KEY(ID);              
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.USER_ROLE;
CREATE MEMORY TABLE PUBLIC.VALIDATION_RULE(
    ID BIGINT NOT NULL,
    COMPONENT VARCHAR(255),
    ERROR_CONDITION VARCHAR(255),
    ISSUE_RECORD VARCHAR(255),
    NOTIFICATION VARCHAR(255),
    NOTIFICATION_TARGET VARCHAR(255),
    RULE VARCHAR(10000),
    RULE_FOCUS VARCHAR(255),
    RULE_TRIGGER VARCHAR(255)
);   
ALTER TABLE PUBLIC.VALIDATION_RULE ADD CONSTRAINT PUBLIC.CONSTRAINT_4 PRIMARY KEY(ID);         
-- 0 +/- SELECT COUNT(*) FROM PUBLIC.VALIDATION_RULE;          
ALTER TABLE PUBLIC.USER ADD CONSTRAINT PUBLIC.UK_OB8KQYQQGMEFL0ACO34AKDTPE UNIQUE(EMAIL);      
ALTER TABLE PUBLIC.ACL_CLASS ADD CONSTRAINT PUBLIC.UK_IY7UA5FSO3IL3U3YMOC4UF35W UNIQUE(CLASS); 
ALTER TABLE PUBLIC.REQUESTMAP ADD CONSTRAINT PUBLIC.UNIQUE_URL UNIQUE(HTTP_METHOD, URL);       
ALTER TABLE PUBLIC.ACL_ENTRY ADD CONSTRAINT PUBLIC.UNIQUE_ACE_ORDER UNIQUE(ACL_OBJECT_IDENTITY, ACE_ORDER);    
ALTER TABLE PUBLIC.ACL_OBJECT_IDENTITY ADD CONSTRAINT PUBLIC.UNIQUE_OBJECT_ID_IDENTITY UNIQUE(OBJECT_ID_CLASS, OBJECT_ID_IDENTITY);            
ALTER TABLE PUBLIC.USER ADD CONSTRAINT PUBLIC.UK_SB8BBOUER5WAK8VYIIY4PF2BX UNIQUE(USERNAME);   
ALTER TABLE PUBLIC.DATA_MODEL_POLICY ADD CONSTRAINT PUBLIC.UK_AGEAWCASIUSOJ14YW6VLO8R42 UNIQUE(NAME);          
ALTER TABLE PUBLIC.ACL_SID ADD CONSTRAINT PUBLIC.UNIQUE_PRINCIPAL UNIQUE(SID, PRINCIPAL);      
ALTER TABLE PUBLIC.OAUTHID ADD CONSTRAINT PUBLIC.UK_2HY0ON28ORON3V471PLRNU1K0 UNIQUE(ACCESS_TOKEN);            
ALTER TABLE PUBLIC.ROLE ADD CONSTRAINT PUBLIC.UK_IRSAMGNERA6ANGM0PRQ1KEMT2 UNIQUE(AUTHORITY);  
ALTER TABLE PUBLIC.MAPPING ADD CONSTRAINT PUBLIC.UNIQUE_SOURCE_ID UNIQUE(DESTINATION_ID, SOURCE_ID);           
ALTER TABLE PUBLIC.RELATIONSHIP_TYPE ADD CONSTRAINT PUBLIC.UK_ISXYGYCHIH5K3D3W0ADSA8DMU UNIQUE(NAME);          
ALTER TABLE PUBLIC.DATA_MODEL_DATA_MODEL_POLICY ADD CONSTRAINT PUBLIC.FK_4D8RTY3536PL29P4TTXC4T634 FOREIGN KEY(DATA_MODEL_POLICIES_ID) REFERENCES PUBLIC.DATA_MODEL(ID) NOCHECK;               
ALTER TABLE PUBLIC.PRIMITIVE_TYPE ADD CONSTRAINT PUBLIC.FK_R1K58XVVHL5TFR5ERT737MCRW FOREIGN KEY(ID) REFERENCES PUBLIC.DATA_TYPE(ID) NOCHECK;  
ALTER TABLE PUBLIC.USER_ROLE ADD CONSTRAINT PUBLIC.FK_IT77EQ964JHFQTU54081EBTIO FOREIGN KEY(ROLE_ID) REFERENCES PUBLIC.ROLE(ID) NOCHECK;       
ALTER TABLE PUBLIC.ACL_OBJECT_IDENTITY ADD CONSTRAINT PUBLIC.FK_NXV5WE2ION9FWEDBKGE7SYOC3 FOREIGN KEY(OWNER_SID) REFERENCES PUBLIC.ACL_SID(ID) NOCHECK;        
ALTER TABLE PUBLIC.ACTION_DEPENDENCY ADD CONSTRAINT PUBLIC.FK_H5T41RH141W87KY6Y3RFDW1TA FOREIGN KEY(DEPENDANT_ID) REFERENCES PUBLIC.ACTION(ID) NOCHECK;        
ALTER TABLE PUBLIC.OAUTHID ADD CONSTRAINT PUBLIC.FK_M55D9KULBI1H29T7XS23OHD0W FOREIGN KEY(USER_ID) REFERENCES PUBLIC.USER(ID) NOCHECK;         
ALTER TABLE PUBLIC.RELATIONSHIP ADD CONSTRAINT PUBLIC.FK_DXXUJFNNEBSKGVSR3PSW7TKQK FOREIGN KEY(DESTINATION_ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;
ALTER TABLE PUBLIC.MAPPING ADD CONSTRAINT PUBLIC.FK_FV0146PRJRJGBW0AX4AC2RYOB FOREIGN KEY(DESTINATION_ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;     
ALTER TABLE PUBLIC.ASSET ADD CONSTRAINT PUBLIC.FK_K73V5HFIBQ49PRM2R9PAXXQPC FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;   
ALTER TABLE PUBLIC.VALIDATION_RULE ADD CONSTRAINT PUBLIC.FK_1XYALG15G7I1TRPB9GRLLT2LD FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;         
ALTER TABLE PUBLIC.RELATIONSHIP ADD CONSTRAINT PUBLIC.FK_HKP4M9EJTK95R55U0GN4OMP8C FOREIGN KEY(DATA_MODEL_ID) REFERENCES PUBLIC.DATA_MODEL(ID) NOCHECK;        
ALTER TABLE PUBLIC.USER ADD CONSTRAINT PUBLIC.FK_8QTPNV06ELXURYEUV1AC4XIMM FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;    
ALTER TABLE PUBLIC.DATA_ELEMENT ADD CONSTRAINT PUBLIC.FK_J2HO6CWLJA2404DWXHCQ332J3 FOREIGN KEY(DATA_TYPE_ID) REFERENCES PUBLIC.DATA_TYPE(ID) NOCHECK;          
ALTER TABLE PUBLIC.ACTION ADD CONSTRAINT PUBLIC.FK_5GT5S8LEE1XU7KE2PFFTMJ5HW FOREIGN KEY(BATCH_ID) REFERENCES PUBLIC.BATCH(ID) NOCHECK;        
ALTER TABLE PUBLIC.DATA_MODEL ADD CONSTRAINT PUBLIC.FK_RVEFKTVB22U3Y4RO3S85TP575 FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;              
ALTER TABLE PUBLIC.MEASUREMENT_UNIT ADD CONSTRAINT PUBLIC.FK_KGU5RJODUT97DGIBTGJGWHFWQ FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;        
ALTER TABLE PUBLIC.PRIMITIVE_TYPE ADD CONSTRAINT PUBLIC.FK_SV8SL9V1BB9TOUKIK1IHXFNLO FOREIGN KEY(MEASUREMENT_UNIT_ID) REFERENCES PUBLIC.MEASUREMENT_UNIT(ID) NOCHECK;          
ALTER TABLE PUBLIC.ACL_OBJECT_IDENTITY ADD CONSTRAINT PUBLIC.FK_6C3UGMK053UY27BK2SRED31LF FOREIGN KEY(OBJECT_ID_CLASS) REFERENCES PUBLIC.ACL_CLASS(ID) NOCHECK;
ALTER TABLE PUBLIC.ACL_ENTRY ADD CONSTRAINT PUBLIC.FK_I6XYFCCD4Y3WLWHGWPO4A9RM1 FOREIGN KEY(SID) REFERENCES PUBLIC.ACL_SID(ID) NOCHECK;        
ALTER TABLE PUBLIC.DATA_TYPE ADD CONSTRAINT PUBLIC.FK_8YH1HG0SLPPDJM9HKS745RVLC FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;               
ALTER TABLE PUBLIC.COLUMN_TRANSFORMATION_DEFINITION ADD CONSTRAINT PUBLIC.FK_3UCA158DL954JHRY8JACWPS2H FOREIGN KEY(DESTINATION_ID) REFERENCES PUBLIC.DATA_ELEMENT(ID) NOCHECK; 
ALTER TABLE PUBLIC.ENUMERATED_TYPE ADD CONSTRAINT PUBLIC.FK_8D8J21OTR9D2NX5JDAUJ3NEFQ FOREIGN KEY(ID) REFERENCES PUBLIC.DATA_TYPE(ID) NOCHECK; 
ALTER TABLE PUBLIC.MAPPING ADD CONSTRAINT PUBLIC.FK_9XB05XT14PHOR5RDRBWKE7D5V FOREIGN KEY(SOURCE_ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;          
ALTER TABLE PUBLIC.ACL_ENTRY ADD CONSTRAINT PUBLIC.FK_FHUOESMJEF3MRV0GPJA4SHVCR FOREIGN KEY(ACL_OBJECT_IDENTITY) REFERENCES PUBLIC.ACL_OBJECT_IDENTITY(ID) NOCHECK;            
ALTER TABLE PUBLIC.ACL_OBJECT_IDENTITY ADD CONSTRAINT PUBLIC.FK_6OAP2K8Q5BL33YQ3YFFRWEDHF FOREIGN KEY(PARENT_OBJECT) REFERENCES PUBLIC.ACL_OBJECT_IDENTITY(ID) NOCHECK;        
ALTER TABLE PUBLIC.ACTION_DEPENDENCY ADD CONSTRAINT PUBLIC.FK_RVC4BRAHQTB99WJEXIEB53NWB FOREIGN KEY(PROVIDER_ID) REFERENCES PUBLIC.ACTION(ID) NOCHECK;         
ALTER TABLE PUBLIC.REFERENCE_TYPE ADD CONSTRAINT PUBLIC.FK_AWJ86V8KGP9DNIUUNRBPLUAXC FOREIGN KEY(DATA_CLASS_ID) REFERENCES PUBLIC.DATA_CLASS(ID) NOCHECK;      
ALTER TABLE PUBLIC.DATA_ELEMENT ADD CONSTRAINT PUBLIC.FK_1AV6343F8NM7CA7OPAF4G9KNM FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;            
ALTER TABLE PUBLIC.RELATIONSHIP ADD CONSTRAINT PUBLIC.FK_K7IT79F0G0W8U9IDH2D6HJT2 FOREIGN KEY(SOURCE_ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;      
ALTER TABLE PUBLIC.EXTENSION_VALUE ADD CONSTRAINT PUBLIC.FK_1PCTQ5R5A3UOKWDBMBN12JT7F FOREIGN KEY(ELEMENT_ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK; 
ALTER TABLE PUBLIC.RELATIONSHIP ADD CONSTRAINT PUBLIC.FK_7SHSJYUHMA7QXSUWMDPSKKJEO FOREIGN KEY(RELATIONSHIP_TYPE_ID) REFERENCES PUBLIC.RELATIONSHIP_TYPE(ID) NOCHECK;          
ALTER TABLE PUBLIC.TAG ADD CONSTRAINT PUBLIC.FK_F7D8J93M1NTNR8WJ1THN34XJW FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;     
ALTER TABLE PUBLIC.CATALOGUE_ELEMENT ADD CONSTRAINT PUBLIC.FK_638SIV71XPEVTCA95WEBCRK50 FOREIGN KEY(DATA_MODEL_ID) REFERENCES PUBLIC.DATA_MODEL(ID) NOCHECK;   
ALTER TABLE PUBLIC.DATA_MODEL_DATA_MODEL_POLICY ADD CONSTRAINT PUBLIC.FK_IA4FKOPYLOM9PD0FRH0UPOTT1 FOREIGN KEY(DATA_MODEL_POLICY_ID) REFERENCES PUBLIC.DATA_MODEL_POLICY(ID) NOCHECK;          
ALTER TABLE PUBLIC.COLUMN_TRANSFORMATION_DEFINITION ADD CONSTRAINT PUBLIC.FK_DC9IXIEVJJI8RN9GMGUXESRTU FOREIGN KEY(SOURCE_ID) REFERENCES PUBLIC.DATA_ELEMENT(ID) NOCHECK;      
ALTER TABLE PUBLIC.USER_ROLE ADD CONSTRAINT PUBLIC.FK_APCC8LXK2XNUG8377FATVBN04 FOREIGN KEY(USER_ID) REFERENCES PUBLIC.USER(ID) NOCHECK;       
ALTER TABLE PUBLIC.ACTION_PARAMETER ADD CONSTRAINT PUBLIC.FK_5US3SSCKQY9ARPGE6JFIEXIOS FOREIGN KEY(ACTION_ID) REFERENCES PUBLIC.ACTION(ID) NOCHECK;            
ALTER TABLE PUBLIC.REFERENCE_TYPE ADD CONSTRAINT PUBLIC.FK_RBX2592NT72IVN7SH8K7NR1NL FOREIGN KEY(ID) REFERENCES PUBLIC.DATA_TYPE(ID) NOCHECK;  
ALTER TABLE PUBLIC.DATA_CLASS ADD CONSTRAINT PUBLIC.FK_FS9S7LKEG7VDLG7GKWAMCV2LE FOREIGN KEY(ID) REFERENCES PUBLIC.CATALOGUE_ELEMENT(ID) NOCHECK;              
ALTER TABLE PUBLIC.RELATIONSHIP_METADATA ADD CONSTRAINT PUBLIC.FK_D9KE242J0CBNB8YU6FTANBN25 FOREIGN KEY(RELATIONSHIP_ID) REFERENCES PUBLIC.RELATIONSHIP(ID) NOCHECK;           
ALTER TABLE PUBLIC.COLUMN_TRANSFORMATION_DEFINITION ADD CONSTRAINT PUBLIC.FK_P2US4XJND5HRSR4R06G4MN1AT FOREIGN KEY(TRANSFORMATION_ID) REFERENCES PUBLIC.CSV_TRANSFORMATION(ID) NOCHECK;        
