package ru.yandex.practicum.filmorate.storage.user.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Primary
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private static final String INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM users ORDER BY user_id";
    private static final String SELECT_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_FRIEND = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_FRIENDS = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
            "FROM users AS u RIGHT JOIN user_friends uf ON uf.friend_id = u.user_id WHERE uf.user_id = ?";
    private static final String SELECT_COM_FRIENDS = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM " +
            "user_friends WHERE user_id = ? INTERSECT SELECT friend_id FROM user_friends WHERE user_id = ?) " +
            "ORDER BY user_id";
    private final JdbcTemplate template;

    public UserDbStorage(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public User add(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_USER, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        template.update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> get() {
        return template.query(SELECT_ALL, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserById(Long id) {
        return template.queryForObject(SELECT_BY_ID, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void addFriend(Long user_id, Long friend_id) {
        template.update(INSERT_FRIEND, user_id, friend_id);
    }

    @Override
    public void removeFriend(Long user_id, Long friend_id) {
        template.update(DELETE_FRIEND, user_id, friend_id);
    }

    @Override
    public List<User> getFriends(Long id) {
        return template.query(SELECT_FRIENDS, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return template.query(SELECT_COM_FRIENDS, (rs, rowNum) -> makeUser(rs), firstUserId, secondUserId);
    }

    private User makeUser(@NotNull ResultSet rs) throws SQLException {
        return new User(rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate());
    }
}
