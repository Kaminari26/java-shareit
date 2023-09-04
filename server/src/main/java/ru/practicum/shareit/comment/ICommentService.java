package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface ICommentService {
    Comment save(Comment comment);

    List<CommentDto> getAllByItemId(Long id);
}
