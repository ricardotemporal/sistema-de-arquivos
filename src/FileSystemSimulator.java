import java.io.*;
import java.util.*;

public class FileSystemSimulator {
    private static final String BASE_FILE = "base.dat";

    private DirectoryEntry baseRoot;
    private final JournalManager journal;

    public FileSystemSimulator() {
        this.journal = new JournalManager();
        this.baseRoot = loadBase();
    }

    // ==== COMANDOS DE LEITURA ====

    public void ls(String path) {
        DirectoryEntry dir = navigateTo(path);
        if (dir == null) {
            System.out.println("Diretório não encontrado.");
            return;
        }

        boolean vazio = true;

        for (String name : dir.getDirectories().keySet()) {
            System.out.println("[DIR] " + name);
            vazio = false;
        }

        for (String name : dir.getFiles().keySet()) {
            System.out.println("[ARQ] " + name);
            vazio = false;
        }

        if (vazio) {
            System.out.println("[vazio]");
        }
    }

    // ==== COMANDOS DE ESCRITA ====

    public void mkdir(String path) {
        journal.log("MKDIR " + path);

        String[] parts = path.split("/");
        DirectoryEntry current = baseRoot;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            current.getDirectories().putIfAbsent(part, new DirectoryEntry(part));
            current = current.getDirectories().get(part);
        }

        saveBase();
        System.out.println("Diretório criado: " + path);
    }

    public void touch(String path, String content) {
        journal.log("TOUCH " + path + " " + content);

        int lastSlash = path.lastIndexOf("/");
        String dirPath = (lastSlash <= 0) ? "/" : path.substring(0, lastSlash);
        String fileName = path.substring(lastSlash + 1);

        DirectoryEntry dir = navigateTo(dirPath);
        if (dir == null) {
            System.out.println("Diretório não encontrado.");
            return;
        }

        if (dir.getFiles().containsKey(fileName)) {
            System.out.println("Arquivo já existe.");
            return;
        }

        dir.addFile(new FileEntry(fileName, content));
        saveBase();
        System.out.println("Arquivo criado: " + path);
    }

    public void rm(String path) {
        int lastSlash = path.lastIndexOf("/");
        String dirPath = (lastSlash <= 0) ? "/" : path.substring(0, lastSlash);
        String name = path.substring(lastSlash + 1);

        DirectoryEntry parent = navigateTo(dirPath);
        if (parent == null) {
            System.out.println("Caminho inválido.");
            return;
        }

        if (parent.getFiles().containsKey(name)) {
            journal.log("DELETE " + path);
            parent.removeFile(name);
            saveBase();
            System.out.println("Arquivo deletado: " + path);
        } else if (parent.getDirectories().containsKey(name)) {
            DirectoryEntry dir = parent.getDirectories().get(name);
            deleteDirectoryRecursively(dir);
            journal.log("RMDIR " + path);
            parent.removeDirectory(name);
            saveBase();
            System.out.println("Diretório (e tudo dentro) deletado: " + path);
        } else {
            System.out.println("Arquivo ou diretório não encontrado.");
        }
    }

    private void deleteDirectoryRecursively(DirectoryEntry dir) {
        for (DirectoryEntry sub : new ArrayList<>(dir.getDirectories().values())) {
            deleteDirectoryRecursively(sub);
        }
        dir.getDirectories().clear();
        dir.getFiles().clear();
    }

    public void rename(String path, String newName) {
        journal.log("RENAME " + path + " " + newName);

        int lastSlash = path.lastIndexOf("/");
        String dirPath = (lastSlash <= 0) ? "/" : path.substring(0, lastSlash);
        String oldName = path.substring(lastSlash + 1);

        DirectoryEntry dir = navigateTo(dirPath);
        if (dir == null) {
            System.out.println("Diretório pai não encontrado.");
            return;
        }

        if (dir.getFiles().containsKey(oldName)) {
            FileEntry oldFile = dir.getFiles().remove(oldName);
            dir.addFile(new FileEntry(newName, oldFile.getContent()));
            saveBase();
            System.out.println("Arquivo renomeado para: " + newName);
        } else if (dir.getDirectories().containsKey(oldName)) {
            DirectoryEntry oldDir = dir.getDirectories().remove(oldName);
            DirectoryEntry renamed = new DirectoryEntry(newName);
            for (FileEntry f : oldDir.getFiles().values()) {
                renamed.addFile(new FileEntry(f.getName(), f.getContent()));
            }
            for (DirectoryEntry d : oldDir.getDirectories().values()) {
                renamed.addDirectory(d.deepCopy());
            }
            dir.addDirectory(renamed);
            saveBase();
            System.out.println("Diretório renomeado para: " + newName);
        } else {
            System.out.println("Arquivo ou diretório não encontrado.");
        }
    }

    public void copy(String origem, String destino) {
        journal.log("COPY " + origem + " " + destino);

        int slashOrigem = origem.lastIndexOf("/");
        int slashDestino = destino.lastIndexOf("/");

        String origemDirPath = (slashOrigem <= 0) ? "/" : origem.substring(0, slashOrigem);
        String origemName = origem.substring(slashOrigem + 1);

        String destinoDirPath = (slashDestino <= 0) ? "/" : destino.substring(0, slashDestino);
        String destinoName = destino.substring(slashDestino + 1);

        DirectoryEntry origemDir = navigateTo(origemDirPath);
        DirectoryEntry destinoDir = navigateTo(destinoDirPath);

        if (origemDir == null || destinoDir == null) {
            System.out.println("Origem ou destino inválido.");
            return;
        }

        if (!origemDir.getFiles().containsKey(origemName)) {
            System.out.println("Arquivo de origem não encontrado.");
            return;
        }

        if (destinoDir.getFiles().containsKey(destinoName)) {
            System.out.println("Arquivo de destino já existe.");
            return;
        }

        FileEntry original = origemDir.getFiles().get(origemName);
        destinoDir.addFile(new FileEntry(destinoName, original.getContent()));
        saveBase();
        System.out.println("Arquivo copiado para: " + destino);
    }

    // ==== JORNAL ====

    public void showJournal() {
        journal.printLog();
    }

    public void closeJournal() {
        journal.close();
    }

    // ==== ÁRVORE DE ARQUIVOS ====

    public void printTree() {
        printTreeRecursive(baseRoot, "");
    }

    private void printTreeRecursive(DirectoryEntry dir, String prefix) {
        System.out.println(prefix + (prefix.isEmpty() ? "" : "└── ") + dir.getName());

        List<String> dirNames = new ArrayList<>(dir.getDirectories().keySet());
        List<String> fileNames = new ArrayList<>(dir.getFiles().keySet());

        for (String name : dirNames) {
            printTreeRecursive(dir.getDirectories().get(name), prefix + "    ");
        }

        for (String name : fileNames) {
            System.out.println(prefix + "    └── " + name);
        }
    }

    // ==== PERSISTÊNCIA BASE.DAT ====

    private void saveBase() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BASE_FILE))) {
            out.writeObject(baseRoot);
        } catch (IOException e) {
            System.err.println("Erro ao salvar base.dat: " + e.getMessage());
        }
    }

    private DirectoryEntry loadBase() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BASE_FILE))) {
            return (DirectoryEntry) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Nenhum base.dat encontrado. Inicializando com diretório vazio.");
            return new DirectoryEntry("/");
        }
    }

    // ==== NAVEGAÇÃO ====

    private DirectoryEntry navigateTo(String path) {
        if (path.equals("/") || path.isEmpty()) return baseRoot;

        String[] parts = path.split("/");
        DirectoryEntry current = baseRoot;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            current = current.getDirectories().get(part);
            if (current == null) return null;
        }

        return current;
    }
}
