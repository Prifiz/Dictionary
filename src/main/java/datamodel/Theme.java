package datamodel;

public class Theme {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String description;

    public Theme(String name, String description) {
        this.name = name;
        this.description = description;
    }


}
