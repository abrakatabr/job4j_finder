package ru.job4j;

public class MainSearcher {
    public static void main(String[] args) {
        Validator validator = new Validator(args);
        FileSearcher searcher = new FileSearcher(validator.getParameters());
        searcher.fileSearch();
    }
}