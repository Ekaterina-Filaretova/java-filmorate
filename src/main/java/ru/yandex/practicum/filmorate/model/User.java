package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    private Long id;
    private Set<Long> userFriends = new HashSet<>();

    @NotBlank(message = "электронная почта не может быть пустой")
    @Email(message = "неправильный формат электронной почты")
    private String email;

    @NotBlank(message = "логин не может быть пустым и содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;
}
