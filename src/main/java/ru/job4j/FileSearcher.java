package ru.job4j;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileSearcher {
    private File startDir;
    private String fileName;
    private String searchType;
    private File outputFile;

    public FileSearcher(Map<String, String> parameters) {
        this.startDir = new File(parameters.get("-d"));
        this.fileName = parameters.get("-n");
        this.searchType = parameters.get("-t");
        this.outputFile = new File(parameters.get("-o"));
    }

    void fileSearch() {
        Visitor visitor = switch (searchType) {
            case "name" -> searchByName();
            case "regex" -> searchByRegex();
            case "mask" -> searchByMask();
            default -> throw new IllegalArgumentException("incorrect search type");
        };
        try {
            Files.walkFileTree(startDir.toPath(), visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        write(visitor);
    }

    private Visitor searchByName() {
        Visitor visitor = new Visitor(path -> fileName.equals(path.getFileName().toString()));
        try {
            Files.walkFileTree(startDir.toPath(), visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return visitor;
    }

    private Visitor searchByRegex() {
        Pattern pattern = Pattern.compile(fileName);
        Visitor visitor = new Visitor(path -> {
            Matcher matcher = pattern.matcher(path.getFileName().toString());
            return matcher.matches();
        });
        return visitor;
    }

    private Visitor searchByMask() {
        String regex = fileName.replace(".", "\\.").replace("?", "\\.")
                .replace("*", "\\S+");
        Pattern pattern = Pattern.compile(regex);
        Visitor visitor = new Visitor(path -> {
            Matcher matcher = pattern.matcher(path.getFileName().toString());
            return matcher.matches();
        });
        return visitor;
    }

    private void write(Visitor visitor) {
        List<Path> fileList = visitor.getFiles();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                fileList.stream()
                        .forEach(file -> {
                            try {
                                writer.write(file.getFileName().toString());
                                writer.write(System.lineSeparator());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
