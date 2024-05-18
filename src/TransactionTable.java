import java.util.HashMap;

public class TransactionTable {
    private String tableName;
    private HashMap<Integer, Table.ColumnInfo> columnNumToInfo;

    public TransactionTable() {
        this.tableName = "Transactions";
        this.columnNumToInfo = new HashMap<>();
    }

    public void addColumn(String columnName, String dataType, int columnNumber) {
        Table.ColumnInfo column = new Table.ColumnInfo(columnName, dataType, columnNumber);
        columnNumToInfo.put(columnNumber, column);
    }

    public Table.ColumnInfo getcolumnInfo(int columnNumber) {
        return columnNumToInfo.get(columnNumber);
    }

    public int getColumnCount() {
       return columnNumToInfo.size();
    }


    public String getTableName() {
        return tableName;
    }
}
