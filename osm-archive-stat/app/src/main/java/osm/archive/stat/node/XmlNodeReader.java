package osm.archive.stat.node;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public class XmlNodeReader implements NodeReader {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    private final XMLStreamReader reader;

    private boolean hasNext;
    private Node lastNode;

    public XmlNodeReader(InputStream inputStream) throws XMLStreamException {
        this.reader = FACTORY.createXMLStreamReader(inputStream, "utf-8");

        try {
            lastNode = readNextNode();
        } catch (NodeReaderException e) {
            hasNext = false;
        }
    }

    private Node readNextNode() throws NodeReaderException {
        try {
            skip();
            if (reader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                throw new XMLStreamException();
            }
            Node node = new Node();
            if ("node".equals(reader.getLocalName())) {
                for (int i = 0; i < reader.getAttributeCount(); i++) {
                    String val = reader.getAttributeValue(i);
                    setNodeParam(node, i, val);
                }
            }
            readTags(node);
            hasNext = true;
            return node;
        } catch (XMLStreamException e) {
            hasNext = false;
            return null;
        }
    }

    @Override
    public Node read() throws NodeReaderException {
        if (!hasNext) {
            throw new NodeReaderException("No node in stream");
        }
        Node newNode = readNextNode();
        Node nodeToReturn = lastNode;
        lastNode = newNode;

        return nodeToReturn;
    }

    private void readTags(Node node) throws XMLStreamException {
        int event;
        event = reader.next();
        while (!reader.isEndElement()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (reader.getAttributeCount() != 2) {
                    throw new XMLStreamException("wrong attributes count in tag");
                }
                Tag tag = new Tag(reader.getAttributeValue(0), reader.getAttributeValue(1));
                node.addTag(tag);
            }
            event = reader.next();
        }
    }

    private void setNodeParam(Node node, int i, String val) throws NodeReaderException {
        switch (reader.getAttributeName(i).getLocalPart()) {
            case "id" -> node.setId(val);
            case "version" -> node.setVersion(val);
            case "timestamp" -> node.setTimestamp(val);
            case "uid" -> node.setUid(val);
            case "user" -> node.setUser(val);
            case "lat" -> node.setLat(val);
            case "lon" -> node.setLon(val);
            case "changeset" -> node.setChangeset(val);
            default -> throw new NodeReaderException("unexpected param for node: "
                    + reader.getAttributeName(i).getLocalPart());
        }
    }

    private void skip() throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && "node".equals(reader.getLocalName())) {
                break;
            }
        }
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return hasNext;
    }
}
