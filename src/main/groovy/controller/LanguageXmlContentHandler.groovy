package controller

import datamodel.language.Gender
import datamodel.language.LanguageInfo
import datamodel.language.PartOfSpeech

class LanguageXmlContentHandler implements LanguagesContentHandler {
    @Override
    Set<LanguageInfo> getLanguages(String content) {
        Set<LanguageInfo> result = new HashSet<>()
        def xml = new XmlParser().parseText(content)
        xml.language.each { language ->
            String languageName = language.@name
            LanguageInfo languageInfo = new LanguageInfo(languageName)
            language.partsOfSpeech.partOfSpeech.each { partOfSpeech ->
                languageInfo.addPartOfSpeech(new PartOfSpeech(partOfSpeech.@title))
            }
            language.genders.gender.each { gender ->
                languageInfo.addGender(new Gender(gender.@title))
            }
            result.add(languageInfo)
        }
        return result
    }
}
