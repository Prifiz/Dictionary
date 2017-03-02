package controller.integration.excel;

import datamodel.Dictionary;
import datamodel.Record;
import gui.swingui.CustomizeViewWindow;
import gui.swingui.ViewCustomizationRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelHandlerImpl implements ExcelHandler {

    private String filename;

    public ExcelHandlerImpl(String filename) {
        this.filename = filename;
    }

    protected Workbook createWorkbook() throws IOException {
        Workbook dictionaryWorkbook;
        if(filename.endsWith("xls")) {
            dictionaryWorkbook = new HSSFWorkbook();
        } else if(filename.endsWith("xlsx")) {
            dictionaryWorkbook = new XSSFWorkbook();
        } else {
            throw new IOException("Incorrect extension");
        }
        return dictionaryWorkbook;
    }

    protected List<ViewCustomizationRecord> getVisibleColumns(List<ViewCustomizationRecord> customization) {
        List<ViewCustomizationRecord> result = new ArrayList<>();
        for(ViewCustomizationRecord record : customization) {
            if(record.isVisible()) {
                result.add(record);
            }
        }
        return result;
    }

//    protected boolean isVisible(String name, List<ViewCustomizationRecord> customization) {
//        boolean result = true;
//        for(ViewCustomizationRecord record : customization) {
//            if(record.getColumnName().equals(name) && !record.isVisible()) {
//                return false;
//            }
//        }
//    }

    @Override
    public void exportDictionary(Dictionary dictionary, List<ViewCustomizationRecord> customization) throws IOException {
        Workbook dictionaryWorkbook = createWorkbook();
        Sheet sheet = dictionaryWorkbook.createSheet("Dictionary");

        List<ViewCustomizationRecord> visibleColumns = getVisibleColumns(customization);

        int rowIndex = 0;
        Row header = sheet.createRow(rowIndex);
        int columnIdx = 0;
        for(ViewCustomizationRecord record : customization) {
            Cell cell = header.createCell(columnIdx);
            cell.setCellValue(record.getColumnName());
            columnIdx++;
        }
        for(Record record : dictionary.getAllRecordsAsList()) {
            Row recordRow = sheet.createRow(rowIndex);
            int colIdx = 0;
            for(ViewCustomizationRecord customRecord : visibleColumns) {
                Cell cell = recordRow.createCell(colIdx);
                cell.setCellValue(record.);
            }
            record.g
            rowIndex++;
        }
    }
}
