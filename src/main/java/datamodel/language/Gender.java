package datamodel.language;

public class Gender {

    private GenderValue value;

    public Gender(GenderValue value) {
        this.value = value;
    }

    public GenderValue getValue() {
        return value;
    }
}
