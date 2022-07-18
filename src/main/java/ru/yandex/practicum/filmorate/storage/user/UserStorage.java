package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(User user);

    List<User> get();

    User getUserById(Long id);

    void addFriend(Long userId, Long friend_id);

    void removeFriend(Long userId, Long friend_id);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long firstId, Long secondId);

}
