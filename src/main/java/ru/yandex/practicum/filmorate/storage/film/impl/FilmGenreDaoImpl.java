package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreDao;

import java.util.List;

@Component
public class FilmGenreDaoImpl implements FilmGenreDao {

    private static final String SELECT_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM genres ORDER BY genre_id";
    private final JdbcTemplate template;

    public FilmGenreDaoImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public FilmGenre getGenreById(Integer id) {
        return template.queryForObject(SELECT_BY_ID, (rs, rowNum) ->
                new FilmGenre(rs.getInt("genre_id"), rs.getString("name")), id);
    }

    @Override
    public List<FilmGenre> getGenres() {
        return template.query(SELECT_ALL, (rs, rowNum) ->
                new FilmGenre(rs.getInt("genre_id"), rs.getString("name")));
    }
}
