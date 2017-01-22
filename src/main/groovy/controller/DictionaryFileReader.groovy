package controller

import datamodel.Dictionary
import datamodel.Language
import datamodel.Record
import datamodel.Theme
import datamodel.Word

public class DictionaryFileReader {
    public static datamodel.Dictionary readFile(String fileContent) {
        def xml = new XmlParser().parseText(fileContent)
        List<Record> records = new ArrayList<>()
        xml.record.each { record ->
            List<Word> words = new ArrayList<>()
            String topic = record.topic.text()
            record.words.word.each { word ->
                Word readWord = new Word((java.lang.String)word.text(), Language.getByName((java.lang.String)word.@language), new Theme(topic, "emptyDescription"))
                words.add(readWord)
            }
            String picturePath = record.picture.text()
            records.add(new Record(words, picturePath))
        }
        return new Dictionary(records);
    }

}