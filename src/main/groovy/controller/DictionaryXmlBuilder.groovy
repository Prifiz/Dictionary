package controller

public class DictionaryXmlBuilder {
    public static String buildDictionaryXml(datamodel.Dictionary existingDictionary) {
        def sw = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(sw)
        xml.setDoubleQuotes(true)
        xml.records() {
            existingDictionary.getAllRecordsAsList().each { currentRecord ->
                record {
                    words() {
                        currentRecord.getWords().each { currentWord ->
                            word(language:currentWord.getLanguage().toString().toLowerCase(), currentWord.getWord())
                        }
                    }
                    if(!currentRecord.getWords().isEmpty()) {
                        topic(currentRecord.getWords().get(0).getTheme().getName())
                    } else {
                        topic("");
                    }
                    picture(currentRecord.getPictureName())
                }
            }
        }
        return sw.toString()
    }
}
