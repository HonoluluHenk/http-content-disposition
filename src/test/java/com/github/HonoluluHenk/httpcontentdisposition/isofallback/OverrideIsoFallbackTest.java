package com.github.HonoluluHenk.httpcontentdisposition.isofallback;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OverrideIsoFallbackTest {

    @Test
    void foo() {
        String actual = new OverrideIsoFallback("foo")
                .fallback("bar");

        assertThat(actual)
                .isEqualTo("foo");
    }
}
