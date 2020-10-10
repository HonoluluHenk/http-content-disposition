package com.github.HonoluluHenk.httpcontentdisposition.internal.rules;

import com.github.HonoluluHenk.httpcontentdisposition.internal.Util;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Check if characters/character-sequences follow specific rules defined in <a href="https://tools.ietf.org/html/rfc5234#appendix-B.1">RFC5234, Appendix B.1</a>
 */
@ThreadSafe
public class RFC5234CharacterRules {

    /**
     * Convenience accessor.
     */
    public static final RFC5234CharacterRules INSTANCE = new RFC5234CharacterRules();

    /**
     * Convenience accessor.
     */
    public static RFC5234CharacterRules getInstance() {
        return INSTANCE;
    }

    /**
     * %x41-5A / %x61-7A ; A-Z / a-z
     */
    public boolean isALPHA(int codePoint) {
        return isUpperALPHA(codePoint) || isLowerAlpha(codePoint);
    }

    private boolean isUpperALPHA(int codePoint) {
        return Util.isBetween(codePoint, 'A', 'Z');
    }

    private boolean isLowerAlpha(int codePoint) {
        return Util.isBetween(codePoint, 'a', 'z');
    }

    /**
     * "0" / "1"
     */
    public boolean isBIT(int codePoint) {
        return '0' == codePoint || '1' == codePoint;
    }

    /**
     * %x01-7F ; any 7-bit US-ASCII character ; excluding NUL
     */
    public boolean isCHAR(int codePoint) {
        return Util.isBetween(codePoint, 0x01, 0x7F);
    }

    /**
     * %x0D ; carriage return
     */
    public boolean isCR(int codePoint) {
        return codePoint == 0x0D;
    }

    /**
     * Internet standard newline
     */
    public boolean isCRLF(char[] c) {
        return c.length == 2
                && isCR(c[0])
                && isLF(c[1]);
    }

    /**
     * Internet standard newline
     */
    public boolean isCRLF(String input) {
        return input.length() == 2
                && isCRLF(new char[]{input.charAt(0), input.charAt(1)});
    }

    /**
     * %x00-1F / %x7F ; controls
     */
    public boolean isCTL(int codePoint) {
        return Util.isBetween(codePoint, 0, 0x1F) || codePoint == 0x7F;
    }

    /**
     * %x30-39 ; 0-9
     */
    public boolean isDIGIT(int codePoint) {
        return Util.isBetween(codePoint, '0', '9');
    }

    /**
     * %x22 ; " (Double Quote)
     */
    public boolean isDQUOTE(int codePoint) {
        return '"' == codePoint;
    }

    /**
     * DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
     */
    public boolean isHEXDIG(int codePoint) {
        return isDIGIT(codePoint) || Util.isBetween(codePoint, 'A', 'F');
    }

    /**
     * %x09 ; horizontal tab
     */
    public boolean isHTAB(int codePoint) {
        return '\t' == codePoint;
    }

    /**
     * %x0A ; linefeed
     */
    public boolean isLF(int codePoint) {
        return 0x0A == codePoint;
    }

    /**
     * *(WSP / CRLF WSP)
     * ; Use of this linear-white-space rule
     * ;  permits lines containing only white
     * ;  space that are no longer legal in
     * ;  mail headers and have caused
     * ;  interoperability problems in other
     * ;  contexts.
     * ; Do not use when defining mail
     * ;  headers and use with caution in
     * ;  other contexts.
     */
    public boolean isLWSP(String input) {
        // please note: https://tools.ietf.org/html/rfc5234#section-3.6
        // the *Rule says that number of occurrences might be 0!
        int length = input.length();
        if (length == 0) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            String remainder = input.substring(i, length);
            if (startsWithCRLF(remainder)) {
                i++; // crlf is 2 characters long => skip one
                continue;
            }

            if (!isWSP(input.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean startsWithCRLF(String input) {
        return input.length() >= 2 && isCRLF(input.substring(0, 2));
    }

    /**
     * %x00-FF ; 8 bits of data
     */
    public boolean isOCTET(int codePoint) {
        return Util.isBetween(codePoint, 0x00, 0xFF);
    }

    /**
     * %x20; the whitespace character ' '
     */
    public boolean isSP(int codePoint) {
        return ' ' == codePoint;
    }

    /**
     * %x21-7E ; visible (printing) characters
     */
    public boolean isVCHAR(int codePoint) {
        return Util.isBetween(codePoint, 0x21, 0x7E);
    }

    /**
     * SP / HTAB ; white space
     */
    public boolean isWSP(int c) {
        return isSP(c) || isHTAB(c);
    }

}
