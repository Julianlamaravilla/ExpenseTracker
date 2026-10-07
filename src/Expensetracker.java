package expensetracker;


import  java.math.BigDecimal;
import  java.nio.file.Path;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * expense-tracker: a simple command-line expense tracker.
 *
 * Entry point. Reads the command name, parse its options and prints the results
 */

public final class ExpenseTracker {

    private  static final String USAGE = String.join(System.lineSeparator(),
        "Usage: expense-tracker <command> [options]",
        "",
        "Commands",
        " add       --description TEXT --amount N [--category NAME] [--date YYYY-MM-DD]",
        " update     --id N [description TEXT] [--category NAME] [--date YYYY-MM-DD]",
        " delete     --id N",
        " list      [--category NAME] [--month 1-12]",
        " summary   [--moth 1-12] [--category NAME]",
        " budget    [--moth 1-12] [--amount N | --clear] (no options: show this year's budget)",
        " export    [--file PATH] [--category NAME] [--moth 1-12] ",
        " help       Show this message ",
        "",
        "Moths refer to the current year. Data is stored in ~/.expense-tracker/expenses.json",
        "(set the EXPENSE_TRACKER_FILE environment variable to use a different file).");

    private ExpenseTracker() {

    }

    public static void main(String[] args){
        System.exit(run(args));
    }

    /** Runs one command and return the process exit code. */
    static int run(String[] argv){
        if(argv.length == 0){
            System.err.print(USAGE);
            return 1;
        }
        String command = argv[0];
        if (command.equals("help") || command.equals("--help") || command.equals("-h")){
            System.out.println( USAGE);
            return 0;
        }

        try {
            ExpeseService service = new ExpenseService(new ExpenseRepository(ExpenseRepository.defaulPath()));

            switch (command) {
                case "add": add(argv, service); break;
                case "update": update(argv, service); break;
                case "delete": delete(argv, service); break;
                case "list":  list(argv, service); break;
                case "summary": summary(argv, service); break;
                case "budget": budget(argv, service); break;
                case "export": export(argv, service); break;
                default:
                    throw new TrackerException("Unknown command '" + command
                            + "'. run 'expense-tracker help' to see the available commands.");
            }
            return  0;
        } catch (TrackerException ex) {
            System.err.println("Error: " + ex.getMessage());
            return 1;

        }
    }


    // --------------------------------------------------------------------------------------- commands

    private static void add(String[] argv, ExpenseService service){
        Args a = Args.parse(argv, 1, Args.set("description", "amount" , "category", "date" ), Args.set());
        String description = Validation.description(a.require("description"));
        BigDecimal amount = Validation.amount(a.require("amount");
        String category = a.has("category") ? Validation.category(a.get("category")) : Validation.DEFAULT_CATEGORY;
        LocalDate date = a.has("date") ? Validation.date(a.get("date")) : LocalDate.now();

        Expense e = service.add(description, amount, category, date);
        System.out.println("Expense added successfully (ID: " + e.getID() + ")");
        printIfPresent(service.budgetWarning(date.getYear(), date.getMonthValue()));
    }

    // Continue in update
}

