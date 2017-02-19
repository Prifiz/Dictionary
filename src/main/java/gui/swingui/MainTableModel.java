package gui.swingui;

import datamodel.Dictionary;
import datamodel.Record;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainTableModel implements TableModel {

    private Dictionary dictionary;

    private Set<TableModelListener> listeners = new HashSet<>();

    public Map<Integer, String> getHeaders() {
        return headerMap;
    }

    private final Map<Integer, String> headerMap = new LinkedHashMap<Integer, String>() {{
        put(0, "English");
        put(1, "German");
        put(2, "Russian");
        put(3, "Picture");
        put(4, "Topic");
    }};


    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public MainTableModel(Dictionary dictionary) {
        this.dictionary = dictionary;
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
        if(columnIndex == 3) {
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

        if (columnIndex < recordList.get(rowIndex).getWords().size()) {
            return recordList.get(rowIndex).getWords().get(columnIndex).getWord();
        } else if (columnIndex == recordList.get(rowIndex).getWords().size()) {
            return new File("pictures", recordList.get(rowIndex).getPictureName());
        } else if(columnIndex == recordList.get(rowIndex).getWords().size() + 1) {
            return recordList.get(rowIndex).getTopicName();
        }
        return StringUtils.EMPTY;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
