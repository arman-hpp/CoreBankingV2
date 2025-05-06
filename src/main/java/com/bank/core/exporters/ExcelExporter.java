package com.bank.core.exporters;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExcelExporter implements IExporter {
    @Async
    public <T> void export(HttpServletResponse response, Class<T> clazz, List<T> list) throws IOException {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(clazz.getName());

        var rowNum = 0;
        var headerRow = sheet.createRow(rowNum);
        var headerStyle = workbook.createCellStyle();
        var headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeight(16);
        headerStyle.setFont(headerFont);

        var fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            var fieldName = fields[i].getName();

            sheet.autoSizeColumn(i);
            createCell(headerRow, i, fieldName, headerStyle);
        }

        rowNum = 1;
        var style = workbook.createCellStyle();
        var font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (var item : list) {
            var row = sheet.createRow(rowNum++);
            int columnCount = 0;

            for (var field : fields) {
                field.setAccessible(true);

                Object fieldValue = null;
                try {
                    fieldValue = field.get(item);
                } catch (IllegalAccessException ex) {
                    // ignore
                }
                createCell(row, columnCount++, fieldValue, style);
            }
        }

        var outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.flush();
        outputStream.close();
    }

    private static void createCell(Row row, int columnCount, Object value, CellStyle style) {
        var cell = row.createCell(columnCount);

        switch (value) {
            case Integer i -> cell.setCellValue(i);
            case Long l -> cell.setCellValue(l);
            case LocalDateTime ldt -> cell.setCellValue(ldt);
            case LocalDate ld -> cell.setCellValue(ld);
            case Boolean b -> cell.setCellValue(b);
            case null, default -> cell.setCellValue(String.valueOf(value));
        }

        cell.setCellStyle(style);
    }
}
