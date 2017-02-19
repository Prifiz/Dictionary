package controller;

import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import gui.swingui.ViewCustomizationRecord;

import java.io.IOException;
import java.util.List;

public interface Controller {
    void startApplication();
    Dictionary getDictionary();
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
    void removeRecord(int recordId) throws IOException;
}
