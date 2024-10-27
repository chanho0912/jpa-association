package persistence.sql;

public class AliasUtils {
    private AliasUtils() {
    }

    public static String alias(String tableName, String columnName) {
        return tableName + "_" + columnName;
    }
}
