package com.github.HonoluluHenk.httpcontentdisposition.isofallback;

import com.github.HonoluluHenk.httpcontentdisposition.rules.DefaultISO88591Encoder;
import com.github.HonoluluHenk.httpcontentdisposition.rules.ISO88591Encoder;

import static java.util.Objects.requireNonNull;

public class EncodingIsoFallback implements IsoFallback {

    private static final long serialVersionUID = -2211120688937047214L;

    private final ISO88591Encoder encoder;

    EncodingIsoFallback(ISO88591Encoder encoder) {
        this.encoder = requireNonNull(encoder);
    }

    public EncodingIsoFallback() {
        this(new DefaultISO88591Encoder());
    }

    @Override
    public String fromOriginal(String input) {
        return encoder.encode(input);
    }
}
