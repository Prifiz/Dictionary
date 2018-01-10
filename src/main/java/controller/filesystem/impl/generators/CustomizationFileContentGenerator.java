package controller.filesystem.impl.generators;

import controller.CustomizationContentHandler;
import controller.CustomizationXmlContentHandler;
import controller.filesystem.FileContentGenerator;
import gui.swingui.ViewCustomizationRecord;

import java.util.List;

public class CustomizationFileContentGenerator implements FileContentGenerator {

    private List<ViewCustomizationRecord> customization;

    public CustomizationFileContentGenerator(List<ViewCustomizationRecord> customization) {
        this.customization = customization;
    }

    @Override
    public String generatePlainText() {
        return null;
    }

    @Override
    public String generateXml() {
        CustomizationContentHandler customizationContentHandler = new CustomizationXmlContentHandler();
        return customizationContentHandler.buildFormattedString(customization);
    }
}
