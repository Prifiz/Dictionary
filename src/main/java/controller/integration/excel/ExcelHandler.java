package controller.integration.excel;

import datamodel.Dictionary;

import javax.swing.*;
import java.io.IOException;

public interface ExcelHandler {
    void exportCurrentDictionaryView(JTable mainTable) throws IOException;
    void importToCurrentDictionaryView(Dictionary dictionary) throws IOException;
}
