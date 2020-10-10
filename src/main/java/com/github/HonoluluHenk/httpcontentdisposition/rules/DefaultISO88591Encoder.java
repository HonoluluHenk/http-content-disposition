package com.github.HonoluluHenk.httpcontentdisposition.rules;

import edu.umd.cs.findbugs.annotations.Nullable;

import javax.annotation.concurrent.ThreadSafe;

import static com.github.HonoluluHenk.httpcontentdisposition.internal.Util.trimToEmpty;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

@ThreadSafe
public class DefaultISO88591Encoder implements ISO88591Encoder {

    private static final long serialVersionUID = -1059462257810038990L;

    @Override
    public String encode(@Nullable String input) {
        String clean = trimToEmpty(input);

        String encoded = new String(clean.getBytes(ISO_8859_1), ISO_8859_1);

        return encoded;
    }
}
