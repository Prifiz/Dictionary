package datamodel.language;

public enum GenderValue {
    FEMALE("female"),
    MALE("male"),
    NEUTER("neuter"),
    NOT_SET("");

    private final String displayValue;

    GenderValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static GenderValue getByValue(String displayValue) {
        for (GenderValue value : values()) {
            if(displayValue.toLowerCase().equals(value.getDisplayValue().toLowerCase())) {
                return value;
            }
        }
        return NOT_SET;
    }
}
