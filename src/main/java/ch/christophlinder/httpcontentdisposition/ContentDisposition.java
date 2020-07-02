package ch.christophlinder.httpcontentdisposition;

import java.util.Locale;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ContentDisposition {
	public enum Disposition {
		INLINE("inline"),
		ATTACHMENT("attachment"),
		;

		public final String disposition;

		Disposition(String disposition) {
			this.disposition = disposition;
		}
	}

	public String build(Disposition disposition, String filename) {
		return this.build(disposition, filename, Locale.forLanguageTag(""));
	}

	/**
	 * Build the Content-Disposition HTTP response header according to
	 * <a href="https://tools.ietf.org/html/rfc6266">RFC6266 -
	 * Use of the Content-Disposition Header Field in the Hypertext Transfer Protocol (HTTP)</a>.
	 *
	 * @param locale The natural locale of the filename.
	 * Mainly for completeness' sake.
	 * From the RFC: "this is of limited use
	 * for filenames and is likely to be ignored by recipients."
	 */
	public String build(Disposition disposition, String filename, Locale locale) {
		Objects.requireNonNull(disposition, "disposition must be given");
		Objects.requireNonNull(filename, "filename must be given");

		String asciiFilename = toASCII(filename);
		String encodedFilename = encodeRFC5987(filename, locale);

		var result = String.format("%s; filename=%s; filename*=%s",
				disposition.disposition, asciiFilename, encodedFilename);

		return result;
	}

	private String encodeRFC5987(String filename, Locale locale) {
		var encodedFilename = new RFC5987PctEncoder().encode(filename);

		final String languageTag = formatLanguageTag(locale);
		var result = String.format("utf-8'%s'%s", languageTag, encodedFilename);

		return result;
	}

	private String formatLanguageTag(Locale locale) {
		if (locale == null || locale.toString().equals("")) {
			return "";
		}

		var result = locale
				.stripExtensions()
				.toLanguageTag();

		return result;
	}

	private String toASCII(String filename) {
		//FIXME: use CharsetEncoder, see docs for getBytes
		byte[] asciiBytes = filename.getBytes(US_ASCII);
		var result = new String(asciiBytes, UTF_8);

		return result;
	}

}
