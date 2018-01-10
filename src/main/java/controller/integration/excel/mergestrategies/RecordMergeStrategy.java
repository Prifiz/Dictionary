package controller.integration.excel.mergestrategies;

import datamodel.Dictionary;
import datamodel.Record;

/**
 * Created by PrifizGamer on 21.05.2017.
 */
public interface RecordMergeStrategy {
    void merge(Dictionary dictionary, Record recordToAdd);
}
