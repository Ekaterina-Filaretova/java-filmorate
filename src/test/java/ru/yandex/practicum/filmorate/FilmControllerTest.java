package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    private final String url = "/films";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void getFilms() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1,1));
        film.setDuration(10);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("film")));
    }

    @Test
    public void addFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1,1));
        film.setDuration(10);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("film")));
    }

    @Test
    public void updateFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1,1));
        film.setDuration(10);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setName("film2");
        film2.setDescription("desc2");
        film2.setReleaseDate(LocalDate.of(2000, 1,1));
        film2.setDuration(10);
        film2.setId(1L);

        mockRequest = putRequest(film2);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("film2")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("film2")));
    }

    @Test
    public void addFilmWithWrongName() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1,1));
        film.setDuration(10);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithLongDescription() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят " +
                "разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, " +
                "который за время «своего отсутствия», стал кандидатом Коломбани.");
        film.setReleaseDate(LocalDate.of(2000, 1,1));
        film.setDuration(10);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithWrongReleaseDate() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1890, 1,1));
        film.setDuration(10);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithWrongDuration() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1,1));
        film.setDuration(0);

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithEmptyRequestBody() throws Exception {
        MockHttpServletRequestBuilder mockRequest = postRequest(null);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder postRequest(Film film) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(film));
    }

    private MockHttpServletRequestBuilder putRequest(Film film) throws JsonProcessingException {
        return MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(film));
    }

}
