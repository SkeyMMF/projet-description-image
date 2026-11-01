package com.progpro.projetdescriptionimage.agent;

import java.util.Objects;

/**
 * @param texte
 */
public record Description(String texte) {

    public Description {
        Objects.requireNonNull(texte, "texte");
        if (texte.isBlank()) {
            throw new IllegalArgumentException("La description est vide");
        }
    }
}
