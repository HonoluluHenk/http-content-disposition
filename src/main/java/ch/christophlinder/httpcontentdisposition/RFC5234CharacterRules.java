package ch.christophlinder.httpcontentdisposition;

import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Predicate;

/**
 * Check if characters/character-sequences follow specific rules defined in <a href="https://tools.ietf.org/html/rfc5234#appendix-B.1">RFC5234, Appendix B.1</a>
 */
@ThreadSafe
@SuppressWarnings("unused") // this is a library module
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
    public boolean isALPHA(char c) {
        return (('A' <= c) && c <= 'Z') || (('a' <= c) && (c <= 'z'));
    }

    /**
     * input starts with %x41-5A / %x61-7A ; A-Z / a-z
     */
    public boolean startsWithALPHA(CharSequence input) {
        return startsWith(input, this::isALPHA);
    }

    /**
     * "0" / "1"
     */
    public boolean isBIT(char c) {
        return '0' == c || '1' == c;
    }

    /**
     * input starts with "0" / "1"
     */
    public boolean startsWithBIT(CharSequence input) {
        return startsWith(input, this::isBIT);
    }

    /**
     * %x01-7F ; any 7-bit US-ASCII character ; excluding NUL
     */
    public boolean isCHAR(char c) {
        return 0x01 <= c && c <= 0x7F;
    }

    /**
     * input starts with %x01-7F ; any 7-bit US-ASCII character ; excluding NUL
     */
    public boolean startsWithCHAR(CharSequence input) {
        return startsWith(input, this::isCHAR);
    }


    /**
     * %x0D ; carriage return
     */
    public boolean isCR(char c) {
        return c == 0x0D;
    }

    /**
     * input starts with %x0D ; carriage return
     */
    public boolean startsWithCR(CharSequence input) {
        return startsWith(input, this::isCR);
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
    public boolean isCRLF(CharSequence input) {
        return input.length() == 2
                && isCR(input.charAt(0))
                && isLF(input.charAt(1));
    }

    /**
     * input starts with the Internet standard newline.
     */
    public boolean startsWithCRLF(CharSequence input) {
        return input.length() >= 2
                && isCRLF(input.subSequence(0, 2));
    }

    /**
     * %x00-1F / %x7F ; controls
     */
    public boolean isCTL(char c) {
        return c <= 0x1F || c == 0x7F;
    }

    /**
     * input starts with %x00-1F / %x7F ; controls
     */
    public boolean startsWithCTL(CharSequence input) {
        return startsWith(input, this::isCTL);
    }


    /**
     * %x30-39 ; 0-9
     */
    public boolean isDIGIT(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * input starts with %x30-39 ; 0-9
     */
    public boolean startsWithDIGIT(CharSequence input) {
        return startsWith(input, this::isDIGIT);
    }


    /**
     * %x22 ; " (Double Quote)
     */
    public boolean isDQUOTE(char c) {
        return '"' == c;
    }

    /**
     * input starts with %x22 ; " (Double Quote)
     */
    public boolean startsWithDQUOTE(CharSequence input) {
        return startsWith(input, this::isDQUOTE);
    }


    /**
     * DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
     */
    public boolean isHEXDIG(char c) {
        return isDIGIT(c) || ('A' <= c && c <= 'F');
    }

    /**
     * input starts with DIGIT / "A" / "B" / "C" / "D" / "E" / "F"
     */
    public boolean startsWithHEXDIG(CharSequence input) {
        return startsWith(input, this::isHEXDIG);
    }


    /**
     * %x09 ; horizontal tab
     */
    public boolean isHTAB(char c) {
        return '\t' == c;
    }

    /**
     * input starts with %x09 ; horizontal tab
     */
    public boolean startsWithHTAB(CharSequence input) {
        return startsWith(input, this::isHTAB);
    }


    /**
     * %x0A ; linefeed
     */
    public boolean isLF(char c) {
        return 0x0A == c;
    }

    /**
     * input starts with %x0A ; linefeed
     */
    public boolean startsWithLF(CharSequence input) {
        return startsWith(input, this::isLF);
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
    public boolean isLWSP(CharSequence input) {
        // please note: https://tools.ietf.org/html/rfc5234#section-3.6
        // the *Rule says that number of occurrences might be 0!
        if (input.length() == 0) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            CharSequence remainder = input.subSequence(i, input.length());
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

    /**
     * input starts with *(WSP / CRLF WSP) (see: {@link #isLWSP(CharSequence)})
     */
    public boolean startsWithLWSP(CharSequence input) {
        return startsWithWSP(input) || startsWithCRLF(input);
    }


    /**
     * %x00-FF ; 8 bits of data
     */
    public boolean isOCTET(char c) {
        return c <= 0xFF;
    }

    /**
     * input starts with %x00-FF ; 8 bits of data
     */
    public boolean startsWithOCTET(CharSequence input) {
        return startsWith(input, this::isOCTET);
    }


    /**
     * %x20; the whitespace character ' '
     */
    public boolean isSP(char c) {
        return ' ' == c;
    }

    /**
     * input starts with %x20; the whitespace character ' '
     */
    public boolean startsWithSP(CharSequence input) {
        return startsWith(input, this::isSP);
    }


    /**
     * %x21-7E ; visible (printing) characters
     */
    public boolean isVCHAR(char c) {
        return 0x21 <= c && c <= 0x7E;
    }

    /**
     * input starts with %x21-7E ; visible (printing) characters
     */
    public boolean startsWithVCHAR(CharSequence input) {
        return startsWith(input, this::isVCHAR);
    }


    /**
     * SP / HTAB ; white space
     */
    public boolean isWSP(char c) {
        return isSP(c) || isHTAB(c);
    }

    /**
     * input starts with SP / HTAB ; white space
     */
    public boolean startsWithWSP(CharSequence input) {
        return startsWith(input, this::isWSP);
    }


    boolean startsWith(CharSequence input, Predicate<Character> predicate) {
        return input.length() >= 1 && predicate.test(input.charAt(0));
    }

}
