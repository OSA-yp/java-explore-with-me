package ru.practicum.explore.server.comments.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.server.comments.controller.params.AddCommentParams;
import ru.practicum.explore.server.comments.dto.FullCommentResponseDto;
import ru.practicum.explore.server.comments.dto.NewCommentDto;
import ru.practicum.explore.server.comments.service.CommentsService;

@Slf4j
@RestController
@AllArgsConstructor
public class PrivateCommentsController {

    private final CommentsService commentsService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public FullCommentResponseDto addComment(
            @Valid
            @RequestBody
            NewCommentDto newCommentDto,

            @PathVariable
            Long eventId,

            @RequestHeader("X-EWM-User-Id")
            Long userId) {

        AddCommentParams params = new AddCommentParams();

        params.setNewCommentDto(newCommentDto);
        params.setEventId(eventId);
        params.setUserId(userId);


        return commentsService.addComment(params);
    }
}
