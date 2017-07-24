package datamodel.language;

import java.util.HashSet;
import java.util.Set;

/**
 * Describes a language
 * Each language can contain the unique sets of parts of speech and genders
 */
public class LanguageInfo {

    private Language language;
    private Set<PartOfSpeech> partsOfSpeech;
    private Set<Gender> genders;

    public LanguageInfo(Language language) {
        this.language = language;
        this.partsOfSpeech = new HashSet<>();
        this.genders = new HashSet<>();
    }

    public LanguageInfo addPartOfSpeech(PartOfSpeechValue value) {
        this.partsOfSpeech.add(new PartOfSpeech(value));
        return this;
    }

    public LanguageInfo addGender(GenderValue value) {
        this.genders.add(new Gender(value));
        return this;
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
