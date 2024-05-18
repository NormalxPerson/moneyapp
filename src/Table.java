import java.util.ArrayList;
import java.util.HashMap;

public class Table {
    private String tableName;
    private HashMap<Integer, ColumnInfo> getColumnHeaderFromColumnNumber;
    private ArrayList<ColumnInfo> columnHeadersInfo;

    public Table(String tableName) {
        this.tableName = tableName;
        this.columnHeadersInfo = new ArrayList<ColumnInfo>();
        this.getColumnHeaderFromColumnNumber = new HashMap<>();
    }

    public Table(String tableName, ArrayList<ColumnInfo> columnInfo) {
        this.tableName = tableName;
        this.columnHeadersInfo = new ArrayList<ColumnInfo>();
        this.columnHeadersInfo.addAll(columnInfo);
    }

    public void addColumnInfo(String columnName, String dataType, int columnNumber) {
        ColumnInfo columnFacts = new ColumnInfo(columnName, dataType, columnNumber);
        getColumnHeaderFromColumnNumber.put(columnNumber, columnFacts);
    }

    public void addColumnInfoList(ArrayList<ColumnInfo> list) {
        columnHeadersInfo.addAll(list);
    }


    public static class ColumnInfo {
        private String columnName;
        private String dataType;
        private int columnNumber;

        public ColumnInfo(String columnName, String dataType, int columnNumber) {
            this.columnName = columnName;
            this.dataType = dataType;
            this.columnNumber = columnNumber;
        }

        public String getColumnName() {
            return this.columnName;
        }

        public String getDataType() {
            return this.dataType;
        }

        public int getColumnNumber() {
            return this.columnNumber;
        }
    }
}