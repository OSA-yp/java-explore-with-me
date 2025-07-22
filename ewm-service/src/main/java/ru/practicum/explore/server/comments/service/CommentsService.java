package ru.practicum.explore.server.comments.service;

import ru.practicum.explore.server.comments.controller.GetPublicCommentsParams;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;

import java.util.Collection;

public interface CommentsService {

    Collection<PublicCommentResponseDto> getPublicEventComments(GetPublicCommentsParams params);

}
