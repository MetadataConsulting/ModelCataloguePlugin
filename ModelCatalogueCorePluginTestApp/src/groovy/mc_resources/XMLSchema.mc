/**
 * XMLSchema classification provides default XML data types and their value domains
 */
dataModel(name: 'XMLSchema') {
    id 'http://www.w3.org/2001/XMLSchema'
    description 'XML Schema provides standard types for describing your own XML formats'

    dataType name: 'xs:boolean', {
        id 'http://www.w3.org/2001/XMLSchema#boolean'
        description 'Binary-valued logic legal literals'
        enumerations: [
                   '0':    'False',
                   '1':    'True',
                   true:   'True',
                   false:  'False'
                ]
    }

    dataType name: 'xs:base64Binary', {
        id 'http://www.w3.org/2001/XMLSchema#base64Binary'
        description 'Base64-encoded arbitrary binary data'
        rule 'x ==~ /[a-zA-Z0-9=]*/'
    }

    dataType name: 'xs:hexBinary', {
        id 'http://www.w3.org/2001/XMLSchema#hexBinary'
        description 'Arbitrary hex-encoded binary data. Example, "0FB7" is a hex encoding for 16-bit int 4023 (binary 111110110111).'
        rule 'x ==~ /[a-fA-F0-9]*/'
    }

    dataType name: 'xs:anyURI', {
        id 'http://www.w3.org/2001/XMLSchema#anyURI'
        description 'A Uniform Resource Identifier Reference (URI). Can be absolute or relative, and may have an optional fragment identifier.'
        rule 'is URI'
    }

    dataType name: 'xs:string', {
        id 'http://www.w3.org/2001/XMLSchema#string'
        description 'Character strings in XML.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            true && (x = parseString(string(x)))
        '''
    }

    dataType name: 'xs:normalizedString', {
        id 'http://www.w3.org/2001/XMLSchema#normalizedString'
        description 'White space normalized strings'
        basedOn 'xs:string'
        rule '!(x =~ /[\\r\\n\\t]/)'
    }

    dataType name: 'xs:token', {
        id 'http://www.w3.org/2001/XMLSchema#token'
        description 'Tokenized strings.'
        basedOn 'xs:normalizedString'
        rule '!(x =~ /\\s+/)'
    }

    dataType name: 'xs:language', {
        id 'http://www.w3.org/2001/XMLSchema#language'
        description 'Tokenized strings.'
        basedOn 'xs:token'
        rule 'maxLength(2) && new Locale(x)'
    }

    dataType name: 'xs:decimal', {
        id 'http://www.w3.org/2001/XMLSchema#decimal'
        description 'Arbitrary precision decimal numbers. Sign omitted, “+” is assumed. Leading and trailing zeroes are optional. If the fractional part is zero, the period and following zero(es) can be omitted.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseDecimal(string(x)) in BigDecimal
        '''
    }

    dataType name: 'xs:double', {
        id 'http://www.w3.org/2001/XMLSchema#double'
        description 'Double-precision 64-bit floating point type legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseDouble(string(x)) in Double
        '''
    }

    dataType name: 'xs:float', {
        id 'http://www.w3.org/2001/XMLSchema#float'
        description 'Double-precision 32-bit floating point type legal literals {0, -0, INF, -INF and NaN} Example, -1E4, 12.78e-2, 12 and INF'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseFloat(string(x)) in Float
        '''
    }

    dataType name: 'xs:integer', {
        id 'http://www.w3.org/2001/XMLSchema#integer'
        description 'Integer or whole numbers - Sign omitted, “+” is assumed. Example: -1, 0, 12678967543233, +100000'
        basedOn 'xs:decimal'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseInteger(string(x)) in BigInteger
        '''
    }

    dataType name: 'xs:long', {
        id 'http://www.w3.org/2001/XMLSchema#long'
        description '9223372036854775807 to -9223372036854775808. Sign omitted, “+” assumed. Example: -1, 0, 12678967543233, +100000.'
        basedOn 'xs:integer'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseLong(string(x)) in Long
        '''
    }

    dataType name: 'xs:int', {
        id 'http://www.w3.org/2001/XMLSchema#int'
        description '2147483647 to -2147483648. Sign omitted, “+” is assumed. Example: -1, 0, 126789675, +100000.'
        basedOn 'xs:long'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseInt(string(x)) in Integer
        '''
    }

    dataType name: 'xs:short', {
        id 'http://www.w3.org/2001/XMLSchema#short'
        description '32767 to -32768. Sign omitted, “+” assumed. Example: -1, 0, 12678, +10000.'
        basedOn 'xs:int'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseShort(string(x)) in Short
        '''
    }

    dataType name: 'xs:byte', {
        id 'http://www.w3.org/2001/XMLSchema#byte'
        description '127 to-128. Sign is omitted, “+” assumed. Example: -1, 0, 126, +100.'
        basedOn 'xs:short'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseByte(string(x)) in Byte
        '''
    }

    dataType name: 'xs:nonNegativeInteger', {
        id 'http://www.w3.org/2001/XMLSchema#nonNegativeInteger'
        description 'Infinite set {0, 1, 2,...}. Sign omitted, “+” assumed. Example: 1, 0, 12678967543233, +100000.'
        basedOn 'xs:integer'
        rule 'minInclusive(0)'
    }

    dataType name: 'xs:nonPositiveInteger', {
        id 'http://www.w3.org/2001/XMLSchema#nonPositiveInteger'
        description 'Infinite set {...,-2,-1,0}. Example: -1, 0, -126733, -100000.'
        basedOn 'xs:integer'
        rule 'maxInclusive(0)'
    }

    dataType name: 'xs:negativeInteger', {
        id 'http://www.w3.org/2001/XMLSchema#negativeInteger'
        description 'Infinite set {...,-2,-1}. Example: -1, -12678967543233, -100000'
        basedOn 'xs:nonPositiveInteger'
        rule 'maxExclusive(0)'
    }

    dataType name: 'xs:positiveInteger', {
        id 'http://www.w3.org/2001/XMLSchema#positiveInteger'
        description 'Infinite set {1, 2,...}. Optional “+” sign,. Example: 1, 12678967543233, +100000.'
        basedOn 'xs:nonNegativeInteger'
        rule 'minExclusive(0)'
    }

    dataType name: 'xs:unsignedLong', {
        id 'http://www.w3.org/2001/XMLSchema#unsignedLong'
        description '0 to 18446744073709551615. Example: 0, 12678967543233, 100000.'
        basedOn 'xs:nonNegativeInteger'
        rule 'minInclusive(0) && maxInclusive(18446744073709551615)'
    }

    dataType name: 'xs:unsignedInt', {
        id 'http://www.w3.org/2001/XMLSchema#unsignedInt'
        description '0 to 4294967295'
        basedOn 'xs:unsignedLong'
        rule 'minInclusive(0) && maxInclusive(4294967295)'
    }

    dataType name: 'xs:unsignedShort', {
        id 'http://www.w3.org/2001/XMLSchema#unsignedShort'
        description '0 to 65535. Example: 0, 12678, 10000.'
        basedOn 'xs:unsignedInt'
        rule 'minInclusive(0) && maxInclusive(65535)'
    }

    dataType name: 'xs:unsignedByte', {
        id 'http://www.w3.org/2001/XMLSchema#unsignedByte'
        description '0 to 255. a finite-length Example: 0, 126, 100.'
        basedOn 'xs:unsignedShort'
        rule 'minInclusive(0) && maxInclusive(255)'
    }

    dataType name: 'xs:date', {
        id 'http://www.w3.org/2001/XMLSchema#date'
        description 'Calendar date.Format YYYY-MM-DD. Example, May the 31st, 1999 is: 1999-05-31.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseDateTime(string(x)) in Calendar
        '''
    }

    dataType name: 'xs:dateTime', {
        id 'http://www.w3.org/2001/XMLSchema#dateTime'
        description 'Specific instant of time. ISO 8601 extended format YYYY-MM-DDThh:mm:ss. Example, to indicate 1:20 pm on May the 31st, 1999 for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC): 1999-05-31T13:20:00-05:00.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseDateTime(string(x)) in Calendar
        '''
    }

    dataType name: 'xs:time', {
        id 'http://www.w3.org/2001/XMLSchema#time'
        description 'An instant of time that recurs every day. Example, 1:20 pm for Eastern Standard Time which is 5 hours behind Coordinated Universal Time (UTC), write: 13:20:00-05:00.'
        rule '''
            import static javax.xml.bind.DatatypeConverter.*

            parseTime(string(x)) in Calendar
        '''
    }

    dataType name: 'xs:duration', {
        id 'http://www.w3.org/2001/XMLSchema#duration'
        description 'A duration of time. ISO 8601 extended format PnYnMnDTnHnMnS. Example, to indicate duration of 1 year, 2 months, 3 days, 10 hours, and 30 minutes: P1Y2M3DT10H30M. One could also indicate a duration of minus 120 days as: -P120D.'
        rule 'x ==~ /-?P\\d+Y(\\d+M(\\d+D(T\\d+H(\\d+M(\\d+S)?)?)?)?)?/'
    }

    dataType name: 'xs:gDay', {
        id 'http://www.w3.org/2001/XMLSchema#gDay'
        description 'Gregorian day. Example a day such as the 5th of the month is 05.'
        rule 'date("dd") in Date'
    }

    dataType name: 'xs:gMonth', {
        id 'http://www.w3.org/2001/XMLSchema#gMonth'
        description 'Gregorian month. Example: May is 05.'
        rule 'date("MM") in Date'
    }

    dataType name: 'xs:gMonthDay', {
        id 'http://www.w3.org/2001/XMLSchema#gMonthDay'
        description 'Gregorian specific day in a month. Example: Feb 5 is 02-05.'
        rule 'date("MM-dd") in Date'
    }

    dataType name: 'xs:gYear', {
        id 'http://www.w3.org/2001/XMLSchema#gYear'
        description 'Gregorian calendar year. Example, year 1999, write: 1999.'
        rule 'date("yyyy") in Date'
    }

    dataType name: 'xs:gYearMonth', {
        id 'http://www.w3.org/2001/XMLSchema#gYearMonth'
        description 'Specific gregorian month and year. Example, May 1999, write: 1999-05.'
        rule 'date("yyyy-MM") in Date'
    }

    dataType name: "xs:QName", {
        id 'http://www.w3.org/2001/XMLSchema#QName'
        description "QName represents XML qualified names. The value space of QName is the set of tuples {namespace name, local part}, where namespace name is an anyURI and local part is an NCName."
    }

    dataType name: "xs:NOTATION", {
        id 'http://www.w3.org/2001/XMLSchema#NOTATION'
        description "NOTATION represents the NOTATION attribute type from [XML 1.0 (Second Edition)]. The value space of NOTATION is the set of QNames of notations declared in the current schema."
    }

}