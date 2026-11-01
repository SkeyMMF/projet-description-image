package com.progpro.projetdescriptionimage;

import com.progpro.projetdescriptionimage.agent.DescriptionImpossibleException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ErreurControleur {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErreurControleur.class);

    @ExceptionHandler(ImageInvalideException.class)
    public ResponseEntity<Map<String, String>> imageInvalide(ImageInvalideException exception) {
        return reponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ImageTropVolumineuseException.class)
    public ResponseEntity<Map<String, String>> imageTropVolumineuse(ImageTropVolumineuseException exception) {
        return reponse(HttpStatus.PAYLOAD_TOO_LARGE, exception.getMessage());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> imageManquante() {
        return reponse(HttpStatus.BAD_REQUEST, "Choisissez une image avant de lancer l'analyse.");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> fichierTropVolumineux() {
        return reponse(HttpStatus.PAYLOAD_TOO_LARGE, "L'image ne doit pas dépasser 10 Mo.");
    }

    @ExceptionHandler(DescriptionImpossibleException.class)
    public ResponseEntity<Map<String, String>> descriptionImpossible(DescriptionImpossibleException exception) {
        LOGGER.warn("La description de l'image a échoué.", exception);
        return reponse(HttpStatus.UNPROCESSABLE_ENTITY,
                "L'image n'a pas pu être analysée. Réessayez avec une autre image.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> erreurInattendue(Exception exception) {
        LOGGER.error("Une erreur inattendue est survenue pendant l'analyse de l'image.", exception);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur inattendue est survenue. Veuillez réessayer plus tard.");
    }

    private ResponseEntity<Map<String, String>> reponse(HttpStatus statut, String message) {
        return ResponseEntity.status(statut).body(Map.of("erreur", message));
    }
}
