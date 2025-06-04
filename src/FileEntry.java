import java.io.Serializable;

public class FileEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String content;

    public FileEntry(String name) {
        this.name = name;
        this.content = "";
    }

    public FileEntry(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    @Override
    public String toString() {
        return "[ARQ] " + name;
    }
}
