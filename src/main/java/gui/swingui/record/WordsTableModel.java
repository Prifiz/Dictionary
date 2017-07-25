package gui.swingui.record;

import datamodel.Word;
import datamodel.language.Gender;
import datamodel.language.GenderValue;
import datamodel.language.PartOfSpeech;
import datamodel.language.PartOfSpeechValue;
import org.apache.commons.lang3.StringUtils;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordsTableModel extends AbstractTableModel implements TableModel {

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
        return 4;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Word";
            case 1:
                return "Language";
            case 2:
                return "Part Of Speech";
            case 3:
                return "Gender";
        }
        return StringUtils.EMPTY;
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex == 1) {
            return false;
        } else {
            return true;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return words.get(rowIndex).getWord();
            case 1:
                return words.get(rowIndex).getLanguage().toString();
            case 2:
                return words.get(rowIndex).getPartOfSpeech().getValue().getDisplayValue();
            case 3:
                return words.get(rowIndex).getGender().getValue().getDisplayValue();
        }
        return StringUtils.EMPTY;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            words.get(rowIndex).setWord(aValue.toString());
        } else if(columnIndex == 2) {
            words.get(rowIndex).setPartOfSpeech(new PartOfSpeech(PartOfSpeechValue.getByValue(aValue.toString())));
        } else if(columnIndex == 3) {
            words.get(rowIndex).setGender(new Gender(GenderValue.getByValue(aValue.toString())));
        }
        fireTableRowsUpdated(rowIndex, columnIndex);
    }

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
