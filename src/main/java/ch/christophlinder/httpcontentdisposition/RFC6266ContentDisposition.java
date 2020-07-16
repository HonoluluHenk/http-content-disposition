package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.doublequotes.DoubleQuotes;
import ch.christophlinder.httpcontentdisposition.doublequotes.QuoteDoubleQuotes;
import ch.christophlinder.httpcontentdisposition.isofallback.EncodingIsoFallback;
import ch.christophlinder.httpcontentdisposition.isofallback.FixedValueIsoFallback;
import ch.christophlinder.httpcontentdisposition.isofallback.IsoFallback;
import ch.christophlinder.httpcontentdisposition.rules.DefaultISO88591Encoder;
import ch.christophlinder.httpcontentdisposition.rules.RFC2616CharacterRules;
import ch.christophlinder.httpcontentdisposition.rules.RFC8187Encoder;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class RFC6266ContentDisposition {
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final DefaultISO88591Encoder ISO_88591_ENCODER = new DefaultISO88591Encoder();

    public static final String FILENAME_PARAM_NAME = "filename";

    private final RFC8187Encoder rfc8187Encoder = new RFC8187Encoder();
    private final RFC2616CharacterRules rfc2616CharacterRules = new RFC2616CharacterRules();

    private final Disposition disposition;
    private final String filename;
    private final IsoFallback isoFallback;
    private final DoubleQuotes doubleQuotes;
    @Nullable
    private final Locale locale;

    RFC6266ContentDisposition(
            Disposition disposition,
            String filename,
            IsoFallback isoFallback,
            DoubleQuotes doubleQuotes,
            @Nullable Locale locale
    ) {
        this.disposition = requireNonNull(disposition);
        this.filename = requireNonNull(filename);
        this.isoFallback = requireNonNull(isoFallback);
        this.doubleQuotes = requireNonNull(doubleQuotes);
        this.locale = locale;
    }

    public String headerName() {
        return CONTENT_DISPOSITION;
    }

    public String headerValue() {
        requireNonNull(disposition, "disposition must be given");
        requireNonNull(filename, "filename must be given");

        //FIXME: implement using the IsoEncoder:
        // get its value and transform using the DefaultIsoEncoder (just in case the user did not implement his stuff correctly)
//
//        String rawIso = this.isoFilename == null
//                ? new DefaultISO88591Encoder().encode(filename).getValue()
//                : this.isoFilename;
//        String iso = doubleQuotes.handle(rawIso);


        if (valueNeedsEncoding()) {
            var result = formatBothIsoAndEncoded();

            return result;

        } else {

            var result = formatIsoOnly();

            return result;
        }
    }

    private boolean valueNeedsEncoding() {
        // allowed values for the ISO-8859-1 header value (type "value")
        // are defined as (RFC2616, Section 3.6)
        boolean isIso = filename.codePoints()
                .allMatch(rfc2616CharacterRules::isOCTET);

        return !isIso;
//        return !rfc2616CharacterRules.isToken(filename);
    }


    private String formatIsoOnly() {
        String value = buildEncodedAndQuotedIsoFilename();

        //TODO: not only filename but an Attribute-Map
        String result = formatContentDisposition(new HeaderValue(FILENAME_PARAM_NAME, value));

        return result;
    }

    private String formatContentDisposition(
            HeaderValue headerValue
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(disposition.getHeaderAttribute());
        sb.append("; ");
        sb.append(headerValue.getName()).append("=").append(headerValue.getIsoValue());
        if (headerValue.getExtValue() != null) {
            sb.append("; ");
            sb.append(headerValue.getName()).append("*=").append(headerValue.getExtValue());
        }

        return sb.toString();
    }

    private String buildEncodedAndQuotedIsoFilename() {
        String isoFilename = isoFallback.fromOriginal(filename);
        // make doubly sure the user of our library did not pass something illegal:
        String surelyEncoded = ISO_88591_ENCODER.encode(isoFilename);

        //FIXME: do cleanup: remove CTLs

        String result = addQuotes(surelyEncoded);

        return result;
    }

    private String addQuotes(String input) {
        if (!isoValueNeedsQuoting(input)) {
            return input;
        }

        String quoted = input.replace("\"", "\\\"");

        return '"' + quoted + '"';
//
//        return doubleQuotes.handle(encodedFilename);
    }

    private boolean isoValueNeedsQuoting(String input) {
        return !rfc2616CharacterRules.isToken(input);
    }


    private String formatBothIsoAndEncoded() {
        var isoEncoded = buildEncodedAndQuotedIsoFilename();
        var rfcEncoded = rfc8187Encoder.encodeExtValue(filename, locale);

        String result = formatContentDisposition(new HeaderValue(FILENAME_PARAM_NAME, isoEncoded, rfcEncoded.getValue()));

        return result;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public String getFilename() {
        return filename;
    }

    public IsoFallback getIsoFallback() {
        return isoFallback;
    }

    public DoubleQuotes getDoubleQuotes() {
        return doubleQuotes;
    }

    @Nullable
    public Locale getLocale() {
        return locale;
    }

    private static class HeaderValue {

        private final String name;
        private final String isoValue;
        @Nullable private final String extValue;

        public HeaderValue(String name, String isoValue, @Nullable String extValue) {
            this.name = requireNonNull(name);
            this.isoValue = requireNonNull(isoValue);
            this.extValue = extValue;
        }

        public HeaderValue(String name, String isoValue) {
            this(name, isoValue, null);
        }

        public String getName() {
            return name;
        }

        public String getIsoValue() {
            return isoValue;
        }

        @Nullable
        public String getExtValue() {
            return extValue;
        }
    }

    public Builder toBuilder() {
        return new BuilderImpl()
                .disposition(this.getDisposition())
                .filename(this.getFilename())
                .isoFallback(this.getIsoFallback())
                .doubleQuotes(this.getDoubleQuotes())
                .locale(this.getLocale())
                ;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public interface Builder {
        Builder disposition(Disposition disposition);

        Disposition getDisposition();

        Builder filename(String filename);

        String getFilename();

        Builder isoFallback(IsoFallback isoFallback);

        Builder isoFallbackValue(String value);

        IsoFallback getIsoFallback();

        BuilderImpl doubleQuotes(DoubleQuotes doubleQuotes);

        DoubleQuotes getDoubleQuotes();

        Builder locale(@Nullable Locale locale);

        @Nullable
        Locale getLocale();

        RFC6266ContentDisposition build();
    }

    private static class BuilderImpl implements Builder {
        private Disposition disposition = Disposition.ATTACHMENT;
        private String filename = "filename.bin";
        private IsoFallback isoFallback = new EncodingIsoFallback();
        @Nullable
        private Locale locale = null;
        private DoubleQuotes doubleQuotes = new QuoteDoubleQuotes();

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
        public Builder isoFallback(IsoFallback isoFallback) {
            this.isoFallback = requireNonNull(isoFallback);

            return this;
        }

        @Override
        public Builder isoFallbackValue(String value) {
            this.isoFallback = new FixedValueIsoFallback(value);

            return this;
        }

        @Override
        public IsoFallback getIsoFallback() {
            return isoFallback;
        }

        @Override
        public BuilderImpl doubleQuotes(DoubleQuotes doubleQuotes) {
            this.doubleQuotes = requireNonNull(doubleQuotes);

            return this;
        }

        @Override
        public DoubleQuotes getDoubleQuotes() {
            return doubleQuotes;
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
            return new RFC6266ContentDisposition(
                    disposition,
                    filename,
                    isoFallback,
                    doubleQuotes,
                    locale
            );
        }
    }
}
