package controller.integration.excel;

import datamodel.Dictionary;
import datamodel.Record;
import gui.swingui.CustomizeViewWindow;
import gui.swingui.ViewCustomizationRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;
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

    @Override
    public void exportCurrentDictionaryView(JTable mainTable) throws IOException {
        Workbook dictionaryWorkbook = createWorkbook();
        Sheet sheet = dictionaryWorkbook.createSheet("Dictionary");

        TableModel mainTableModel = mainTable.getModel();


        Row headerRow = sheet.createRow(0); //Create row at line 0
        int headingIdx = 0;
        for(int headings = 0; headings < mainTableModel.getColumnCount(); headings++){ //For each column
            if(mainTable.getColumnModel().getColumn(headings).getWidth() != 0) {
                headerRow.createCell(headingIdx).setCellValue(mainTableModel.getColumnName(headings));//Write column name
                headingIdx++;
            }
        }

        Row row = sheet.createRow(1);
        for(int rows = 0; rows < mainTableModel.getRowCount(); rows++){ //For each table row
            int colIdx = 0;
            for(int cols = 0; cols < mainTableModel.getColumnCount(); cols++){ //For each table column
                if(mainTable.getColumnModel().getColumn(cols).getWidth() != 0) {
                    row.createCell(colIdx).setCellValue(mainTableModel.getValueAt(rows, cols).toString()); //Write value
                    colIdx++;
                }
            }

            //Set the row to the next one in the sequence
            row = sheet.createRow((rows + 2));
        }
        dictionaryWorkbook.write(new FileOutputStream(filename));
    }
}
