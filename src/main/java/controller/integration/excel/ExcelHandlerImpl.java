package controller.integration.excel;

import controller.integration.excel.mergestrategies.ImportFilePriorityMergeStrategy;
import controller.integration.excel.mergestrategies.RecordMergeStrategy;
import datamodel.Dictionary;
import datamodel.EmptyTheme;
import datamodel.Record;
import datamodel.Theme;
import datamodel.Word;
import datamodel.language.LanguageInfo;
import gui.swingui.MainTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import utils.Constants;
import utils.LanguageUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExcelHandlerImpl implements ExcelHandler {

    private final String filename;
    private final Set<LanguageInfo> supportedLanguages;
    private static final Logger LOGGER = LogManager.getLogger(ExcelHandlerImpl.class);

    public ExcelHandlerImpl(String filename, Set<LanguageInfo> supportedLanguages) {
        this.filename = filename;
        this.supportedLanguages = supportedLanguages;
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


//        CreationHelper creationHelper = dictionaryWorkbook.getCreationHelper();
//        Drawing commentDrawing = sheet.createDrawingPatriarch();

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

//
//                    // When the comment box is visible, have it show in a 1x3 space
//                    ClientAnchor clientAnchor = creationHelper.createClientAnchor();
//                    clientAnchor.setCol1(dataCell.getColumnIndex());
//                    clientAnchor.setCol2(dataCell.getColumnIndex() + 1);
//                    clientAnchor.setRow1(row.getRowNum());
//                    clientAnchor.setRow2(row.getRowNum() + 3);
//
//                    // Create the comment and set the text+author
//                    Comment comment = commentDrawing.createCellComment(clientAnchor);
//                    RichTextString str = creationHelper.createRichTextString("Hello, World!");
//                    comment.setString(str);
//                    comment.setAuthor("Dictionary");
//
//                    // Assign the comment to the cell
//                    dataCell.setCellComment(comment);

                    dataCell.setCellStyle(dataCellStyle);

                    if(File.class.equals(mainTableModel.getColumnClass(cols))) {
                        String picturePath = mainTableModel.getValueAt(rows, cols).toString();
                        if(picturePath != Constants.PICTURES_DIR_NAME) {
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
                        }
                    } else {
                        dataCell.setCellValue(mainTableModel.getValueAt(rows, cols).toString());
                        sheet.autoSizeColumn(columnIdx);
                    }
                    columnIdx++;
                }
            }
            row = sheet.createRow((rows + 2));
        }
        OutputStream outputStream = new FileOutputStream(filename);
        dictionaryWorkbook.write(outputStream);
        outputStream.close();
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
            if("Russian".toLowerCase().equals(headerValue.toLowerCase())) {
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
                                    FileOutputStream fop = new FileOutputStream(file);
                                        if (!file.exists()) {
                                            file.createNewFile();
                                        }
                                        fop.write(data);
                                        fop.flush();
                                        fop.close();
                                        return fullPictureName;
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
    public void importToCurrentDictionaryView(Dictionary dictionary, MainTableModel mainTableModel)
            throws IOException, InvalidFormatException {
        FileInputStream inputStream = new FileInputStream(filename);
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

                //int wordIdx = 0;
                for(int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                    String excelTableHeaderValue = header.getCell(colNum).getStringCellValue();

                    if(columnNamesMapping.containsValue(excelTableHeaderValue)) {
                        Cell cell = row.getCell(colNum);
                        if(Constants.PICTURE_COLUMN_TITLE.equalsIgnoreCase(excelTableHeaderValue)) {
                            pictureName = saveCellPicture(sheet, cell);
                        } else if("Topic".equalsIgnoreCase(excelTableHeaderValue)) {
                            final String topic = cell.getStringCellValue();
                            wordsMapping.forEach((integer, wrd) -> wrd.setTheme(new Theme(topic, "empty")));
                        } else if("Description".equalsIgnoreCase(excelTableHeaderValue)) {
                            description = cell.getStringCellValue();
                        } else if(LanguageUtils.isSupportedLanguage(excelTableHeaderValue, supportedLanguages)) {
                            int wordIdx = -1;
                            for(Map.Entry<Integer, String> entry : columnNamesMapping.entrySet()) {
                                if(entry.getValue().equalsIgnoreCase(excelTableHeaderValue)) {
                                    wordIdx = entry.getKey();
                                }
                            }
                            if(wordIdx != -1) {
                                wordsMapping.put(
                                        wordIdx, new Word(
                                                cell.getStringCellValue(),
                                                excelTableHeaderValue,
                                                new EmptyTheme(),
                                                false));
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
        LOGGER.info("Export from excel done!");
    }
}
