package ch.christophlinder.httpcontentdisposition.rules;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

import static ch.christophlinder.httpcontentdisposition.internal.Util.setOf;

/**
 * Check if characters/character-sequences follow specific rules defined in <a href="https://tools.ietf.org/html/rfc5987#section-3.2">RFC5982, Section 3.2</a>
 */
@ThreadSafe
public class RFC5987CharacterRules {
    private static final RFC5987CharacterRules instance = new RFC5987CharacterRules();

    public static RFC5987CharacterRules getInstance() {
        return instance;
    }

    // From RFC5987, Section 3.2
    // https://tools.ietf.org/html/rfc5987#section-3.2
	/*
     value-chars   = *( pct-encoded / attr-char )

     attr-char     = ALPHA / DIGIT
                   / "!" / "#" / "$" / "&" / "+" / "-" / "."
                   / "^" / "_" / "`" / "|" / "~"
                   ; token except ( "*" / "'" / "%" )
	 */
    private static final Set<Character> ATTR_CHAR_SPECIALS = setOf(
            '!', '#', '$', '&', '+', '-', '.',
            '^', '_', '`', '|', '~'
    );


    /**
     * ALPHA / DIGIT
     * / "!" / "#" / "$" / "&" / "+" / "-" / "."
     * / "^" / "_" / "`" / "|" / "~"
     * ; token except ( "*" / "'" / "%" )
     */
    public boolean isAttrChar(int codePoint) {
        boolean result = RFC5234CharacterRules.INSTANCE.isALPHA(codePoint)
                || RFC5234CharacterRules.INSTANCE.isDIGIT(codePoint)
                || isAttrSpecialChar(codePoint);

        return result;
    }

    private boolean isAttrSpecialChar(int codepoint) {
        if (!RFC5234CharacterRules.INSTANCE.isOCTET(codepoint)) {
            return false;
        }

        // simply casting only works for octets, hence the check above
        char character = (char) codepoint;

        return ATTR_CHAR_SPECIALS.contains(character);
    }
}
