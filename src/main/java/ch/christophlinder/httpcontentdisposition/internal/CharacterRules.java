package ch.christophlinder.httpcontentdisposition.internal;

public final class CharacterRules {
    private CharacterRules() {
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

}
