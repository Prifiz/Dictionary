package controller.filesystem;

import controller.DictionaryXmlBuilder;
import datamodel.Dictionary;

import java.io.IOException;

public class SaveToXmlCommand extends AbstractSaveCommand {

    public SaveToXmlCommand(String filePath) throws IOException {
        super(filePath);
    }

    @Override
    public void execute(Dictionary dictionary) throws IOException {
        String fileContent = DictionaryXmlBuilder.buildDictionaryXml(dictionary);
        save(fileContent);
    }
}
