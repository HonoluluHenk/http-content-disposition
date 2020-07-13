package ch.christophlinder.httpcontentdisposition.rules;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

@ThreadSafe
public class RFC2616CharacterRules {

    /**
     * Convenience accessor.
     */
    public static final RFC2616CharacterRules INSTANCE = new RFC2616CharacterRules();

    /**
     * Convenience accessor.
     */
    public static RFC2616CharacterRules getInstance() {
        return INSTANCE;
    }

    private static final Set<Character> SEPARATOR_CHARS = Set.of(
            '(', ')', '<', '>', '@',
            ',', ';', ':', '\\', '\"',
            '/', '[', ']', '?', '=',
            '{', '}', ' ', '\t'
    );

    /**
     * &lt;any 8-bit sequence of data&gt;
     */
    public boolean isOCTET(int codePoint) {
        return isBetween(codePoint, 0, 255);
    }

    /**
     * &lt;any US-ASCII character (octets 0 - 127)&gt;
     */
    public boolean isCHAR(int codePoint) {
        return isBetween(codePoint, 0, 127);
    }

    /**
     * &lt;any US-ASCII control character (octets 0 - 31) and DEL (127)&gt;
     */
    public boolean isCTL(int codePoint) {
        return isBetween(codePoint, 0, 31) || codePoint == 127;
    }

    /**
     * 1*&lt;any CHAR except CTLs or separators&gt;
     */
    public boolean isToken(@Nullable CharSequence input) {
        if (input == null || input.length() == 0) {
            return false;
        }

        return input.chars().allMatch(this::isTokenChar);
    }

    /**
     * One character as defined in {@link #isToken(CharSequence)}.
     */
    public boolean isTokenChar(int codePoint) {
        return isCHAR(codePoint) && !isCTL(codePoint) && !isSeparator(codePoint);
    }

    /**
     * separators =
     * "(" | ")" | "<" | ">" | "@"
     * | "," | ";" | ":" | "\" | <">
     * | "/" | "[" | "]" | "?" | "="
     * | "{" | "}" | SP | HT
     */
    public boolean isSeparator(int codePoint) {
        if (!isOCTET(codePoint)) {
            return false;
        }

        char c = (char) codePoint;
        return SEPARATOR_CHARS.contains(c);
    }

    private boolean isBetween(int codePoint, int lowerBound, int upperBound) {
        assert upperBound >= lowerBound : "Input must be lowerBound <= upperBound but was: " + lowerBound + "/" + upperBound;
        return lowerBound <= codePoint && codePoint <= upperBound;
    }
}
