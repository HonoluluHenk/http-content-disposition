package com.github.HonoluluHenk.httpcontentdisposition.internal.rules;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

import static com.github.HonoluluHenk.httpcontentdisposition.internal.Util.isBetween;
import static com.github.HonoluluHenk.httpcontentdisposition.internal.Util.setOf;

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

    private static final Set<Character> SEPARATOR_CHARS = setOf(
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
    public boolean isToken(@Nullable String input) {
        if (input == null || input.length() == 0) {
            return false;
        }

        boolean result = input.codePoints()
                .allMatch(this::isTokenChar);

        return result;
    }

    /**
     * One character as defined in {@link #isToken(String)}.
     */
    public boolean isTokenChar(int codePoint) {
        boolean result = isCHAR(codePoint) && !isCTL(codePoint) && !isSeparator(codePoint);

        return result;
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

//    /**
//     * token | quoted-string
//     */
//    public boolean isValue(String input) {
//        return isToken(input); // FIXME: implement: || isQuotedString(input);
//    }
}
