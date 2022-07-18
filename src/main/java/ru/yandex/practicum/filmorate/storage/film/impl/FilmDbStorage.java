package ru.yandex.practicum.filmorate.storage.film.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Primary
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, " +
            "rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ? where film_id = ?";
    private static final String SELECT_ALL = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
            "f.rating_id, r.name AS r_name FROM films AS f INNER JOIN rating AS r ON f.rating_id = r.rating_id " +
            "ORDER BY film_id";
    private static final String SELECT_BY_ID = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
            "f.rating_id, r.name AS r_name FROM films AS f INNER JOIN rating AS r ON f.rating_id = r.rating_id " +
            "WHERE film_id = ?";
    private static final String INSERT_LIKE = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String SELECT_POP = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
            "f.rating_id, r.name AS r_name FROM films AS f LEFT JOIN (SELECT film_id, COUNT(user_id) AS user_id FROM " +
            "film_likes GROUP BY film_id ORDER BY user_id DESC) AS fl ON f.film_id = fl.film_id INNER JOIN rating AS r " +
            "ON f.rating_id = r.rating_id ORDER BY user_id DESC LIMIT ?";
    private static final String SELECT_GENRE = "SELECT g.genre_id, g.name FROM films AS f INNER JOIN film_genre AS fg " +
            "ON f.film_id = fg.film_id INNER JOIN genres AS g ON fg.genre_id = g.genre_id WHERE f.film_id = ?";
    private static final String DELETE_GENRE = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String INSERT_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private final JdbcTemplate template;

    public FilmDbStorage(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_FILM, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        template.update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> get() {
        return template.query(SELECT_ALL, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmById(Long id) {
        return template.queryForObject(SELECT_BY_ID, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        template.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        template.update(DELETE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        return template.query(SELECT_POP, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public void setFilmGenre(@NotNull Film film) {
        film.setGenres(template.query(SELECT_GENRE, (rs, rowNum) ->
                        new FilmGenre(rs.getInt("genre_id"), rs.getString("name")),
                film.getId()));
    }

    @Override
    public void addFilmGenre(@NotNull Film film) {
        template.update(DELETE_GENRE, film.getId());
        for (FilmGenre genre : film.getGenres()) {
            template.update(INSERT_GENRE, film.getId(), genre.getId());
        }
    }

    private Film makeFilm(@NotNull ResultSet rs) throws SQLException {
        return new Film(rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                new FilmRating(rs.getInt("rating_id"), rs.getString("r_name")));
    }
}