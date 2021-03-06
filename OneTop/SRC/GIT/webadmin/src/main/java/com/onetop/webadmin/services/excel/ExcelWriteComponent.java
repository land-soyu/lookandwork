package com.onetop.webadmin.services.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class ExcelWriteComponent {

    private Workbook workbook;
    private Map<String, Object> model;
    private HttpServletResponse response;
    private int rowNum =0;

    public ExcelWriteComponent(Workbook workbook, Map<String, Object> model, HttpServletResponse response) {
        this.workbook = workbook;
        this.model = model;
        this.response = response;
    }

    public void create() {
        setFileName(response, mapToFileName());

        Sheet sheet = workbook.createSheet();

        createHead(sheet, mapToHeadList());

        createBody(sheet, mapToBodyList());
    }

    private String mapToFileName() {
        return (String) model.get(ExcelConstant.FILE_NAME);
    }

    private List<List<String>> mapToHeadList() {
        return (List<List<String>>) model.get(ExcelConstant.HEAD);
    }

    private List<List<String>> mapToBodyList() {
        return (List<List<String>>) model.get(ExcelConstant.BODY);
    }

    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + getFileExtension(fileName) + "\"");
    }

    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            fileName += ".xls";
        }

        return fileName;
    }

    private void createHead(Sheet sheet, List<List<String>> headList) {
        Row row = null;
        Cell cell = null;

        DataFormat format = workbook.createDataFormat();

        // ?????? ?????? ??????
        Font headFont = workbook.createFont();
        headFont.setFontHeightInPoints((short)11);
        headFont.setFontName("??????");
        headFont.setBold(true);

        // ?????? ??? ????????? ??????
        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setAlignment(HorizontalAlignment.CENTER);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headStyle.setFont(headFont);
        headStyle.setBorderBottom(BorderStyle.THIN);
        headStyle.setBorderLeft(BorderStyle.THIN);
        headStyle.setBorderRight(BorderStyle.THIN);
        headStyle.setBorderTop(BorderStyle.THIN);

        // ?????? ?????? ??????
        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 9);
        bodyFont.setFontName("??????");
        headFont.setBold(true);

        // Default ?????? ????????? ??????
        CellStyle bodyStyle = DefaultCreateCellStyle(workbook);
        bodyStyle.setFont(bodyFont);

        // Number ?????? ????????? ??????
        CellStyle bodyNumStyle = DefaultCreateCellStyle(workbook);
        bodyNumStyle.setFont(bodyFont);
        bodyNumStyle.setDataFormat(format.getFormat("#,##0"));

        // ????????? ?????? ????????? ??????
        CellStyle bodyDecimalStyle = DefaultCreateCellStyle(workbook);
        bodyDecimalStyle.setFont(bodyFont);
        bodyDecimalStyle.setDataFormat(format.getFormat("#,##0.0#######"));

        int cellCount = 1;
        for(int i = 0; i < headList.size(); i++) {
            row = sheet.createRow(rowNum);
            if(cellCount < headList.get(i).size()) cellCount = headList.get(i).size();
            for(int j = 0; j< headList.get(i).size(); j++){
                cell = row.createCell(j);
                cell.setCellStyle(headStyle);
                if(headList.get(i).get(j).startsWith(ExcelConstant.PREV_MERGE_CODE)){
                    row.getCell(j).setCellValue(headList.get(i).get(j).replace(ExcelConstant.PREV_MERGE_CODE, ""));
                }
                else{
                    row.getCell(j).setCellValue(headList.get(i).get(j));
                }


                // ????????? ?????? ??????
                // ????????? ??????
                if(rowNum > 0){
                    // ?????? ???????????? ????????? ??????????????? ??????
                    //if(sheet.getRow(rowNum - 1).getCell(j).getStringCellValue().equals(cell.getStringCellValue())){
                    if(!headList.get(i - 1).get(j).startsWith(ExcelConstant.PREV_MERGE_CODE) && headList.get(i - 1).get(j).equals(headList.get(i).get(j))){
                        // ?????? ?????? ????????? ????????? ??????
                        int oldRegionIdx = GetMergedRegionIdx(sheet, rowNum - 1, j);
                        if(oldRegionIdx >= 0){
                            CellRangeAddress oldRange = sheet.getMergedRegion(oldRegionIdx);
                            oldRange.setLastRow(rowNum);
                            sheet.removeMergedRegion(oldRegionIdx);
                            sheet.addMergedRegion(oldRange);
                        }
                        else{
                            sheet.addMergedRegion(
                                    // ?????? ???, ??? ???, ?????? ???, ??? ???
                                    new CellRangeAddress(
                                            rowNum - 1, rowNum , j, j
                                    )
                            );
                        }

                    }
                }
                // ????????? ??????
                if(j > 0){
                    // ?????? ????????? ????????? ??????????????? ??????
                    if(!headList.get(i).get(j - 1).startsWith(ExcelConstant.PREV_MERGE_CODE) && headList.get(i).get(j - 1).equals(headList.get(i).get(j))){
                        //if(sheet.getRow(rowNum).getCell(j - 1).getStringCellValue().equals(cell.getStringCellValue())){
                        int oldRegionIdx = GetMergedRegionIdx(sheet, rowNum, j - 1);
                        if(oldRegionIdx >= 0){
                            CellRangeAddress oldRange = sheet.getMergedRegion(oldRegionIdx);
                            oldRange.setLastColumn(j);
                            sheet.removeMergedRegion(oldRegionIdx);
                            sheet.addMergedRegion(oldRange);
                        }
                        else{
                            sheet.addMergedRegion(
                                    new CellRangeAddress(
                                            rowNum , rowNum, j -1, j
                                    )
                            );
                        }

                    }
                }

            }

            rowNum++;
        }

        for(int i = 0; i < cellCount; i++) {
            sheet.setColumnWidth(i, (int) (15 * 1.14388 * 256));
        }
    }

    private int GetMergedRegionIdx(Sheet sheet, int rowNum, int colNum){
        for(int i =0; i < sheet.getNumMergedRegions(); i++){
            if(sheet.getMergedRegion(i).isInRange(rowNum, colNum)){
                return i;
            }
        }
        return -1;
    }

    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), this.rowNum++);
        }
    }

    private void createRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);

        for (int i = 0; i < size; i++) {
            row.createCell(i).setCellValue(cellList.get(i));
        }
    }

    private CellStyle DefaultCreateCellStyle(Workbook workbook) {
        CellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.setWrapText(true);
        bodyStyle.setAlignment(HorizontalAlignment.CENTER);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setBorderTop(BorderStyle.THIN);
        return bodyStyle;
    }
}