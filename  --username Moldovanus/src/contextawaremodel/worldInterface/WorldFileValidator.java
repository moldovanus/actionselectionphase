package contextawaremodel.worldInterface;

import contextawaremodel.WorldFileValidationException;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.validation.Validator;
import java.io.*;

public class WorldFileValidator {

    public void validateXml(Schema schema, String xmlName) throws WorldFileValidationException {
        try {
            // creating a Validator instance
            Validator validator = schema.newValidator();

            // preparing the XML file as a SAX source
            SAXSource source = new SAXSource( new InputSource(new java.io.FileInputStream(xmlName)));

            // validating the SAX source against the schema
            validator.validate(source);

        } catch (Exception e) {
            throw new WorldFileValidationException(e.toString());
        }
    }

    public Schema loadSchema(String name) throws WorldFileValidationException {
        Schema schema = null;
        try {
            String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
            SchemaFactory factory = SchemaFactory.newInstance(language);
            schema = factory.newSchema(new File(name));
        } catch (Exception e) {
            throw new WorldFileValidationException(e.toString());
        }
        return schema;
    }
}