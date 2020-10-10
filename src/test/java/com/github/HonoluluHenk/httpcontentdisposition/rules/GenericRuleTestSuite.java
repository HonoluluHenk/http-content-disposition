package com.github.HonoluluHenk.httpcontentdisposition.rules;

import com.github.HonoluluHenk.httpcontentdisposition.helpers.CharInput;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.HonoluluHenk.httpcontentdisposition.helpers.Helpers.inverse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public abstract class GenericRuleTestSuite<Rules> {
    private final Rules rules;
    private final BiFunction<Rules, Character, Boolean> predicate;

    private final List<CharInput> allowed;
    private final List<CharInput> forbidden;

    public GenericRuleTestSuite(
            Rules rules,
            BiFunction<Rules, Character, Boolean> predicate,
            Stream<CharInput> allowed,
            @Nullable Stream<CharInput> forbidden
    ) {
        this.rules = rules;
        this.predicate = predicate;
        this.allowed = allowed.collect(Collectors.toList());
        this.forbidden = forbidden != null
                ? forbidden.collect(Collectors.toList())
                : inverse(this.allowed.stream()).collect(Collectors.toList());
    }

    public GenericRuleTestSuite(
            Rules rules,
            BiFunction<Rules, Character, Boolean> predicate,
            Stream<CharInput> allowed
    ) {
        this(rules, predicate, allowed, null);
    }

    Stream<CharInput> allowed() {
        return allowed.stream();
    }

    Stream<CharInput> forbidden() {
        return forbidden.stream();
    }

    @ParameterizedTest()
    @MethodSource("allowed")
    void isFOOShouldAllow(CharInput c) {
        boolean actual = predicate.apply(rules, c.getCharacter());

        assertThat(actual)
                .isTrue();
    }

    @ParameterizedTest()
    @MethodSource("forbidden")
    void isFOOShouldReject(CharInput c) {
        boolean actual = predicate.apply(rules, c.getCharacter());

        assertThat(actual)
                .isFalse();
    }

}
