package datamodel;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
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


    public int getSize() {
        return records.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void addRecord(Record record) {
        records.add(record);
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

    public void replaceRecord(Record recordToEdit, Record newRecord) {
        int idx = getRecordIndex(recordToEdit);
        if(idx >= 0) {
            records.get(idx).setWords(newRecord.getWords());
            records.get(idx).setPictureName(newRecord.getPictureName());
        }
    }

    public void replaceUnique(Record recordToEdit, Record newRecord) {
        int idx = getRecordIndex(recordToEdit);
        if(idx >= 0) {

        }
    }

    public void removeRecord(int number) {
        if(number >= 0 && number < records.size()) {
            records.remove(number);
        }
    }

    public void removeAll(Collection<Record> records) {
        records.removeAll(records);
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
