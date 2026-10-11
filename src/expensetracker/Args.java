package expensetracker;

import java.lang.reflect.Array;
import java.util.*;


/**
 * minimal command-line option parser.
 * Support "--name value" , "--name=value" and boolean switches like "--clear"
 * A value start with a single dash, so "--amount -5" reaches validation as "-5"
 */


final class Args {

    private final Map<String, String> values = new HashMap<>();
    private final Set<String> switches = new HashSet<>();

    private Args() {

    }

    /**
     * @param argv          all program arguments
     * @param start         index of the first option (after the command name)
     * @param valueOptions  options that take a value (without leading dashes)
     * @param switchOptions options that take no value
     */

    static Args parse(String[] argv, int start, Set<String> valueOptions , Set<String> switchOptions) {
        Args args = new Args();
        for (int i = start; i < argv.length; i ++) {
            String token = argv[i];
            if (!token.startsWith("--") || token.length() == 2) {
                throw new TrackerException("Unexpected argument '" + token + "'. Options look like --name value.");
            }
            String name = token.substring(2);
            String inlineValue = null;
            int eq = name.indexOf('=');
            if (eq >= 0) {
                inlineValue = name.substring(eq + 1);
                name = name.substring(0, eq);
            }

            if (switchOptions.contains(name)) {
                if (inlineValue != null) {
                    throw new TrackerException("Option --" + name + " does not take a value");
                }
                args.switches.add(name);
            } else if (valueOptions.contains(name)) {
                if (args.values.containsKey(name)) {
                    throw new TrackerException("Option --" + name + " was given more than once");
                }
                String value = inlineValue;
                if ( value == null) {
                    if (i + 1 >= argv.length || argv[i + 1].startsWith("--")) {
                        throw new TrackerException("Option --" + name + " needs a value.");
                    }
                    value = argv[++i];
                }
                args.values.put(name , value);
            } else {
                Set<String> allowed = new TreeSet<>(valueOptions);
                allowed.addAll(switchOptions);
                StringBuilder list = new StringBuilder();
                for (String a : allowed) {
                    list.append(list.length() == 0 ? "" : ", ").append("--").append(a);
                }

                throw new TrackerException("Unknow option --" + name + ". Allowed: " +
                        (list.length() == 0 ? "none" : list) + ". ");
            }
        }

        return args;
    }

    static Set<String> set(String... names) {
        return new HashSet<>(Arrays.asList(names));
    }

    boolean has(String name) {
        return values.containsKey(name) || switches.contains(name);
    }

    String get(String name) {
        return values.get(name);
    }

    String require(String name) {
        String value = values.get(name);
        if (value == null) {
            throw new TrackerException("Missing required option --" + name + ". ");
        }

        return value;
    }

    /** Return the option as an Integer, or null if it was not given */

    Integer getInt(String name) {
        String value = values.get(name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new TrackerException("Option --" + name + " must be a whole number, got '" + value + "'. ");
        }
    }

    int requireInt(String name) {
        require(name);
        return  getInt(name);
    }
}
