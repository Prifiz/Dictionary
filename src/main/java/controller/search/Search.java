package controller.search;

import datamodel.Dictionary;
import datamodel.Record;

import java.util.List;
import java.util.Set;

/**
 * Created by Prifiz on 03.01.2017.
 */
public interface Search {
    List<Record> findAll(Dictionary dictionary);
    List<Record> findByThemeName(Dictionary dictionary, String themeName);
    Set<String> findAllTopics(Dictionary dictionary);
}
