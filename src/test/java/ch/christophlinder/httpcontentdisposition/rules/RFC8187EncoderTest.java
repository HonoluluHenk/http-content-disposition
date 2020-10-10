package ch.christophlinder.httpcontentdisposition.rules;

import ch.christophlinder.httpcontentdisposition.helpers.CharInput;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
public class RFC8187EncoderTest {
    private final RFC8187Encoder encoder = new RFC8187Encoder();

    @Test
    void shouldHandleEmptyString() {
        Encoded actual = encoder.encodeExtValue("");

        assertThat(actual)
                .isEqualTo(new Encoded("UTF-8''", false));
    }

    @Test
    void shouldEscapeDoubleQuote() {
        Encoded actual = encoder.encodeExtValue("\"");

        assertThat(actual)
                .isEqualTo(new Encoded("UTF-8''%22", true));
    }

    @Nested
    class IsEncodedTest {
        @Test
        void shouldReturnFalseForSimpleChars() {
            Encoded actual = encoder.encodeExtValue("a");

            assertThat(actual.isEncoded())
                    .isFalse();
        }

        @Test
        void shouldReturnTrueorSimpleChars() {
            Encoded actual = encoder.encodeExtValue("ö");

            assertThat(actual.isEncoded())
                    .isTrue();
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    class SingleCharacterAsciiRangeTest {
        Stream<Character> allowedChars() {
            return concat(
                            charRange('a', 'z'),
                            charRange('A', 'Z'),
                            charRange('0', '9'),
                            chars('!', '#', '$', '&', '+', '-', '.',
                                    '^', '_', '`', '|', '~')
            ).map(CharInput::getCharacter);
        }

        @ParameterizedTest
        @MethodSource("allowedChars")
        void shouldNotEscapeCharsDigitsSpecials(Character c) {
            String input = String.valueOf(c);

            Encoded actual = encoder.encodeExtValue(input);

            assertThat(actual)
                    .isEqualTo(new Encoded("UTF-8''" + input, false));
        }

        @Test
        void shouldEscapeEverythingElse() {
            Set<Character> unescaped = allowedChars().collect(Collectors.toSet());

            for (char c = 0; c <= 127; c++) {
                if (unescaped.contains(c)) {
                    continue;
                }

                String input = String.valueOf(c);
                Encoded actual = encoder.encodeExtValue(input);

                assertThat(actual.getValue())
                        .describedAs("Character: " + c + ", name: " + Character.getName(c))
                        .startsWith("UTF-8''%")
                        .isNotEqualTo(input);
            }
        }
    }


    @Nested
    class WithDifferentCharacterSizesTest {
        @Test
        void shouldNotEncodeCharacterInLowAsciiRange() {
            // ascii: 97 = 0x61
            Encoded actual = encoder.encodeExtValue("a");

            assertThat(actual)
                    .isEqualTo(new Encoded("UTF-8''a", false));
        }

        @Test
        void shouldEncodeCharacterInHighAsciiRange() {
            // iso-8859-1: 246 = 0xF6
            Encoded actual = encoder.encodeExtValue("ö");

            assertThat(actual)
                    .isEqualTo(new Encoded("UTF-8''%C3%B6", true));
        }

        @Test
        void shouldEncode3ByteUTFChar() {
            // codepoint: 8364 = 0x20AC
            Encoded actual = encoder.encodeExtValue("€");

            assertThat(actual)
                    .isEqualTo(new Encoded("UTF-8''%E2%82%AC", true));
        }

        @Test
        void shouldEncode4ByteUTFChar() {
//            String string = Character.toString(194564);
            String string = new String(Character.toChars(194564));

            Encoded actual = encoder.encodeExtValue(string);

            assertThat(actual)
                    .isEqualTo(new Encoded("UTF-8''%F0%AF%A0%84", true));
        }
    }

    // remember: single-quotes are used in CsvSource to preserve whitespace, but only if used directly after comma (,)
    @ParameterizedTest
    @CsvSource({
            "❤, UTF-8''%E2%9D%A4",
            "' ❤', UTF-8''%20%E2%9D%A4",
            "'❤ ', UTF-8''%E2%9D%A4%20",
            "' ❤ ', UTF-8''%20%E2%9D%A4%20",
            "\", UTF-8''%22",
            "♠, UTF-8''%E2%99%A0",
            "Hell\"ö-♠1234.bin, UTF-8''Hell%22%C3%B6-%E2%99%A01234.bin",
            "€, UTF-8''%E2%82%AC",
    })
    void shouldHandleSomeRandomStrings(String in, String expected) {
        Encoded actual = encoder.encodeExtValue(in);

        assertThat(actual)
                .isEqualTo(new Encoded(expected, true));
    }

    @Nested
    class LanguageTagTest {
        @Test
        void shouldHandleNullLocale() {
            String input = "asdf";

            String actual = encoder.encodeExtValue(input, null).getValue();

            assertThat(actual)
                    .isEqualTo("UTF-8''asdf");
        }

        @ParameterizedTest
        @CsvSource({
                "'', '', und", // empty locale produces language tag 'und' (undefined)
                "de, '', de",
                "'', CH, und-CH",
                "de, CH, de-CH",
        })
        void shouldConvertLanguageTag(String language, String country, String expectedLanguageTag) {
            Locale locale = new Locale(language, country);
            String input = "asdf";

            String actual = encoder.encodeExtValue(input, locale).getValue();

            assertThat(actual)
                    .isEqualTo(String.format("UTF-8'%s'asdf", expectedLanguageTag));
        }

        @Test
        void shouldStripExtensions() {
            String input = "asdf";
            // extensions: https://docs.oracle.com/javase/tutorial/i18n/locale/extensions.html
            Locale locale = Locale.forLanguageTag("de-DE-u-email-co-phonebk-x-linux");

            String actual = encoder.encodeExtValue(input, locale).getValue();

            assertThat(actual)
                    .isEqualTo("UTF-8'de-DE'asdf");
        }

        @Test
        void shouldStripVariants() {
            String input = "asdf";
            // extensions: https://docs.oracle.com/javase/tutorial/i18n/locale/extensions.html
            Locale locale = new Locale("de", "DE", "foo");

            String actual = encoder.encodeExtValue(input, locale).getValue();

            assertThat(actual)
                    .isEqualTo("UTF-8'de-DE'asdf");
        }
    }
}
