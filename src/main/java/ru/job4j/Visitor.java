package ru.job4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class Visitor extends SimpleFileVisitor<Path> {

    private List<Path> fileList = new ArrayList<>();
    private Predicate<Path> predicate;

    public Visitor(Predicate<Path> predicate) {
        this.predicate = predicate;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (predicate.test(file)) {
            fileList.add(file);
        }
        return FileVisitResult.CONTINUE;
    }

    public List<Path> getFiles() {
        return fileList;
    }

    public Predicate<Path> getPredicate() {
        return this.predicate;
    }
}
