package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.doublequotes.ReplaceDoubleQuotes;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReplaceDoubleQuotesTest {
    @Nested
    class DefaultValueText {
        ReplaceDoubleQuotes out = new ReplaceDoubleQuotes();

        @Test
        void replacementShouldBeEmptyString() {
            assertThat(out.getReplacement())
                    .isEqualTo("");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "\"asdf",
                "\"asdf\"",
                "asdf\"",
                "as\"df",
                "\"as\"df\"",
        })
        void shouldReplaceWithEmpty(String input) {
            String actual = out.handle(input);

            assertThat(actual)
                    .isEqualTo("asdf");
        }
    }

    @Nested
    class CustomValueText {
        @ParameterizedTest
        @CsvSource({
                ",java.lang.NullPointerException",
                "\",java.lang.IllegalArgumentException"
        })
        void shouldThrowOnIllegalReplacement(@Nullable String replacement, String expectedExcpetionClass) throws ClassNotFoundException {
            @SuppressWarnings("unchecked")
            Class<? extends Exception> expectedException =
                    (Class<? extends Exception>) Class.forName(expectedExcpetionClass);

            assertThrows(expectedException,
                    () -> new ReplaceDoubleQuotes(replacement));
        }

        @Test
        void replacementShouldBeSameAsCtorParam() {
            ReplaceDoubleQuotes out = new ReplaceDoubleQuotes("foo");

            assertThat(out.getReplacement())
                    .isEqualTo("foo");
        }

        @ParameterizedTest
        @CsvSource({
                "\", foo",
                "\"asdf, fooasdf",
                "\"asdf\", fooasdffoo",
                "asdf\", asdffoo",
                "as\"df, asfoodf",
                "\"as\"df\", fooasfoodffoo",
        })
        void shouldReplaceWithEmpty(String input, String expected) {
            ReplaceDoubleQuotes out = new ReplaceDoubleQuotes("foo");

            String actual = out.handle(input);

            assertThat(actual)
                    .isEqualTo(expected);
        }
    }

}