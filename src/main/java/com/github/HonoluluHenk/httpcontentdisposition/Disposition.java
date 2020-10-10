package com.github.HonoluluHenk.httpcontentdisposition;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public enum Disposition {
    INLINE("inline"),
    ATTACHMENT("attachment"),
    ;

    private final String headerAttribute;

    Disposition(String headerAttribute) {
        this.headerAttribute = requireNonNull(headerAttribute);
    }

    public String getHeaderAttribute() {
        return headerAttribute;
    }

    public static Optional<Disposition> fromText(@Nullable String disposition) {
        if (disposition == null) {
            return Optional.empty();
        }

        String normalized = disposition.toLowerCase(Locale.US).trim();
        for (Disposition entry : values()) {
            if (entry.headerAttribute.equals(normalized)) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }
}
