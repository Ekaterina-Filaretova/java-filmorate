package ru.yandex.practicum.filmorate.model;

import jdk.jfr.Threshold;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Threshold
public class FilmRating {

    private Integer id;
    private String name;

    public FilmRating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
