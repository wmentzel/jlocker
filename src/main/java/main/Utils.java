package main;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Utils {
    public static void createExcelSheet(List<String> header, List<List<Object>> data, String filename) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        int columnCount = 0;

        // write header
        Row headerRow = sheet.createRow(0);
        for (String column : header) {
            Cell cell = headerRow.createCell(columnCount++);
            cell.setCellValue(column);
        }

        // write data
        int rowCount = 1;
        for (List<Object> columns : data) {
            Row row = sheet.createRow(rowCount++);
            columnCount = 0;

            for (Object column : columns) {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(column.toString());
            }
        }

        File xlsxSheetFile = new File(filename);

        System.out.println("Excel sheet " + filename + ".xlsx has been saved to " + xlsxSheetFile.getAbsolutePath());
        try (FileOutputStream outputStream = new FileOutputStream(xlsxSheetFile + ".xlsx")) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }
}
