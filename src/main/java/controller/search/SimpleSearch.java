package controller.search;

import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SimpleSearch implements Search {

    public List<Record> findAll(Dictionary dictionary) {
        return dictionary.getAllRecordsAsList();
    }

    private boolean isThemeFoundInWords(List<Word> words, String themeName) {
        boolean result = false;
        for(Word word : words) {
            if(word.getTheme().getName().equals(themeName)) {
                return true;
            }
        }
        return result;
    }

    public List<Record> findByThemeName(Dictionary dictionary, String themeName) {
        List<Record> result = new ArrayList<>();
        for(Record record : dictionary.getAllRecordsAsList()) {
            if(isThemeFoundInWords(record.getWords(), themeName)) {
                result.add(record);
            }
        }
        return result;
    }

    public Set<String> findAllTopics(Dictionary dictionary) {
        Set<String> result = new TreeSet<>();
        for(Record record : dictionary.getAllRecordsAsList()) {
            if(!record.getWords().isEmpty()) {
                String topic = record.getWords().get(0).getTheme().getName();
                if(StringUtils.isNoneBlank(topic)) {
                    result.add(topic);
                }
            }
        }
        return result;
    }
}
