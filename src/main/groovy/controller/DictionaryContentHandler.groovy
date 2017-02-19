package controller

import datamodel.Dictionary

interface DictionaryContentHandler {
    Dictionary getDictionary(String content)
    String buildFormattedString(Dictionary dictionary)
    boolean isValid(String content)
}