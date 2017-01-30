package controller;

import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;

import java.io.IOException;
import java.util.List;

public interface Controller {
    void startApplication();
    Dictionary getDictionary();
    void loadDictionary() throws IOException;
    void saveDictionary() throws IOException;
    void removeTopic(String topicName) throws IOException;
    void addRecord(List<Word> words, String pictureName) throws IOException;
    void editRecord(Record recordToEdit, List<Word> editedWords, String editedPictureName) throws IOException;
    void removeRecord(int recordId) throws IOException;
}
