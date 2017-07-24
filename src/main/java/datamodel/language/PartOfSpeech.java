package datamodel.language;

public class PartOfSpeech {

    private PartOfSpeechValue value;

    public PartOfSpeech(PartOfSpeechValue value) {
        this.value = value;
    }

    public PartOfSpeechValue getValue() {
        return value;
    }
}
