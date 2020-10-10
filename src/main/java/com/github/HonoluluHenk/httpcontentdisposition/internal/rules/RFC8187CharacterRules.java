package com.github.HonoluluHenk.httpcontentdisposition.internal.rules;

import com.github.HonoluluHenk.httpcontentdisposition.internal.Util;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class RFC8187CharacterRules {

    public static final RFC8187CharacterRules INSTANCE = new RFC8187CharacterRules();

    public static RFC8187CharacterRules getInstance() {
        return INSTANCE;
    }

    public boolean isAttrChar(int codePoint) {
        return isALPHA(codePoint)
                || isDIGIT(codePoint)
                || isAttrCharSpecial(codePoint);
    }

    private boolean isAttrCharSpecial(int codePoint) {
        return Util.isOneOf(codePoint,
                '!', '#', '$', '&', '+', '-', '.',
                '^', '_', '`', '|', '~');
    }

    private boolean isALPHA(int codePoint) {
        return isUpperALPHA(codePoint) || isLowerAlpha(codePoint);
    }

    private boolean isUpperALPHA(int codePoint) {
        return Util.isBetween(codePoint, 'A', 'Z');
    }

    private boolean isLowerAlpha(int codePoint) {
        return Util.isBetween(codePoint, 'a', 'z');
    }

    private boolean isDIGIT(int codePoint) {
        return Util.isBetween(codePoint, '0', '9');
    }

}
