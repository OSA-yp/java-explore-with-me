package ru.practicum.explore.server.users.service;

import ru.practicum.explore.server.users.dto.UserResponseDto;
import ru.practicum.explore.server.users.model.User;

import java.util.Collection;

public interface UserService {
    UserResponseDto addUser(User user);

    Collection<UserResponseDto> getUsers(Collection<Long> ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}
