package controller.integration.excel;

import controller.integration.excel.mergestrategies.ImportFilePriorityMergeStrategy;
import controller.integration.excel.mergestrategies.RecordMergeStrategy;
import datamodel.Dictionary;
import datamodel.EmptyTheme;
import datamodel.language.Language;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private String getRussianFieldValue(Row pictureRow, Row headerRow) throws IOException {
        for(int i = 0; i < headerRow.getLastCellNum(); i++) {
            String headerValue = headerRow.getCell(i).getStringCellValue();
            if("Russian".equals(headerValue)) {
                return pictureRow.getCell(i).getStringCellValue();
            }
        }
        throw new IOException("No Russian Field Detected!");
    }

//    private boolean isRussianFieldExists(Row headerRow) {
//        for(int i = 0; i < headerRow.getLastCellNum(); i++) {
//            String headerValue = headerRow.getCell(i).getStringCellValue();
//            if("Russian".equals(headerValue)) {
//                return true;
//            }
//        }
//        return false;
//    }

    private String buildPictureName(Row pictureRow, Row headerRow) throws IOException {
        return Translit.getTranslitedRusToEngWord(getRussianFieldValue(pictureRow, headerRow));
    }

    private String saveCellPicture(Sheet sheet, Cell currentCell) throws IOException {
        HSSFPatriarch patriarch = (HSSFPatriarch) sheet.getDrawingPatriarch();
        String result = "";
        if (patriarch != null) {
            for (HSSFShape shape : patriarch.getChildren()) {
                if (shape instanceof HSSFPicture) {
                    HSSFPicture picture = (HSSFPicture) shape;
                    if (picture.getShapeType() == HSSFSimpleShape.OBJECT_TYPE_PICTURE) {
                        if (picture.getImageDimension() != null) {
                            Row pictureRow = sheet.getRow(picture.getPreferredSize().getRow1());
                            if (pictureRow != null) {
                                Cell pictureCell = pictureRow.getCell(picture.getPreferredSize().getCol1());

                                if (pictureCell.equals(currentCell)) {
                                    HSSFPictureData pictureData = picture.getPictureData();
                                    byte[] data = pictureData.getData();
                                    String fullPictureName = buildPictureName(pictureRow, sheet.getRow(0)) + "." + pictureData.suggestFileExtension();
                                    File file = new File("pictures", fullPictureName);
                                    try (FileOutputStream fop = new FileOutputStream(file)) {
                                        if (!file.exists()) {
                                            file.createNewFile();
                                        }
                                        fop.write(data);
                                        fop.flush();
                                        fop.close();
                                        return fullPictureName;
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
        return result;
    }

    @Override
    public void importToCurrentDictionaryView(Dictionary dictionary, MainTableModel mainTableModel) throws IOException {

        FileInputStream inputStream = new FileInputStream(filename);
        try {
            Workbook dictionaryWorkbook = WorkbookFactory.create(inputStream);
            Sheet sheet = dictionaryWorkbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            Map<Integer, String> columnNamesMapping = mainTableModel.getHeaders();

            for(int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
                Row row = sheet.getRow(rowNumber);
                Map<Integer, Word> wordsMapping = new HashMap<>();

                //String topic = "";// FIXME unbind topic from word!
                String description = "";

                // should be at least 1 russian word
                String pictureName = "";
                for(int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                    String headerValue = header.getCell(colNum).getStringCellValue();
                    if(columnNamesMapping.containsValue(headerValue)) {
                        Cell cell = row.getCell(colNum);

                        switch (headerValue) {
                            case "Picture": {
                                pictureName = saveCellPicture(sheet, cell);
                                break;
                            }
                            case "English": {
                                wordsMapping.put(0, new Word(cell.getStringCellValue(), Language.ENGLISH, new EmptyTheme(), false));
                                break;
                            }
                            case "German": {
                                wordsMapping.put(1, new Word(cell.getStringCellValue(), Language.GERMAN, new EmptyTheme(), false));
                                break;
                            }
                            case "Russian": {
                                wordsMapping.put(2, new Word(cell.getStringCellValue(), Language.RUSSIAN, new EmptyTheme(), false));
                                break;
                            }
                            case "Topic": {
                                final String topic = cell.getStringCellValue();
                                wordsMapping.forEach((integer, wrd) -> wrd.setTheme(new Theme(topic, "empty")));
                                break;
                            }
                            case "Description": {
                                description = cell.getStringCellValue();
                                break;
                            }
                        }
                    }
                }

                List<Word> words = wordsMapping.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());

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
