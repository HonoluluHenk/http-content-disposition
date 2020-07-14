package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.rules.Encoded;
import ch.christophlinder.httpcontentdisposition.rules.Latin1Encoder;
import ch.christophlinder.httpcontentdisposition.rules.RFC8187Encoder;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class RFC6266ContentDisposition {
    private final RFC8187Encoder rfc8187Encoder = new RFC8187Encoder();

    /**
     * Convenience: calls {@link #filename(Disposition, String, Locale)} with null locale.
     */
    public String filename(Disposition disposition, String filename) {
        return this.filename(disposition, filename, null);
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


        var latin1Encoded = new Latin1Encoder().encode(filename);
        String latin1Quoted = quoteLatin1(latin1Encoded);

        // FIXME: das reicht so nicht: was ist mit den ganzen Special Chars???
        if (latin1Encoded.isEncoded()) {
            var rfcEncoded = rfc8187Encoder.encodeExtValue(filename, locale);
            var result = formatBothIsoAndEncoded(disposition, latin1Quoted, rfcEncoded.getValue());

            return result;

        } else {
            var result = formatLatin1Only(disposition, latin1Quoted);

            return result;
        }

    }

    private String formatLatin1Only(Disposition disposition, String latin1Filename) {
        var result = String.format("%s; filename=%s", disposition.getHeaderAttribute(), latin1Filename);

        return result;
    }

    private String quoteLatin1(Encoded isoFilename) {
        String input = isoFilename.getValue();
        if (!input.contains("\"")) {
            return input;
        }

        String quoted = input.replace("\"", "\\\"");
        return '"' + quoted + '"';
    }

    private String formatBothIsoAndEncoded(Disposition disposition, String latin1Filename, String encodedFilename) {
        var result = String.format("%s; filename=%s; filename*=%s",
                disposition.getHeaderAttribute(), latin1Filename, encodedFilename);

        return result;
    }

}
