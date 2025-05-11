package org.example.repository;

import org.apache.tomcat.jni.FileInfo;
import org.example.model.Discount;
import org.example.utils.CsvUtil;
import org.example.utils.DateUtil;
import org.example.utils.FileNameUtil;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DiscountRepository implements ItemRepository<Discount> {

    @Override
    public List<Discount> loadEntriesFromFile(String filePath) {
        List<Discount> discounts = new ArrayList<>();

        String fileName = new File(filePath).getName();
        FileNameUtil.FileInfo fileInfo = FileNameUtil.parseFileName(fileName);

        if (!fileInfo.isDiscountFile()) {
            System.out.println("Skipping non-discount files: " + fileName);
            return discounts;
        }

        LocalDate date = fileInfo.getDate();

        for (String[] row : CsvUtil.readCsv(filePath, ";")) {
            if (row.length < 9) continue;

            try {
                Discount discount = new Discount();
                discount.setProductId(row[0]);
                discount.setProductName(row[1]);
                discount.setBrand(row[2]);
                discount.setPackageQuantity(Double.parseDouble(row[3]));
                discount.setPackageUnit(row[4]);
                discount.setProductCategory(row[5]);
                discount.setStartingDate(DateUtil.parseDate(row[6]));
                discount.setEndingDate(DateUtil.parseDate(row[7]));
                discount.setPercentageOfDiscount(Double.parseDouble(row[8]));
                discounts.add(discount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return discounts;
    }
}
