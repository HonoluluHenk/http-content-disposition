package ch.christophlinder.httpcontentdisposition.rules;

import java.util.Set;

public class RFC2231CharacterRules {
    public static final RFC2231CharacterRules INSTANCE = new RFC2231CharacterRules();

    /**
     * RFC2045: tspecials :=
     * "(" / ")" / "<" / ">" / "@" /
     * "," / ";" / ":" / "\" / <">
     * "/" / "[" / "]" / "?" / "="
     */
    private static final Set<Character> TSPECIALS = Set.of(
            // SPACE (btw: tab also included in CTLs)
            // "tspecials" defined in RFC2045
            '(', ')', '<', '>', '@',
            ',', ';', ':', '\\', '"',
            '/', '[', ']', '?', '='
    );

    public static RFC2231CharacterRules getInstance() {
        return INSTANCE;
    }

    /**
     * attribute-char := <any (US-ASCII) CHAR except SPACE, CTLs,
     * "*", "'", "%", or tspecials>
     */
    public boolean isAttributeChar(int codePoint) {
        return isCHAR(codePoint)
                && !isSPACE(codePoint)
                && !isCTL(codePoint)
                && !isAttributeCharForbidden(codePoint)
                && !isTspecials(codePoint);
    }

    private boolean isAttributeCharForbidden(int codePoint) {
        return codePoint == '*' || codePoint == '\'' || codePoint == '%';
    }

    /**
     * RFC822: CHAR = <any ASCII character> ; (ASCII: 0-127)
     */
    private boolean isCHAR(int codePoint) {
        return 0 <= codePoint && codePoint <= 127;
    }

    /**
     * RFC822: CTL <any ASCII control character and DEL> ; (ASCII char 0-31 and 127)
     */
    private boolean isCTL(int codePoint) {
        return (0 <= codePoint && codePoint <= 31) || codePoint == 127;
    }

    /**
     * RFC822: SPACE = <ASCII SP, space> : (ASCII char 32)
     */
    private boolean isSPACE(int codePoint) {
        return codePoint == 32;
    }

    /**
     * RFC2045: tspecials :=
     * "(" / ")" / "<" / ">" / "@" /
     * "," / ";" / ":" / "\" / <">
     * "/" / "[" / "]" / "?" / "="
     * ; Must be in quoted-string,
     * ; to use within parameter values
     */
    private boolean isTspecials(int codePoint) {
        // isCHAR implies values 0..127 and thus casting to char should produce no overflow
        return isCHAR(codePoint) && TSPECIALS.contains((char) codePoint);
    }
}
