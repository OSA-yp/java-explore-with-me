package ru.practicum.explore.server.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.server.comments.controller.params.*;
import ru.practicum.explore.server.comments.dal.CommentMapper;
import ru.practicum.explore.server.comments.dal.CommentsRepository;
import ru.practicum.explore.server.comments.dto.FullCommentResponseDto;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;
import ru.practicum.explore.server.comments.model.Comment;
import ru.practicum.explore.server.comments.model.CommentStatus;
import ru.practicum.explore.server.event.dto.EventFullDto;
import ru.practicum.explore.server.event.service.PublicEventService;
import ru.practicum.explore.server.exception.ForbiddenException;
import ru.practicum.explore.server.exception.NotFoundException;
import ru.practicum.explore.server.users.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public FullCommentResponseDto updateComment(UpdateCommentParams params) {
        Comment comment = commentsRepository.findById(params.getCommentId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!comment.getCommentator().equals(params.getUserId())) {
            throw new ForbiddenException("Редактировать можно только свои комментарии");
        }
        comment.setText(params.getDto().getComment());
        comment.setStatus(CommentStatus.NEW);

        return CommentMapper.toFullCommentResponseDto(commentsRepository.save(comment));
    }

    @Override
    public void deleteComment(DeleteCommentParams params) {
        Comment comment = commentsRepository.findById(params.getCommentId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getCommentator().equals(params.getUserId())) {
            throw new ForbiddenException("Удалять можно только свои комментарии");
        }
        commentsRepository.delete(comment);

    }

    @Override
    public List<FullCommentResponseDto> getUserComments(GetUserCommentsParams params) {
        checkUser(params.getUserId());
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        CommentStatus status = parseFilter(params.getFilter());
        List<Comment> comments;
        if (status == null) {
            comments = commentsRepository.findAllByCommentator(params.getUserId(), pageable);
        } else {
            comments = commentsRepository.findAllByCommentatorAndStatus(params.getUserId(), status, pageable);
        }
        return comments.stream()
                .map(CommentMapper::toFullCommentResponseDto)
                .collect(Collectors.toList());
    }

    private CommentStatus parseFilter(String filter) {
        return switch (filter.toUpperCase()) {
            case "NEW" -> CommentStatus.NEW;
            case "PUBLISHED" -> CommentStatus.PUBLISHED;
            case "REJECTED" -> CommentStatus.REJECTED;
            case "ALL" -> null;
            default -> throw new NotFoundException("Некорректный фильтр по статусу: " + filter);
        };
    }

    private void checkUser(Long userId) {

        // проверка на существование пользователя на стороне сервиса пользователей
        userService.checkUser(userId);
    }


    private EventFullDto checkEvent(Long eventId) {

        // проверка на существование события на стороне сервиса событий
        // так как получаются только опубликованные события, то для всех остальных будет 404
        return publicEventService.getPublicEventById(eventId);

    }

    private void checkEvent(Long eventId, Long userId) {

        EventFullDto event = checkEvent(eventId);

        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ForbiddenException("Комментировать можно только чужие события");
        }

    }
}
