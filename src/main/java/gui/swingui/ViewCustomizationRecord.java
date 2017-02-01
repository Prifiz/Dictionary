package gui.swingui;

public class ViewCustomizationRecord {

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    private Boolean visible;
    private String columnName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewCustomizationRecord)) return false;

        ViewCustomizationRecord record = (ViewCustomizationRecord) o;

        if (visible != null ? !visible.equals(record.visible) : record.visible != null) return false;
        if (columnName != null ? !columnName.equals(record.columnName) : record.columnName != null) return false;
        return columnDescription != null ? columnDescription.equals(record.columnDescription) : record.columnDescription == null;

    }

    @Override
    public int hashCode() {
        int result = visible != null ? visible.hashCode() : 0;
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        result = 31 * result + (columnDescription != null ? columnDescription.hashCode() : 0);
        return result;
    }

    public ViewCustomizationRecord(Boolean visible, String columnName, String columnDescription) {
        this.visible = visible;
        this.columnName = columnName;
        this.columnDescription = columnDescription;
    }

    public void setColumnDescription(String columnDescription) {
        this.columnDescription = columnDescription;
    }

    private String columnDescription;

    public Boolean getVisible() {
        return visible;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnDescription() {
        return columnDescription;
    }

}
