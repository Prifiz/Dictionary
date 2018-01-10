package controller.integration.excel.mergestrategies;

import datamodel.Dictionary;
import datamodel.Record;

import java.util.ArrayList;
import java.util.List;

public class ImportFilePriorityMergeStrategy implements RecordMergeStrategy {
    @Override
    public void merge(Dictionary dictionary, Record recordToAdd) {
        if(!hasEqualities(dictionary, recordToAdd)) {
            List<Record> equivalents = getEquivalents(dictionary, recordToAdd);
            if(!equivalents.isEmpty()) {
                dictionary.removeAll(equivalents);
            }
            dictionary.addRecord(recordToAdd);
        }
    }

    protected List<Record> getEquivalents(Dictionary dictionary, Record recordToAdd) {
        List<Record> result = new ArrayList<>();
        for(Record dictionaryRecord : dictionary.getAllRecordsAsList()) {
            if(recordToAdd.isEquivalent(dictionaryRecord)) {
                result.add(dictionaryRecord);
            }
        }
        return result;
    }

    protected boolean hasEqualities(Dictionary dictionary, Record recordToAdd) {
        boolean result = false;
        for(Record dictionaryRecord : dictionary.getAllRecordsAsList()) {
            if(recordToAdd.equals(dictionaryRecord)) {
                return true;
            }
        }
        return result;
    }
}
