package datamodel.language;

public enum PartOfSpeechValue {
    NOUN("noun"),
    ADJECTIVE("adjective"),
    PREPOSITION("preposition"),
    NOT_SET("");

    private final String displayValue;

    PartOfSpeechValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static PartOfSpeechValue getByValue(String displayValue) {
        for (PartOfSpeechValue value : values()) {
            if(displayValue.toLowerCase().equals(value.getDisplayValue().toLowerCase())) {
                return value;
            }
        }
        return NOT_SET;
    }

}
