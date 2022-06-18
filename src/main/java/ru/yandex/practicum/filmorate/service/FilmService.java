package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private static final LocalDate WRONG_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Long id = 1L;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        if (isValidDate(film.getReleaseDate()) && !filmStorage.get().containsKey(film.getId())) {
            if (film.getId() == null) {
                film.setId(id++);
            }
            log.info("Добавлен фильм {}", film);
            return filmStorage.add(film);
        } else {
            throw new ObjectAlreadyExistException("Фильм с id " + film.getId() + " уже был добавлен в список");
        }
    }

    public Film updateFilm(Film film) {
        if (isValidDate(film.getReleaseDate()) && filmStorage.get().containsKey(film.getId())) {
            log.info("Фильм обновлен {}", film);
            return filmStorage.add(film);
        } else {
            throw new ObjectNotFoundException("Фильма с id " + film.getId() + " нет в списке");
        }
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.get().values());
    }

    public Film getFilmById(Long id) {
        if (filmStorage.get().containsKey(id)) {
            return filmStorage.get().get(id);
        } else {
            throw new ObjectNotFoundException("Фильма с id " + id + " нет в списке");
        }
    }

    public Film addLike(Long filmId, Long userId) {
        if (filmStorage.get().containsKey(filmId)) {
            if (userStorage.get().containsKey(userId)) {
                getFilmById(filmId).getFilmLikes().add(userId);
                log.info("Пользователь {} добавил лайк фильму {}", userStorage.get().get(userId), getFilmById(filmId));
                return getFilmById(filmId);
            } else {
                throw new ObjectNotFoundException("Пользователя " + userStorage.get().get(userId) + " нет в списке");
            }
        } else {
            throw new ObjectNotFoundException("Фильма " + getFilmById(filmId) + " нет в списке");
        }
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (filmStorage.get().containsKey(filmId)) {
            if (userStorage.get().containsKey(userId)) {
                getFilmById(filmId).getFilmLikes().remove(userId);
                log.info("Пользователь {} удалил лайк к фильму {}", userStorage.get().get(userId), getFilmById(filmId));
                return getFilmById(filmId);
            } else {
                throw new ObjectNotFoundException("Пользователя " + userStorage.get().get(userId) + " нет в списке");
            }
        } else {
            throw new ObjectNotFoundException("Фильма " + getFilmById(filmId) + " нет в списке");
        }
    }

    public List<Film> getPopularFilms(int count) {
        if (count > filmStorage.get().size()) {
            count = filmStorage.get().size();
        }
        List<Film> popularFilms = getFilms();
        popularFilms.sort((o1, o2) -> o2.getFilmLikes().size() - o1.getFilmLikes().size());
        return popularFilms.subList(0, count);
    }

    private boolean isValidDate(LocalDate date) {
        if (date.isBefore(WRONG_RELEASE_DATE)) {
            throw new ValidationException("дата релиза должна быть не раньше 28.12.1895." +
                    " Переданная дата: " + date);
        }
        return true;
    }
}
