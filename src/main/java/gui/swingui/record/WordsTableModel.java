package gui.swingui.record;

import datamodel.Word;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordsTableModel implements TableModel {

    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

    private List<Word> words;

    public List<Word> getWords() {
        return words;
    }

    public WordsTableModel(List<Word> words) {
        this.words = words;
    }

    public int getRowCount() {
        return words.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Word";
            case 1:
                return "Language";
        }
        return StringUtils.EMPTY;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return words.get(rowIndex).getWord();
            case 1:
                return words.get(rowIndex).getLanguage().toString();
        }
        return StringUtils.EMPTY;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            words.get(rowIndex).setWord(aValue.toString());
        }
    }

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
