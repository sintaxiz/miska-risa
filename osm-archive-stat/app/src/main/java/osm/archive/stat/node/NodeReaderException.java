package osm.archive.stat.node;

public class NodeReaderException extends Exception {
    public NodeReaderException(Throwable cause) {
        super(cause);
    }

    public NodeReaderException(String message) {
        super(message);
    }

    public NodeReaderException() {
        super();
    }
}
