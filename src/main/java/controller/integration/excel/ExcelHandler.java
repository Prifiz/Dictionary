package controller.integration.excel;

import datamodel.Dictionary;
import gui.swingui.ViewCustomizationRecord;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.util.List;

public interface ExcelHandler {
    void exportCurrentDictionaryView(JTable mainTable) throws IOException;
}
