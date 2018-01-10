package controller

import datamodel.language.LanguageInfo

interface LanguagesContentHandler {
    Set<LanguageInfo> getLanguages(String content)
}