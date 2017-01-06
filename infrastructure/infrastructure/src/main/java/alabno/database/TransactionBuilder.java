package alabno.database;

import java.util.ArrayList;
import java.util.List;

public class TransactionBuilder {

    private List<TransactionElement> elements = new ArrayList<>();

    public List<TransactionElement> getElements() {
        return elements;
    }
    
    public void add(String sql, String[] args) {
        elements.add(new TransactionElement(sql, args));
    }
    
}
