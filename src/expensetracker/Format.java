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
        appendRow(sb , headers, widths, rightAligned);
        for(String[] row : rows) {
            appendRow(sb , row , widths, rightAligned);
        }

        return sb.toString();
    }

    private static void appendRow(StringBuilder sb , String[] cells, int[] widths, int[] rightAligned) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.length; i++){
            if (i > 0) {
                line.append(" ");
            }
            String pad = repeat(' ', widths[i] - cells[i].length());
            line.append(contains(rightAligned, i) ? pad + cells[i] : cells[i] + pad);
        }

        int end = line.length();
        while (end > 0 && line.charAt(end - 1) == ' '){
            end--;
        }

        sb.append(line, 0, end).append((System.lineSeparator()));
    }

    private static boolean contains(int[] values, int target){
        for ( int v : values) {
            if ( v == target) {
                return true;
            }
        }

        return false;
    }

    private static String repeat(char c , int count){
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < count; i ++){
            sb.append(c);
        }
        return sb.toString();
    }
}
