package controller.filesystem.impl.generators;

import controller.DictionaryContentHandler;
import controller.DictionaryXmlContentHandler;
import controller.filesystem.FileContentGenerator;
import datamodel.Dictionary;

public class DictionaryFileContentGenerator implements FileContentGenerator {

    private Dictionary dictionary;
    private DictionaryContentHandler dictionaryContentHandler;

    public DictionaryFileContentGenerator(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String generatePlainText() {
        return null;
    }

    @Override
    public String generateXml() {
        dictionaryContentHandler = new DictionaryXmlContentHandler();
        return dictionaryContentHandler.buildFormattedString(dictionary);
    }
}
