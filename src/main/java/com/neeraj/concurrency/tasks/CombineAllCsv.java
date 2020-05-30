package com.neeraj.concurrency.tasks;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.List;

public class CombineAllCsv implements Runnable {

    private List<String> csvFileNames;

    public CombineAllCsv(List<String> csvFileNames) {
        this.csvFileNames = csvFileNames;
    }

    @Override
    public void run() {
        System.out.println("Combine CSV Task Thread Name  " + Thread.currentThread().getName());
        File outFileName = new File("stores.csv");
        if (outFileName.exists()) {
            outFileName.delete();
        }
        try {
            FileWriter out = new FileWriter(outFileName);
            CSVPrinter printer = new CSVPrinter(out,
                    CSVFormat.TDF.withHeader("Id", "Name", "Address", "Address2", "City", "State", "Zip")
            );


            csvFileNames.stream().forEach((fileName) -> {
                Reader in = null;
                File file = new File(fileName);
                try {
                    in = new FileReader(file);
                    CSVParser csvParser = CSVFormat.DEFAULT
                            .withSkipHeaderRecord()
                            .parse(in);

                    List<CSVRecord> records = csvParser.getRecords();
                    records.stream().forEach((record) -> {
                        try {
                            printer.printRecord(record.get(0), record.get(1), record.get(2), record.get(3), record.get(4), record.get(5), record.get(6));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                    printer.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    file.delete();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        System.out.println("Generated stores.csv complete" );
    }
}
