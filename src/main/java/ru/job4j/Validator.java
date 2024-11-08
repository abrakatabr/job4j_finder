package ru.job4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Validator {
    private Map<String, String> parameters = new HashMap<>();
    private StringBuilder massage;

    public Validator(String[] stringParameters) {
        String sep = System.lineSeparator();
        this.massage = new StringBuilder("Please enter the parameters in the form:").append(sep)
                .append("-d=dir -n=name -t=type -o=file").append(sep)
                .append("Where:").append(sep)
                .append("\"dir\" - Search directory").append(sep)
                .append("\"name\" - file name, mask, or regular expression").append(sep)
                .append("\"type\" - search type: \"mask\" search by mask, \"name\" by full name match,"
                       + " \"regex\" by regular expression").append(sep)
                .append("\"file\" - write the result to a file").append(sep);
        validateParameters(stringParameters);
    }

    private void validateParameters(String[] stringParameters) {
        if (stringParameters == null) {
            throw new IllegalArgumentException("parameters not entered\n" + massage);
        }
            for (String parameter : stringParameters) {
                String[] strings = parameter.split("=");
                parameters.put(strings[0], strings[1]);
            }
            if (parameters.size() != 4) {
                throw new IllegalArgumentException("parameters entered incorrectly\n" + massage);
            }
            validateDir();
            validateType();
            validateFileName();
            validateOutputFile();
    }

    private void validateDir() {
        if (parameters.containsKey("-d")) {
            String value = parameters.get("-d");
            Path file = Path.of(value);
            if (!Files.exists(file)) {
                throw new IllegalArgumentException("Directory not found\n" + massage);
            }
            if (!Files.isDirectory(file)) {
                throw new IllegalArgumentException("invalid directory\n" + massage);
            }
        } else {
            throw new IllegalArgumentException("missing parameter -d");
        }
    }

    private void  validateType() {
        if (parameters.containsKey("-t")) {
            Pattern pattern = Pattern.compile("mask|name|regex");
            Matcher matcher = pattern.matcher(parameters.get("-t"));
            if (!matcher.matches()) {
                throw new IllegalArgumentException("invalid search type");
            }
        } else {
            throw new IllegalArgumentException("missing parameter -t\n" + massage);
        }
    }

    private void validateFileName() {
        if (parameters.containsKey("-n")) {
            if ("name".equals(parameters.get("-t"))) {
                Pattern pattern = Pattern.compile("\\S+\\.[a-z]++");
                Matcher matcher = pattern.matcher(parameters.get("-n"));
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("invalid file name\n" + massage);
                }
            }
            if ("mask".equals(parameters.get("-t"))) {
                Pattern pattern = Pattern.compile("[*?]");
                Matcher matcher = pattern.matcher(parameters.get("-n"));
                if (!matcher.find()) {
                    throw new IllegalArgumentException("invalid mask\n" + massage);
                }
            }
        } else {
            throw new IllegalArgumentException("missing parameter -n\n" + massage);
        }
    }

    private void validateOutputFile() {
        Path path = Paths.get(parameters.get("-o"));
        if (!Files.exists(path.getParent())) {
            throw new IllegalArgumentException("Target file directory not found\n" + massage);
        }
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}
