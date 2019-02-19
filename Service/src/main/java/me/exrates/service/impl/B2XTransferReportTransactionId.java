package me.exrates.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.CurrencyReportInfoDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class B2XTransferReportTransactionId {

    private static final String SHEET1_NAME = "Транзакции трансфера с ноды на резервный кошелек";

    private static boolean success;

    public static boolean generate(List<String> transactionsId) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);

        XSSFSheet sheet1 = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet1.createRow(0);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("№:");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Transaction id");
        cell.setCellStyle(header1Style);


        sheet1.autoSizeColumn(0, false);
        sheet1.setColumnWidth(0, sheet1.getColumnWidth(0) + 256);
        sheet1.autoSizeColumn(1, false);
        sheet1.setColumnWidth(1, sheet1.getColumnWidth(1) + 256);

        //body
        int i = 0;
        for (String transaction : transactionsId) {
            row = sheet1.createRow(i + 1);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(transaction);
            cell.setCellStyle(body1Style);

            i++;
        }

        FileOutputStream bos = new FileOutputStream("/b2x_transactions_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss")) + ".xlsx");

        try {
            workbook.write(bos);
            bos.close();

            success = true;
        } catch (IOException ex) {
            success = false;
            throw new Exception("Problem with convert workbook to byte array", ex);
        }
        return success;
    }

    private static CellStyle getHeader1Style(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        font.setBold(true);
        headerStyle.setFont(font);

        headerStyle.setWrapText(true);

        return headerStyle;
    }

    private static CellStyle getBode1Style(XSSFWorkbook workbook) {
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        bodyStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        bodyStyle.setFont(font);

        return bodyStyle;
    }

}
