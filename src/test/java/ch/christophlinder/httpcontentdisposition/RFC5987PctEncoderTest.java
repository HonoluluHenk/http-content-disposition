package ch.christophlinder.httpcontentdisposition;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
public class RFC5987PctEncoderTest {
	private final RFC5987PctEncoder encoder = new RFC5987PctEncoder();

	@Test
	void shouldHandleEmptyString() {
		String actual = encoder.encode("");

		assertThat(actual)
				.isEqualTo("");
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS) // so we can use @MethodSource in @Nested class
	class AsciiRangeTest {
		 Stream<Character> specials() {
			return Set.of(
					'!', '#', '$', '&', '+', '-', '.',
					'^', '_', '`', '|', '~').stream();
		}

		 Stream<Character> chars() {
			return IntStream.concat(
					IntStream.rangeClosed('a', 'z'),
					IntStream.rangeClosed('A', 'Z')
			).mapToObj(i -> (char) i);
		}

		Stream<Character> digits() {
			return IntStream.rangeClosed('0', '9')
					.mapToObj(i -> (char) i);
		}

		 Stream<Character> unescapedChars() {
			return concat(concat(
					chars(),
					digits()),
					specials()
			);
		}

		@ParameterizedTest
		@MethodSource("unescapedChars")
		void shouldNotEscapeCharsDigitsSpecials(Character c) {
			String input = String.valueOf(c);

			String actual = encoder.encode(input);

			assertThat(actual)
					.isEqualTo(input);
		}

		@Test
		void shouldEscapeEverythingElse() {
			Set<Character> unescaped = unescapedChars().collect(Collectors.toSet());

			for (char c = 0; c <= 255; c++) {
				if (unescaped.contains(c)) {
					continue;
				}

				String input = String.valueOf(c);
				String actual = encoder.encode(input);

				assertThat(actual)
						.describedAs("Character: " + c + ", name: " + Character.getName(c))
						.startsWith("%")
						.isNotEqualTo(input);
			}
		}
	}


	@Nested
	class WithDifferentCharacterSizesTest {
		@Test
		void shouldEncodeCharacterInLowAsciiRange() {
			// ascii: 97 = 0x61
			String actual = encoder.encode("a");

			assertThat(actual)
					.isEqualTo("a");
		}

		@Test
		void shouldEncodeCharacterInHighAsciiRange() {
			// iso-8859-1: 246 = 0xF6
			String actual = encoder.encode("ö");

			assertThat(actual)
					.isEqualTo("%C3%B6");
		}

		@Test
		void shouldEncode3ByteUTFChar() {
			// codepoint: 8364 = 0x20AC
			String actual = encoder.encode("€");

			assertThat(actual)
					.isEqualTo("%E2%82%AC");
		}

		@Test
		void shouldEncode4ByteUTFChar() {
			String string = Character.toString(194564);

			String actual = encoder.encode(string);

			assertThat(actual)
					.isEqualTo("%F0%AF%A0%84");
		}
	}

	// remember: single-quotes are used by CsvSource to preserve whitespace!
	@ParameterizedTest
	@CsvSource({
			"❤,%E2%9D%A4",
			"\",%22",
			"♠,%E2%99%A0",
			"Hell\"ö-♠1234.bin,Hell%22%C3%B6-%E2%99%A01234.bin",
			"' asdf ','%20asdf%20'",
			"'asdf ','asdf%20'",
			"' asdf','%20asdf'",
			"€,%E2%82%AC",
	})
	void shouldHandleSomeSpecialStrings(String in, String expected) {
		String actual = encoder.encode(in);

		assertThat(actual)
				.isEqualTo(expected);
	}

}