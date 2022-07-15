package ru.yandex.practicum.filmorate.storage.film.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmRatingDao;

import java.util.List;

@Component
public class FilmRatingDaoImpl implements FilmRatingDao {

    private final JdbcTemplate template;

    public FilmRatingDaoImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public FilmRating getRatingById(Integer id) {
        String sql = "SELECT * FROM rating WHERE rating_id = ?";
        return template.queryForObject(sql, (rs, rowNum) ->
                new FilmRating(rs.getInt("rating_id"), rs.getString("name")), id);
    }

    @Override
    public List<FilmRating> getRatings() {
        String sql = "SELECT * FROM rating ORDER BY rating_id";
        return template.query(sql, (rs, rowNum) ->
                new FilmRating(rs.getInt("rating_id"), rs.getString("name")));
    }
}
