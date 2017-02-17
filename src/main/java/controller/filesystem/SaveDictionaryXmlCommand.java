package controller.filesystem;

import controller.DictionaryXmlBuilder;
import datamodel.Dictionary;

import java.io.IOException;

public class SaveDictionaryXmlCommand extends AbstractSaveCommand {

    private Dictionary dictionary;

    public SaveDictionaryXmlCommand(String filePath, Dictionary dictionary) throws IOException {
        super(filePath);
        this.dictionary = dictionary;
    }

    @Override
    public void execute() throws IOException {
        String fileContent = DictionaryXmlBuilder.buildDictionaryXml(dictionary);
        save(fileContent);
    }
}
