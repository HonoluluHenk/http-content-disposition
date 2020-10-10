package com.github.HonoluluHenk.httpcontentdisposition.isofallback;

import edu.umd.cs.findbugs.annotations.Nullable;

public class OverrideIsoFallback implements IsoFallback {
    private static final long serialVersionUID = -5421954892971161340L;

    @Nullable
    private final String value;
    private final boolean needsEncoding;

    public OverrideIsoFallback(@Nullable String value) {
        this(value, true);
    }

    /**
     * If passing needsEncoding=false,
     * <strong></strong>you are fully responsible</strong>
     * to pass a valid unquoted "value" as defined in
     * <a href="https://tools.ietf.org/html/rfc6266#section-4.1">RFC 6266, section 4.1. Grammar</a>.
     */
    public OverrideIsoFallback(@Nullable String value, boolean needsEncoding) {
        this.value = value;
        this.needsEncoding = needsEncoding;
    }

    @Nullable
    @Override
    public String fallback(String input) {
        return value;
    }

    @Override
    public boolean needsEncoding() {
        return needsEncoding;
    }
}
