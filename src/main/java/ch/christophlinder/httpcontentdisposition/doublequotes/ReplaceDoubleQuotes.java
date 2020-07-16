package ch.christophlinder.httpcontentdisposition.doublequotes;

import static java.util.Objects.requireNonNull;

public class ReplaceDoubleQuotes implements DoubleQuotes {
    private static final long serialVersionUID = -153332297588970014L;

    private final String replacement;

    public ReplaceDoubleQuotes(String replacement) {
        String r = requireNonNull(replacement, "No replacement");
        if (r.contains("\"")) {
            throw new IllegalArgumentException("Replacement may not contain the double-quote character but was: " + replacement);
        }

        this.replacement = r;
    }

    /**
     * Remove double quotes (i.e.: replace with empty string)
     */
    public ReplaceDoubleQuotes() {
        this.replacement = "";
    }

    public String getReplacement() {
        return replacement;
    }

    @Override
    public String handle(String input) {
        return input.replace("\"", replacement);
    }
}
