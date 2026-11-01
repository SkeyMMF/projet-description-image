package com.progpro.projetdescriptionimage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.progpro.projetdescriptionimage.agent.Description;
import com.progpro.projetdescriptionimage.agent.DescriptionImpossibleException;
import com.progpro.projetdescriptionimage.agent.DescripteurImage;
import com.progpro.projetdescriptionimage.agent.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DescriptionControleur.class)
@Import(ErreurControleur.class)
class DescriptionControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DescripteurImage descripteurImage;

    @Test
    void accepteUneImageEtUneQuestionFacultative() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "photo.png", "image/png", new byte[] {1, 2, 3});
        when(descripteurImage.decrire(any(Image.class), eq("Que voit-on ?")))
                .thenReturn(new Description("Un paysage."));

        mockMvc.perform(multipart("/api/description")
                        .file(fichier)
                        .param("question", "Que voit-on ?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Un paysage."));

        verify(descripteurImage).decrire(any(Image.class), eq("Que voit-on ?"));
    }

    @Test
    void accepteUneImageSansQuestion() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "photo.gif", "image/webp", new byte[] {1});
        when(descripteurImage.decrire(any(Image.class), eq(null)))
                .thenReturn(new Description("Une image."));

        mockMvc.perform(multipart("/api/description").file(fichier))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Une image."));
    }

    @Test
    void refuseUneImageManquante() throws Exception {
        mockMvc.perform(multipart("/api/description"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value(
                        "Choisissez une image non vide avant de lancer l'analyse."));

        verify(descripteurImage, never()).decrire(any(Image.class), any());
    }

    @Test
    void refuseUnFichierVide() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "photo.png", "image/png", new byte[0]);

        mockMvc.perform(multipart("/api/description").file(fichier))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value(
                        "Choisissez une image non vide avant de lancer l'analyse."));
    }

    @Test
    void refuseUnTypeMimeNonAutorise() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "photo.gif", "image/gif", new byte[] {1});

        mockMvc.perform(multipart("/api/description").file(fichier))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erreur").value(
                        "Format non accepté. Choisissez une image JPEG, PNG ou WEBP."));
    }

    @Test
    void refuseUnFichierTropVolumineux() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "photo.png", "image/png", new byte[10 * 1024 * 1024 + 1]);

        mockMvc.perform(multipart("/api/description").file(fichier))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.erreur").value("L'image ne doit pas dépasser 10 Mo."));
    }

    @Test
    void convertitUneErreurDeDescriptionEnMessageFrancais() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "photo.png", "image/png", new byte[] {1});
        when(descripteurImage.decrire(any(Image.class), eq(null)))
                .thenThrow(new DescriptionImpossibleException("détail technique"));

        mockMvc.perform(multipart("/api/description").file(fichier))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erreur").value(
                        "L'image n'a pas pu être analysée. Réessayez avec une autre image."));
    }
}
