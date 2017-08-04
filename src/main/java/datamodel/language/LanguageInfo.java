package datamodel.language;

import java.util.HashSet;
import java.util.Set;

/**
 * Describes a language
 * Each language can contain the unique sets of parts of speech and genders
 */
public class LanguageInfo {

    private String language;
    private Set<PartOfSpeech> partsOfSpeech;
    private Set<Gender> genders;

    public LanguageInfo(String language) {
        this.language = language;
        this.partsOfSpeech = new HashSet<>();
        this.genders = new HashSet<>();
    }

    public LanguageInfo addPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partsOfSpeech.add(partOfSpeech);
        return this;
    }

    public LanguageInfo addGender(Gender gender) {
        this.genders.add(gender);
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public Set<PartOfSpeech> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public Set<Gender> getGenders() {
        return genders;
    }
}
