package com.github.HonoluluHenk.httpcontentdisposition.rules;

import java.io.Serializable;

public interface ISO88591Encoder extends Serializable {

    String encode(String input);
}
