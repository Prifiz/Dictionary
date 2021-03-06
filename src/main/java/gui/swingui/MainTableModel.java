package gui.swingui;

import datamodel.Dictionary;
import datamodel.Record;
import datamodel.language.LanguageInfo;
import org.apache.commons.lang3.StringUtils;
import utils.Constants;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainTableModel implements TableModel {

    private Dictionary dictionary;
    private final Map<Integer, String> headerMap;
    private final Set<LanguageInfo> supportedLanguages;

    private Set<TableModelListener> listeners = new HashSet<>();

    MainTableModel(Dictionary dictionary, Set<LanguageInfo> supportedLanguages) {
        this.dictionary = dictionary;
        this.supportedLanguages = supportedLanguages;
        this.headerMap = new LinkedHashMap<Integer, String>() {{
            int headerIdx = 0;
            Iterator iterator = supportedLanguages.iterator();
            while (iterator.hasNext()) {
                put(headerIdx, ((LanguageInfo)iterator.next()).getLanguage());
                headerIdx++;
            }
            put(headerIdx, Constants.PICTURE_COLUMN_TITLE);
            headerIdx++;
            put(headerIdx, "Topic");
            headerIdx++;
            put(headerIdx, "Description");
        }};
    }

    public Map<Integer, String> getHeaders() {
        return headerMap;
    }


    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public int getRowCount() {
        return dictionary.getSize();
    }

    public int getColumnCount() {
        return headerMap.size();
    }

    public String getColumnName(int columnIndex) {
        if(columnIndex < headerMap.size()) {
            return headerMap.get(columnIndex);
        } else {
            return StringUtils.EMPTY;
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        if(Constants.PICTURE_COLUMN_TITLE.equals(getColumnName(columnIndex))) {
            return File.class;
        } else {
            return String.class;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        List<Record> recordList = dictionary.getAllRecordsAsList();
        Record record = recordList.get(rowIndex);
        int langsCount = supportedLanguages.size();
        if (columnIndex < langsCount) {
            return record.getWords().get(columnIndex).getWord();
        } else if (columnIndex == langsCount) {
            return new File(Constants.PICTURES_DIR_NAME, record.getPictureName());
        } else if(columnIndex == langsCount + 1) {
            return record.getTopicName();
        } else if(columnIndex == langsCount + 2) {
            return record.getDescription();
        }
        return StringUtils.EMPTY;
    }

    // Got from AbstractTableModel
    private void fireTableChanged(TableModelEvent e) {
        // Guaranteed to return a non-null array

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.size() - 2; i >= 0; i -= 2) {
            if (listeners.toArray()[i] == TableModelListener.class) {
                ((TableModelListener)listeners.toArray()[i + 1]).tableChanged(e);
            }
        }
    }

    private void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    void removeRow(int row) {
        dictionary.removeRecord(row);
        fireTableRowsDeleted(row, row);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
