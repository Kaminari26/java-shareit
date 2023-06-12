package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentServiceImpl implements ICommentService {
    private final CommentRepository repository;

    @Override
    public Comment save(Comment comment) {
        return repository.save(comment);
    }

    @Override
    public List<CommentDto> getAllByItemId(Long id) {
        return repository.findAllByItemId(id).stream().map(CommentMapper::mapToDto).collect(Collectors.toList());
    }
}
