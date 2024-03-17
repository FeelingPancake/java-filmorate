package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInvalidDataEmailMustBeEmail() throws Exception {
        // Подготовка JSON с невалидными данными
        String invalidDataJson = "{\"email\": \"exskske.wq\"," +
                " \"login\": \"ilya\"," +
                " \"birthday\": \"1900-03-25\"," +
                " \"name\": \"Hastrur\"}";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataLoginMustBeWithoutEmptySpaces() throws Exception {
        // Подготовка JSON с невалидными данными
        String invalidDataJson = "{\"email\": \"example@mail.ru\"," +
                " \"login\": \"ilya        \"," +
                " \"birthday\": \"1900-03-25\"," +
                " \"name\": \"Hastrur\"}";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataLoginMustBe() throws Exception {
        // Подготовка JSON с невалидными данными
        String invalidDataJson = "{\"email\": \"example@mail.ru\"," +
                " \"login\": \"\"," +
                " \"birthday\": \"1900-03-25\"," +
                " \"name\": \"Hastrur\"}";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataBirthdayCantBeInFuture() throws Exception {
        // Подготовка JSON с невалидными данными
        String invalidDataJson = "{\"email\": \"example@mail.ru\"," +
                " \"login\": \"Ilya\"," +
                " \"birthday\": \"2900-03-25\"," +
                " \"name\": \"Hastrur\"}";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidDataIfNameNullLoginBecomeName() throws Exception {
        // Подготовка JSON с невалидными данными
        String invalidDataJson = "{\"email\": \"example@mail.ru\"," +
                " \"login\": \"Ilya\"," +
                " \"birthday\": \"1900-03-25\"," +
                " \"name\": null}";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Ilya"));
    }

    @Test
    public void testInvalidDataWhenBodyIsNull() throws Exception {
        String invalidDataJson = "null";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidDataJson))
                .andExpect(status().isBadRequest());
    }
}

