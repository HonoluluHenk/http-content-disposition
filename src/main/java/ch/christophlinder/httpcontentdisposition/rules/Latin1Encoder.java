package ch.christophlinder.httpcontentdisposition.rules;

import java.nio.charset.StandardCharsets;

public class Latin1Encoder {

    public Encoded encode(String input) {
        String encoded = new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        boolean isEncoded = !encoded.equals(input);

        return new Encoded(encoded, isEncoded);
    }
}
