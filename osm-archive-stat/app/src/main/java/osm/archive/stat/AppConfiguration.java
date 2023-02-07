package osm.archive.stat;

public class AppConfiguration {
    private boolean isArchive = true;
    private String path;

    public String path() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public void setArchive(boolean archive) {
        isArchive = archive;
    }
}
