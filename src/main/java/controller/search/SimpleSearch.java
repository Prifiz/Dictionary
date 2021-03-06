package controller.search;

import datamodel.Dictionary;
import datamodel.Record;
import datamodel.Word;
import org.apache.commons.lang3.StringUtils;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleSearch implements Search {

    public List<Record> findAll(Dictionary dictionary) {
        return dictionary.getAllRecordsAsList();
    }

    private boolean isThemeFoundInWords(List<Word> words, String themeName) {
        boolean result = false;
        for(Word word : words) {
            if(word.getTheme().getName().equalsIgnoreCase(themeName)) {
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

    private boolean checkRecord(List<Word> wordsToCheck, String phrase) {
        return wordsToCheck
                .stream()
                .anyMatch(word ->
                        Pattern.compile(phrase).matcher(word.getWord()).find());
    }

    @Override
    public List<Record> findAnyWordOccurrence(Dictionary dictionary, String phrase, String language) {
        List<Record> result = new ArrayList<>();
        for(Record record : dictionary.getAllRecordsAsList()) {
            List<Word> wordsToCheck;
            if(Constants.ANY_LANGUAGE.equalsIgnoreCase(language)) {
                wordsToCheck = record.getWords();
            } else {
                wordsToCheck = (record.getWords())
                        .stream()
                        .filter(word -> word.getLanguage().equalsIgnoreCase(language))
                        .collect(Collectors.toList());
            }
            if(checkRecord(wordsToCheck, phrase)) {
                result.add(record);
            }
        }
        return result;
    }
}
