package com.github.HonoluluHenk.httpcontentdisposition.helpers;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Sole purpose: make the unit dest descriptions render a usable character description.
 */
public class CharInput {
    private final Character character;

    public CharInput(Character character) {
        this.character = requireNonNull(character);
    }

    public CharInput(int charCode) {
        this.character = (char) charCode;
    }

    public Character getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        String displayName;
        if (32 <= character && character <= 126) {
            // printable ascii range
            displayName = String.valueOf(character);
        } else {
            displayName = Character.getName(character);
        }

        return String.format("0x%02X", (int) character) + " = " + displayName;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return character.equals(((CharInput) o).character);
    }

    @Override
    public int hashCode() {
        return Objects.hash(character);
    }
}
