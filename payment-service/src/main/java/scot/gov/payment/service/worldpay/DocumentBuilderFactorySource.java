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

        // prevent external dtd from being fetched
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory;
    }
}
