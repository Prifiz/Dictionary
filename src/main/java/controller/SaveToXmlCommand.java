package controller;

import datamodel.Dictionary;

import java.io.IOException;

/**
 * Created by vaba1010 on 10.01.2017.
 */
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
