package org.modelcatalogue.core.dataarchitect

import org.xml.sax.SAXException
import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class SchemaValidatorService {

    static transactional = false

    String validateSchema(InputStream schemaStream, InputStream markupFile) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
            Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream))
            Source xml    = new StreamSource(markupFile)
            Validator validator = schema.newValidator();
            validator.validate(xml);
            return "File is VALID"
        } catch (SAXException e) {
            return "File is INVALID\nReason: ${e.localizedMessage}"
        }
    }
}
