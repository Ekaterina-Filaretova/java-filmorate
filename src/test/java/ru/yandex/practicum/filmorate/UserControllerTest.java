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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/delete-data.sql")
public class UserControllerTest {

    private final String url = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void getUsers() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Nick")));
    }

    @Test
    public void addUser() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Nick")));
    }

    @Test
    public void updateUser() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("asd@mail.com");
        user2.setLogin("asdfg");
        user2.setName("Name");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        mockRequest = putRequest(user2);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Name")));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Name")));
    }

    @Test
    public void addUserWithWrongLogin() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwe rty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithWrongEmail() throws Exception {
        User user = new User();
        user.setEmail("mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithWrongBirthday() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwerty");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2030, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithEmptyName() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwerty");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("qwerty")));
    }

    @Test
    public void addUserWithEmptyRequestBody() throws Exception {
        MockHttpServletRequestBuilder mockRequest = postRequest(null);
        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserById() throws Exception {
        User user = new User();
        user.setEmail("qwe@mail.com");
        user.setLogin("qwert");
        user.setName("Nick");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        MockHttpServletRequestBuilder mockRequest = postRequest(user);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Nick")));
    }


    @Test
    public void addFriend() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders
                        .put(url + "/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteFriend() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/friends/2"));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url + "/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserFriends() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/friends/2"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/1/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    public void getCommonFriends() throws Exception {
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
        user3.setEmail("qwe@mail.com");
        user3.setLogin("qwert");
        user3.setName("Nick");
        user3.setBirthday(LocalDate.of(2000, 1, 1));

        mockRequest = postRequest(user3);
        mockMvc.perform(mockRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/friends/2"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/1/friends/3"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/2/friends/1"));
        mockMvc.perform(MockMvcRequestBuilders.put(url + "/3/friends/1"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(url + "/2/friends/common/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    private MockHttpServletRequestBuilder postRequest(User user) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));
    }

    private MockHttpServletRequestBuilder putRequest(User user) throws JsonProcessingException {
        return MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));
    }
}
