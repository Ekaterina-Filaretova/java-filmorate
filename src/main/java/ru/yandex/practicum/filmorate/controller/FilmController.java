package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;
    private static final LocalDate WRONG_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            film.setId(id++);
        }
        try {
            if (isValidDate(film.getReleaseDate()) && !films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Добавлен фильм {}", film);
                return new ResponseEntity<>(film, HttpStatus.OK);
            } else {
                log.info("Фильм с id {} уже был добавлен в список", film.getId());
            }
        } catch (ValidationException e) {
            log.info("Поймано исключение: ", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        try {
            if (isValidDate(film.getReleaseDate()) && films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.info("Фильм обновлен {}", film);
                return new ResponseEntity<>(film, HttpStatus.OK);
            } else {
                log.info("Фильма с id {} нет в списке", film.getId());
            }
        } catch (ValidationException e) {
            log.info("Поймано исключение: ", e);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean isValidDate(LocalDate date) {
        if (date.isBefore(WRONG_RELEASE_DATE)) {
            throw new ValidationException("дата релиза должна быть не раньше 28.12.1895." +
                    " Переданная дата: " + date);
        }
        return true;
    }
}
