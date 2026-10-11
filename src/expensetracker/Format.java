package expensetracker;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/** Output formatting helpers. */

final class Format {

    private static final DecimalFormatSymbols US = DecimalFormatSymbols.getInstance(Locale.US);

    private Format() {

    }

    /** $20 for whole amounts, $12.50 otherwise*/
    static String money(BigDecimal value) {
        boolean whole = value.stripTrailingZeros().scale() <= 0;
        DecimalFormat df = new DecimalFormat(whole ? "#,##0" : ",##0.00", US);

        return String.format("$" + df.format(value));
    }


    static String monthName(int month){
        return Month.of(month).getDisplayName(TextStyle.FULL , Locale.ENGLISH);
    }

    /** Prints an aligned table; columns listed in rightAligned are right-justified. */

    static String table(String[] headers, List<String[]> rows , int ... rightAligned){
        int[] widths = new int[headers.length];
        for(int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
            for (String[] row : rows) {
                widths[i] = Math.max(widths[i] , row[i].length());
            }

        }
        StringBuilder sb = new StringBuilder();
        appendRow(sb , row, widths, rightAligned);
        for(String[] row : rows) {
            appendRow(sb , row , widths, rightAligned);
        }

        return sb.toString();
    }

    // missing appendRow() , contains() and repeat()
}
