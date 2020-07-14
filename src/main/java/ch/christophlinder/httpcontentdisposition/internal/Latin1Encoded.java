package ch.christophlinder.httpcontentdisposition.internal;

import static java.util.Objects.requireNonNull;

/**
 * Latin1, a.k.a. ISO-8859-1
 */
public class Latin1Encoded {
    private final String text;
    private final boolean withDoubleQuotes;

    public Latin1Encoded(String text, boolean withDoubleQuotes) {
        this.text = requireNonNull(text);
        this.withDoubleQuotes = withDoubleQuotes;
    }

    public String getText() {
        return text;
    }

    public boolean isWithDoubleQuotes() {
        return withDoubleQuotes;
    }
}
