classification(name: 'XMLSchema', namespace: 'http://www.w3.org/2001/XMLSchema') {
    id 'http://www.w3.org/2001/XMLSchema'

    // data types are missing the link to the XMLSchema, but the all have xs prefix so no name clash should happen
    globalSearchFor dataType

    // if not set explicitly, the data type with the same name and description is created for value domains
    automatic dataType

    valueDomain name: 'xs:boolean', {
        id 'http://www.w3.org/2001/XMLSchema#boolean'
        description 'Binary-valued logic legal literals'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseBoolean(string(x)))
        '''
        dataType enumerations: [
                '0':    'False',
                '1':    'True',
                true:   'True',
                false:  'False'
        ]
    }

    valueDomain name: 'xs:base64Binary', {
        id 'http://www.w3.org/2001/XMLSchema#base64Binary'
        description 'Base64-encoded arbitrary binary data'
        rule 'x ==~ /[a-zA-Z0-9=]*/'
    }

    valueDomain name: 'xs:hexBinary', {
        id 'http://www.w3.org/2001/XMLSchema#hexBinary'
        description 'Arbitrary hex-encoded binary data. Example, "0FB7" is a hex encoding for 16-bit int 4023 (binary 111110110111).'
        rule 'x ==~ /[a-fA-F0-9]*/'
    }

    valueDomain name: 'xs:anyURI', {
        id 'http://www.w3.org/2001/XMLSchema#anyURI'
        description 'A Uniform Resource Identifier Reference (URI). Can be absolute or relative, and may have an optional fragment identifier.'
        rule 'is URI'
    }

    valueDomain name: 'xs:string', {
        id 'http://www.w3.org/2001/XMLSchema#string'
        description 'Character strings in XML.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseString(string(x)))
        '''
    }

    valueDomain name: 'xs:normalizedString', {
        id 'http://www.w3.org/2001/XMLSchema#normalizedString'
        description 'White space normalized strings'
        basedOn 'xs:string'
        rule 'string(x)?.replace(/\\s/, " ")'
    }

    valueDomain name: 'xs:token', {
        id 'http://www.w3.org/2001/XMLSchema#token'
        description 'Tokenized strings.'
        basedOn 'xs:normalizedString'
        rule 'string(x)?.replaceAll(/\\s+/, " ")'
    }

    valueDomain name: 'xs:language', {
        id 'http://www.w3.org/2001/XMLSchema#language'
        description 'Tokenized strings.'
        basedOn 'xs:token'
        rule 'string(x)?.size() <=2 && new Locale(x)'
    }

    valueDomain name: 'xs:decimal', {
        id 'http://www.w3.org/2001/XMLSchema#decimal'
        description 'Arbitrary precision decimal numbers. Sign omitted, “+” is assumed. Leading and trailing zeroes are optional. If the fractional part is zero, the period and following zero(es) can be omitted.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseDecimal(string(x)))
        '''
    }

    valueDomain name: 'xs:double', {
        id 'http://www.w3.org/2001/XMLSchema#double'
        description 'Double-precision 64-bit floating point type legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseDouble(string(x)))
        '''
    }

    valueDomain name: 'xs:float', {
        id 'http://www.w3.org/2001/XMLSchema#float'
        description 'Double-precision 32-bit floating point type legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseFloat(string(x)))
        '''
    }

    valueDomain name: 'xs:date', {
        id 'http://www.w3.org/2001/XMLSchema#date'
        description 'Calendar date.Format YYYY-MM-DD. Example, May the 31st, 1999 is: 1999-05-31.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseDateTime(string(x))?.time)
        '''
    }

    valueDomain name: 'xs:dateTime', {
        id 'http://www.w3.org/2001/XMLSchema#dateTime'
        description 'Specific instant of time. ISO 8601 extended format YYYY-MM-DDThh:mm:ss. Example, to indicate 1:20 pm on May the 31st, 1999 for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC): 1999-05-31T13:20:00-05:00.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseDateTime(string(x))?.time)
        '''
    }

    valueDomain name: 'xs:time', {
        id 'http://www.w3.org/2001/XMLSchema#time'
        description 'An instant of time that recurs every day. Example, 1:20 pm for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC), write: 13:20:00-05:00.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            (x = parseTime(string(x))?.time)
        '''
    }

    valueDomain name: 'xs:duration', {
        id 'http://www.w3.org/2001/XMLSchema#duration'
        description 'A duration of time. ISO 8601 extended format PnYnMnDTnHnMnS. Example, to indicate duration of 1 year, 2 months, 3 days, 10 hours, and 30 minutes: P1Y2M3DT10H30M. One could also indicate a duration of minus 120 days as: -P120D.'
        rule 'x ==~ /-?P\\d+Y(\\d+M(\\d+D(T\\d+H(\\d+M(\\d+S)?)?)?)?)?/'
    }

    valueDomain name: 'xs:gDay', {
        id 'http://www.w3.org/2001/XMLSchema#gDay'
        description 'Gregorian day. Example a day such as the 5th of the month is 05.'
        rule 'date("dd")'
    }

    valueDomain name: 'xs:gMonth', {
        id 'http://www.w3.org/2001/XMLSchema#gMonth'
        description 'Gregorian month. Example: May is 05.'
        rule 'date("MM")'
    }

    valueDomain name: 'xs:gMonthDay', {
        id 'http://www.w3.org/2001/XMLSchema#gMonthDay'
        description 'Gregorian specific day in a month. Example: Feb 5 is 02-05.'
        rule 'date("MM-dd")'
    }

    valueDomain name: 'xs:gYear', {
        id 'http://www.w3.org/2001/XMLSchema#gYear'
        description 'Gregorian calendar year. Example, year 1999, write: 1999.'
        rule 'date("yyyy")'
    }

    valueDomain name: 'xs:gYearMonth', {
        id 'http://www.w3.org/2001/XMLSchema#gYearMonth'
        description 'Specific gregorian month and year. Example, May 1999, write: 1999-05.'
        rule 'date("yyyy-MM")'
    }

    valueDomain name: "xs:QName", {
        id 'http://www.w3.org/2001/XMLSchema#QName'
        description "QName represents XML qualified names. The value space of QName is the set of tuples {namespace name, local part}, where namespace name is an anyURI and local part is an NCName."
    }

    valueDomain name: "xs:NOTATION", {
        id 'http://www.w3.org/2001/XMLSchema#NOTATION'
        description "NOTATION represents the NOTATION attribute type from [XML 1.0 (Second Edition)]. The value space of NOTATION is the set of QNames of notations declared in the current schema."
    }

}