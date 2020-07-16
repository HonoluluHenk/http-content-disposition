package ch.christophlinder.httpcontentdisposition.isofallback;

import static java.util.Objects.requireNonNull;

public class FixedValueIsoFallback implements IsoFallback {
    private static final long serialVersionUID = -5421954892971161340L;

    private final String value;

    public FixedValueIsoFallback(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String fromOriginal(String input) {
        return value;
    }
}
