package org.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    public static List<String[]> readCsv(String filePath, String delimiter) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(delimiter, -1);
                rows.add(tokens);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }
}