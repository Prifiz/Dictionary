package controller.integration.excel;

import gui.swingui.MainTableModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static short pixel2WidthUnits(int pxs) {
        final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
        final int UNIT_OFFSET_LENGTH = 7;
        final int[] UNIT_OFFSET_MAP = new int[]
                { 0, 36, 73, 109, 146, 182, 219 };
        short widthUnits = (short) (EXCEL_COLUMN_WIDTH_FACTOR *
                (pxs / UNIT_OFFSET_LENGTH));
        widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)];
        return widthUnits;
    }

    public static int pointsToPixels(double points) {
        final int PIXELS_PER_INCH = 96;
        return (int) Math.round(points / 72D * PIXELS_PER_INCH);
    }

    double pixelsToPoints(int pixels) {
        final int PIXELS_PER_INCH = 96;
        return 72D * pixels / PIXELS_PER_INCH;
    }

    @Override
    public void exportCurrentDictionaryView(JTable mainTable) throws IOException {
        Workbook dictionaryWorkbook = createWorkbook();
        Sheet sheet = dictionaryWorkbook.createSheet("Dictionary");

        TableModel mainTableModel = mainTable.getModel();


        CellStyle headerStyle = dictionaryWorkbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THICK);
        headerStyle.setBorderLeft(BorderStyle.THICK);
        headerStyle.setBorderRight(BorderStyle.THICK);
        headerStyle.setBorderTop(BorderStyle.THICK);
        Font headerFont = dictionaryWorkbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);
        int headingIdx = 0;
        for (int headings = 0; headings < mainTableModel.getColumnCount(); headings++) {
            if (mainTable.getColumnModel().getColumn(headings).getWidth() != 0) {
                Cell headerCell = headerRow.createCell(headingIdx);
                headerCell.setCellStyle(headerStyle);
                headerCell.setCellValue(mainTableModel.getColumnName(headings));
                headingIdx++;
            }
        }


        CellStyle dataCellStyle = dictionaryWorkbook.createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.LEFT);
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        Row row = sheet.createRow(1);
        for (int rows = 0; rows < mainTableModel.getRowCount(); rows++) {
            int columnIdx = 0;
            for (int cols = 0; cols < mainTableModel.getColumnCount(); cols++) {
                TableColumn column = mainTable.getColumnModel().getColumn(cols);
                if (column.getWidth() != 0) {
                    Cell dataCell = row.createCell(columnIdx);
                    dataCell.setCellStyle(dataCellStyle);
                    if(File.class.equals(mainTableModel.getColumnClass(cols))) {
                        String picturePath = mainTableModel.getValueAt(rows, cols).toString();
                        InputStream inputStream = new FileInputStream(picturePath);
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        int pictureIdx = dictionaryWorkbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                        inputStream.close();
                        CreationHelper helper = dictionaryWorkbook.getCreationHelper();
                        Drawing drawing = sheet.createDrawingPatriarch();
                        row.setHeightInPoints((float) pixelsToPoints(150));
                        sheet.setColumnWidth(columnIdx, pixel2WidthUnits(150));
//                        sheet.setColumnWidth(columnIdx, 150);
//                        dataCell.getRow().setHeight((short) (150));

                        ClientAnchor anchor = helper.createClientAnchor();

                        anchor.setCol1(columnIdx); //Column B
                        anchor.setRow1(rows + 1); //Row 3
                        anchor.setCol2(columnIdx + 1); //Column C
                        anchor.setRow2(rows + 2); //Row 4

                        //Creates a picture
                        Picture pict = drawing.createPicture(anchor, pictureIdx);
                    } else {
                        dataCell.setCellValue(mainTableModel.getValueAt(rows, cols).toString());
                    }
                    columnIdx++;
                }
            }
            row = sheet.createRow((rows + 2));
        }

//        for(int rowNumber = 0; rowNumber < sheet.getLastRowNum(); rowNumber++) {
//            Row currentRow = sheet.getRow(rowNumber);
//            for (int columnNumber = 0; columnNumber < currentRow.getLastCellNum(); columnNumber++) {
//                sheet.autoSizeColumn(columnNumber);
//            }
//        }


        // TEST START




        // TEST END


        dictionaryWorkbook.write(new FileOutputStream(filename));
    }
}
