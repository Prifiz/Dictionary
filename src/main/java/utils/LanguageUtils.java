package utils;

import datamodel.language.LanguageInfo;

import java.util.Set;

public class LanguageUtils {
    public static boolean isSupportedLanguage(String languageCandidate, Set<LanguageInfo> supportedLanguages) {
        for(LanguageInfo languageInfo : supportedLanguages) {
            if(languageCandidate.equalsIgnoreCase(languageInfo.getLanguage())) {
                return true;
            }
        }
        return false;
    }
}
