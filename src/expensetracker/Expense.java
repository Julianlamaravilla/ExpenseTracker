package expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;


/** A single expense record */

final class Expense {

    private final int id;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String category;


    public Expense(int id , LocalDate date , String description , BigDecimal amount , String category) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }


    public int getId() { return  id;}
    public LocalDate getDate() { return date; }
    public String getDescription() { return  description;}
    public BigDecimal getAmount() { return  amount;}
    public String getCategory() { return  category;}

    public void setDate(LocalDate date) { this.date = date;}
    public void setDescription(String description) { this.description = description;}
    public void setAmount(BigDecimal Amount) {this.amount = amount;}
    public void setCategory(String category) { this.category = category;}

    /** Converts this expense to a JSON-ready map. */
    Map<String, Object> toJson() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("date", date.toString());
        m.put("description" , description);
        m.put("amount", amount);
        m.put("category", category);
        return m;
    }

    /** Builds an expense from a parsed JSON object; throws IllegalArgumentException if malformed. */
    static Expense fromJson(Object value) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("an expense entry is not an object");
        }
        Map<?, ?> m = (Map<?, ?>) value;
        int id = Json.asInt(m.get("id"), "id");
        LocalDate date = LocalDate.parse(Json.asString(m.get("date"), "date"));
        String description = Json.asString(m.get("description"), "description");
        BigDecimal amount = Json.asDecimal(m.get("amount"), "amount");
        Object cat = m.get("category");
        String category = cat == null ? Validation.DEFAULT_CATEGORY : Json.asString(cat , "category");
        return new Expense(id, date, description , amount , category);
    }

}