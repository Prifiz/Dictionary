package datamodel.language;

public class PartOfSpeech {

    //private PartOfSpeechValue value;
    private String value;

//    public PartOfSpeech(PartOfSpeechValue value) {
//        this.value = value;
//    }

    public PartOfSpeech(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
