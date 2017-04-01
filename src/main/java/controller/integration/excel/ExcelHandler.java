package controller.integration.excel;

import datamodel.Dictionary;
import gui.swingui.MainTableModel;

import javax.swing.*;
import java.io.IOException;

public interface ExcelHandler {
    void exportCurrentDictionaryView(JTable mainTable) throws IOException;
    void importToCurrentDictionaryView(Dictionary dictionary, MainTableModel mainTableModel) throws IOException;
}
