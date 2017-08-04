package controller.filesystem.impl.parsers;

import controller.LanguageXmlContentHandler;
import controller.LanguagesContentHandler;
import controller.filesystem.FileContentParser;
import datamodel.language.LanguageInfo;

import java.util.HashSet;
import java.util.Set;

public class LanguagesFileContentParser implements FileContentParser {

    private Set<LanguageInfo> languages;

    public LanguagesFileContentParser() {
        this.languages = new HashSet<>();
    }

    public Set<LanguageInfo> getLanguages() {
        return languages;
    }

    @Override
    public void parseXml(String content) {
        LanguagesContentHandler languagesContentHandler = new LanguageXmlContentHandler();
        this.languages.clear();
        this.languages.addAll(languagesContentHandler.getLanguages(content));
    }
}
