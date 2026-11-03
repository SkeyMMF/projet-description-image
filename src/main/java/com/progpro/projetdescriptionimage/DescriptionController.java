package com.progpro.projetdescriptionimage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.progpro.projetdescriptionimage.agent.Description;
import com.progpro.projetdescriptionimage.agent.DescriptionImpossibleException;
import com.progpro.projetdescriptionimage.agent.DescripteurImage;
import com.progpro.projetdescriptionimage.agent.Image;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/descriptions")
public class DescriptionController {

    private static final Set<String> TYPES_IMAGE_ACCEPTES =
            Set.of(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, "image/webp");

    private final DescripteurImage descripteur;

    public DescriptionController(DescripteurImage descripteur) {
        this.descripteur = descripteur;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> decrire(
            @RequestParam("image") MultipartFile fichier,
            @RequestParam(value = "question", required = false) String question) throws IOException,
            DescriptionImpossibleException {
        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le fichier image est vide."));
        }

        String typeMime = fichier.getContentType();
        if (typeMime == null || !TYPES_IMAGE_ACCEPTES.contains(typeMime.toLowerCase())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Format non accepté. Envoyez une image PNG, JPG ou WEBP."));
        }

        Image image = new Image(fichier.getBytes(), typeMime.toLowerCase());
        Description description = descripteur.decrire(image, question);
        return ResponseEntity.ok(description);
    }

    @ExceptionHandler(DescriptionImpossibleException.class)
    public ResponseEntity<Map<String, String>> gererEchecDescription(
            DescriptionImpossibleException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", exception.getMessage()));
    }
}
