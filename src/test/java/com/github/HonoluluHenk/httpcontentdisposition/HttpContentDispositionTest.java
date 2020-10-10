package com.github.HonoluluHenk.httpcontentdisposition;

import com.github.HonoluluHenk.httpcontentdisposition.isofallback.OverrideIsoFallback;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.github.HonoluluHenk.httpcontentdisposition.Disposition.INLINE;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpContentDispositionTest {

    public static HttpContentDisposition withFilename(String filename) {
        return HttpContentDisposition.builder()
                .filename(filename)
                .build();
    }

    @Nested
    class HeaderName {
        @Test
        void correct_header_name() {
            String actual = withFilename("ignored")
                    .headerName();

            assertThat(actual)
                    .isEqualToIgnoringCase("Content-Disposition");
        }
    }

    @Nested
    class PlainFilenameTest {
        @Test
        public void shouldNotAddExtendedIfPlainAscii() {
            HttpContentDisposition out = withFilename("asdf.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=asdf.bin";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesWhenFilenameContainsQuote() {
            HttpContentDisposition out = withFilename("as\"df.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=\"as\\\"df.bin\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesWhenFilenameContainsSpace() {
            HttpContentDisposition out = withFilename("as df.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=\"as df.bin\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesWhenFilenameContainsSlash() {
            HttpContentDisposition out = withFilename("as/df.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=\"as/df.bin\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesWhenFilenameContainsBackslash() {
            HttpContentDisposition out = withFilename("as\\df.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=\"as\\df.bin\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldNotAddExtendedFilenameOnISO_8859_1() {
            HttpContentDisposition out = withFilename("«plans: µ».pdf");

            String actual = out.headerValue();

            String expected = "attachment; filename=\"«plans: µ».pdf\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        void shouldNotAddExtendedFilenameOnISO_8859_1_more_tests() {
            HttpContentDisposition out = withFilename("Hello \"Woö'ld\"?");

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=\"Hello \\\"Woö'ld\\\"?\"");
        }
    }

    @Nested
    class Non_ISO_8859_1_Characters {
        @Test
        public void shouldEncodeSpecialChars() {
            HttpContentDisposition out = withFilename("I ❤ Ada Lovelace");

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=\"I ? Ada Lovelace\"; filename*=UTF-8''I%20%E2%9D%A4%20Ada%20Lovelace");
        }

        @Test
        public void shouldEncodeEuro() {
            HttpContentDisposition out = withFilename("€ rates.pdf");

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=\"? rates.pdf\"; filename*=UTF-8''%E2%82%AC%20rates.pdf");
        }

        @Test
        public void should_encode_special_chars() {
            HttpContentDisposition out = withFilename("€\\'*%().pdf");

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=\"?\\'*%().pdf\"; filename*=UTF-8''%E2%82%AC%5C%27%2A%25%28%29.pdf");
        }

        @Test
        public void should_encode_hex_escape() {
            HttpContentDisposition out = withFilename("the%20plans.pdf");

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=the%20plans.pdf");
        }

    }

    @Nested
    class FallbackTest {
        @Test
        public void omits_fallback_when_null() {
            HttpContentDisposition out = HttpContentDisposition.builder()
                    .filename("hello-world.txt")
                    .isoFallback(new OverrideIsoFallback(null))
                    .build();

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment");
        }

        @Test
        public void overrides_iso_with_fallback_value() {
            HttpContentDisposition out = HttpContentDisposition.builder()
                    .filename("I ❤ Ada Lovelace")
                    .isoFallbackValue("Bugger")
                    .build();

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=Bugger; filename*=UTF-8''I%20%E2%9D%A4%20Ada%20Lovelace");
        }

        @Test
        public void overrides_iso_with_forced_fallback_value() {
            HttpContentDisposition out = HttpContentDisposition.builder()
                    .filename("I ❤ Ada Lovelace")
                    .isoFallback(new OverrideIsoFallback("Euro: €", false))
                    .build();

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=\"Euro: €\"; filename*=UTF-8''I%20%E2%9D%A4%20Ada%20Lovelace");
        }

        @Test
        public void includes_locale() {
            HttpContentDisposition out = HttpContentDisposition.builder()
                    .filename("﷽")
                    .locale(Locale.forLanguageTag("ar-JO"))
                    .build();

            String actual = out.headerValue();

            assertThat(actual)
                    .isEqualTo("attachment; filename=\"?\"; filename*=UTF-8'ar-JO'%EF%B7%BD");
        }

    }

    @Nested
    class BuilderTest {
        @Test
        void passesAllParams() {
            OverrideIsoFallback whatever = new OverrideIsoFallback("whatever");
            HttpContentDisposition.Builder builder = HttpContentDisposition.builder()
                    .disposition(INLINE)
                    .filename("﷽")
                    .isoFallback(whatever)
                    .locale(Locale.CANADA);

            assertThat(builder.getDisposition())
                    .isEqualTo(INLINE);
            assertThat(builder.getFilename())
                    .isEqualTo("﷽");
            assertThat(builder.getIsoFallback())
                    .isEqualTo(whatever);
            assertThat(builder.getLocale())
                    .isEqualTo(Locale.CANADA);

            HttpContentDisposition cd = builder.build();

            assertThat(cd.getDisposition())
                    .isEqualTo(INLINE);
            assertThat(cd.getFilename())
                    .isEqualTo("﷽");
            assertThat(cd.getIsoFallback())
                    .isEqualTo(whatever);
            assertThat(cd.getLocale())
                    .isEqualTo(Locale.CANADA);

            HttpContentDisposition other = cd.toBuilder().build();
            assertThat(other.getDisposition())
                    .isEqualTo(INLINE);
            assertThat(other.getFilename())
                    .isEqualTo("﷽");
            assertThat(other.getIsoFallback())
                    .isEqualTo(whatever);
            assertThat(other.getLocale())
                    .isEqualTo(Locale.CANADA);
        }
    }

}
