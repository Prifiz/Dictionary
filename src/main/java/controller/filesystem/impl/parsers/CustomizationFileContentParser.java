package controller.filesystem.impl.parsers;

import controller.CustomizationContentHandler;
import controller.CustomizationXmlContentHandler;
import controller.filesystem.FileContentParser;
import gui.swingui.ViewCustomizationRecord;

import java.util.List;

public class CustomizationFileContentParser implements FileContentParser {

    private List<ViewCustomizationRecord> customizationData;

    public List<ViewCustomizationRecord> getCustomizationData() {
        return customizationData;
    }

    @Override
    public void parseXml(String content) {
        CustomizationContentHandler customizationHandler = new CustomizationXmlContentHandler();
        this.customizationData = customizationHandler.getCustomization(content);
    }
}
