package ch.christophlinder.httpcontentdisposition.helpers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Helpers {

    public static Stream<CharInput> charRange(char startInclusive, char endInclusive) {
        return IntStream.rangeClosed(startInclusive, endInclusive)
                .mapToObj(CharInput::new);
    }

    public static Stream<CharInput> codePointRange(int startInclusive, int endInclusive) {
        return charRange((char) startInclusive, (char) endInclusive);
    }

    public static Stream<CharInput> chars(char... chars) {
        // :)
        return new String(chars)
                .chars()
                .mapToObj(CharInput::new);
    }

    public static Stream<CharInput> codePoints(int... codePoints) {
        return IntStream.of(codePoints)
                .mapToObj(CharInput::new);
    }

    /**
     * Characters in range: [0 - 255]
     */
    public static Stream<CharInput> latin1() {
        return IntStream.rangeClosed(0, 255)
                .mapToObj(CharInput::new);
    }

    /**
     * Characters in range: [0 - 127]
     */
    public static Stream<CharInput> ascii() {
        return IntStream.rangeClosed(0, 127)
                .mapToObj(CharInput::new);
    }

    /**
     * Stream charaters that are in "all" but not in "except"
     */
    public static Stream<CharInput> allExcept(
            Stream<CharInput> all,
            Stream<CharInput> except
    ) {
        Set<CharInput> chars = all.collect(Collectors.toCollection(LinkedHashSet::new));
        Set<CharInput> exceptChars = except.collect(Collectors.toSet());

        chars.removeAll(exceptChars);

        return chars.stream();
    }

    public static Stream<CharInput> inverse(Stream<CharInput> allowed) {
        return allExcept(
                latin1(),
                allowed
        );
    }

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>... streams) {
        return Stream.of(streams)
                .flatMap(s -> s);
    }
}
