package ch.christophlinder.httpcontentdisposition.rules;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

class RFC5234CharacterRulesTest {
    private final RFC5234CharacterRules rules = RFC5234CharacterRules.getInstance();

    @Nested
    class InstantiationTest {
        @Test
        void getInstanceShouldReturnInstance() {
            assertThat(RFC5234CharacterRules.getInstance())
                    .isNotNull(); // and implicitly: does not throw
        }

        @Test
        void newShouldNotThrow() {
            assertThat(new RFC5234CharacterRules())
                    .isNotNull(); // and implicitly: does not throw
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    class ALPHATest extends GenericPredicate<RFC5234CharacterRules> {
        ALPHATest() {
            super(rules,
                    RFC5234CharacterRules::isALPHA,
                    concat(charRange('a', 'z'), charRange('A', 'Z'))
            );
        }
    }

    @Nested
    class BITTest extends GenericPredicate<RFC5234CharacterRules> {
        BITTest() {
            super(rules,
                    RFC5234CharacterRules::isBIT,
                    chars('0', '1')
            );
        }
    }

    @Nested
    class CHARTest extends GenericPredicate<RFC5234CharacterRules> {
        CHARTest() {
            super(rules,
                    RFC5234CharacterRules::isCHAR,
                    charRange((char) 0x01, (char) 0x7F));
        }
    }

    @Nested
    class CRTest extends GenericPredicate<RFC5234CharacterRules> {
        CRTest() {
            super(
                    rules,
                    RFC5234CharacterRules::isCR,
                    chars('\r')
            );
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    class CRLFTest {
        Stream<String> illegal() {
            return Stream.of("", " ", "a", "aa", "\r", "\n", "\n\r");
        }

        @Test
        void shouldAcceptCRLF() {
            assertTrue(rules.isCRLF("\r\n"));
        }

        @ParameterizedTest
        @MethodSource("illegal")
        void shouldRejectEmptyAndShort(String input) {
            assertFalse(rules.isCRLF(input));
        }

        @Test
        void charSequenceShouldCallBasicFunction() {
            var rulesSpy = Mockito.spy(rules);

            rulesSpy.isCRLF("xy");

            Mockito.verify(rulesSpy).isCRLF(new char[]{'x', 'y'});
        }

    }

    @Nested
    class CTLTest extends GenericPredicate<RFC5234CharacterRules> {
        CTLTest() {
            super(rules,
                    RFC5234CharacterRules::isCTL,
                    concat(codePointRange(0x00, 0x1F), codePoints(0x7F))
            );
        }
    }

    @Nested
    class DIGITTest extends GenericPredicate<RFC5234CharacterRules> {
        DIGITTest() {
            super(rules,
                    RFC5234CharacterRules::isDIGIT,
                    charRange('0', '9')
            );
        }
    }

    @Nested
    class DQUOTETest extends GenericPredicate<RFC5234CharacterRules> {
        DQUOTETest() {
            super(rules,
                    RFC5234CharacterRules::isDQUOTE,
                    chars('"')
            );
        }
    }

    @Nested
    class HEXDIGTest extends GenericPredicate<RFC5234CharacterRules> {
        HEXDIGTest() {
            super(rules,
                    RFC5234CharacterRules::isHEXDIG,
                    chars('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F')
            );
        }
    }

    @Nested
    class HTABTest extends GenericPredicate<RFC5234CharacterRules> {
        HTABTest() {
            super(rules,
                    RFC5234CharacterRules::isHTAB,
                    chars('\t')
            );
        }
    }

    @Nested
    class LFTest extends GenericPredicate<RFC5234CharacterRules> {
        LFTest() {
            super(rules,
                    RFC5234CharacterRules::isLF,
                    chars('\n')
            );
        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    class LWSPTest {
        // reminder:
        // LWSP = *(WSP / CRLF WSP)
        // WSP  = SP / HTAB

        Stream<String> allowedChars() {
            return Stream.of(
                    "", // empty: because of the *Rule
                    " ",
                    "\t",
                    "\r\n"
            );
        }

        Stream<String> illegal() {
            return Stream.concat(Stream.of(
                    // "invalid" whitespace at beginning
                    "\n",
                    "\r",
                    "\n\r",
                    "\ra",
                    "\na",
                    // non-LWS at beginning
                    "a",
                    "a ",
                    "a\t",
                    "a\r\n",
                    "a\r",
                    "a\n",
                    "a\n\r"),
                    lwsPlusOthers()
            );
        }

        // LWS at beginning but non-LWS lateron
        Stream<String> lwsPlusOthers() {
            return Stream.of(
                    " a",
                    "\ta",
                    "\r\na"
            );
        }

        @ParameterizedTest
        @MethodSource("allowedChars")
        void shouldAcceptLWSPChars(String input) {
            assertTrue(rules.isLWSP(input));
        }

        @ParameterizedTest
        @MethodSource("illegal")
        void shouldRejectIllegalCombinations(String input) {
            assertFalse(rules.isLWSP(input));
        }

    }

    @Nested
    class OCTETTest extends GenericPredicate<RFC5234CharacterRules> {
        OCTETTest() {
            super(rules,
                    RFC5234CharacterRules::isOCTET,
                    codePointRange(0x00, 0xFF),
                    codePoints(0xFF + 1)
            );
        }
    }

    @Nested
    class SPTest extends GenericPredicate<RFC5234CharacterRules> {
        SPTest() {
            super(rules,
                    RFC5234CharacterRules::isSP,
                    chars(' ')
            );
        }
    }

    @Nested
    class VCHARTest extends GenericPredicate<RFC5234CharacterRules> {
        VCHARTest() {
            super(rules,
                    RFC5234CharacterRules::isVCHAR,
                    charRange('!', '~')
            );
        }
    }

    @Nested
    class WSPTest extends GenericPredicate<RFC5234CharacterRules> {
        WSPTest() {
            super(rules,
                    RFC5234CharacterRules::isWSP,
                    chars(' ', '\t')
            );
        }
    }

}