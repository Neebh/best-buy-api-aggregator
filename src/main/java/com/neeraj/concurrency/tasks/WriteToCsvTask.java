package com.neeraj.concurrency.tasks;

import com.neeraj.concurrency.model.Store;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class WriteToCsvTask implements Function<List<Store>, String> {
    private String generateFileName() {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString + ".csv";

    }

    @Override
    public String apply(List<Store> stores) {
        FileWriter out = null;
        String fileName = generateFileName();
        try {
            out = new FileWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withSkipHeaderRecord())) {
                stores.stream().forEach((store) -> {
                    try {
                        printer.printRecord(store.getId(), store.getName(), store.getAddress(), store.getAddress2(), store.getCity(), store.getState(), store.getZip());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                printer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }
}
