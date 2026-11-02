package com.progpro.projetdescriptionimage.agent;

import java.time.Duration;
import java.util.Base64;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "description.agent", havingValue = "ollama")
public class OllamaDescripteur implements DescripteurImage {

    private final static String CONSIGNE = """
            Décris cette image.
            Commence par les éléments essentiels et la scène générale, puis ajoute les détails visuels importants.
            Recopie le texte lisible présent dans l'image, sans le compléter ni le corriger.
            Ne rajoute rien de plus que ce qui est visible. Si un détail t'est ilisible ou flou, indique-le au lieu de deviner.
            """;

    private final ChatModel modele;

    public OllamaDescripteur(
            @Value("${description.ollama.url:http://localhost:11434}") String url,
            @Value("${description.ollama.modele:llava:latest}") String nomModele,
            @Value("${description.ollama.delai-ms:120000}") long delaiMs) {
        this.modele = OllamaChatModel.builder()
                .baseUrl(url)
                .modelName(nomModele)
                .timeout(Duration.ofMillis(delaiMs))
                .build();
    }

    @Override
    public Description decrire(Image image, String question) throws DescriptionImpossibleException {
        try {
            String imageBase64 = Base64.getEncoder().encodeToString(image.octets());
            UserMessage message = UserMessage.from(
                    TextContent.from(construirePrompte(question)),
                    ImageContent.from(imageBase64, image.typeMime()));
            String texte = modele.chat(message).aiMessage().text().replace("**","");

            if (texte == null || texte.isBlank()) {
                throw new DescriptionImpossibleException("Ollama a renvoyé une réponse vide.");
            }
            return new Description(texte.strip());
        } catch (DescriptionImpossibleException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new DescriptionImpossibleException(
                    "Impossible d'obtenir une description depuis Ollama.", exception);
        }
    }

    static String construirePrompte(String question) {
        if (question == null || question.isBlank()) {
            return CONSIGNE;
        }
        return "Réponds en francais a cette question en te servant de l'image : " + question.strip();
    }
}
