package com.github.HonoluluHenk.httpcontentdisposition.isofallback;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.Serializable;

@FunctionalInterface
public interface IsoFallback extends Serializable {
    @Nullable
    String fallback(String input);

    default boolean needsEncoding() {
        return true;
    }
}
