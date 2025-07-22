package ru.practicum.explore.server.comments.dal;

import ru.practicum.explore.server.comments.dto.FullCommentResponseDto;
import ru.practicum.explore.server.comments.dto.PublicCommentResponseDto;
import ru.practicum.explore.server.comments.model.Comment;

public class CommentMapper {

    public static FullCommentResponseDto toFullCommentResponseDto(Comment comment) {


        FullCommentResponseDto dto = new FullCommentResponseDto();

        dto.setId(comment.getId());
        dto.setCommentator(comment.getCommentator());
        dto.setEvent(comment.getEvent());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setStatus(comment.getStatus());

        if (comment.getPublished() != null) {
            dto.setPublished(comment.getPublished());
        }

        return dto;
    }

    public static PublicCommentResponseDto toPublicCommentResponseDto(Comment comment) {

        PublicCommentResponseDto dto = new PublicCommentResponseDto();

        dto.setId(comment.getId());
        dto.setCommentator(comment.getCommentator());
        dto.setEvent(comment.getEvent());
        dto.setText(comment.getText());
        if (comment.getPublished() != null) {
            dto.setPublished(comment.getPublished());
        }

        return dto;
    }

}
