package ch.christophlinder.httpcontentdisposition;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SuppressWarnings("ClassCanBeStatic")
class RFC5234CharacterRulesTest {
    private final RFC5234CharacterRules rules = new RFC5234CharacterRules();

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
    class StartsWithTest {
        @Test
        void shouldThrowOnNull() {
            //noinspection ConstantConditions
            assertThrows(NullPointerException.class,
                    () -> rules.startsWith(null, ignored -> true)
            );
        }

        @Test
        void shouldRejectEmptyString() {
            boolean actual = rules.startsWith("", ignored -> true);

            assertThat(actual)
                    .isFalse();
        }

        @ParameterizedTest
        @CsvSource({
                "a,true",
                "aa,true",
                "aaa,true",
                "ab,true",
                "abc,true",
                "b,false",
                "ba,false",
        })
        void shouldReturnPredicateOtherwise(String input, boolean expected) {
            boolean actual = rules.startsWith(input, c -> c == 'a');

            assertThat(actual)
                    .isEqualTo(expected);

        }
    }

    @Nested
    @TestInstance(PER_CLASS)
    class ALPHATest extends GenericPredicate {
        ALPHATest() {
            super(rules,
                    RFC5234CharacterRules::isALPHA,
                    RFC5234CharacterRules::startsWithALPHA,
                    concat(charRange('a', 'z'), charRange('A', 'Z'))
            );
        }
    }

    @Nested
    class BITTest extends GenericPredicate {
        BITTest() {
            super(rules,
                    RFC5234CharacterRules::isBIT,
                    RFC5234CharacterRules::startsWithBIT,
                    characters('0', '1')
            );
        }
    }

    @Nested
    class CHARTest extends GenericPredicate {
        CHARTest() {
            super(rules,
                    RFC5234CharacterRules::isCHAR,
                    RFC5234CharacterRules::startsWithCHAR,
                    charRange((char) 0x01, (char) 0x7F));
        }
    }

    @Nested
    class CRTest extends GenericPredicate {
        CRTest() {
            super(
                    rules,
                    RFC5234CharacterRules::isCR,
                    RFC5234CharacterRules::startsWithCR,
                    characters('\r')
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
        void startWithShouldAcceptCRLF() {
            assertTrue(rules.startsWithCRLF("\r\nHello World"));
        }

        @ParameterizedTest
        @MethodSource("illegal")
        void startWithShouldRejectIllegal(String input) {
            assertFalse(rules.startsWithCRLF(input));
        }

        @ParameterizedTest
        @MethodSource("illegal")
        void startWithShouldRejectIllegalPlus(String input) {
            assertFalse(rules.startsWithCRLF(input + "xxx"));
        }

        @Test
        void startWithShouldCallBasicFunction() {
            var rulesSpy = Mockito.spy(rules);
            rulesSpy.startsWithCRLF("asdf");

            Mockito.verify(rulesSpy).isCRLF("as");
        }
    }

    @Nested
    class CTLTest extends GenericPredicate {
        CTLTest() {
            super(rules,
                    RFC5234CharacterRules::isCTL,
                    RFC5234CharacterRules::startsWithCTL,
                    concat(charRange(0x00, 0x1F), characters(0x7F))
            );
        }
    }

    @Nested
    class DIGITTest extends GenericPredicate {
        DIGITTest() {
            super(rules,
                    RFC5234CharacterRules::isDIGIT,
                    RFC5234CharacterRules::startsWithDIGIT,
                    charRange('0', '9')
            );
        }
    }

    @Nested
    class DQUOTETest extends GenericPredicate {
        DQUOTETest() {
            super(rules,
                    RFC5234CharacterRules::isDQUOTE,
                    RFC5234CharacterRules::startsWithDQUOTE,
                    characters('"')
            );
        }
    }

    @Nested
    class HEXDIGTest extends GenericPredicate {
        HEXDIGTest() {
            super(rules,
                    RFC5234CharacterRules::isHEXDIG,
                    RFC5234CharacterRules::startsWithHEXDIG,
                    characters('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F')
            );
        }
    }

    @Nested
    class HTABTest extends GenericPredicate {
        HTABTest() {
            super(rules,
                    RFC5234CharacterRules::isHTAB,
                    RFC5234CharacterRules::startsWithHTAB,
                    characters('\t')
            );
        }
    }

    @Nested
    class LFTest extends GenericPredicate {
        LFTest() {
            super(rules,
                    RFC5234CharacterRules::isLF,
                    RFC5234CharacterRules::startsWithLF,
                    characters('\n')
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

        Stream<String> startsWithAllowed() {
            return Stream.concat(
                    Stream.of(
                            "  aa",
                            " \taa",
                            " \r\naa",
                            "\t aa",
                            "\t\taa",
                            "\t\r\naa",
                            "\r\n\r\naa",
                            "\r\n aa",
                            "\r\n\taa"
                    ),
                    lwsPlusOthers()
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

        @ParameterizedTest
        @MethodSource("startsWithAllowed")
        void startWithShouldAccept(String input) {
            assertTrue(rules.startsWithLWSP(input));
        }

    }

    @Nested
    class OCTETTest extends GenericPredicate {
        OCTETTest() {
            super(rules,
                    RFC5234CharacterRules::isOCTET,
                    RFC5234CharacterRules::startsWithOCTET,
                    charRange(0x00, 0xFF),
                    characters(0xFF + 1)
            );
        }
    }

    @Nested
    class SPTest extends GenericPredicate {
        SPTest() {
            super(rules,
                    RFC5234CharacterRules::isSP,
                    RFC5234CharacterRules::startsWithSP,
                    characters(' ')
            );
        }
    }

    @Nested
    class VCHARTest extends GenericPredicate {
        VCHARTest() {
            super(rules,
                    RFC5234CharacterRules::isVCHAR,
                    RFC5234CharacterRules::startsWithVCHAR,
                    charRange('!', '~')
            );
        }
    }

    @Nested
    class WSPTest extends GenericPredicate {
        WSPTest() {
            super(rules,
                    RFC5234CharacterRules::isWSP,
                    RFC5234CharacterRules::startsWithWSP,
                    characters(' ', '\t')
            );
        }
    }

}