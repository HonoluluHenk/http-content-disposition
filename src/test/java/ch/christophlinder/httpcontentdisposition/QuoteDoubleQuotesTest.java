package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.doublequotes.QuoteDoubleQuotes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class QuoteDoubleQuotesTest {

    private static Stream<Arguments> testdata() {
        // input => expected
        return Stream.of(
                arguments("\"", "\"\\\"\""),
                arguments("asdf\"", "\"asdf\\\"\""),
                arguments("\"asdf", "\"\\\"asdf\""),
                arguments("\"asdf\"", "\"\\\"asdf\\\"\""),
                arguments("\"as\"df\"", "\"\\\"as\\\"df\\\"\""),
                arguments(" \"asdf\" ", "\" \\\"asdf\\\" \"")
        );
    }

    @ParameterizedTest
    @MethodSource("testdata")
    void shouldQuote(String input, String expected) {
        String actual = new QuoteDoubleQuotes().handle(input);

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    void shouldNotQuoteInputWithoutDQuotes() {
        String actual = new QuoteDoubleQuotes().handle("asdf");

        assertThat(actual)
                .isEqualTo("asdf");
    }

    @Test
    void shouldLeaveEmptyString() {
        String actual = new QuoteDoubleQuotes().handle("");

        assertThat(actual)
                .isEqualTo("");
    }
}
