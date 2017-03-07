package controller.integration.excel;

import javax.swing.*;
import java.io.IOException;

public interface ExcelHandler {
    void exportCurrentDictionaryView(JTable mainTable) throws IOException;
    void importToCurrentDictionaryView(JTable mainTable);
}
