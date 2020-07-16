package ch.christophlinder.httpcontentdisposition.isofallback;

import java.io.Serializable;

public interface IsoFallback extends Serializable {
    String fromOriginal(String input);
}
