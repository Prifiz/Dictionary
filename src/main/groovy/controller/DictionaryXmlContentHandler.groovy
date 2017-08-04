package controller

import datamodel.Dictionary
import datamodel.Record
import datamodel.Theme
import datamodel.Word
import datamodel.language.*
import groovy.xml.MarkupBuilder
import org.apache.commons.lang3.StringUtils

class DictionaryXmlContentHandler implements DictionaryContentHandler {
    @Override
    Dictionary getDictionary(String content) {
        def xml = new XmlParser().parseText(content)
        List<Record> records = new ArrayList<>()
        xml.record.each { record ->
            List<Word> words = new ArrayList<>()
            String topic = record.topic.text()
            record.words.word.each { word ->
                Word readWord

                boolean keyField
                String partOfSpeech
                String gender

                if(word.@keyField == null || word.@keyField == "") {
                    keyField = false
                } else {
                    keyField = Boolean.parseBoolean(word.@keyField)
                }

                if(word.@partOfSpeech == null) {
                    partOfSpeech = ""
                } else {
                    partOfSpeech = word.@partOfSpeech
                }

                if(word.@gender == null) {
                    gender = ""
                } else {
                    gender = word.@gender
                }

                readWord = new Word(
                        (java.lang.String) word.text(),
                        Language.getByName((java.lang.String) word.@language),
                        new Theme(topic, "emptyDescription"),
                        new PartOfSpeech(partOfSpeech),
                        new Gender(gender),
                        keyField)
                words.add(readWord)
            }
            String picturePath = record.picture.text()
            String description = record.description.text()
            records.add(new Record(words, picturePath, description))
        }
        return new Dictionary(records)
    }

    @Override
    String buildFormattedString(Dictionary dictionary) {
        def sw = new StringWriter()
        def xml = new MarkupBuilder(sw)
        xml.setDoubleQuotes(true)
        xml.records() {
            dictionary.getAllRecordsAsList().each { currentRecord ->
                record {
                    words() {
                        currentRecord.getWords().each { currentWord ->
                            word(
                                    language:currentWord.getLanguage().toString().toLowerCase(),
                                    partOfSpeech:currentWord.getPartOfSpeech().getValue().toString().toLowerCase(),
                                    gender:currentWord.getGender().getValue().toString().toLowerCase(),
                                    currentWord.getWord())
                        }
                    }
                    if(!currentRecord.getWords().isEmpty()) {
                        topic(currentRecord.getWords().get(0).getTheme().getName())
                    } else {
                        topic(StringUtils.EMPTY)
                    }
                    picture(currentRecord.getPictureName())
                    description(currentRecord.getDescription())
                }
            }
        }
        return sw.toString()
    }

    @Override
    boolean isValid(String content) {
        // TODO xsd validation
        return true
    }
}
