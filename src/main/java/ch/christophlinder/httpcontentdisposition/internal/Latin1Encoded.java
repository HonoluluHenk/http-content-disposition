package ch.christophlinder.httpcontentdisposition.internal;

import static java.util.Objects.requireNonNull;

/**
 * Latin1, a.k.a. ISO-8859-1
 */
public class Latin1Encoded {
    private final String text;
    private final boolean isTransformed;

    public Latin1Encoded(String text, boolean isTransformed) {
        this.text = requireNonNull(text);
        this.isTransformed = isTransformed;
    }

    public String getText() {
        return text;
    }

    public boolean isTransformed() {
        return isTransformed;
    }

    public boolean isOriginal() {
        return !isTransformed;
    }
}
