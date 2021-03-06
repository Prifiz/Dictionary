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
        this.records.addAll(new ArrayList<>(dictionary.getAllRecordsAsList()));
    }

    public void resetWithNewData(List<Record> records) {
        this.records.clear();
        this.records.addAll(records);
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

//    public void replaceUnique(Record recordToEdit, Record newRecord) {
//        int idx = getRecordIndex(recordToEdit);
//        if(idx >= 0) {
//
//        }
//    }

    public void removeRecord(int number) {
        if(number >= 0 && number < records.size()) {
            records.remove(number);
        }
    }

    public void removeAll(Collection<Record> records) {
        this.records.removeAll(records);
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


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Dictionary)) return false;

        Dictionary that = (Dictionary) o;

        if(that.getAllRecordsAsList() == null && records == null) {
            return true;
        } else if(that.getAllRecordsAsList() != null && records != null
                && that.getAllRecordsAsList().size() == records.size()) {
            for(int i = 0; i < that.getAllRecordsAsList().size(); i++) {
                if(that.getAllRecordsAsList().get(i) != null && records.get(i) != null) {
                    if(!that.getAllRecordsAsList().get(i).equals(records.get(i))) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return records != null ? records.hashCode() : 0;
    }
}
