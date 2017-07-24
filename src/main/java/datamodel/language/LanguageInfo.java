package datamodel.language;

import java.util.Set;

/**
 * Describes a language
 * Each language can contain the unique sets of parts of speech and genders
 */
public class LanguageInfo {

    private Language language;
    private Set<PartOfSpeech> partsOfSpeech;
    private Set<Gender> genders;

    public LanguageInfo(Language language, Set<PartOfSpeech> partsOfSpeech, Set<Gender> genders) {
        this.language = language;
        this.partsOfSpeech = partsOfSpeech;
        this.genders = genders;
    }

    public Language getLanguage() {
        return language;
    }

    public Set<PartOfSpeech> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public Set<Gender> getGenders() {
        return genders;
    }
}
