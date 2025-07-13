package ru.practicum.explore.server.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.server.users.dal.UserMapper;
import ru.practicum.explore.server.users.dto.NewUserRequestDto;
import ru.practicum.explore.server.users.dto.UserResponseDto;
import ru.practicum.explore.server.users.service.UserService;
import ru.practicum.explore.server.utils.HitSender;

import java.util.Collection;

@RestController
@AllArgsConstructor
public class UsersController {

    private final UserService userService;
    private final HitSender hitSender;


    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto addUser(
            @Valid
            @RequestBody
            NewUserRequestDto newUser,
            HttpServletRequest request) {

        hitSender.send(request);
        return userService.addUser(UserMapper.toUser(newUser));
    }

    @GetMapping("/admin/users")
    public Collection<UserResponseDto> getUsers(

            @RequestParam(name = "ids", required = false)
            Collection<Long> ids,

            @Valid
            @RequestParam(name = "from", defaultValue = "0", required = false)
            Integer from,

            @RequestParam(name = "size", defaultValue = "10", required = false)
            @Valid
            Integer size,

            HttpServletRequest request) {

        hitSender.send(request);
        return userService.getUsers(ids, from, size);

    }

    @DeleteMapping("/admin/users/{userId}")
    public void deleteUserById(@PathVariable Long userId,
                               HttpServletRequest request) {

        hitSender.send(request);
        userService.deleteUserById(userId);

    }

}
