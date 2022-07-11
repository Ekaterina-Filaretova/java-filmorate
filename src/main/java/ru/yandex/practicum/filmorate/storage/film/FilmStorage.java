package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {

    Film add(Film user);

    Film update(Film user);

    Map<Long, Film> get();

}
