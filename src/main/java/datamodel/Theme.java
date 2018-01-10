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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Theme)) return false;

        Theme theme = (Theme) o;

        if (name != null ? !name.equals(theme.name) : theme.name != null) return false;
        return description != null ? description.equals(theme.description) : theme.description == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
