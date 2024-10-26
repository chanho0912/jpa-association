package persistence.sql.definition;

public class TableCollectionDefinition {
    private final TableDefinition tableDefinition;
    private final Class<?> clazz;
    private final String fieldName;

    public TableCollectionDefinition(TableDefinition tableDefinition,
                                     Class<?> clazz,
                                     String fieldName) {
        this.tableDefinition = tableDefinition;
        this.clazz = clazz;
        this.fieldName = fieldName;
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getFieldName() {
        return fieldName;
    }
}
