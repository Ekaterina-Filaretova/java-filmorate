package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql")
public class FilmRatingControllerTest {

    private final String url = "/mpa";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRatingById() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(url + "/1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("G")));
    }

    @Test
    public void getRatings() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(url);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[4].name", is("NC-17")));
    }

    @Test
    public void getUnknownRating() throws Exception {
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.get(url + "/-1");
        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }
}

