package com.progpro.projetdescriptionimage;

import com.progpro.projetdescriptionimage.agent.Description;
import com.progpro.projetdescriptionimage.agent.DescriptionImpossibleException;
import com.progpro.projetdescriptionimage.agent.DescripteurImage;
import com.progpro.projetdescriptionimage.agent.Image;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DescriptionControleur {

    private static final long TAILLE_MAXIMALE = 10L * 1024 * 1024;
    private static final Set<String> TYPES_ACCEPTES =
            Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, "image/webp");

    private final DescripteurImage descripteurImage;

    public DescriptionControleur(DescripteurImage descripteurImage) {
        this.descripteurImage = descripteurImage;
    }

    @PostMapping(path = "/api/description", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DescriptionReponse decrire(
            @RequestPart(value = "image", required = false) MultipartFile fichier,
            @RequestParam(value = "question", required = false) String question)
            throws DescriptionImpossibleException {
        if (fichier == null || fichier.isEmpty()) {
            throw new ImageInvalideException("Choisissez une image non vide avant de lancer l'analyse.");
        }
        if (fichier.getSize() > TAILLE_MAXIMALE) {
            throw new ImageTropVolumineuseException("L'image ne doit pas dépasser 10 Mo.");
        }

        String typeMime = fichier.getContentType();
        if (typeMime == null || !TYPES_ACCEPTES.contains(typeMime.toLowerCase(Locale.ROOT))) {
            throw new ImageInvalideException("Format non accepté. Choisissez une image JPEG, PNG ou WEBP.");
        }

        try {
            Description description = descripteurImage.decrire(
                    new Image(fichier.getBytes(), typeMime.toLowerCase(Locale.ROOT)), question);
            return new DescriptionReponse(description.texte());
        } catch (IOException exception) {
            throw new ImageInvalideException("L'image envoyée n'a pas pu être lue.");
        }
    }

    public record DescriptionReponse(String description) {
    }
}
