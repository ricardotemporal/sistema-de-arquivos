import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DirectoryEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Map<String, FileEntry> files = new HashMap<>();
    private Map<String, DirectoryEntry> directories = new HashMap<>();

    public DirectoryEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, FileEntry> getFiles() {
        return files;
    }

    public Map<String, DirectoryEntry> getDirectories() {
        return directories;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void addFile(FileEntry file) {
        files.put(file.getName(), file);
    }

    public void addDirectory(DirectoryEntry dir) {
        directories.put(dir.getName(), dir);
    }

    public void removeFile(String name) {
        files.remove(name);
    }

    public void removeDirectory(String name) {
        directories.remove(name);
    }

    public DirectoryEntry deepCopy() {
        DirectoryEntry copy = new DirectoryEntry(name);
        for (FileEntry f : files.values()) {
            copy.addFile(new FileEntry(f.getName(), f.getContent()));
        }
        for (DirectoryEntry d : directories.values()) {
            copy.addDirectory(d.deepCopy());
        }
        return copy;
    }

    @Override
    public String toString() {
        return "[DIR] " + name;
    }
}
