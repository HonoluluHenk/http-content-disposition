package ch.christophlinder.httpcontentdisposition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DispositionTest {
    @Test
    void inlineHeaderAttributeShouldBeCorrect() {
        assertThat(Disposition.INLINE.getHeaderAttribute())
                .isEqualTo("inline");
    }

    @Test
    void attachmentShouldStartWithAttachment() {
        assertThat(Disposition.ATTACHMENT.getHeaderAttribute())
                .isEqualTo("attachment");
    }

    @Test
    void fromTextShouldFindCaseInsensitive() {
        assertAll(
                () -> assertThat(Disposition.fromText("attachment")).hasValue(Disposition.ATTACHMENT),
                () -> assertThat(Disposition.fromText("AtTaChMeNt")).hasValue(Disposition.ATTACHMENT),
                () -> assertThat(Disposition.fromText("ATTACHMENT")).hasValue(Disposition.ATTACHMENT),
                () -> assertThat(Disposition.fromText("inline")).hasValue(Disposition.INLINE),
                () -> assertThat(Disposition.fromText("INLINE")).hasValue(Disposition.INLINE)
        );
    }

    @Test
    void fromTextShouldBeLenientWithWhitespace() {
        assertAll(
                () -> assertThat(Disposition.fromText("attachment ")).hasValue(Disposition.ATTACHMENT),
                () -> assertThat(Disposition.fromText(" attachment")).hasValue(Disposition.ATTACHMENT),
                () -> assertThat(Disposition.fromText("inline ")).hasValue(Disposition.INLINE),
                () -> assertThat(Disposition.fromText(" inline")).hasValue(Disposition.INLINE)
        );
    }

    @Test
    void fromTextShouldHandleIllegalArguments() {
        assertAll(
                () -> assertThat(Disposition.fromText(null)).isEmpty(),
                () -> assertThat(Disposition.fromText("")).isEmpty(),
                () -> assertThat(Disposition.fromText("foobar")).isEmpty(),
                () -> assertThat(Disposition.fromText("foobar")).isEmpty()
        );
    }
}