package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            user.setId(id++);
        }
        try {
            if (isLoginValid(user.getLogin()) && !users.containsKey(user.getId())) {
                user = nameVerification(user);
                users.put(user.getId(), user);
                log.info("добавлен пользователь {}", user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            log.info("Пользователь с id {} уже был добавлен в список", user.getId());
        } catch (ValidationException e) {
            log.info("Поймано исключение: ", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            if (isLoginValid(user.getLogin()) && users.containsKey(user.getId())) {
                user = nameVerification(user);
                users.put(user.getId(), user);
                log.info("Пользователь обновлен {}", user);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                log.info("Пользователя с id {} нет в списке", user.getId());
            }
        } catch (ValidationException e) {
            log.info("Поймано исключение: ", e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean isLoginValid(String login) {
        if (login.contains(" ")) {
            throw new ValidationException("логин " + login + " не должен содержать пробелы");
        }
        return true;
    }

    private User nameVerification(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return user;
    }


}
