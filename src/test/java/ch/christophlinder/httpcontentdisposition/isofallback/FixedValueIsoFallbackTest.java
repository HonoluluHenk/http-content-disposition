package ch.christophlinder.httpcontentdisposition.isofallback;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FixedValueIsoFallbackTest {

    @Test
    void foo() {
        String actual = new FixedValueIsoFallback("foo")
                .fromOriginal("bar");

        assertThat(actual)
                .isEqualTo("foo");
    }
}
