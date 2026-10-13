package expensetracker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


/** Business logic: adding , updating , deleting , filtering , totals, budgets and CSV export. */


final class ExpenseService {

    private  static final BigDecimal WARNING_RATIO = new BigDecimal("0.9");

    private final ExpenseRepository repository;
    private final ExpenseRepository.Data data;

    ExpenseService(ExpenseRepository repository){
        this.repository = repository;
        this.data = repository.load();

    }


    // -------------------------------------------------------------------- expenses

    Expense add(String description , BigDecimal amount , String category, LocalDate date) {
        Expense expense = new Expense(data.nextId , date, description , amount , category);
        data.expenses.add(expense);
        data.nextId++;
        repository.save(data);
        return expense;
    }


    /** Updates only fields that are non-null. */
    Expense update(int id, String description , BigDecimal amount , String category, LocalDate date) {
        Expense expense = find(id);
        if (description != null) expense.setDescription(description);
        if (amount != null) expense.setAmount(amount);
        if(date != null) expense.setDate(date);
        repository.save(data);
        return expense;
    }


    void delete(int id) {
        data.expenses.remove(find(id));
        repository.save(data);
    }

    Expense find(int id) {
        for (Expense e : data.expenses){
            if(e.getId() == id) {
                return e;
            }
        }

        throw new TrackerException("expense with ID " + id + " not found.");
    }

    /**
     * Returns expense sorted by date then ID.
     * month (1-12, current year) and category are optional filters (null = filter)
     */
}
