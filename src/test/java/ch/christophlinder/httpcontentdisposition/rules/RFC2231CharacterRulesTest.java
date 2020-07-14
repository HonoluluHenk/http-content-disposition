package ch.christophlinder.httpcontentdisposition.rules;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

class RFC2231CharacterRulesTest {
    private final RFC2231CharacterRules rules = RFC2231CharacterRules.getInstance();

    @Nested
    @TestInstance(PER_CLASS)
    class IsAttributeCharTest extends GenericRuleTestSuite<RFC2231CharacterRules> {
        public IsAttributeCharTest() {
            super(
                    rules,
                    RFC2231CharacterRules::isAttributeChar,
// FIXME: for use in RFC8187CharacterRules
//                    concat(
//                            charRange('a', 'z'),
//                            charRange('A', 'Z'),
//                            charRange('0', '9'),
//                            chars('!', '#', '$', '&', '+', '-', '.',
//                                    '^', '_', '`', '|', '~')
//                    )
                    allExcept(
                            ascii(),
                            concat(
                                    chars('*', '\'', '%'), // explicitly statet in RFC
                                    chars(' '), // SPACE
                                    charRange((char) 0, (char) 31), //CTLs
                                    chars((char) 127), // CTLs
                                    chars('(', ')', '<', '>', '@',
                                            ',', ';', ':', '\\', '"',
                                            '/', '[', ']', '?', '=') // tspecials
                            )
                    )
            );
        }

        @ParameterizedTest
        @ValueSource(ints = {
                Integer.MIN_VALUE,
                -1,
                128, // first character above ascii
                '\u0100', // first character above latin1
                194564, // somewhere in the middle
                1114111, // last character defined in unicode
                Integer.MAX_VALUE,
        })
        void shouldRejectOutsideASCII(int codePoint) {
            assertThat(rules.isAttributeChar(codePoint))
                    .isFalse();
        }

    }

}
