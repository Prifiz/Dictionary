package gui.swingui;

import datamodel.Dictionary;
import datamodel.Record;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by PrifizGamer on 08.01.2017.
 */
public class MainTableModel implements TableModel {

    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    private Dictionary dictionary;

    public MainTableModel(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getRowCount() {
        return dictionary.getSize();
    }

    public int getColumnCount() {
        return 5;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "English";
            case 1:
                return "German";
            case 2:
                return "Russian";
            case 3:
                return "Picture";
            case 4:
                return "Topic";
//            case 5:
//                return "Similarity";
        }
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 3) {
            return File.class;
        }
//        else if(columnIndex == 5) {
//            return JComboBox.class;
//        }
        else {
            return String.class;
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        if(columnIndex == 5) {
//            return true;
//        } else {
            return false;
        //}
    }

    protected ImageIcon createImageByPath(String path) {
        return new ImageIcon(path);
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
//        else if(columnIndex == recordList.get(rowIndex).getWords().size() + 2) {
//            return recordList.get(rowIndex).getSimilarity().toString();
//        }
        return "";
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    public void removeRow(int row) {
        dictionary.removeRecord(row);
    }

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
