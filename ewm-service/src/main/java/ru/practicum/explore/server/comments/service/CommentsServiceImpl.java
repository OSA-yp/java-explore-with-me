package ru.practicum.explore.server.comments.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.server.comments.controller.GetPublicCommentsParams;
import ru.practicum.explore.server.comments.dal.CommentMapper;
import ru.practicum.explore.server.comments.dal.CommentsRepository;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;
import ru.practicum.explore.server.comments.model.Comment;
import ru.practicum.explore.server.comments.model.CommentStatus;
import ru.practicum.explore.server.event.service.PublicEventService;
import ru.practicum.explore.server.exception.NotFoundException;

import java.util.Collection;

@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;
    private final PublicEventService publicEventService;

    public Collection<PublicCommentResponseDto> getPublicEventComments (GetPublicCommentsParams params){

        // проверка на существование события на стороне сервиса событий
        publicEventService.getPublicEventById(params.eventId);

//        Pageable pageable = PageRequest.of(params.getFrom(), params.getSize());
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Page<Comment> comments = commentsRepository.findByEventAndStatusOrderByPublishedDesc(
                params.eventId,
                CommentStatus.PUBLISHED,
                pageable);

        return  comments.stream()
                .map(CommentMapper::toPublicCommentResponseDto)
                .toList();
    }
}
