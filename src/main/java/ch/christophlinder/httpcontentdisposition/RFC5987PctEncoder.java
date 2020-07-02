package ch.christophlinder.httpcontentdisposition;

import java.io.CharArrayWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Locale;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Encodes a string for the use in Content-Dispositon HTTP response headers using the percent-encoding described in
 * <a href="https://tools.ietf.org/html/rfc5987#section-3.2">RFC5987 Section 3.2</a>.
 *
 * <p>
 * This is quite similar to the widespread <a href="https://en.wikipedia.org/wiki/Percent-encoding">URL-Encoding</a>
 * but differs mostly in characters that get encoded or not.
 * </p>
 */
public class RFC5987PctEncoder {
	// From RFC5987, Section 3.2
	// https://tools.ietf.org/html/rfc5987#section-3.2
	/*
     value-chars   = *( pct-encoded / attr-char )

     attr-char     = ALPHA / DIGIT
                   / "!" / "#" / "$" / "&" / "+" / "-" / "."
                   / "^" / "_" / "`" / "|" / "~"
                   ; token except ( "*" / "'" / "%" )
	 */
	private static final Set<Character> ATTR_CHAR_SPECIALS = Set.of(
			'!', '#', '$', '&', '+', '-', '.',
			'^', '_', '`', '|', '~'
	);

	/**
	 * The length of one encoded token is three characters (see e.g.: "%FF")
	 */
	public static final int HEX_TOKEN_LENGTH = 3;

	private boolean isAttrChar(char[] codepointChars) {
		if (isMultibyte(codepointChars)) {
			return false;
		}
		char asciiLikeChar = charInASCIIRange(codepointChars);

		boolean result = isALPHA(asciiLikeChar);
		result |= isDIGIT(asciiLikeChar);
		result |= isAttrCharSpecial(asciiLikeChar);

		return result;
	}

	private boolean isALPHA(char c) {
		var result = (('a' <= c) && (c <= 'z')) || (('A' <= c) && c <= 'Z');

		return result;
	}

	private boolean isDIGIT(char c) {
		boolean result = '0' <= c && c <= '9';

		return result;
	}

	private boolean isAttrCharSpecial(char c) {
		boolean result = ATTR_CHAR_SPECIALS.contains(c);

		return result;
	}

	private char charInASCIIRange(char[] codepointChars) {
		return codepointChars[0];
	}

	private boolean isMultibyte(char[] codepointChars) {
		return codepointChars.length > 1;
	}

	private static String pctEncode(char[] codepointChars) {
		ByteBuffer bytes = UTF_8.encode(CharBuffer.wrap(codepointChars));

		StringBuilder hex = new StringBuilder(HEX_TOKEN_LENGTH * bytes.limit());
		for (int i = 0; i < bytes.limit(); i++) {
			hex.append('%').append(hexString(bytes.get(i)));
		}

		return hex.toString().toUpperCase(Locale.US);
	}

	private static String hexString(byte aByte) {
		// implicit conversion to int might create negative numbers (-127 upto 128)
		// but we need the unsigned value (0 - 255) for toHexString to work
		int unsignedInt = aByte & 0xff;
		return String.format("%02X", unsignedInt);
	}

	public String encode(String in) {
		try (CharArrayWriter writer = new CharArrayWriter(in.length() * HEX_TOKEN_LENGTH)) {

			in.codePoints().forEachOrdered(codePoint -> {
				char[] chars = Character.toChars(codePoint);

				if (isAttrChar(chars)) {
					writer.append(new String(chars));
				} else {
					writer.append(pctEncode(chars));
				}
			});

			return writer.toString();
		}
	}
}
