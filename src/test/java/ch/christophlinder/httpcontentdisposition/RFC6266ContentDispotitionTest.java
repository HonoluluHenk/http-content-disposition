package ch.christophlinder.httpcontentdisposition;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RFC6266ContentDispotitionTest {

    public static RFC6266ContentDisposition withFilename(String filename) {
        return RFC6266ContentDisposition.builder()
                .filename(filename)
                .build();
    }

    @Nested
    class PlainFilenameTest {
        @Test
        public void shouldNotAddExtendedIfNotNeeded() {
            String actual = withFilename("asdf.bin").headerValue();

            String expected = "attachment; filename=asdf.bin";
            assertThat(actual)
                    .isEqualTo(expected);
        }

        @Test
        public void shouldAddQuotesIfNeeded() {
            String actual = withFilename("as\"df.bin").headerValue();

            //FIXME: not sure if filename* is needed here
            String expected = "attachment; filename=\"as\\\"df.bin\"; filename*=UTF-8''as%22df.bin";
            assertThat(actual)
                    .isEqualTo(expected);
        }
    }

    @Test
    public void shouldEncodeSpecialChars() {
        String actual = withFilename("€ rates").headerValue();

        String expected = "attachment; filename=? rates; filename*=UTF-8''%E2%82%AC%20rates";
        assertThat(actual)
                .isEqualTo(expected);
    }

//    @Test
//    void testLatin1() {
//        String actual = withFilename("Hello \"Woö'ld\"?", null).headerValue();
//
//        assertThat(actual)
//                .isEqualTo("attachment; filename=\"Hello \\\"Woö'ld\\\"?\"");
//    }

}
