package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private Long id = 1L;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (isLoginValid(user.getLogin()) && !userStorage.get().containsKey(user.getId())) {
            if (user.getId() == null) {
                user.setId(id++);
            }
            user = nameVerification(user);
            log.info("Добавлен пользователь {}", user);
            return userStorage.add(user);
        } else {
            throw new ObjectAlreadyExistException("Пользователь с id " + user.getId() + " уже был добавлен в список");
        }
    }

    public User updateUser(User user) {
        if (isLoginValid(user.getLogin()) && userStorage.get().containsKey(user.getId())) {
            user = nameVerification(user);
            log.info("Пользователь обновлен {}", user);
            return userStorage.update(user);
        } else {
            throw new ObjectNotFoundException("Пользователя с id " + user.getId() + " нет в списке");
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.get().values());
    }

    public User getUserById(Long id) {
        if (userStorage.get().containsKey(id)) {
            return userStorage.get().get(id);
        } else {
            throw new ObjectNotFoundException("Пользователя с id " + id + " нет в списке");
        }
    }

    public User addFriend(Long userId, Long friendId) {
        if (userStorage.get().containsKey(userId)) {
            if (userStorage.get().containsKey(friendId)) {
                getUserById(userId).getUserFriends().add(friendId);
                getUserById(friendId).getUserFriends().add(userId);
                log.info("Пользователи {} и {} стали друзьями", getUserById(userId), getUserById(friendId));
                return getUserById(userId);
            } else {
                throw new ObjectNotFoundException("Пользователя " + getUserById(friendId) + " нет в списке");
            }
        } else {
            throw new ObjectNotFoundException("Пользователя " + getUserById(userId) + " нет в списке");
        }
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (userStorage.get().containsKey(userId)) {
            if (userStorage.get().containsKey(friendId)) {
                getUserById(userId).getUserFriends().remove(friendId);
                getUserById(friendId).getUserFriends().remove(userId);
                log.info("Пользователи {} и {} перестали быть друзьями", getUserById(userId), getUserById(friendId));
                return getUserById(userId);
            } else {
                throw new ObjectNotFoundException("Пользователя " + getUserById(friendId) + " нет в списке");
            }
        } else {
            throw new ObjectNotFoundException("Пользователя " + getUserById(userId) + " нет в списке");
        }
    }

    public List<User> getUserFriends(Long id) {
        if (userStorage.get().containsKey(id)) {
            Set<Long> friendsIds = getUserById(id).getUserFriends();
            List<User> friends = new ArrayList<>();
            for (Long friendsId : friendsIds) {
                for (User user : userStorage.get().values()) {
                    if (friendsId.equals(user.getId())) {
                        friends.add(user);
                        break;
                    }
                }
            }
            return friends;
        }
        throw new ObjectNotFoundException("Пользователя " + getUserById(id) + " нет в списке");
    }

    public List<User> getCommonFriends(Long firstId, Long secondId) {
        List<User> commonFriends = new ArrayList<>(getUserFriends(firstId));
        commonFriends.retainAll(getUserFriends(secondId));
        return commonFriends;
    }

    private boolean isLoginValid(String login) {
        if (login.contains(" ")) {
            throw new ValidationException("Логин " + login + " не должен содержать пробелы");
        }
        return true;
    }

    private User nameVerification(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}