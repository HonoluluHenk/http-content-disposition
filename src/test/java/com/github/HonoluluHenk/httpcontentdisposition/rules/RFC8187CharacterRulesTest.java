package com.github.HonoluluHenk.httpcontentdisposition.rules;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;

import static com.github.HonoluluHenk.httpcontentdisposition.helpers.Helpers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

class RFC8187CharacterRulesTest {
    private static final RFC8187CharacterRules rules = RFC8187CharacterRules.getInstance();

    @Nested
    @TestInstance(PER_CLASS)
    class IsAttrCharTest extends GenericRuleTestSuite<RFC8187CharacterRules> {
        public IsAttrCharTest() {
            super(
                    rules,
                    RFC8187CharacterRules::isAttrChar,
                    concat(
                            charRange('a', 'z'),
                            charRange('A', 'Z'),
                            charRange('0', '9'),
                            chars('!', '#', '$', '&', '+', '-', '.',
                                    '^', '_', '`', '|', '~')
                    )
            );
        }
    }
}
