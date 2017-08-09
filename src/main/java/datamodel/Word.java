package datamodel;

import datamodel.language.Gender;
import datamodel.language.PartOfSpeech;
import org.apache.commons.lang3.StringUtils;

public class Word {

    private String word;
    private String language;
    private Theme theme;
    private boolean keyField;
    private PartOfSpeech partOfSpeech;
    private Gender gender;

    public boolean isKeyField() {
        return keyField;
    }

    public Word(String word, String language, Theme theme) {
        this.word = word;
        this.language = language.toLowerCase();
        this.theme = theme;
        this.keyField = false;
        this.partOfSpeech = new PartOfSpeech("");
        this.gender = new Gender("");
    }

    public Word(String word, String language, Theme theme, PartOfSpeech partOfSpeech, Gender gender, boolean keyField) {
        this.word = word;
        this.language = language.toLowerCase();
        this.theme = theme;
        this.partOfSpeech = partOfSpeech;
        this.gender = gender;
        this.keyField = keyField;
    }

    public Word(String word, String language, Theme theme, boolean keyField) {
        this.word = word;
        this.language = language.toLowerCase();
        this.theme = theme;
        this.partOfSpeech = new PartOfSpeech("");
        this.gender = new Gender("");
        this.keyField = keyField;
    }

    public void removeTheme() {
        this.theme.setName(StringUtils.EMPTY);
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public String getWord() {
        return word;
    }

    public String getLanguage() {
        return language;
    }

    public Theme getTheme() {
        return theme;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        StringBuilder wordBuilder = new StringBuilder();
        wordBuilder.append(word);
        wordBuilder.append(" [");
        wordBuilder.append(language);
        wordBuilder.append("] ");
        return wordBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word1 = (Word) o;

        if (word != null ? !word.equals(word1.word) : word1.word != null) return false;
        if (language != word1.language) return false;
        return theme != null ? theme.equals(word1.theme) : word1.theme == null;

    }

    @Override
    public int hashCode() {
        int result = word != null ? word.hashCode() : 0;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (theme != null ? theme.hashCode() : 0);
        return result;
    }
}
