package com.progpro.projetdescriptionimage.agent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "description.agent", havingValue = "faux", matchIfMissing = true)
public class FauxDescripteur implements DescripteurImage {

    @Override
    public Description decrire(Image image, String question) {
        if (question == null || question.isBlank()) {
            return new Description("Ceci est une description de test. "
                    + "L'image contient un paysage de montagne avec un lac au premier plan.");
        }
        return new Description("Ceci est une réponse de test à votre question : " + question.strip());
    }
}
