package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.RFC6266ContentDisposition.Builder;
import ch.christophlinder.httpcontentdisposition.rules.DefaultISO88591Encoder;
import ch.christophlinder.httpcontentdisposition.rules.RFC8187Encoder;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class RFC6266ContentDisposition {
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    private final RFC8187Encoder rfc8187Encoder = new RFC8187Encoder();

    private final Disposition disposition;
    private final String filename;
    /**
     * Filename, ISO-8859-1 compatible
     */
    private final @Nullable String isoFilename;
    private final @Nullable Locale locale;

    public RFC6266ContentDisposition(
            Disposition disposition,
            String filename,
            @Nullable String isoFilename,
            @Nullable Locale locale
    ) {
        this.disposition = requireNonNull(disposition);
        this.filename = requireNonNull(filename);
        this.isoFilename = isoFilename;
        this.locale = locale;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public String getFilename() {
        return filename;
    }

    @Nullable
    public String getIsoFilename() {
        return isoFilename;
    }

    @Nullable
    public Locale getLocale() {
        return locale;
    }

    public String headerName() {
        return CONTENT_DISPOSITION;
    }

    public String headerValue() {
        requireNonNull(disposition, "disposition must be given");
        requireNonNull(filename, "filename must be given");


        String rawLatin1 = this.isoFilename == null
                ? new DefaultISO88591Encoder().encode(filename).getValue()
                : this.isoFilename;
        String latin1 = quoteLatin1(rawLatin1);

        if (rfc8187Encoder.needsEncoding(filename)) {
            var rfcEncoded = rfc8187Encoder.encodeExtValue(filename, locale);
            var result = formatBothIsoAndEncoded(disposition, latin1, rfcEncoded.getValue());

            return result;

        } else {

            var result = formatLatin1Only(disposition, latin1);

            return result;
        }
    }


    private String formatLatin1Only(Disposition disposition, String latin1Filename) {
        var result = String.format("%s; filename=%s", disposition.getHeaderAttribute(), latin1Filename);

        return result;
    }

    private String quoteLatin1(String input) {
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

    public Builder toBuilder() {
        return new BuilderImpl()
                .disposition(this.getDisposition())
                .filename(this.getFilename())
                .iso88591Filename(this.getIsoFilename());
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public interface Builder {
        Builder disposition(Disposition disposition);

        Disposition getDisposition();

        Builder filename(String filename);

        String getFilename();

        Builder iso88591Filename(@Nullable String filename);

        @Nullable
        String getIso88591Filename();

        Builder locale(@Nullable Locale locale);

        @Nullable
        Locale getLocale();

        RFC6266ContentDisposition build();
    }


}

class BuilderImpl implements Builder {
    private Disposition disposition = Disposition.ATTACHMENT;
    private String filename = "filename.bin";
    private @Nullable String iso88591Filename = null;
    private @Nullable Locale locale = null;

    @Override
    public Builder disposition(Disposition disposition) {
        this.disposition = requireNonNull(disposition);

        return this;
    }

    @Override
    public Disposition getDisposition() {
        return disposition;
    }

    @Override
    public Builder filename(String filename) {
        this.filename = requireNonNull(filename, "No filename");

        return this;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public Builder iso88591Filename(@Nullable String filename) {
        this.iso88591Filename = filename;

        return this;
    }

    @Override
    @Nullable
    public String getIso88591Filename() {
        return iso88591Filename;
    }

    @Override
    public Builder locale(@Nullable Locale locale) {
        this.locale = locale;

        return this;
    }

    @Override
    @Nullable
    public Locale getLocale() {
        return locale;
    }

    @Override
    public RFC6266ContentDisposition build() {
        return new RFC6266ContentDisposition(disposition, filename, iso88591Filename, locale);
    }
}
