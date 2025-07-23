package ru.practicum.explore.server.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.server.comments.controller.params.AddCommentParams;
import ru.practicum.explore.server.comments.controller.params.GetPublicCommentsParams;
import ru.practicum.explore.server.comments.dal.CommentMapper;
import ru.practicum.explore.server.comments.dal.CommentsRepository;
import ru.practicum.explore.server.comments.dto.FullCommentResponseDto;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;
import ru.practicum.explore.server.comments.model.Comment;
import ru.practicum.explore.server.comments.model.CommentStatus;
import ru.practicum.explore.server.event.dto.EventFullDto;
import ru.practicum.explore.server.event.service.PublicEventService;
import ru.practicum.explore.server.exception.ForbiddenException;
import ru.practicum.explore.server.users.service.UserService;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final PublicEventService publicEventService;
    private final UserService userService;


    public Collection<PublicCommentResponseDto> getPublicEventComments(GetPublicCommentsParams params) {

        checkEvent(params.eventId);

//        Pageable pageable = PageRequest.of(params.getFrom(), params.getSize());
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Page<Comment> comments = commentsRepository.findByEventAndStatusOrderByPublishedDesc(
                params.eventId,
                CommentStatus.PUBLISHED,
                pageable);

        return comments.stream()
                .map(CommentMapper::toPublicCommentResponseDto)
                .toList();
    }

    @Override
    public FullCommentResponseDto addComment(AddCommentParams params) {

        checkUser(params.getUserId());
        checkEvent(params.getEventId(), params.getUserId());


        Comment comment = CommentMapper.toComment(params);

        Comment newComment = commentsRepository.save(comment);

        log.info("Comment with id={} was created", newComment.getId());

        return CommentMapper.toFullCommentResponseDto(newComment);
    }

    private void checkUser(Long userId) {

        // проверка на существование пользователя на стороне сервиса пользователей
        userService.checkUser(userId);
    }


    private EventFullDto checkEvent(Long eventId) {
        // проверка на существование события на стороне сервиса событий
        return publicEventService.getPublicEventById(eventId);
    }

    private void checkEvent(Long eventId, Long userId) {

        EventFullDto event = checkEvent(eventId);

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("Комментировать можно только чужие события");
        }

    }
}
