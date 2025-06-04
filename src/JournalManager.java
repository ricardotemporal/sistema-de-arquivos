import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JournalManager {
    private static final String LOG_FILE = "journal.log";
    private final BufferedWriter writer;

    public JournalManager() {
        try {
            FileWriter fw = new FileWriter(LOG_FILE, true); // modo append
            this.writer = new BufferedWriter(fw);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir o journal.log", e);
        }
    }

    public void log(String entry) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + entry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Erro ao gravar no journal: " + e.getMessage());
        }
    }

    public List<String> readEntries() {
        List<String> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                entries.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o journal: " + e.getMessage());
        }
        return entries;
    }

    public void printLog() {
        List<String> entries = readEntries();
        if (entries.isEmpty()) {
            System.out.println("[Journal está vazio]");
        } else {
            System.out.println("Conteúdo do journal.log:");
            for (String line : entries) {
                System.out.println(line);
            }
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar o journal: " + e.getMessage());
        }
    }
}
