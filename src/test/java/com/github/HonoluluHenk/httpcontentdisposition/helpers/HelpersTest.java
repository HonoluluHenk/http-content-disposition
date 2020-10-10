package com.github.HonoluluHenk.httpcontentdisposition.helpers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class HelpersTest {
    @Test
    void allExceptTest() {

        List<Character> actual = Helpers.allExcept(
                Helpers.charRange('a', 'c'),
                Helpers.charRange('c', 'e')
        )
                .map(CharInput::getCharacter)
                .collect(Collectors.toList());

        assertThat(actual)
                .containsExactly('a', 'b');
    }

}
