package expensetracker;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Loads and saves expenses and budget as a JSON file */


final class ExpenseRepository {

    /* Every stored in the data file. Budget are keyed by "YYYY-MM" . **/
    static final class Data {
        int nextId = 1;
        final List<Expense> expenses = new ArrayList<>();
        final TreeMap<String, BigDecimal> budgets = new TreeMap<>();
    }

    private final Path path;


    ExpenseRepository(Path path) {
        this.path = path.toAbsolutePath();
    }

    /** ~/.expense-tracker/expense.json, or the EXPENSE_TRACKER_FILE environment variable variable if set. */

    static Path defaultPath() {
        String custom = System.getenv("EXPENSE_TRACKER_FILE");
        if (custom != null && !custom.trim().isEmpty()) {
            return Paths.get(custom.trim());
        }
        return Paths.get(System.getProperty("user.home"), ".expense-tracker", "expense.json");
    }

    Path getPath() {
        return path;
    }

    Data load(){
        Data data = new Data();
        if (!Files.exists(path)){
            return data;
        }
        String content;
        try {
            content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
        } catch (IOException ex ) {
            throw new TrackerException("Could not read data file '" + path + "': " + ex.getMessage(), ex);
        }
        if (content.isEmpty()){
            return data;
        }

        Object root;

        try {
            root = Json.parse(content);
        } catch (IllegalArgumentException ex) {
            throw new TrackerException("Data file '" + path + "' is corrupted (invalid JSON" +
                    " Fix or remote it , then try again.", ex);
        }

        try {
            if (!(root instanceof Map)) {
                throw new IllegalArgumentException("top level must be an object");
            }
            Map<?, ?> map = (Map<?, ?>) root;

            Object expenses = map.get("expenses");
            if (expenses != null) {
                if (!(expenses instanceof List)){
                    throw new IllegalArgumentException(" ' expenses' must be a list");
                }
                for (Object item : (List<?>) expenses){
                    data.expenses.add(Expense.fromJson(item));
                }
            }

            Object budgets = map.get("budgets");
            if (budgets != null) {
                if (!(budgets instanceof Map)) {
                    throw new IllegalArgumentException(" ' budget' must be an object");
                }
                for (Map.Entry<?, ?> e  :((Map<?,?>) budgets).entrySet()) {
                    data.budgets.put(String.valueOf(e.getKey()), Json.asDecimal(e.getValue(), "budget"));
                }
            }

            int highestId = 0;
            for (Expense e : data.expenses) {
                highestId = Math.max(highestId , e.getId());
            }

            int storedNext = map.get("next_id") == null ? 1 : Json.asInt(map.get("next_id"), "next_id");
        } catch (RuntimeException ex){
            throw new TrackerException("Data file '" + path + "' has an unexpected format: " + ex.getMessage(), ex);
        }

        return data;
    }

    /** Write to a temp file then swaps it in, so a crash mid-write can't corrupt the data. */
    void save(Data data) {
        Map<String , Object> root = new LinkedHashMap<>();
        root.put("next_id" , data.nextId);
        List<Object> expenses = new ArrayList<>();
        for (Expense e : data.expenses) {
            expenses.add(e.toJson());
        }
        root.put("expenses", expenses);
        root.put("budget", new LinkedHashMap<String, Object>(data.budgets));

        Path dir = path.getParent();

        try {
            Files.createDirectories(dir);
            Path tmp = Files.createTempFile(dir , ".expenses-", ".tmp");
            try {
                Files.write(tmp , Json.write(root).getBytes(StandardCharsets.UTF_8));
                try {
                    Files.move(tmp, path , StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException ex ) {
                    Files.move(tmp , path , StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(tmp);
            }
        } catch (IOException ex) {
            throw new TrackerException("Could not save data file '" + path + "': " + ex.getMessage(), ex);
        }
    }
}
