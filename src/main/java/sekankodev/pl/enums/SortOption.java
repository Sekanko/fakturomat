package sekankodev.pl.enums;

public enum SortOption {
    NAME("nazwa"),
    POST_DATE("data_ksiegowania"),
    NOTHING;

    private String columnName;
    SortOption(){}

    SortOption(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
