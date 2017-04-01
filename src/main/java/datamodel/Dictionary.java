package datamodel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Dictionary {

    private List<Record> records;

    public Dictionary() {
        this.records = new ArrayList<>();
    }

    public Dictionary(List<Record> records) {
        this.records = records;
    }

    public void resetWithNewData(Dictionary dictionary) {
        this.records.clear();
        for(Record record : dictionary.getAllRecordsAsList()) {
            this.records.add(record);
        }
    }

    public List<Record> getEquivalentRecords(Record record) {
        List<Record> result = new ArrayList<>();
        List<Word> recordWords = record.getWords();
        for(Record rec : records) {
            List<Word> recWords = rec.getWords();
            for(Word word : recWords) {
                if(recordWords.contains(word)) {
                    result.add(rec);
                    break;
                }
            }
        }
        return result;
    }

    public int getSize() {
        return records.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public void mergeRecord(Record record) {
        int equivalentsFound = 0;
        for(Record rec : records) {
            if(isEquivalent(rec, record)) {

                equivalentsFound++;
            }
        }
        if(equivalentsFound == 0) {
            records.add(record);
        }


        // TODO merge to existing records
        // if not exists -> add
        // if exists but import brings new values -> rewrite (optional?)
        // complete non-filled values
    }

    private int getRecordIndex(Record record) {
        if(records.isEmpty() || record == null) {
            return -1;
        }

        for(int i = 0; i < records.size(); i++) {
            if(record.equals(records.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public void replaceRecord(Record recordToEdit, Record editedRecord) {
        int idx = getRecordIndex(recordToEdit);
        if(idx >= 0) {
            records.get(idx).setWords(editedRecord.getWords());
            records.get(idx).setPictureName(editedRecord.getPictureName());
        }
    }

    public void removeRecord(int number) {
        if(number >= 0 && number < records.size()) {
            records.remove(number);
        }
    }

    public void removeAllTopicOccurences(String topicName) {
        if(topicName != null && !StringUtils.EMPTY.equals(topicName)) {
            for(Record record : records) {
                for(Word word : record.getWords()) {
                    if(topicName.equalsIgnoreCase(word.getTheme().getName())) {
                        word.removeTheme();
                    }
                }
            }
        }
    }

    public List<Record> getAllRecordsAsList() {
        return records;
    }
}
