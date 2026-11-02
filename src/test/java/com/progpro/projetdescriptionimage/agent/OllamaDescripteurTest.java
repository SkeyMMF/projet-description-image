package com.progpro.projetdescriptionimage.agent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OllamaDescripteurTest {

    @Test
    void consigneSansQuestionDemandeUneDescriptionPrudenteEtDetaillee() {
        String consigne = OllamaDescripteur.construirePrompte(null);

        assertTrue(consigne.contains("éléments essentiels"));
        assertTrue(consigne.contains("détails visuels importants"));
        assertTrue(consigne.contains("texte lisible"));
        assertTrue(consigne.contains("au lieu de deviner"));
        assertFalse(consigne.contains("Réponds aussi à cette question"));
    }

    @Test
    void consigneAjouteLaQuestionQuandElleEstPresente() {
        String consigne = OllamaDescripteur.construirePrompte("  Combien de personnes ?  ");

        assertTrue(consigne.contains("Réponds aussi à cette question"));
        assertTrue(consigne.endsWith("Combien de personnes ?"));
    }

    @Test
    void consigneIgnoreUneQuestionVide() {
        String consigne = OllamaDescripteur.construirePrompte("  ");

        assertFalse(consigne.contains("Réponds aussi à cette question"));
    }
}
