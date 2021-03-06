package gui.swingui;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomizeViewTableModel implements TableModel {

    private Set<TableModelListener> listeners = new HashSet<>();

    List<ViewCustomizationRecord> getCustomizationRecords() {
        return customizationRecords;
    }

    private List<ViewCustomizationRecord> customizationRecords = new ArrayList<>();

    CustomizeViewTableModel(JTable mainTable) {
        for(int columnIdx = 0; columnIdx < mainTable.getModel().getColumnCount(); columnIdx++) {
            customizationRecords.add(new ViewCustomizationRecord(
                    mainTable.getColumnModel().getColumn(columnIdx).getMaxWidth() > 0,
                    mainTable.getModel().getColumnName(columnIdx), StringUtils.EMPTY));
        }
    }

    @Override
    public int getRowCount() {
        return customizationRecords.size();
    }

    @Override
    public int getColumnCount() {
        return ViewCustomizationRecord.class.getDeclaredFields().length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if(columnIndex == 0) {
            return "Visible";
        } else if(columnIndex == 1) {
            return "Column Name";
        } else if(columnIndex == 2) {
            return "Description";
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 || columnIndex == 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            return customizationRecords.get(rowIndex).isVisible();
        } else if(columnIndex == 1) {
            return customizationRecords.get(rowIndex).getColumnName();
        } else if(columnIndex == 2) {
            return customizationRecords.get(rowIndex).getColumnDescription();
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            customizationRecords.get(rowIndex).setVisible((Boolean) aValue);
        }
        if(columnIndex == 2) {
            customizationRecords.get(rowIndex).setColumnDescription((String) aValue);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }
}
