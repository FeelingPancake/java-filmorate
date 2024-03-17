package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class FilmValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInvalidDataNameShouldNotBeEmpty() throws Exception {
        // Подготовка JSON с невалидными данными
        String invalidDataJson = "{\"name\": \"\"," +
                " \"description\": \"Description\"," +
                " \"releaseDate\": \"1900-03-25\"," +
                " \"duration\": 200}";


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataDescriptionSizeOverMaximum() throws Exception {
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            description.append("description");
        }
        String invalidDataJson = "{\"name\": \"testName\"," +
                " \"description\": \"" + description + "\"," +
                " \"releaseDate\": \"1900-03-25\"," +
                " \"duration\": 200}";


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataWithDateBeforeCreationOfFilms() throws Exception {
        String invalidDataJson = "{\"name\": \"testName\"," +
                " \"description\": \"description\"," +
                " \"releaseDate\": \"1895-12-27\"," +
                " \"duration\": 200}";


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataWithNegativeDuration() throws Exception {
        String invalidDataJson = "{\"name\": \"testName\"," +
                " \"description\": \"description\"," +
                " \"releaseDate\": \"2000-12-27\"," +
                " \"duration\": -200}";


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataWhenBodyIsNull() throws Exception {
        String invalidDataJson = "null";


        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }
}
