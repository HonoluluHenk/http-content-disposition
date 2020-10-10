package com.github.HonoluluHenk.httpcontentdisposition.doublequotes;

public class QuoteDoubleQuotes implements DoubleQuotes {

    private static final long serialVersionUID = -3195297319505260187L;

    @Override
    public String handle(String input) {
        if (!input.contains("\"")) {
            return input;
        }

        String quoted = input.replace("\"", "\\\"");

        return '"' + quoted + '"';
    }
}
