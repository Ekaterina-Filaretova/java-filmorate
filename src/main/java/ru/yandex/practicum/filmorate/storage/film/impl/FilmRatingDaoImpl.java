package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmRatingDao;

import java.util.List;

@Component
public class FilmRatingDaoImpl implements FilmRatingDao {

    private static final String SELECT_BY_ID = "SELECT * FROM rating WHERE rating_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM rating ORDER BY rating_id";
    private final JdbcTemplate template;

    public FilmRatingDaoImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public FilmRating getRatingById(Integer id) {
        return template.queryForObject(SELECT_BY_ID, (rs, rowNum) ->
                new FilmRating(rs.getInt("rating_id"), rs.getString("name")), id);
    }

    @Override
    public List<FilmRating> getRatings() {
        return template.query(SELECT_ALL, (rs, rowNum) ->
                new FilmRating(rs.getInt("rating_id"), rs.getString("name")));
    }
}
