package controller.filesystem;

import controller.DictionaryContentHandler;
import controller.DictionaryXmlContentHandler;
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
