package ch.christophlinder.httpcontentdisposition;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class RFC6266ContentDispotitionTest {

    private final RFC6266ContentDisposition cd = new RFC6266ContentDisposition();


    @Nested
    class LanguageTest {
        @Test
        void shouldMapNullToEmpty() {
            String actual = cd.filename(Disposition.INLINE, "foobar", null);

            assertThat(actual)
                    .contains("filename*=utf-8''");
        }

        @Test
        void shouldWorkWithEmptyLanguage() {
            String actual = cd.filename(Disposition.INLINE, "foobar", Locale.forLanguageTag(""));

            assertThat(actual)
                    .contains("filename*=utf-8''");
        }

        @Test
        void shouldInsertLanguage() {
            String actual = cd.filename(Disposition.INLINE, "foobar", Locale.forLanguageTag("en-US"));

            assertThat(actual)
                    .contains("filename*=utf-8'en-US'");
        }

        @Test
        void shouldInsertLanguageWithoutExtensions() {
            String actual = cd.filename(Disposition.INLINE, "foobar", Locale.forLanguageTag("ja-JP-u-ca-japanese"));

            assertThat(actual)
                    .contains("filename*=utf-8'ja-JP'");
        }
    }


    @Nested
    class PlainFilenameTest {
        @Test
        public void shouldPrintPlainAscii() {
            String actual = cd.filename(Disposition.ATTACHMENT, "€ rates");

            assertThat(actual)
                    .isEqualTo("attachment; filename=? rates; filename*=utf-8''%E2%82%AC%20rates");
            //				.isEqualTo("attachment; filename=\"asdf\"; filename*=utf-8''%e2%82%ac%20rates"");
        }
    }

    @Test
    void testFoo() {
        String actual = cd.filename(Disposition.ATTACHMENT, "€ rates", null);

        assertThat(actual)
                .isEqualTo("asdf");
    }

    @Test
    void testBar() {
        String actual = cd.filename(Disposition.ATTACHMENT, "Hello \"Woö'ld\"?", null);

        assertThat(actual)
                .isEqualTo("asdf");
    }

}
