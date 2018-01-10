package controller.filesystem.impl.parsers;

import controller.DictionaryContentHandler;
import controller.DictionaryXmlContentHandler;
import controller.filesystem.FileContentParser;
import datamodel.Dictionary;

public class DictionaryFileContentParser implements FileContentParser {

    private Dictionary dictionary;

    public Dictionary getDictionary() {
        return dictionary;
    }

    @Override
    public void parseXml(String content) {
        DictionaryContentHandler dictionaryContentHandler = new DictionaryXmlContentHandler();
        this.dictionary = dictionaryContentHandler.getDictionary(content);
    }
}
