package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.UserSummaryOrdersDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.NONE)
public class ReportEightExcelGeneratorUtil {

    private static final String SHEET1_NAME = "Sheet1 - Выгрузить buy ордера";
    private static final String SHEET2_NAME = "Sheet2 - Выгрузить sell ордера";

    public static byte[] generate(List<UserSummaryOrdersDto> buyOrdersData,
                                  List<UserSummaryOrdersDto> sellOrdersData,
                                  Map<String, Pair<BigDecimal, BigDecimal>> ratesMap) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();

        CellStyle header1Style = getHeader1Style(workbook);
        CellStyle body1Style = getBode1Style(workbook);
        CellStyle footer1Style = getFooter1Style(workbook);
        CellStyle footer2Style = getFooter2Style(workbook);

        XSSFSheet sheet1 = workbook.createSheet(SHEET1_NAME);

        XSSFRow row;
        XSSFCell cell;

        row = sheet1.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Электронная почта");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Валютная пара");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Конвертируемая валюта");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Роль");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Buy");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Buy fee");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Курс ($)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Buy к USD");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Buy fee к USD");
        cell.setCellStyle(header1Style);

        sheet1.autoSizeColumn(0, true);
        sheet1.setColumnWidth(0, sheet1.getColumnWidth(0) + 256);
        sheet1.autoSizeColumn(1, true);
        sheet1.setColumnWidth(1, sheet1.getColumnWidth(1) + 256);
        sheet1.autoSizeColumn(2, true);
        sheet1.setColumnWidth(2, sheet1.getColumnWidth(2) + 256);
        sheet1.autoSizeColumn(3, true);
        sheet1.setColumnWidth(3, sheet1.getColumnWidth(3) + 256);
        sheet1.autoSizeColumn(4, true);
        sheet1.setColumnWidth(4, sheet1.getColumnWidth(4) + 256);
        sheet1.autoSizeColumn(5, true);
        sheet1.setColumnWidth(5, sheet1.getColumnWidth(5) + 256);
        sheet1.autoSizeColumn(6, true);
        sheet1.setColumnWidth(6, sheet1.getColumnWidth(6) + 256);
        sheet1.autoSizeColumn(7, true);
        sheet1.setColumnWidth(7, sheet1.getColumnWidth(7) + 256);
        sheet1.autoSizeColumn(8, true);
        sheet1.setColumnWidth(8, sheet1.getColumnWidth(8) + 256);

        int bound = buyOrdersData.size();

        //footer
        row = sheet1.createRow(1);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellFormula("SUM(H" + 3 + ":H" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(8, CellType.NUMERIC);
        cell.setCellFormula("SUM(I" + 3 + ":I" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        //body
        int i = 0;
        for (UserSummaryOrdersDto sod : buyOrdersData) {
            final String email = nonNull(sod.getEmail()) ? sod.getEmail() : StringUtils.EMPTY;
            final String currencyPairName = sod.getCurrencyPairName();
            final String currencyName = sod.getCurrencyName();
            final String role = sod.getRole();
            final double amountBuy = nonNull(sod.getAmount()) ? sod.getAmount().doubleValue() : 0;
            final double amountBuyFee = nonNull(sod.getCommission()) ? sod.getCommission().doubleValue() : 0;

            Pair<BigDecimal, BigDecimal> ratePair = ratesMap.get(currencyName);
            if (isNull(ratePair)) {
                ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
            }
            final double usdRate = ratePair.getLeft().doubleValue();

            row = sheet1.createRow(i + 2);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(email);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(currencyPairName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(currencyName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(role);
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(amountBuy);
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(amountBuyFee);
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(usdRate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellFormula("E" + (i + 3) + "*G" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellFormula("F" + (i + 3) + "*G" + (i + 3));
            cell.setCellStyle(body1Style);

            i++;
        }

        //footer
        row = sheet1.createRow((bound - 1) + 3);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellFormula("SUM(H" + 3 + ":H" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(8, CellType.NUMERIC);
        cell.setCellFormula("SUM(I" + 3 + ":I" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        //      -------------------------------------------------------------------------------

        XSSFSheet sheet2 = workbook.createSheet(SHEET2_NAME);

        row = sheet2.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Электронная почта");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Валютная пара");
        cell.setCellStyle(header1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Конвертируемая валюта");
        cell.setCellStyle(header1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Роль");
        cell.setCellStyle(header1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Sell");
        cell.setCellStyle(header1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Sell fee");
        cell.setCellStyle(header1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Курс ($)");
        cell.setCellStyle(header1Style);

        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Sell к USD");
        cell.setCellStyle(header1Style);

        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Sell fee к USD");
        cell.setCellStyle(header1Style);

        sheet2.autoSizeColumn(0, true);
        sheet2.setColumnWidth(0, sheet2.getColumnWidth(0) + 256);
        sheet2.autoSizeColumn(1, true);
        sheet2.setColumnWidth(1, sheet2.getColumnWidth(1) + 256);
        sheet2.autoSizeColumn(2, true);
        sheet2.setColumnWidth(2, sheet2.getColumnWidth(2) + 256);
        sheet2.autoSizeColumn(3, true);
        sheet2.setColumnWidth(3, sheet2.getColumnWidth(3) + 256);
        sheet2.autoSizeColumn(4, true);
        sheet2.setColumnWidth(4, sheet2.getColumnWidth(4) + 256);
        sheet2.autoSizeColumn(5, true);
        sheet2.setColumnWidth(5, sheet2.getColumnWidth(5) + 256);
        sheet2.autoSizeColumn(6, true);
        sheet2.setColumnWidth(6, sheet2.getColumnWidth(6) + 256);
        sheet2.autoSizeColumn(7, true);
        sheet2.setColumnWidth(7, sheet2.getColumnWidth(7) + 256);
        sheet2.autoSizeColumn(8, true);
        sheet2.setColumnWidth(8, sheet2.getColumnWidth(8) + 256);

        bound = sellOrdersData.size();

        //footer
        row = sheet2.createRow(1);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellFormula("SUM(H" + 3 + ":H" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(8, CellType.NUMERIC);
        cell.setCellFormula("SUM(I" + 3 + ":I" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        //body
        i = 0;
        for (UserSummaryOrdersDto sod : sellOrdersData) {
            final String email = nonNull(sod.getEmail()) ? sod.getEmail() : StringUtils.EMPTY;
            final String currencyPairName = sod.getCurrencyPairName();
            final String currencyName = sod.getCurrencyName();
            final String role = sod.getRole();
            final double amountSell = nonNull(sod.getAmount()) ? sod.getAmount().doubleValue() : 0;
            final double amountSellFee = nonNull(sod.getCommission()) ? sod.getCommission().doubleValue() : 0;

            Pair<BigDecimal, BigDecimal> ratePair = ratesMap.get(currencyName);
            if (isNull(ratePair)) {
                ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
            }
            final double usdRate = ratePair.getLeft().doubleValue();

            row = sheet2.createRow(i + 2);

            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(email);
            cell.setCellStyle(body1Style);

            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(currencyPairName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(currencyName);
            cell.setCellStyle(body1Style);

            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(role);
            cell.setCellStyle(body1Style);

            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(amountSell);
            cell.setCellStyle(body1Style);

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(amountSellFee);
            cell.setCellStyle(body1Style);

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(usdRate);
            cell.setCellStyle(body1Style);

            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellFormula("E" + (i + 3) + "*G" + (i + 3));
            cell.setCellStyle(body1Style);

            cell = row.createCell(8, CellType.NUMERIC);
            cell.setCellFormula("F" + (i + 3) + "*G" + (i + 3));
            cell.setCellStyle(body1Style);

            i++;
        }

        //footer
        row = sheet2.createRow((bound - 1) + 3);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Итого:");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("-");
        cell.setCellStyle(footer1Style);

        cell = row.createCell(7, CellType.NUMERIC);
        cell.setCellFormula("SUM(H" + 3 + ":H" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        cell = row.createCell(8, CellType.NUMERIC);
        cell.setCellFormula("SUM(I" + 3 + ":I" + ((bound - 1) + 3) + ")");
        cell.setCellStyle(footer2Style);

        //      -------------------------------------------------------------------------------

        XSSFSheet sheet3 = workbook.createSheet("Sheet3 - Summary");

        row = sheet3.createRow(0);

        //header
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Buy + Sell к USD");
        cell.setCellStyle(header1Style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Buy fee + Sell fee к USD");
        cell.setCellStyle(header1Style);

        sheet2.autoSizeColumn(0, true);
        sheet2.setColumnWidth(0, sheet2.getColumnWidth(0) + 256);
        sheet2.autoSizeColumn(1, true);
        sheet2.setColumnWidth(1, sheet2.getColumnWidth(1) + 256);

        row = sheet3.createRow(1);

        //body
        cell = row.createCell(0, CellType.NUMERIC);
        cell.setCellFormula("('" + SHEET1_NAME + "'!H2 + '" + SHEET2_NAME + "'!H2) / 2");
        cell.setCellStyle(body1Style);

        cell = row.createCell(1, CellType.NUMERIC);
        cell.setCellFormula("'" + SHEET1_NAME + "'!H2 + '" + SHEET2_NAME + "'!H2");
        cell.setCellStyle(body1Style);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            bos.close();
        } catch (IOException ex) {
            throw new Exception("Problem with convert workbook to byte array", ex);
        }
        return bos.toByteArray();
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

    private static CellStyle getFooter1Style(XSSFWorkbook workbook) {
        CellStyle footerStyle = workbook.createCellStyle();
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderRight(BorderStyle.THIN);
        footerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        footerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        footerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        font.setBold(true);
        footerStyle.setFont(font);

        return footerStyle;
    }

    private static CellStyle getFooter2Style(XSSFWorkbook workbook) {
        CellStyle footerStyle = workbook.createCellStyle();
        footerStyle.setBorderBottom(BorderStyle.THIN);
        footerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderLeft(BorderStyle.THIN);
        footerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderRight(BorderStyle.THIN);
        footerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setBorderTop(BorderStyle.THIN);
        footerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        footerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        footerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        footerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        font.setBold(true);
        footerStyle.setFont(font);

        return footerStyle;
    }
}
