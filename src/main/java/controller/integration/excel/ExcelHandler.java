package controller.integration.excel;

import datamodel.Dictionary;
import gui.swingui.ViewCustomizationRecord;

import java.io.IOException;
import java.util.List;

public interface ExcelHandler {
    void exportDictionary(Dictionary dictionary, List<ViewCustomizationRecord> customization) throws IOException;
}
