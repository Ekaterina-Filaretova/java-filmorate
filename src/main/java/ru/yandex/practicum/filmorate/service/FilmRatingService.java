package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmRatingDao;

import java.util.List;

@Slf4j
@Service
public class FilmRatingService {

    private final FilmRatingDao filmRating;

    public FilmRatingService(FilmRatingDao filmRating) {
        this.filmRating = filmRating;
    }

    public FilmRating getRatingById(Integer id) {
        try {
            FilmRating rating = filmRating.getRatingById(id);
            log.info("Получен рейтинг {}", rating);
            return rating;
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Рейтинг с id " + id + " не найден");
        }
    }

    public List<FilmRating> getRatings() {
        List<FilmRating> ratings = filmRating.getRatings();
        log.info("Получен список рейтингов {}", ratings);
        return ratings;
    }
}
