package ch.christophlinder.httpcontentdisposition.internal;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Util {
    private Util() {
        // utility class
    }

    public static boolean isBetween(int codePoint, int lowerBound, int upperBound) {
        assert upperBound >= lowerBound : "Input must be lowerBound <= upperBound but was: " + lowerBound + "/" + upperBound;
        return lowerBound <= codePoint && codePoint <= upperBound;
    }

    public static boolean isOneOf(int codePoint, char... chars) {
        for (char aChar : chars) {
            if (aChar == codePoint) {
                return true;
            }
        }
        return false;
    }

    public static String trimToEmpty(String input) {
        return input == null ? "" : input.trim();
    }

    @SafeVarargs
    public static <T> Set<T> setOf(T...t) {
        Set<T> set = new LinkedHashSet<>(Arrays.asList(t));

        return set;
    }
}
