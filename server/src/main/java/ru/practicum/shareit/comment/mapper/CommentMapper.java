package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment mapToModel(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
