package com.progpro.projetdescriptionimage.agent;

import java.util.Objects;

/**
 * @param octets
 * @param typeMime
 */
public record Image(byte[] octets, String typeMime) {

    public Image {
        Objects.requireNonNull(octets, "octets");
        Objects.requireNonNull(typeMime, "typeMime");
        if (octets.length == 0) {
            throw new IllegalArgumentException("L'image est vide");
        }
        if (typeMime.isBlank()) {
            throw new IllegalArgumentException("Le type MIME est vide");
        }
    }
}
