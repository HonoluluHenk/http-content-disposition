package com.github.HonoluluHenk.httpcontentdisposition.internal.rules;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@ThreadSafe
public class Encoded {
    private final String value;
    private final boolean isEncoded;

    Encoded(String value, boolean isEncoded) {
        this.value = requireNonNull(value);
        this.isEncoded = isEncoded;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns true if {@link #getValue()} is an encoded string.
     * <p>
     * Returns false if the input did contain allowed chars only (i.e.: did not require any encoding).
     */
    public boolean isEncoded() {
        return isEncoded;
    }

    @Override
    public String toString() {
        return "Encoded[" + isEncoded + "," + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Encoded other = (Encoded) o;
        return isEncoded == other.isEncoded
                && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isEncoded);
    }
}
