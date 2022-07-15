package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql")
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
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

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
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("film")))
                .andExpect(jsonPath("$.mpa.id", is(1)));
    }

    @Test
    public void updateFilm() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setName("film2");
        film2.setDescription("desc2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(10);
        film2.setId(1L);
        film2.setMpa(new FilmRating(2, null));

        mockRequest = putRequest(film2);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("film2")))
                .andExpect(jsonPath("$.mpa.id", is(2)));

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
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

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
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithWrongReleaseDate() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithWrongDuration() throws Exception {
        Film film = new Film();
        film.setName("name");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);
        film.setMpa(new FilmRating(1, null));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addFilmWithEmptyRequestBody() throws Exception {
        MockHttpServletRequestBuilder mockRequest = postRequest((Film) null);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getFilmById() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("film")));
    }

    @Test
    public void addLike() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(url + "/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteLike() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/like/1"));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url + "/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getPopular() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user2 = new User();
        user2.setEmail("asd@mail.com");
        user2.setLogin("asdfg");
        user2.setName("name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        mockRequest = postRequest(user2);
        mockMvc.perform(mockRequest);

        User user3 = new User();
        user3.setEmail("zxc@mail.com");
        user3.setLogin("zxcvb");
        user3.setName("surname");
        user3.setBirthday(LocalDate.of(2000, 1, 1));

        mockRequest = postRequest(user3);
        mockMvc.perform(mockRequest);

        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));

        mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        Film film2 = new Film();
        film2.setName("film2");
        film2.setDescription("desc2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(10);
        film2.setMpa(new FilmRating(3, null));

        mockRequest = postRequest(film2);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/like/1"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/like/2"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/2/like/3"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/2/like/1"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/2/like/2"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("film2")))
                .andExpect(jsonPath("$[1].name", is("film")));
    }

    @Test
    public void addGenre() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));
        film.setGenres(List.of(new FilmGenre(1, null), new FilmGenre(3, null)));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres", hasSize(2)))
                .andExpect(jsonPath("$.genres[1].id", is(3)));

    }

    @Test
    public void changeGenre() throws Exception {
        Film film = new Film();
        film.setName("film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(10);
        film.setMpa(new FilmRating(1, null));
        film.setGenres(List.of(new FilmGenre(1, null), new FilmGenre(3, null)));

        MockHttpServletRequestBuilder mockRequest = postRequest(film);
        mockMvc.perform(mockRequest);

        film.setId(1L);
        film.setGenres(List.of(new FilmGenre(5, null)));

        mockMvc.perform(putRequest(film))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0].id", is(5)));
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

    private MockHttpServletRequestBuilder postRequest(User user) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));
    }

}
