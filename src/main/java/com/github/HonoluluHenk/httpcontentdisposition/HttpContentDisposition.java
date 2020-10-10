package com.github.HonoluluHenk.httpcontentdisposition;

import com.github.HonoluluHenk.httpcontentdisposition.internal.rules.*;
import com.github.HonoluluHenk.httpcontentdisposition.isofallback.EncodeIsoFallback;
import com.github.HonoluluHenk.httpcontentdisposition.isofallback.IsoFallback;
import com.github.HonoluluHenk.httpcontentdisposition.isofallback.OverrideIsoFallback;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * This class and its {@link #builder()} help creating a mostly
 * <a href="https://tools.ietf.org/html/rfc6266">RFC 6266</a> compatible HTTP Content-Disposition header.
 *
 * <p>
 * The difference between this implementation and the RFC is: the RFC requires either the old ISO-8859-1 attribute
 * <b>or</b> the encoded extended attribute.
 * This library provides - for much better compatibility with broken/old clients - both.
 */
public class HttpContentDisposition {
    /**
     * Convenience: "Content-Disposition"
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final RFC8187Encoder rfc8187Encoder = new RFC8187Encoder();
    private static final RFC2616CharacterRules rfc2616CharacterRules = new RFC2616CharacterRules();
    private static final ISO88591Encoder ISO_88591_ENCODER = new DefaultISO88591Encoder();
    private static final String FILENAME_PARAM_NAME = "filename";

    private final Disposition disposition;
    private final String filename;
    private final IsoFallback isoFallback;
    @Nullable
    private final Locale locale;

    HttpContentDisposition(
            Disposition disposition,
            String filename,
            IsoFallback isoFallback,
            @Nullable Locale locale
    ) {
        this.disposition = requireNonNull(disposition);
        this.filename = requireNonNull(filename);
        this.isoFallback = requireNonNull(isoFallback);
        this.locale = locale;
    }

    /**
     * Convenience: "Content-Disposition"
     */
    public String headerName() {
        return CONTENT_DISPOSITION;
    }

    public String headerValue() {
        requireNonNull(disposition, "disposition must be given");
        requireNonNull(filename, "filename must be given");

        if (valueNeedsEncoding()) {
            String result = formatBothIsoAndEncoded();

            return result;

        } else {

            String result = formatIsoOnly();

            return result;
        }
    }

    private boolean valueNeedsEncoding() {
        // allowed values for the ISO-8859-1 header value (type "value")
        // are defined as (RFC2616, Section 3.6)
        boolean isIso = filename.codePoints()
                .allMatch(rfc2616CharacterRules::isOCTET);

        return !isIso;
    }


    private String formatIsoOnly() {
        String value = buildEncodedAndQuotedIsoFilename();

        //TODO: not only filename but an Attribute-Map
        String result = formatContentDisposition(singletonList(new HeaderValue(FILENAME_PARAM_NAME, value)));

        return result;
    }

    private String formatContentDisposition(
            List<HeaderValue> headerValues
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(disposition.getHeaderAttribute());
        for (HeaderValue headerValue : headerValues) {
            if (headerValue.getIsoValue() != null) {
                sb.append("; ");
                sb.append(headerValue.getName()).append("=").append(headerValue.getIsoValue());
            }
            if (headerValue.getExtValue() != null) {
                sb.append("; ");
                sb.append(headerValue.getName()).append("*=").append(headerValue.getExtValue());
            }
        }

        return sb.toString();
    }

    @Nullable
    private String buildEncodedAndQuotedIsoFilename() {
        String isoFilename = isoFallback.fallback(filename);
        if (isoFilename == null) {
            return null;
        }

        String actual = isoFilename;
        if (isoFallback.needsEncoding()) {
            actual = ISO_88591_ENCODER.encode(isoFilename);
        }

        String result = addQuotes(actual);

        return result;
    }

    private String addQuotes(String input) {
        if (!isoValueNeedsQuoting(input)) {
            return input;
        }

        String quoted = input.replace("\"", "\\\"");

        return '"' + quoted + '"';
    }

    private boolean isoValueNeedsQuoting(String input) {
        return !rfc2616CharacterRules.isToken(input);
    }


    private String formatBothIsoAndEncoded() {
        String isoEncoded = buildEncodedAndQuotedIsoFilename();
        Encoded rfcEncoded = rfc8187Encoder.encodeExtValue(filename, locale);

        String result = formatContentDisposition(
                singletonList(new HeaderValue(FILENAME_PARAM_NAME, isoEncoded, rfcEncoded.getValue()))
        );

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

    @Nullable
    public Locale getLocale() {
        return locale;
    }

    private static class HeaderValue {

        private final String name;
        @Nullable
        private final String isoValue;
        @Nullable
        private final String extValue;

        public HeaderValue(String name, @Nullable String isoValue, @Nullable String extValue) {
            this.name = requireNonNull(name);
            this.isoValue = isoValue;
            this.extValue = extValue;
        }

        public HeaderValue(String name, @Nullable String isoValue) {
            this(name, isoValue, null);
        }

        public String getName() {
            return name;
        }

        @Nullable
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

        Builder isoFallbackValue(@Nullable String value);

        IsoFallback getIsoFallback();

        Builder locale(@Nullable Locale locale);

        @Nullable
        Locale getLocale();

        HttpContentDisposition build();
    }

    private static class BuilderImpl implements Builder {
        private Disposition disposition = Disposition.ATTACHMENT;
        private String filename = "filename.bin";
        private IsoFallback isoFallback = new EncodeIsoFallback();
        @Nullable
        private Locale locale = null;

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
        public Builder isoFallbackValue(@Nullable String value) {
            this.isoFallback = new OverrideIsoFallback(value);

            return this;
        }

        @Override
        public IsoFallback getIsoFallback() {
            return isoFallback;
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
        public HttpContentDisposition build() {
            return new HttpContentDisposition(
                    disposition,
                    filename,
                    isoFallback,
                    locale
            );
        }
    }
}
