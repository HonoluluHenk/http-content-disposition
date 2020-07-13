package ch.christophlinder.httpcontentdisposition.rules;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.CharArrayWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Encodes a string so that is a valid "pct-encoded" as described in
 * <a href="https://tools.ietf.org/html/rfc8187#section-3.2">RFC58187, Section 3.2
 * (Indicating Character Encoding and Language for HTTP Header Field Parameters)</a>.
 * <p>
 * FIXME: add some examples
 */
public class RFC8187Encoder {
    private static final RFC2231CharacterRules RFC_2231_CHARACTER_RULES = RFC2231CharacterRules.getInstance();

    /**
     * The length of one pct-encoded token (e.g.: "%FF") is three characters.
     * <p>
     * pct-encoded   = "%" HEXDIG HEXDIG
     */
    private static final int PCT_ENCODED_LENGTH = 3;


    /**
     * pct-encode a string according to <a href="https://tools.ietf.org/html/rfc8187#section-3.2">RFC8187, Section 3.2</a>.
     */
    public RFC8187Encoded encodeExtValue(String input, @Nullable Locale locale) {
        String languageTag = parseLanguageTag(locale);
        var encoded = pctEncode(input);
        String valueChars = encoded.getValue();

        String value = "UTF-8" + "'" + languageTag + "'" + valueChars;

        return new RFC8187Encoded(value, encoded.isEncoded());
    }

    public RFC8187Encoded encodeExtValue(String input) {
        return encodeExtValue(input, null);
    }

    public boolean needsEncoding(String input) {
        return input.codePoints()
                .allMatch(this::isAllowedChar);
    }

    private String parseLanguageTag(@Nullable Locale locale) {
        if (locale == null) {
            return "";
        }

        Locale clean = new Locale(locale.getLanguage(), locale.getCountry());

        return clean.toLanguageTag();
    }

    private RFC8187Encoded pctEncode(String input) {
        int length = input.length();

        try (CharArrayWriter writer = new CharArrayWriter(calcEncodedLength(length))) {

            boolean isEncoded = false;

            for (int offset = 0; offset < length; ) {
                int codePoint = input.codePointAt(offset);
                int charCount = Character.charCount(codePoint);

                if (isAllowedChar(codePoint)) {
                    writer.append(input.substring(offset, offset + charCount));
                } else {
                    writer.append(pctEncode(codePoint));
                    isEncoded = true;
                }

                offset += charCount;
            }

            return new RFC8187Encoded(writer.toString(), isEncoded);
        }
    }

    private String pctEncode(int codePoint) {
        ByteBuffer bytes = UTF_8.encode(CharBuffer.wrap(Character.toChars(codePoint)));

        StringBuilder hex = new StringBuilder(PCT_ENCODED_LENGTH * bytes.limit());
        for (int i = 0; i < bytes.limit(); i++) {
            hex.append('%').append(hexString(bytes.get(i)));
        }

        return hex.toString().toUpperCase(Locale.US);
    }

    private boolean isAllowedChar(int codePoint) {
        return RFC_2231_CHARACTER_RULES.isAttributeChar(codePoint);
    }

    private int calcEncodedLength(int numChars) {
        return Math.multiplyExact(numChars, PCT_ENCODED_LENGTH);
    }

    private String hexString(byte aByte) {
        // implicit conversion to int might create negative numbers (-127 upto 128)
        // but we need the unsigned value (0 - 255) for toHexString to work
        int unsignedInt = aByte & 0xff;
        return String.format("%02X", unsignedInt);
    }

}
