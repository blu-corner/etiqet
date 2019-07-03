package com.neueda.etiqet.core.config.xml;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Generic XML parser for Etiqet. This class can be used to parse {@link com.neueda.etiqet.core.config.dtos.EtiqetConfiguration}s,
 * {@link com.neueda.etiqet.core.config.dtos.Messages}, and {@link com.neueda.etiqet.core.config.dtos.Protocol} objects
 * as well as validating instances of each of these against the Etiqet schema when instantiated via other means
 */
public class XmlParser {

    private static final Logger LOG = LoggerFactory.getLogger(XmlParser.class);

    /**
     * Name of the Etiqet schema file stored within the classpath
     */
    private static final String SCHEMA_FILE = "etiqet.protocol.xsd";

    /**
     * Unmarshalls the file at xmlPath into the class provided. This also performs schema validation in line with the
     * Etiqet schema in the classpath.
     *
     * Suppresses warnings of unchecked casts from the XML reader into the return type.
     *
     * @param xmlPath path to the XML file to be parsed
     * @param clazz Class reference of T
     * @param <T> Type of object to be returns
     * @return instance of the class provided as parse from the XML
     * @throws EtiqetException when an error occurs trying to parse <code>xmlPath</code> as an instance of
     * <code>clazz</code>
     * @see #getSchemaFile()
     */
    @SuppressWarnings("unchecked")
    public <T> T parse(String xmlPath, Class<T> clazz) throws EtiqetException {
        // Setup SAX parser for schema validation
        Schema schema = getSchema();
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setSchema(schema);

        try {
            // Create the JAXB Unmarshaller and Handler for getting the Protocol object from SAX
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setSchema(schema);
            UnmarshallerHandler handler = jaxbUnmarshaller.getUnmarshallerHandler();

            // Create the XMLReader and parse the protocol file
            XMLReader reader = spf.newSAXParser().getXMLReader();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new FileInputStream(new File(Environment.resolveEnvVars(xmlPath)))));

            return (T) handler.getResult();
        } catch (ParserConfigurationException | SAXException | JAXBException e) {
            String error = String.format("Exception thrown while parsing %s as %s", xmlPath, clazz.getName());
            LOG.error(error, e);
            throw new EtiqetException(error, e);
        } catch (IOException e) {
            String error = String.format("Exception thrown while trying to read %s", xmlPath);
            LOG.error(error, e);
            throw new EtiqetException(error, e);
        }
    }

    /**
     * Validates the instance comply with the Etiqet schema
     *
     * @param instance instance to be validated against the Etiqet schema
     * @param source where the instance originated from
     * @param <T> type of class to be validates
     * @throws EtiqetException when the object doesn't comply to the schema
     * @see #SCHEMA_FILE
     */
    public <T> void validate(T instance, Class<?> source) throws EtiqetException {
        try {
            Schema schema = getSchema();
            JAXBContext jaxbContext = JAXBContext.newInstance(instance.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setSchema(schema);
            // we don't care about the output, we just want to parse against the schema
            StringWriter writer = new StringWriter();
            marshaller.marshal(instance, writer);
        } catch (JAXBException e) {
            throw new EtiqetException("Unable to successfully validate the class " + source.getName()
                + " against " + SCHEMA_FILE, e);
        }
    }

    /**
     * Gets the Etiqet schema to be used when parsing XML
     *
     * @return Schema object referencing
     * @throws EtiqetException when the schema file cannot be found or parsed.
     * @see #getSchema()
     */
    private Schema getSchema() throws EtiqetException {
        // Create the schema object using XSD 1.1 - use of <xs:assert> requires 1.1
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
        URL protocolSchema = getClass().getClassLoader().getResource(getSchemaFile());
        if (protocolSchema == null) {
            throw new EtiqetException("Could not read " + getSchemaFile()
                + ", please check your classpath configuration");
        }
        Schema schema;
        try {
            schema = factory.newSchema(protocolSchema);
        } catch (SAXException e) {
            LOG.error("Error parsing schema", e);
            throw new EtiqetException("Error parsing schema", e);
        }

        return schema;
    }

    /**
     * Returns the file name of the XSD to be used to validate the XML document. Abstracted to help with unit testing
     *
     * @return name of the schema file
     */
    protected String getSchemaFile() {
        return SCHEMA_FILE;
    }
}
