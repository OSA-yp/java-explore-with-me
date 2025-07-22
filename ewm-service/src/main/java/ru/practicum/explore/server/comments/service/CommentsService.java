package ru.practicum.explore.server.comments.service;

import ru.practicum.explore.server.comments.controller.params.AddCommentParams;
import ru.practicum.explore.server.comments.controller.params.GetPublicCommentsParams;
import ru.practicum.explore.server.comments.dto.FullCommentResponseDto;
import ru.practicum.explore.server.comments.dto.NewCommentDto;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;

import java.util.Collection;

public interface CommentsService {

    Collection<PublicCommentResponseDto> getPublicEventComments(GetPublicCommentsParams params);

    FullCommentResponseDto addComment(AddCommentParams params);
}
