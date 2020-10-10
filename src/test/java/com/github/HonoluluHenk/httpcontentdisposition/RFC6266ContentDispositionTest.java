package com.github.HonoluluHenk.httpcontentdisposition;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RFC6266ContentDispositionTest {

    public static RFC6266ContentDisposition withFilename(String filename) {
        return RFC6266ContentDisposition.builder()
                .filename(filename)
                .build();
    }

    @Nested
    class PlainFilenameTest {
        @Test
        public void shouldNotAddExtendedIfNotNeeded() {
            RFC6266ContentDisposition out = withFilename("asdf.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=asdf.bin";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesIfNeeded() {
            RFC6266ContentDisposition out = withFilename("as\"df.bin");

            String actual = out.headerValue();

            String expected = "attachment; filename=\"as\\\"df.bin\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }
    }

    @Test
    public void shouldEncodeSpecialChars() {
        RFC6266ContentDisposition out = withFilename("I ❤ Ada Lovelace");

        String actual = out.headerValue();

        assertThat(actual)
                .isEqualTo("attachment; filename=\"I ? Ada Lovelace\"; filename*=UTF-8''I%20%E2%9D%A4%20Ada%20Lovelace");
    }

    @Test
    void testIso8859_1() {
        RFC6266ContentDisposition out = withFilename("Hello \"Woö'ld\"?");

        String actual = out.headerValue();

        assertThat(actual)
                .isEqualTo("attachment; filename=\"Hello \\\"Woö'ld\\\"?\"");
    }

}
