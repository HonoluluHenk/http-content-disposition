package ch.christophlinder.httpcontentdisposition.rules;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.*;
import static org.assertj.core.api.Assertions.assertThat;

class RFC5987CharacterRulesTest {
    private final RFC5987CharacterRules rules = RFC5987CharacterRules.getInstance();

    @Nested
    class AttrCharTest extends GenericPredicate<RFC5987CharacterRules> {
        
        AttrCharTest() {
            super(rules,
                    RFC5987CharacterRules::isAttrChar,
                    concat(
                            charRange('a', 'z'), charRange('A', 'Z'),
                            charRange('0', '9'),
                            chars('!', '#', '$', '&', '+', '-', '.', '^', '_', '`', '|', '~')
                        )
                    );
        }

        @Test
        void shouldRejectCharactersOutsideLatin1() {
            boolean actual = rules.isAttrChar('\u0100');

            assertThat(actual)
                    .isFalse();
        }

    }
}