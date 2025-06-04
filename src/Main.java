import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileSystemSimulator fs = new FileSystemSimulator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Simulador de Sistema de Arquivos");
        System.out.println("Comandos: mkdir, touch, ls, rm, rename, copy, showlog, tree, exit");
        System.out.println("Use caminhos como /empresa/documentos/arquivo.txt");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                fs.closeJournal();
                System.out.println("Encerrando o simulador...");
                break;
            }

            processCommand(input, fs);
        }

        scanner.close();
    }

    private static void processCommand(String input, FileSystemSimulator fs) {
        String[] tokens = input.split(" ", 3);
        String command = tokens[0].toLowerCase();

        try {
            switch (command) {
                case "mkdir":
                    if (tokens.length < 2) {
                        System.out.println("Uso: mkdir <caminho>");
                    } else {
                        fs.mkdir(tokens[1]);
                    }
                    break;

                case "touch":
                    if (tokens.length < 2) {
                        System.out.println("Uso: touch <caminho> [conteúdo]");
                    } else {
                        String caminho = tokens[1];
                        String conteudo = (tokens.length >= 3) ? tokens[2] : "";
                        fs.touch(caminho, conteudo);
                    }
                    break;

                case "ls":
                    if (tokens.length < 2) {
                        System.out.println("Uso: ls <caminho>");
                    } else {
                        fs.ls(tokens[1]);
                    }
                    break;

                case "rm":
                    if (tokens.length < 2) {
                        System.out.println("Uso: rm <caminho>");
                    } else {
                        fs.rm(tokens[1]);
                    }
                    break;

                case "rename":
                    if (tokens.length < 3) {
                        System.out.println("Uso: rename <caminho> <novo_nome>");
                    } else {
                        fs.rename(tokens[1], tokens[2]);
                    }
                    break;

                case "copy":
                    if (tokens.length < 3) {
                        System.out.println("Uso: copy <origem> <destino>");
                    } else {
                        fs.copy(tokens[1], tokens[2]);
                    }
                    break;

                case "showlog":
                    fs.showJournal();
                    break;

                case "tree":
                    fs.printTree();
                    break;

                default:
                    System.out.println("Comando desconhecido: " + command);
            }

        } catch (Exception e) {
            System.out.println("Erro ao executar comando: " + e.getMessage());
        }
    }
}
