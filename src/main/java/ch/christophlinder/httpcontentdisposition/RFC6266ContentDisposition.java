package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.internal.Latin1Encoded;
import ch.christophlinder.httpcontentdisposition.rules.RFC8187Encoder;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class RFC6266ContentDisposition {

    public String filename(Disposition disposition, String filename) {
        return this.filename(disposition, filename, Locale.forLanguageTag(""));
    }

    /**
     * Build the Content-Disposition HTTP response header according to
     * <a href="https://tools.ietf.org/html/rfc6266">RFC6266 -
     * Use of the Content-Disposition Header Field in the Hypertext Transfer Protocol (HTTP)</a>.
     *
     * @param locale The natural locale of the filename.
     *               Mainly for completeness' sake.
     *               From the RFC: "this is of limited use
     *               for filenames and is likely to be ignored by recipients."
     */
    public String filename(Disposition disposition, String filename, @Nullable Locale locale) {
        requireNonNull(disposition, "disposition must be given");
        requireNonNull(filename, "filename must be given");

        // FIXME: which RFC?
        Latin1Encoded latin1Encoded = new Latin1Encoded("", false); //iso88591Strategy.encode(filename, locale);
        var isoFilename = latin1Encoded.getText();

        if (latin1Encoded.isTransformed()) {
            // RFC5987
            var encodedFilename = encodeRFC5987(filename, locale);
            var result = formatBothIsoAndEncoded(disposition, isoFilename, encodedFilename, locale);

            return result;
        } else {

            var result = formatIsoOnly(disposition, isoFilename);

            return result;
        }

    }

    private String formatIsoOnly(Disposition disposition, String isoFilename) {
        var result = String.format("%s; filename=%s", disposition.getHeaderAttribute(), isoFilename);

        return result;
    }

    private String formatBothIsoAndEncoded(Disposition disposition, String isoFilename, String encodedFilename, Locale locale) {
        var result = String.format("%s; filename=%s; filename*=%s",
                disposition.getHeaderAttribute(), isoFilename, encodedFilename);

        return result;
    }

    private String encodeRFC5987(String filename, @Nullable Locale locale) {
        var encodedFilename = new RFC8187Encoder().encodeExtValue(filename);

        final String languageTag = formatLanguageTag(locale);
        var result = String.format("utf-8'%s'%s", languageTag, encodedFilename);

        return result;
    }

    private String formatLanguageTag(@Nullable Locale locale) {
        if (locale == null || locale.toString().equals("")) {
            return "";
        }

        var result = locale
                .stripExtensions()
                .toLanguageTag();

        return result;
    }
}
