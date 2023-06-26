package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    CommentRepository repository;

    @Test
    void saveTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        User author = new User(1L, "Petya", "Pupok21rus@yandex.ru");
        Item item = new Item(1L, "name", "discription", true, 1L, 1L);
        Mockito.when(repository.save(Mockito.any(Comment.class))).thenReturn(new Comment(1L, "tz so hard", item, author, localDateTime));

        CommentServiceImpl commentService = new CommentServiceImpl(repository);

        Comment comment = commentService.save(new Comment());

        assertEquals("Comment(id=1, text=tz so hard, item=Item(id=1, name=name, description=discription, available=true, owner=1, requestId=1), author=User(id=1, name=Petya, email=Pupok21rus@yandex.ru), created=" + localDateTime + ")", comment.toString());
    }

    @Test
    void getAllByItemIdTest() {
        List<Comment> comments = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        User author = new User(1L, "Petya", "Pupok21rus@yandex.ru");
        Item item = new Item(1L, "name", "discription", true, 1L, 1L);
        comments.add(new Comment(1L, "tz so hard", item, author, localDateTime));
        Mockito.when(repository.findAllByItemId(Mockito.anyLong())).thenReturn(comments);

        CommentServiceImpl commentService = new CommentServiceImpl(repository);

        List<CommentDto> commentDtos = commentService.getAllByItemId(123L);

        assertEquals("[CommentDto(id=1, text=tz so hard, authorName=Petya, created=" + localDateTime + ")]", commentDtos.toString());
    }
}