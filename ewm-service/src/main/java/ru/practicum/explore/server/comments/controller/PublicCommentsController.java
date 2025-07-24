package ru.practicum.explore.server.comments.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.server.comments.controller.params.DeleteCommentParams;
import ru.practicum.explore.server.comments.controller.params.GetPublicCommentsParams;
import ru.practicum.explore.server.comments.controller.params.GetUserCommentsParams;
import ru.practicum.explore.server.comments.controller.params.UpdateCommentParams;
import ru.practicum.explore.server.comments.dto.FullCommentResponseDto;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;
import ru.practicum.explore.server.comments.dto.UpdateCommentDto;
import ru.practicum.explore.server.comments.service.CommentsService;
import ru.practicum.explore.server.exception.ValidationException;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class PublicCommentsController {

    private final CommentsService commentsService;

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public Collection<PublicCommentResponseDto> getEventComments(

            @PathVariable
            Long eventId,

            @Valid
            @RequestParam(name = "from", defaultValue = "0", required = false)
            Integer from,

            @Valid
            @RequestParam(name = "size", defaultValue = "10", required = false)
            Integer size) {


        log.info("Запрос публичных событий: eventId={}, from={}, size={}", eventId, from, size);

        if (size < 1) {
            throw new ValidationException("Size parameter must be >=1");
        }

        if (from < 0) {
            throw new ValidationException("From parameter must be >=0");
        }


        GetPublicCommentsParams params = new GetPublicCommentsParams();
        params.setEventId(eventId);
        params.setFrom(from);
        params.setSize(size);

        return commentsService.getPublicEventComments(params);
    }

    @PatchMapping("/comments/{commentId}")
    public FullCommentResponseDto updateComment(@RequestHeader("X-EWM-User-Id") Long userId,
                                                @PathVariable Long commentId,
                                                @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        UpdateCommentParams params = new UpdateCommentParams();
        params.setUserId(userId);
        params.setCommentId(commentId);
        params.setDto(updateCommentDto);
        return commentsService.updateComment(params);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@RequestHeader("X-EWM-User-Id") Long userId,
                              @PathVariable Long commentId) {
        DeleteCommentParams params = new DeleteCommentParams();
        params.setUserId(userId);
        params.setCommentId(commentId);
        commentsService.deleteComment(params);
    }

    @GetMapping("/users/comments")
    public List<FullCommentResponseDto> getUserComments(
            @RequestHeader("X-EWM-User-Id") Long userId,
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        GetUserCommentsParams params = new GetUserCommentsParams();
        params.setUserId(userId);
        params.setFilter(filter);
        params.setFrom(from);
        params.setSize(size);
        return commentsService.getUserComments(params);
    }
}
