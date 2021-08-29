import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * MapReader reads XML file, convert contents of same <mapping></mapping> element to HashMap
 * for each K-V: Key is the text of client element; Value is the text of server element
 * then return the HashMap
 */
public class MapReader {

    private HashMap<String, String> myMap = new HashMap<String, String>();

    public HashMap<String, String> readMap() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new File("mapping.xml"));
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName("mapping");

        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node node = nList.item(temp);
            System.out.println("");    //Just a separator
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) node;
                String key = eElement.getElementsByTagName("client").item(0).getTextContent();
                String value = eElement.getElementsByTagName("server").item(0).getTextContent();
                myMap.put(key,value);
            }
        }

        return myMap;
    }
}
