package controller;

import controller.filesystem.*;
import controller.integration.excel.ExcelHandler;
import controller.integration.excel.ExcelHandlerImpl;
import controller.record.AddCommand;
import controller.record.EditCommand;
import controller.record.RemoveCommand;
import controller.record.RemoveTopicCommand;
import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import gui.swingui.MainTableModel;
import gui.swingui.MainWindow;
import gui.swingui.ViewCustomizationRecord;
import gui.swingui.record.CustomizationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class SwingApplicationController implements Controller {

    private Dictionary dictionary;
    private static final Logger LOGGER = LogManager.getLogger(SwingApplicationController.class);

    private static final SwingApplicationController INSTANCE = new SwingApplicationController();

    private SwingApplicationController() {
        this.dictionary = new Dictionary();
    }

    public static SwingApplicationController getInstance() {
        return INSTANCE;
    }

    public void startApplication() {
        LOGGER.info("Starting application...");
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        mainWindow.customize(CustomizationUtils.loadViewCustomization());
    }

    @Override
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void loadDictionary() throws IOException {
        FileOperation loadOperation = new LoadFileOperation("Dictionary.xml");
        loadOperation.doFileOperation();
        String dictionaryFileContent = ((AbstractFileOperation)loadOperation).getFileContent();
        FileContentParser parser = new DictionaryFileContentParser();
        parser.parseXml(dictionaryFileContent);
        dictionary.resetWithNewData(((DictionaryFileContentParser)parser).getDictionary());
    }

    @Override
    public void saveDictionary() throws IOException {
        FileContentGenerator generator = new DictionaryFileContentGenerator(dictionary);
        String dictionaryXmlContent = generator.generateXml();
        FileOperation saveOperation = new SaveFileOperation("Dictionary.xml", dictionaryXmlContent);
        saveOperation.doFileOperation();
    }

    @Override
    public List<ViewCustomizationRecord> loadCustomization() throws IOException {
        FileOperation loadCustomizationOperation = new LoadFileOperation(Constants.VIEW_CUSTOMIZATION_FILEPATH);
        loadCustomizationOperation.doFileOperation();
        String customizationFileContent = ((AbstractFileOperation)loadCustomizationOperation).getFileContent();
        FileContentParser parser = new CustomizationFileContentParser();
        parser.parseXml(customizationFileContent);
        return ((CustomizationFileContentParser)parser).getCustomizationData();
    }

    @Override
    public void saveCustomization(List<ViewCustomizationRecord> customization) throws IOException {
        FileContentGenerator customizationGenerator = new CustomizationFileContentGenerator(customization);
        String customizationContent = customizationGenerator.generateXml();
        FileOperation saveOperation = new SaveFileOperation(Constants.VIEW_CUSTOMIZATION_FILEPATH, customizationContent);
        saveOperation.doFileOperation();
    }

    @Override
    public void removeTopic(String topicName) throws IOException {
        Command removeTopicCommand = new RemoveTopicCommand(topicName);
        removeTopicCommand.execute(dictionary);
    }

    @Override
    public void addRecord(List<Word> words, String pictureName) throws IOException {
        Command addCommand = new AddCommand(words, pictureName);
        addCommand.execute(dictionary);
    }

    @Override
    public void addRecord(List<Word> words, String pictureName, String description) throws IOException {
        Command addCommand = new AddCommand(words, pictureName, description);
        addCommand.execute(dictionary);
    }

    @Override
    public void editRecord(Record recordToEdit, List<Word> editedWords, String editedPictureName) throws IOException {
        Command editCommand = new EditCommand(recordToEdit, editedWords, editedPictureName);
        editCommand.execute(dictionary);
    }

    @Override
    public void editRecord(Record recordToEdit, List<Word> editedWords, String editedPictureName, String editedDescription)
            throws IOException {
        Command editCommand = new EditCommand(recordToEdit, editedWords, editedPictureName, editedDescription);
        editCommand.execute(dictionary);
    }

    @Override
    public void removeRecord(int recordId) throws IOException {
        Command removeCommand = new RemoveCommand(recordId);
        removeCommand.execute(dictionary);
    }

    @Override
    public void exportToExcel(String filePath, JTable currentDictionaryView) {
        ExcelHandler excelHandler = new ExcelHandlerImpl(filePath);
        try {
            excelHandler.exportCurrentDictionaryView(currentDictionaryView);
        } catch (IOException ex) {
            // TODO
        }
    }

    @Override
    public void importFromExcel(String filePath, MainTableModel mainTableModel) {
        ExcelHandler excelHandler = new ExcelHandlerImpl(filePath);
        try {
            excelHandler.importToCurrentDictionaryView(dictionary, mainTableModel);
        } catch (IOException e) {
            // TODO
        }
    }


}
