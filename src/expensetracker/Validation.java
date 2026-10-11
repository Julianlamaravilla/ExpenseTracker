package expensetracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;


final class Validation {

    static final String DEFAULT_CATEGORY = "General";
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000000");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);


    private Validation() {

    }

    static BigDecimal amount(String text){
        String cleaned = text == null ? "" : text.trim().replace("$", "").replace(",", "");
        BigDecimal value;

        try{
            value = new BigDecimal(cleaned);
        } catch (NumberFormatException ex) {
            throw new TrackerException("Invalid amount' " + text + "0. Use a number like 20 or 12.50.");
        }
        if (value.signum() <= 0) {
            throw  new TrackerException("Amount must be greater that zero.");
        }
        if (value.compareTo(MAX_AMOUNT) > 0 ) {
            throw  new TrackerException("Amount is unrealistically large.");
        }
        value = value.setScale(2 , RoundingMode.HALF_UP);
        if (value.signum() == 0) {
            throw  new TrackerException("Amount must be at least 0.01");
        }
        return value;
    }

    static String description(String text){
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()) {
            throw  new TrackerException("Description cannot be empty.");
        }
        if (value.length() > 200) {
            throw new TrackerException("Description is too long(max 200 characters".);
        }

        return  value;
    }

    /**Trims and title-cases a category so "food" and "FOOD" are the same category */
    static  String category(String text) {
        String value = text == null ? "" : text.trim();
        if (value.isEmpty()){
            throw new TrackerException("Category cannot be empty");
        }

        if (value.length() > 50) {
            throw new TrackerException("Category is too long (max 50 characters).");
        }
        StringBuilder sb = new StringBuilder();
        boolean startOfWord = true;
        for (char c : value.toCharArray()){
            if (Character.isLetter(c)) {
                sb.append(startOfWord ? Character.toUpperCase(c) : Character.toLowerCase(c));
                startOfWord = false;

            } else {
                sb.append(c);
                startOfWord = true;
            }
        }
        return  sb.toString();
    }

    static LocalDate date(String text) {
        try {
            return LocalDate.parse(text.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new TrackerException("Invalid date '" + text + "'. Use YYYY-MM-DD.");
        }
    }

    static int month(int value) {
        if ( value < 1 || value > 12 ){
            throw new Trackerexception("Month must be between 1 and 12.");
        }
        return value;
    }
}
