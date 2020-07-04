package ch.christophlinder.httpcontentdisposition;

import ch.christophlinder.httpcontentdisposition.helpers.CharInput;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.christophlinder.httpcontentdisposition.helpers.Helpers.inverse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public abstract class GenericPredicate {
    private final RFC5234CharacterRules rules;
    private final BiFunction<RFC5234CharacterRules, Character, Boolean> predicate;
    private final BiFunction<RFC5234CharacterRules, CharSequence, Boolean> startsWithPredicate;

    private final List<CharInput> allowed;
    private final List<CharInput> forbidden;

    public GenericPredicate(
            RFC5234CharacterRules rules,
            BiFunction<RFC5234CharacterRules, Character, Boolean> predicate,
            BiFunction<RFC5234CharacterRules, CharSequence, Boolean> startsWithPredicate,
            Stream<CharInput> allowed,
            @Nullable Stream<CharInput> forbidden
    ) {
        this.rules = rules;
        this.predicate = predicate;
        this.startsWithPredicate = startsWithPredicate;
        this.allowed = allowed.collect(Collectors.toList());
        this.forbidden = forbidden != null
                ? forbidden.collect(Collectors.toList())
                : inverse(this.allowed.stream()).collect(Collectors.toList());
    }

    public GenericPredicate(
            RFC5234CharacterRules rules,
            BiFunction<RFC5234CharacterRules, Character, Boolean> predicate,
            BiFunction<RFC5234CharacterRules, CharSequence, Boolean> startsWithPredicate,
            Stream<CharInput> allowed
    ) {
        this(rules, predicate, startsWithPredicate, allowed, null);
    }

    Stream<CharInput> allowed() {
        return allowed.stream();
    }

    Stream<CharInput> forbidden() {
        return forbidden.stream();
    }

    @ParameterizedTest()
    @MethodSource("allowed")
    void isAlphaShouldAllow(CharInput c) {
        boolean actual = predicate.apply(rules, c.getCharacter());

        assertThat(actual)
                .isTrue();
    }

    @ParameterizedTest()
    @MethodSource("forbidden")
    void isAlphaShouldReject(CharInput c) {
        boolean actual = predicate.apply(rules, c.getCharacter());

        assertThat(actual)
                .isFalse();
    }

    @Test
    void startsWithAlphaShouldRejectEmptyString() {
        boolean actual = startsWithPredicate.apply(rules, "");

        assertThat(actual)
                .isFalse();
    }

    // since we don't want to run all tests again with startWith,
    // do at least verify that the (tested) base method is called
    //
    @Test
    void startsWithShouldCallIsMethod() {
        assertCharPredicateIsCalled(
                startsWithPredicate,
                predicate
        );
    }

    @SuppressWarnings("ReturnValueIgnored")
    void assertCharPredicateIsCalled(
            BiFunction<RFC5234CharacterRules, CharSequence, Boolean> testFunction,
            BiFunction<RFC5234CharacterRules, Character, Boolean> charPredicate
    ) {
        var rulesSpy = Mockito.spy(rules);
        testFunction.apply(rulesSpy, "xyz");

        // the is* method must be called
        charPredicate.apply(Mockito.verify(rulesSpy), 'x');

        // the is* method is called via the generic startsWith(CharSequence, Predicate<>).
        // This ensures proper/tested substringing of the input argument.
        Mockito.verify(rulesSpy).startsWith(ArgumentMatchers.eq("xyz"), ArgumentMatchers.any());
    }

}
