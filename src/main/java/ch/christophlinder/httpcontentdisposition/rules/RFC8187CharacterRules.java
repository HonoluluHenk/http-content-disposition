package ch.christophlinder.httpcontentdisposition.rules;

import static ch.christophlinder.httpcontentdisposition.internal.CharacterRules.isBetween;
import static ch.christophlinder.httpcontentdisposition.internal.CharacterRules.isOneOf;

public class RFC8187CharacterRules {

    public static final RFC8187CharacterRules INSTANCE = new RFC8187CharacterRules();

    public static RFC8187CharacterRules getInstance() {
        return INSTANCE;
    }

    public boolean isAttrChar(int codePoint) {
        return isALPHA(codePoint)
                || isDIGIT(codePoint)
                || isOneOf(codePoint,
                '!', '#', '$', '&', '+', '-', '.',
                '^', '_', '`', '|', '~');
    }

    private boolean isALPHA(int codePoint) {
        return isUpperALPHA(codePoint) || isLowerAlpha(codePoint);
    }

    private boolean isUpperALPHA(int codePoint) {
        return isBetween(codePoint, 'A', 'Z');
    }

    private boolean isLowerAlpha(int codePoint) {
        return isBetween(codePoint, 'a', 'z');
    }

    private boolean isDIGIT(int codePoint) {
        return isBetween(codePoint, '0', '9');
    }

}
