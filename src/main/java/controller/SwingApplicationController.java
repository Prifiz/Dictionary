package controller;

import controller.filesystem.*;
import controller.filesystem.impl.LoadFileOperation;
import controller.filesystem.impl.SaveFileOperation;
import controller.filesystem.impl.generators.CustomizationFileContentGenerator;
import controller.filesystem.impl.generators.DictionaryFileContentGenerator;
import controller.filesystem.impl.parsers.CustomizationFileContentParser;
import controller.filesystem.impl.parsers.DictionaryFileContentParser;
import controller.filesystem.impl.parsers.LanguagesFileContentParser;
import controller.integration.excel.ExcelHandler;
import controller.integration.excel.ExcelHandlerImpl;
import controller.record.AddCommand;
import controller.record.EditCommand;
import controller.record.RemoveTopicCommand;
import controller.search.Search;
import controller.search.SimpleSearch;
import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import datamodel.language.LanguageInfo;
import gui.swingui.MainTableModel;
import gui.swingui.MainWindow;
import gui.swingui.ViewCustomizationRecord;
import gui.swingui.record.CustomizationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.Constants;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SwingApplicationController implements Controller {

    private Dictionary dictionary;
    private Dictionary dictionaryBackup;
    private Set<LanguageInfo> supportedLanguages;
    private static final Logger LOGGER = LogManager.getLogger(SwingApplicationController.class);

    private static final SwingApplicationController INSTANCE = new SwingApplicationController();

    @Override
    public Set<LanguageInfo> getSupportedLanguages() {
        return supportedLanguages;
    }

    private Set<LanguageInfo> initSupportedLanguages() {
        try {
            FileOperation loadOperation = new LoadFileOperation("config/Languages.xml");
            loadOperation.doFileOperation();
            String languagesFileContent = ((AbstractFileOperation)loadOperation).getFileContent();
            FileContentParser parser = new LanguagesFileContentParser();
            parser.parseXml(languagesFileContent);
            return ((LanguagesFileContentParser)parser).getLanguages();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
            return new HashSet<>();
        }
    }

    private SwingApplicationController() {
        this.dictionary = new Dictionary();
        this.dictionaryBackup = new Dictionary();
        this.supportedLanguages = initSupportedLanguages();
    }

    public static SwingApplicationController getInstance() {
        return INSTANCE;
    }

    public void startApplication() {
        LOGGER.info("Starting application...");
        MainWindow mainWindow = new MainWindow();
        mainWindow.customize(CustomizationUtils.loadViewCustomization());
        mainWindow.setVisible(true);
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

//    @Override
//    public void removeRecord(int recordId) throws IOException {
//        Command removeCommand = new RemoveCommand(recordId);
//        removeCommand.execute(dictionary);
//    }
//
//    @Override
//    public void removeRecords(List<Integer> recordsIds) throws IOException {
//        for(Integer record : recordsIds) {
//            Command removeCommand = new RemoveCommand(record);
//            removeCommand.execute(dictionary);
//        }
//    }

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
    public void importFromExcel(String filePath, MainTableModel mainTableModel) throws IOException {
        ExcelHandler excelHandler = new ExcelHandlerImpl(filePath);
        excelHandler.importToCurrentDictionaryView(dictionary, mainTableModel);

    }

    @Override
    public void searchRecordsByLanguage(String searchPhrase, String language) {
        Search search = new SimpleSearch();
        if(dictionaryBackup.isEmpty()) {
            dictionaryBackup.resetWithNewData(dictionary);
        }
        List<Record> foundRecords = search.findAnyWordOccurrence(dictionaryBackup, searchPhrase, language);
        dictionary.resetWithNewData(foundRecords);
    }

    @Override
    public void resetSearch() {
        if(!dictionaryBackup.isEmpty()) {
            dictionary.resetWithNewData(dictionaryBackup);
        }
    }

    @Override
    public void saveSearchHistory(Set<String> history) {
        StringBuilder historyBuilder = new StringBuilder();
        history.forEach(historyBuilder::append);
        FileOperation saveOperation = new SaveFileOperation(
                Constants.SEARCH_HISTORY_FILEPATH, historyBuilder.toString());
        try {
            saveOperation.doFileOperation();
        } catch (IOException ex) {
            LOGGER.error("Couldn't save search history");
            LOGGER.error(ex.getMessage());
        }
    }

    @Override
    public Set<String> loadSearchHistory() {
        Set<String> result = new HashSet<>();
        FileOperation loadOperation = new LoadFileOperation(Constants.SEARCH_HISTORY_FILEPATH, true);
        try {
            loadOperation.doFileOperation();
            String historyContent = ((AbstractFileOperation)loadOperation).getFileContent();
            Collections.addAll(result, historyContent.split(Constants.EOL));
        } catch (IOException ex) {
            LOGGER.error("Couldn't load search history");
            LOGGER.error(ex.getMessage());
        }
        return result;
    }

}
