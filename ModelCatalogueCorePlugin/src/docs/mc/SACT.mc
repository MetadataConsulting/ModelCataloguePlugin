classification(name: 'SACT', id: 'http://www.chemodataset.nhs.uk/') {
    // formats as value domains
    // value domain definition

    valueDomain(name: "an1") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 1 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{1}/)
    }

    valueDomain(name: "an2") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 2 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{2}/)
    }

    valueDomain(name: "an3") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 3 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{3}/)
    }

    valueDomain(name: "an4") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 4 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{4}/)
    }

    valueDomain(name: "an5") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 5 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{5}/)
    }

    valueDomain(name: "an6") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 6 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{6}/)
    }

    valueDomain(name: "an8") {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of exact length of 8 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{8}/)
    }

    valueDomain(name: 'an1 or an2') {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of length of 1 or 2 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{1,2}/)
    }

    valueDomain(name: 'an3 or an5') {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of length of 3 or 5 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        rule 'x ==~ /[a-zA-Z0-9]+/ && (length(3) || length(5))'
    }

    valueDomain(name: 'max an35') {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of maximal length of 35 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{1,35}/)
    }

    valueDomain(name: 'max an8') {
        description """
            Alfa-numerical characters a-z, A-Z and 0-9 of maximal length of 8 characters
        """
        basedOn 'XMLSchema', 'xs:string'
        regex(/[a-zA-Z0-9]{1,8}/)
    }

    valueDomain(name: 'an10 ccyy-mm-dd') {
        description """
            Date in xml format yyyy-mm-dd. Year, month and date separated by '-' e.g. 2014-12-24.
        """
        basedOn 'XMLSchema', 'xs:date'
        rule 'date("yyyy-MM-dd")'
    }

    valueDomain(name: "max n2") {
        description """
            Numerical value as up to two digits.
        """
        basedOn 'XMLSchema', 'xs:integer'
        regex(/[0-9]{1,2}/)
    }

    valueDomain(name: "max n7") {
        description """
            Numerical value as up to seven digits.
        """
        basedOn 'XMLSchema', 'xs:integer'
        regex(/[0-9]{1,7}/)
    }

    valueDomain(name: "n10") {
        description """
            Numerical value as exactly ten digits.
        """
        basedOn 'XMLSchema', 'xs:integer'
        regex(/[0-9]{10}/)
    }

    valueDomain(name: "max n3.max n3") {
        description """
            Decimal value as up to three digits followed by up to another three digits after the decimal point.
        """
        basedOn 'XMLSchema', 'xs:decimal'
        regex(/[0-9]{1,3}(\.[0-9]{1,3})?/)
    }

    valueDomain(name: "n1.max n2") {
        description """
            Decimal value with single digit followed by up to two digits after the decimal point.
        """
        basedOn 'XMLSchema', 'xs:decimal'
        regex(/[0-9]{1}\.[0-9]{1,2}/)
    }

    //models
    model(name: 'Demographics and Consultant') {
        dataElement(name: 'NHS number') {
            description 'Primary identifier, essential for data linkage'
            valueDomain(name: 'NHS NUMBER') {
                basedOn 'n10'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '1'
        }
        dataElement(name: 'Date of birth') {
            description 'Secondary identifier and allows analysis of provision by age'
            valueDomain(name: 'PERSON BIRTH DATE') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '2'
        }
        dataElement(name: 'Gender - current') {
            description 'To allow analysis by gender'
            valueDomain(name: 'PERSON GENDER CODE CURRENT') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '3'
        }
        dataElement(name: 'Ethnicity') {
            description 'To allow analysis by ethnicity using ONS categories'
            valueDomain(name: 'ETHNIC CATEGORY') {
                basedOn 'an2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '4'
        }
        dataElement(name: 'Patient postcode') {
            description 'To allow analysis of geographical patterns of care'
            valueDomain(name: 'POSTCODE OF USUAL ADDRESS') {
                basedOn 'max an8'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '5'
        }
        dataElement(name: 'Registered GP practice code') {
            description 'This allows reporting of commissioning'
            valueDomain(name: 'GENERAL MEDICAL PRACTICE CODE (PATIENT REGISTRATION)') {
                basedOn 'an6'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '6'
        }
        dataElement(name: 'Consultant GMC code') {
            description 'This allows identification of consultant team and patterns of management'
            valueDomain(name: 'CONSULTANT CODE (INITIATED SYSTEMIC ANTI-CANCER THERAPY)') {
                basedOn 'an8'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '7'
        }
        dataElement(name: 'Consultant speciality code') {
            description 'This facilitates analysis by speciality'
            valueDomain(name: 'CARE PROFESSIONAL MAIN SPECIALTY CODE') {
                basedOn 'an3'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '8'
        }
        dataElement(name: 'Organisation code of provider') {
            description 'This is the code of the provider initiating the programme of chemotherapy'
            valueDomain(name: 'ORGANISATION CODE (CODE OF PROVIDER)') {
                basedOn 'an3 or an5'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '9'
        }
    }
    model(name: 'Clinical Status') {
        dataElement(name: 'Primary diagnosis(ICD - 10)') {
            description 'ICD - 10: To allow for analysis by tumour site'
            valueDomain(name: 'PRIMARY DIAGNOSIS(ICD AT START SYSTEMIC ANTI - CANCER THERAPY)') {
                basedOn 'an6'
            }
            ext 'Usage', 'Mandatory or field 11'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '10'
        }
        dataElement(name: 'Morphology') {
            description 'ICD - O3: Is essential for some tumour sites e.g.haematology and lung, where ICD - 10 is inadequate'
            valueDomain(name: 'MORPHOLOGY(ICD - O AT START SYSTEMIC ANTI - CANCER THERAPY)') {
                basedOn 'an6'
            }
            ext 'Usage', 'Mandatory or field 10'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '11'
        }

        dataElement(name: 'Stage of disease') {
            description 'To allow analysis by stage, which is essential for outcome analysis'
            valueDomain(name: 'TNM CATEGORY(FINAL PRETREATMENT)') {
                basedOn 'an5'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '12'
        }
    }
    model(name: 'Programme and Regimen') {
        dataElement(name: 'Programme number') {
            description 'To allow for sequential analysis of patient care, may start at any number to account for previous treatment and is unique to each patient'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER THERAPY PROGRAMME NUMBER') {
                basedOn 'max n2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '13'
        }

        dataElement(name: 'Regimen number') {
            description 'To allow for sequential analysis of patient care, may start at any number to account for previous treatment and is unique to each patient'
            valueDomain(name: 'ANTI-CANCER REGIMEN NUMBER') {
                basedOn 'max n2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '14'
        }
        dataElement(name: 'Intent of treatment') {
            description 'To allow analysis by treatment intent'
            valueDomain(name: 'DRUG TREATMENT INTENT') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '15'
        }
        dataElement(name: 'Regimen') {
            description 'To be consistent with the National Regimen List (when established)'
            valueDomain(name: 'DRUG REGIMEN ACRONYM') {
                basedOn 'max an35'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '16'
        }
        dataElement(name: 'Height at start of regimen') {
            description 'To allow comparison of dose by metre²'
            valueDomain(name: 'PERSON HEIGHT IN METRES') {
                basedOn 'n1.max n2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '17'
        }
        dataElement(name: 'Weight at start of regimen') {
            description 'To allow comparison of dose by metre²'
            valueDomain(name: 'PERSON WEIGHT') {
                basedOn 'max n3.max n3'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '18'
        }
        dataElement(name: 'Performance status at start of regimen') {
            description 'WHO or Lansky - to allow casemix adjusted analysis'
            valueDomain(name: 'PERFORMANCE STATUS FOR (ADULT) or PERFORMANCE STATUS CODE (YOUNG PERSON)') {
                // TODO this shuld be declared as union of two value domains
                basedOn 'an1 or an2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '19'
        }

        dataElement(name: 'Co-morbidity adjustment') {
            description 'Yes/no -to allow casemix adjusted analysis'
            valueDomain(name: 'CO-MORBIDITY ADJUSTMENT INDICATOR') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '20'
        }
        dataElement(name: 'Date decision to treat') {
            description 'To allow analysis of wait before treatment start'
            valueDomain(name: ' DECISION TO TREAT DATE (ANTI-CANCER DRUG REGIMEN)') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '21'
        }
        dataElement(name: 'Start date of regimen') {
            description 'To allow analysis by time period'
            valueDomain(name: 'START DATE ANTI- CANCER DRUG REGIMEN') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '16'
        }
        dataElement(name: 'Clinical trial') {
            description 'Yes/no -to identify chemotherapy given within clinical trials'
            valueDomain(name: 'CLINICAL TRIAL INDICATOR') {
                basedOn 'an2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '23'
        }
        dataElement(name: 'Chemo-radiation') {
            description 'Yes/no - to identify use of chemo-radiation, only used where this is a recognised treatment regimen'
            valueDomain(name: 'CHEMO-RADIATION INDICATOR') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '24'
        }
        dataElement(name: 'Number of cycles planned') {
            description 'To allow comparison with number of cycles actually given. Not necessarily relevant for palliative treatment'
            valueDomain(name: 'NUMBER OF SYSTEMIC ANTI-CANCER THERAPY CYCLES PLANNED') {
                basedOn 'max n2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '25'
        }
    }
    model(name: 'Cycle') {
        dataElement(name: 'Cycle number') {
            description 'Sequential within each regimen and indicates the patient\'s progress through the regimen'
            valueDomain(name: 'ANTI-CANCER DRUG CYCLE IDENTIFIER') {
                basedOn 'max n2'
            }
            ext 'Usage', 'Mandatory'
            ext 'Min Occurs', '1'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '26'
        }
        dataElement(name: 'Start date of cycle') {
            description 'The date of the first administration in each cycle'
            valueDomain(name: 'START DATE (SYSTEMIC ANTI-CANCER DRUG CYCLE)') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '27'
        }
        dataElement(name: 'Weight at start of cycle') {
            description 'Where relevant to allow for recalculation of dose'
            valueDomain(name: 'PERSON WEIGHT') {
                basedOn 'max n3.max n3'
            }
            ext 'Usage', 'Optional'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '28'
        }
        dataElement(name: 'Performance status at start of cycle') {
            description 'To assess patient\'s suitability for further treatment'
            valueDomain(name: 'PERFORMANCE STATUS FOR (ADULT) or PERFORMANCE STATUS CODE (YOUNG PERSON)') {
                basedOn 'an1 or an2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '29'
        }
        dataElement(name: 'OPCS procurement code') {
            description 'PRIMARY PROCEDURE (OPCS)'
            valueDomain(name: 'To await final decision on PbR structure') {
                basedOn 'an4'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '30'
        }
    }
    model(name: 'Drug Details') {
        dataElement(name: 'Drug name') {
            description 'This is the approved name in the BNF. It identifies individual drug usage'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER DRUG NAME') {
                basedOn 'max an35'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '31'
        }
        dataElement(name: 'Actual dose per administration') {
            description 'For oral regimens this is the daily dose. Allows calculation of cumulative dose per patient and global drug usage'
            valueDomain(name: 'CHEMOTHERAPY ACTUAL DOSE') {
                basedOn 'max n7'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '32'
        }
        dataElement(name: 'Administration route') {
            description 'Pick list - to allow analysis by route of administration for each drug'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER THERAPY DRUG ROUTE OF ADMINISTRATION') {
                basedOn 'an2'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '33'
        }
        dataElement(name: 'Administration date') {
            description 'The date of actual administration'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER THERAPY ADMINISTRATION DATE') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '34'
        }
        dataElement(name: 'Organisation code of provider') {
            description 'This may change throughout a regimen. Allows analysis by provider'
            valueDomain(name: 'ORGANISATION CODE (CODE OF PROVIDER)') {
                basedOn 'an3 or an5'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '35'
        }
        dataElement(name: 'OPCS delivery code') {
            description 'To await final decision on PbR structure'
            valueDomain(name: 'PRIMARY PROCEDURE (OPCS)') {
                basedOn 'an4'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '36'
        }
    }
    model(name: 'Outcome') {
        dataElement(name: 'Date of final treatment') {
            description 'Date of the start of the final cycle'
            valueDomain(name: 'START DATE (FINAL SYSTEMIC ANTI-CANCER THERAPY)') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '37'
        }
        dataElement(name: 'Regimen modification - dose reduction') {
            description 'Yes/no - where a dose of any SACT drug is reduced at any cycle'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER THERAPY REGIMEN MODIFICATION INDICATOR (DOSE REDUCTION)') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '38'
        }
        dataElement(name: 'Regimen modification - time delay') {
            description 'Yes/no - where administration of drugs is delayed > 5 days at any cycle'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER THERAPY REGIMEN MODIFICATION INDICATOR (TIME DELAY)') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '39'
        }
        dataElement(name: 'Regimen modification - stopped early') {
            description 'Yes/no - where the regimen is abandoned before the planned number of cycles'
            valueDomain(name: 'SYSTEMIC ANTI-CANCER THERAPY REGIMEN MODIFICATION INDICATOR (DAYS REDUCED)') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '40'
        }
        dataElement(name: 'Regimen outcome summary') {
            description 'Six option pick list to summarise the regimen outcome'
            valueDomain(name: 'PLANNED TREATMENT CHANGE REASON') {
                basedOn 'an1'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '41'
        }
        dataElement(name: 'Date of death') {
            description 'To estimate 30-day mortality or to analyse survival after chemotherapy. May be sourced from ONS statistics'
            valueDomain(name: 'PERSON DEATH DATE') {
                basedOn 'an10 ccyy-mm-dd'
            }
            ext 'Usage', 'Required'
            ext 'Min Occurs', '0'
            ext 'Max Occurs', '1'
            ext 'Data Item Number', '42'
        }
    }
}