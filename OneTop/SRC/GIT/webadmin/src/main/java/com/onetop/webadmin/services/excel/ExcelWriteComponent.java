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

        // 헤드 폰트 설정
        Font headFont = workbook.createFont();
        headFont.setFontHeightInPoints((short)11);
        headFont.setFontName("돋움");
        headFont.setBold(true);

        // 헤드 셀 스타일 설정
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

        // 바디 폰트 설정
        Font bodyFont = workbook.createFont();
        bodyFont.setFontHeightInPoints((short) 9);
        bodyFont.setFontName("돋움");
        headFont.setBold(true);

        // Default 바디 스타일 설정
        CellStyle bodyStyle = DefaultCreateCellStyle(workbook);
        bodyStyle.setFont(bodyFont);

        // Number 바디 스타일 설정
        CellStyle bodyNumStyle = DefaultCreateCellStyle(workbook);
        bodyNumStyle.setFont(bodyFont);
        bodyNumStyle.setDataFormat(format.getFormat("#,##0"));

        // 소수점 바디 스타일 설정
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


                // 병합할 내역 찾기
                // 세로줄 병합
                if(rowNum > 0){
                    // 이전 세로줄과 동일한 데이터이면 병합
                    //if(sheet.getRow(rowNum - 1).getCell(j).getStringCellValue().equals(cell.getStringCellValue())){
                    if(!headList.get(i - 1).get(j).startsWith(ExcelConstant.PREV_MERGE_CODE) && headList.get(i - 1).get(j).equals(headList.get(i).get(j))){
                        // 기존 병합 내역이 있는지 확인
                        int oldRegionIdx = GetMergedRegionIdx(sheet, rowNum - 1, j);
                        if(oldRegionIdx >= 0){
                            CellRangeAddress oldRange = sheet.getMergedRegion(oldRegionIdx);
                            oldRange.setLastRow(rowNum);
                            sheet.removeMergedRegion(oldRegionIdx);
                            sheet.addMergedRegion(oldRange);
                        }
                        else{
                            sheet.addMergedRegion(
                                    // 시작 행, 끝 행, 시작 열, 끝 열
                                    new CellRangeAddress(
                                            rowNum - 1, rowNum , j, j
                                    )
                            );
                        }

                    }
                }
                // 가로줄 병합
                if(j > 0){
                    // 이전 컬럽과 동일한 데이터이면 병합
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