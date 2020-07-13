package ch.christophlinder.httpcontentdisposition.rules;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.*;
import static org.assertj.core.api.Assertions.assertThat;

class RFC2616CharacterRulesTest {

    private final RFC2616CharacterRules rules = RFC2616CharacterRules.getInstance();

    @Nested
    class OCTETTest extends GenericPredicate<RFC2616CharacterRules> {
        OCTETTest() {
            super(
                    rules,
                    RFC2616CharacterRules::isOCTET,
                    latin1(),
                    codePoints( // forbidden: anything outside of latin1
                            '\u0100', // first character above latin1
                            194564, // somewhere in the middle
                            1114111 // last character defined in unicode
                    )
            );
        }
    }

    @Nested
    class CHARTest extends GenericPredicate<RFC2616CharacterRules> {
        CHARTest() {
            super(
                    rules,
                    RFC2616CharacterRules::isCHAR,
                    ascii()
            );
        }
    }

    @Nested
    class CTLTest extends GenericPredicate<RFC2616CharacterRules> {
        CTLTest() {
            super(
                    rules,
                    RFC2616CharacterRules::isCTL,
                    concat(
                            codePointRange(0, 31),
                            codePoints(127)
                    )
            );
        }
    }

    @Nested
    class TokenCharTest extends GenericPredicate<RFC2616CharacterRules> {
        TokenCharTest() {
            super(
                    rules,
                    RFC2616CharacterRules::isTokenChar,
                    allExcept(
                            codePointRange(0, 127), // CHAR
                            concat(
                                    // CTL
                                    concat(
                                            codePointRange(0, 31),
                                            codePoints(127)
                                    ),
                                    // separators
                                    chars(
                                            '(' , ')' , '<' , '>' , '@'
                                                    , ',' , ';' , ':' , '\\' , '"'
                                                    , '/' , '[' , ']' , '?' , '='
                                                    , '{' , '}' , ' ' , '\t'
                                    )
                            )
                    )
            );
        }
    }

    @Nested
    class IsTokenTest {
        @ParameterizedTest
        @CsvSource({
                ",false",
                "'',false",
                "asdf,true",
                // control characters
                "asdf@fdsa,false",
                "@asdf,false",
                "@asdf@,false",
                "asdf@,false",
        })
        void shouldHandleCorrectly(@Nullable String input, boolean expected) {
            boolean actual = rules.isToken(input);

            assertThat(actual)
                    .isEqualTo(expected);
        }
    }

    @Nested
    class IsSeparatorTest extends GenericPredicate<RFC2616CharacterRules> {
        IsSeparatorTest() {
            super(
                    rules,
                    RFC2616CharacterRules::isSeparator,
                    chars(
                            '(' , ')' , '<' , '>' , '@'
                            , ',' , ';' , ':' , '\\' , '"'
                            , '/' , '[' , ']' , '?' , '='
                            , '{' , '}' , ' ' , '\t'
                    )
            );
        }

        @Test
        void shouldRejectCharsOutsiteLatin1() {
            boolean actual = rules.isSeparator('\u0100');

            assertThat(actual)
                    .isFalse();
        }
    }
}
