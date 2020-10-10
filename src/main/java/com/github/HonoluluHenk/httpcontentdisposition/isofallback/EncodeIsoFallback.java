package com.github.HonoluluHenk.httpcontentdisposition.isofallback;

import com.github.HonoluluHenk.httpcontentdisposition.internal.rules.DefaultISO88591Encoder;
import com.github.HonoluluHenk.httpcontentdisposition.internal.rules.ISO88591Encoder;
import edu.umd.cs.findbugs.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Tries to encode the original java string value to ISO-8859-1.
 * <p>
 * This implementation replaces invalid chars with the replacement-char: '?'.
 */
public class EncodeIsoFallback implements IsoFallback {

    private static final long serialVersionUID = -2211120688937047214L;

    private final ISO88591Encoder encoder;

    EncodeIsoFallback(ISO88591Encoder encoder) {
        this.encoder = requireNonNull(encoder);
    }

    public EncodeIsoFallback() {
        this(new DefaultISO88591Encoder());
    }

    @Nullable
    @Override
    public String fallback(String input) {
        return encoder.encode(input);
    }
}
