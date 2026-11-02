package com.progpro.projetdescriptionimage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void envoieImageEtQuestionAuDescripteurEtRetourneLaDescription() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image", "photo.png", "image/png", new byte[] {1, 2, 3});

        mockMvc.perform(multipart("/api/descriptions")
                        .file(image)
                        .param("question", "Que voit-on ?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texte").value(
                        "Ceci est une réponse de test à votre question : Que voit-on ?"));
    }

    @Test
    void refuseUnFichierQuiNEstPasUneImageAcceptee() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile(
                "image", "document.txt", "text/plain", new byte[] {1, 2, 3});

        mockMvc.perform(multipart("/api/descriptions").file(fichier))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "Format non accepté. Envoyez une image PNG, JPG ou WEBP."));
    }
}
