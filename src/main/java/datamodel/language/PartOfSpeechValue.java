package datamodel.language;

public enum PartOfSpeechValue {
    NOUN, ADJECTIVE, PREPOSITION, NOT_SET;

    public static PartOfSpeechValue getByName(String name) throws IllegalArgumentException {
        for(PartOfSpeechValue partOfSpeechValue : values()) {
            if(name.toUpperCase().equals(partOfSpeechValue.toString())) {
                return partOfSpeechValue;
            }
        }
        throw new IllegalArgumentException("No such part of speech!");
    }
}
