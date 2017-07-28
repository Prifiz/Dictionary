package controller.search;

import datamodel.Dictionary;
import datamodel.Record;

import java.util.List;
import java.util.Set;

public interface Search {
    List<Record> findAll(Dictionary dictionary);
    List<Record> findByThemeName(Dictionary dictionary, String themeName);
    Set<String> findAllTopics(Dictionary dictionary);
    List<Record> findAnyWordOccurrence(Dictionary dictionary, String phrase, String language);
}
