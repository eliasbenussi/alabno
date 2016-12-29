package alabno.database;

public class TransactionElement {

    private String sql;
    private String[] args;

    public TransactionElement(String sql, String[] args) {
        this.sql = sql;
        this.args = args;
    }

    public String getSql() {
        return sql;
    }

    public String[] getArgs() {
        return args;
    }

}
