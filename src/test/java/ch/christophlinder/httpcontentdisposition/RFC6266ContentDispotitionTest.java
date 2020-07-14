package ch.christophlinder.httpcontentdisposition;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RFC6266ContentDispotitionTest {

    private final RFC6266ContentDisposition cd = new RFC6266ContentDisposition();

    @Nested
    class PlainFilenameTest {
        @Test
        public void shouldNotAddExtendedIfNotNeeded() {
            String actual = cd.filename(Disposition.ATTACHMENT, "asdf.bin");

            String expected = "attachment; filename=asdf.bin";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesIfNeeded() {
            String actual = cd.filename(Disposition.ATTACHMENT, "as\"df.bin");

            String expected = "attachment; filename=\"as\\\"df.bin\"";
            assertThat(actual)
                    .isEqualTo(expected);
        }
    }

    @Test
    public void shouldEncodeSpecialChars() {
        String actual = cd.filename(Disposition.ATTACHMENT, "€ rates");

        String expected = "attachment; filename=? rates; filename*=UTF-8''%E2%82%AC%20rates";
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void testLatin1() {
        String actual = cd.filename(Disposition.ATTACHMENT, "Hello \"Woö'ld\"?", null);

        assertThat(actual)
                .isEqualTo("attachment; filename=\"Hello \\\"Woö'ld\\\"?\"");
    }

}
