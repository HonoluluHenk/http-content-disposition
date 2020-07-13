package ch.christophlinder.httpcontentdisposition.rules;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class RFC8187Encoded {
    private final String value;
    private final boolean isEncoded;

    RFC8187Encoded(String value, boolean isEncoded) {
        this.value = requireNonNull(value);
        this.isEncoded = isEncoded;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns true if {@link #getValue()} is an encoded string.
     *
     * Returns false if the input did contain allowed chars only (i.e.: did not require any encoding).
     */
    public boolean isEncoded() {
        return isEncoded;
    }

    @Override
    public String toString() {
        return "Result[" + isEncoded + "," + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RFC8187Encoded other = (RFC8187Encoded) o;
        return isEncoded == other.isEncoded
                && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isEncoded);
    }
}
