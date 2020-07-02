package ch.christophlinder.httpcontentdisposition;

import java.util.Locale;

import ch.christophlinder.httpcontentdisposition.ContentDisposition.Disposition;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentDispotitionTest {

	private final ContentDisposition cd = new ContentDisposition();

	@Nested
	class DispositionTest {
		@Test
		void inlineShouldStartWithInline() {
			String actual = cd.build(Disposition.INLINE, "foobar");

			assertThat(actual)
					.startsWith("inline;");
		}

		@Test
		void attachmentShouldStartWithAttachment() {
			String actual = cd.build(Disposition.ATTACHMENT, "foobar");

			assertThat(actual)
					.startsWith("attachment;");
		}
	}


	@Nested
	class LanguageTest {
		@Test
		void shouldMapNullToEmpty() {
			String actual = cd.build(Disposition.INLINE, "foobar", null);

			assertThat(actual)
					.contains("filename*=utf-8''");
		}

		@Test
		void shouldWorkWithEmptyLanguage() {
			String actual = cd.build(Disposition.INLINE, "foobar", Locale.forLanguageTag(""));

			assertThat(actual)
					.contains("filename*=utf-8''");
		}

		@Test
		void shouldInsertLanguage() {
			String actual = cd.build(Disposition.INLINE, "foobar", Locale.forLanguageTag("en-US"));

			assertThat(actual)
					.contains("filename*=utf-8'en-US'");
		}

		@Test
		void shouldInsertLanguageWithoutExtensions() {
			String actual = cd.build(Disposition.INLINE, "foobar", Locale.forLanguageTag("ja-JP-u-ca-japanese"));

			assertThat(actual)
					.contains("filename*=utf-8'ja-JP'");
		}
	}


	@Nested
	class PlainFilenameTest {
		@Test
		public void shouldPrintPlainAscii() {
			String actual = cd.build(Disposition.ATTACHMENT, "€ rates");

			assertThat(actual)
					.isEqualTo("attachment; filename=? rates; filename*=utf-8''%E2%82%AC%20rates");
			//				.isEqualTo("attachment; filename=\"asdf\"; filename*=utf-8''%e2%82%ac%20rates"");
		}
	}


}
