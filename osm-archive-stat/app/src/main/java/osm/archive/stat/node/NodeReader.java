package osm.archive.stat.node;

import javax.xml.stream.XMLStreamException;

public interface NodeReader {
    Node read() throws NodeReaderException;

    boolean hasNext() throws XMLStreamException;
}
