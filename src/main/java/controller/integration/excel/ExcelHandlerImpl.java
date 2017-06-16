package controller.integration.excel;

import controller.integration.excel.mergestrategies.ImportFilePriorityMergeStrategy;
import controller.integration.excel.mergestrategies.RecordMergeStrategy;
import datamodel.Dictionary;
import datamodel.EmptyTheme;
import datamodel.Language;
import datamodel.Record;
import datamodel.Theme;
import datamodel.Word;
import gui.swingui.MainTableModel;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                        ClientAnchor anchor = helper.createClientAnchor();
                        anchor.setCol1(columnIdx);
                        anchor.setRow1(rows + 1);
                        anchor.setCol2(columnIdx + 1);
                        anchor.setRow2(rows + 2);
                        Picture pict = drawing.createPicture(anchor, pictureIdx);
                    } else {
                        dataCell.setCellValue(mainTableModel.getValueAt(rows, cols).toString());
                        sheet.autoSizeColumn(columnIdx);
                    }
                    columnIdx++;
                }
            }
            row = sheet.createRow((rows + 2));
        }
        dictionaryWorkbook.write(new FileOutputStream(filename));
    }

    private Map<Integer, String> getSuitableCellNames(Row header, MainTableModel mainTableModel) {
        Map<Integer, String> result = new LinkedHashMap<>();
        int cellNo = 0;
        for(Cell headerCell : header) {
            String fieldName = headerCell.getStringCellValue();
            if(mainTableModel.getHeaders().containsValue(fieldName)) {
                result.put(cellNo, fieldName);
            }
            cellNo++;
        }
        return result;
    }

    @Override
    public void importToCurrentDictionaryView(Dictionary dictionary, MainTableModel mainTableModel) throws IOException {

        FileInputStream inputStream = new FileInputStream("Test.xlsx");
        try {
            Workbook dictionaryWorkbook = WorkbookFactory.create(inputStream);
            Sheet sheet = dictionaryWorkbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            Map<Integer, String> columnNamesMapping = mainTableModel.getHeaders();//getSuitableCellNames(header, mainTableModel);

            for(int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
                Row row = sheet.getRow(rowNumber);
                Map<String, String> recordParams = new LinkedHashMap<>();
                List<Word> words = new ArrayList<>();
                String topic = "";// FIXME unbind topic from word!
                String description = "";


                String pictureName = "";
                for(int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                    String headerValue = header.getCell(colNum).getStringCellValue();
                    if(columnNamesMapping.containsValue(headerValue)) {
                        Cell cell = row.getCell(colNum);

                        switch (headerValue) {
                            case "Picture": {

                                HSSFPatriarch patriarch = (HSSFPatriarch) sheet.getDrawingPatriarch();
                                if (patriarch != null) {
                                    // Loop through the objects
                                    for (HSSFShape shape : patriarch.getChildren()) {
                                        if (shape instanceof HSSFPicture) {
                                            HSSFPicture picture = (HSSFPicture) shape;
                                            if (picture.getShapeType() == HSSFSimpleShape.OBJECT_TYPE_PICTURE) {
                                                if (picture.getImageDimension() != null) {
                                                    Row pictureRow = sheet.getRow(picture.getPreferredSize().getRow1());
                                                    if (pictureRow != null) {
                                                        Cell pictureCell = pictureRow.getCell(picture.getPreferredSize().getCol1());

                                                        if (pictureCell.equals(cell)) {
                                                            HSSFPictureData pictureData = picture.getPictureData();
                                                            byte[] data = pictureData.getData();

                                                            File file = new File(pictureRow.getCell(1).getStringCellValue() + "." + pictureData.suggestFileExtension());
                                                            try (FileOutputStream fop = new FileOutputStream(file)) {

                                                                if (!file.exists()) {
                                                                    file.createNewFile();
                                                                }
                                                                fop.write(data);
                                                                fop.flush();
                                                                fop.close();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                pictureName = cell.getStringCellValue();
                                break;
                            }
                            case "English": {
                                words.add(new Word(cell.getStringCellValue(), Language.ENGLISH, new EmptyTheme(), false));
                                break;
                            }
                            case "German": {
                                words.add(new Word(cell.getStringCellValue(), Language.GERMAN, new EmptyTheme(), false));
                                break;
                            }
                            case "Russian": {
                                words.add(new Word(cell.getStringCellValue(), Language.RUSSIAN, new EmptyTheme(), false));
                                break;
                            }
                            case "Topic": {
                                topic = cell.getStringCellValue();
                                for(Word word : words) {
                                    word.setTheme(new Theme(topic, "empty"));
                                }
                                break;
                            }
                            case "Description": {
                                description = cell.getStringCellValue();
                                break;
                            }
                        }
                    }
                }
                RecordMergeStrategy mergeStrategy = new ImportFilePriorityMergeStrategy();
                mergeStrategy.merge(dictionary, new Record(words, pictureName, description));
            }

        } catch (InvalidFormatException ex) {
            // TODO
            System.out.println("Exception!");
        }
        System.out.println("DONE");

    }

}
