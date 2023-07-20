package scot.gov.payment.service.worldpay;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Common code for the document builder factory to use when creatnig and parseing payment document from worldpay.
 * Just used to make sure we are setting the same features and attributtes to controle downloading of external DTD.
 */
public class DocumentBuilderFactorySource {

    private DocumentBuilderFactorySource() {
        // prevent object creation
    }

    static DocumentBuilderFactory get() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        // see https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
        // the section "if you can't completely disable DTDs:"
        documentBuilderFactory.setAttribute("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setAttribute("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return documentBuilderFactory;
    }
}
