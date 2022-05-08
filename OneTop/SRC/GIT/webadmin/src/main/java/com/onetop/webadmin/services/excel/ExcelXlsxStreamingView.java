package com.onetop.webadmin.services.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class ExcelXlsxStreamingView extends AbstractXlsxStreamingView {

    private Map<String, Object> map;

    public ExcelXlsxStreamingView(Map<String, Object> map){
        this.map =  map;
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
        new ExcelWriteComponent(workbook, this.map, response).create();
    }
}
