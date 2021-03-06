package controller;

import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import datamodel.language.LanguageInfo;
import gui.swingui.MainTableModel;
import gui.swingui.ViewCustomizationRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Controller {
    void startApplication();
    Dictionary getDictionary();
    Set<LanguageInfo> getSupportedLanguages();
    void loadDictionary() throws IOException;
    void saveDictionary() throws IOException;
    List<ViewCustomizationRecord> loadCustomization() throws IOException;
    void saveCustomization(List<ViewCustomizationRecord> customization) throws IOException;
    void removeTopic(String topicName) throws IOException;
    @Deprecated
    void addRecord(List<Word> words, String pictureName) throws IOException;
    void addRecord(List<Word> words, String pictureName, String description) throws IOException;
    @Deprecated
    void editRecord(Record recordToEdit, List<Word> editedWords, String editedPictureName) throws IOException;
    void editRecord(Record recordToEdit, List<Word> editedWords, String editedPictureName, String description) throws IOException;
    //void removeRecord(int recordId) throws IOException;
    //void removeRecords(List<Integer> recordsIds) throws IOException;
    void exportToExcel(String filePath, JTable currentDictionaryView) throws IOException;
    void importFromExcel(String filePath, MainTableModel mainTableModel) throws IOException, InvalidFormatException;
    void searchRecordsByLanguage(String searchPhrase, String language);
    void resetSearch();
    void saveSearchHistory(Collection<? extends String> history);
    List<String> loadSearchHistory();
    boolean isDictionaryNeedSaving();
}
