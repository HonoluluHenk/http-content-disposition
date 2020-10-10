package com.github.HonoluluHenk.httpcontentdisposition.internal.rules;

import edu.umd.cs.findbugs.annotations.Nullable;

import javax.annotation.concurrent.ThreadSafe;
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
 */
@ThreadSafe
public class RFC8187Encoder {
    private static final RFC8187CharacterRules RFC_8187_CHARACTER_RULES = RFC8187CharacterRules.getInstance();

    /**
     * The length of one pct-encoded token (e.g.: "%FF") is three characters.
     * <p>
     * pct-encoded   = "%" HEXDIG HEXDIG
     */
    private static final int PCT_ENCODED_LENGTH = 3;

    /**
     * pct-encode a string according to <a href="https://tools.ietf.org/html/rfc8187#section-3.2">RFC8187, Section 3.2</a>.
     */
    public Encoded encodeExtValue(String input, @Nullable Locale locale) {
        String languageTag = parseLanguageTag(locale);
        Encoded pctEncoded = pctEncode(input);
        String valueChars = pctEncoded.getValue();

        String value = String.format("UTF-8'%s'%s", languageTag, valueChars);

        return new Encoded(value, pctEncoded.isEncoded());
    }

    public Encoded encodeExtValue(String input) {
        return encodeExtValue(input, null);
    }

    private String parseLanguageTag(@Nullable Locale locale) {
        if (locale == null) {
            return "";
        }

        // main purpose: strip variants and extensions
        Locale clean = new Locale(locale.getLanguage(), locale.getCountry());

        return clean.toLanguageTag();
    }

    private Encoded pctEncode(String input) {
        int length = input.length();

        try (CharArrayWriter writer = new CharArrayWriter(calcEncodedLength(length))) {

            boolean isEncoded = false;

            for (int offset = 0; offset < length; ) {
                int codePoint = input.codePointAt(offset);
                int charCount = Character.charCount(codePoint);

                if (isAllowed(codePoint)) {
                    writer.append(input.substring(offset, offset + charCount));
                } else {
                    writer.append(pctEncode(codePoint));
                    isEncoded = true;
                }

                offset += charCount;
            }

            return new Encoded(writer.toString(), isEncoded);
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

    private boolean isAllowed(int codePoint) {
        return RFC_8187_CHARACTER_RULES.isAttrChar(codePoint);
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
