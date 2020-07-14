package ch.christophlinder.httpcontentdisposition.rules;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.nio.charset.StandardCharsets;

import static ch.christophlinder.httpcontentdisposition.internal.Util.trimToEmpty;

public class DefaultISO88591Encoder implements ISO88591Encoder {

    @Override
    public Encoded encode(@Nullable String input) {
        String clean = trimToEmpty(input);

        String encoded = new String(clean.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        boolean isEncoded = !encoded.equals(input);

        return new Encoded(encoded, isEncoded);
    }

}
