package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.IUserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository repository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    IUserService userService;
    @Mock
    CommentRepository commentRepository;

    @Test
    void addItemOkTest() {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(null, null, null));
        Mockito.when(repository.save(Mockito.any(Item.class))).thenReturn(new Item(1L, "GameBoy", "help me", true, 1L, 2L));


        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDto itemDto = itemService.add(new ItemDto(null, "GameBoy", "help me", true, null, 2L), 1L);


        Assertions.assertEquals("GameBoy", itemDto.getName());
        Assertions.assertEquals("help me", itemDto.getDescription());
        Assertions.assertEquals(true, itemDto.getAvailable());
        Assertions.assertEquals(1L, itemDto.getOwner());
        Assertions.assertEquals(2L, itemDto.getRequestId());
        Assertions.assertEquals(1L, itemDto.getId());
    }

    @Test
    void addItemUserNotFoundTest() {
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(null);

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> itemService.add(new ItemDto(null, "GameBoy", "help me", true, null, 2L), 1L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void updateItemOkTest() {
        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(new Item(1L, "GameBoy", "help me", true, 1L, 2L));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDto itemDto = itemService.update(new ItemDto(null, "GameGirl", "save us", false, 1L, 2L), 1L, 1L);
        Assertions.assertEquals("GameGirl", itemDto.getName());
        Assertions.assertEquals("save us", itemDto.getDescription());
        Assertions.assertEquals(false, itemDto.getAvailable());
        Assertions.assertEquals(1L, itemDto.getOwner());
        Assertions.assertEquals(2L, itemDto.getRequestId());
        Assertions.assertEquals(1L, itemDto.getId());
    }

    @Test
    void updateItemExceptionTest() {
        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(new Item(1L, "GameBoy", "help me", true, 1L, 2L));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        final NullPointerException exception = Assertions.assertThrows(NullPointerException.class, () -> itemService.update(new ItemDto(null, "GameGirl", "save us", true, 1L, 2L), 1L, 99L));
        Assertions.assertEquals(null, exception.getMessage());

    }

    @Test
    void updateItemOkNameNullTest() {
        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(new Item(1L, "GameBoy", "help me", true, 1L, 2L));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDto itemDto = itemService.update(new ItemDto(null, null, "save us", false, 1L, 2L), 1L, 1L);
        Assertions.assertEquals("GameBoy", itemDto.getName());
        Assertions.assertEquals("save us", itemDto.getDescription());
        Assertions.assertEquals(false, itemDto.getAvailable());
        Assertions.assertEquals(1L, itemDto.getOwner());
        Assertions.assertEquals(2L, itemDto.getRequestId());
        Assertions.assertEquals(1L, itemDto.getId());
    }

    @Test
    void updateItemOkDiscriptionNullTest() {
        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(new Item(1L, "GameBoy", "help me", true, 1L, 2L));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDto itemDto = itemService.update(new ItemDto(null, "GameGirl", null, false, 1L, 2L), 1L, 1L);
        Assertions.assertEquals("GameGirl", itemDto.getName());
        Assertions.assertEquals("help me", itemDto.getDescription());
        Assertions.assertEquals(false, itemDto.getAvailable());
        Assertions.assertEquals(1L, itemDto.getOwner());
        Assertions.assertEquals(2L, itemDto.getRequestId());
        Assertions.assertEquals(1L, itemDto.getId());
    }

    @Test
    void updateItemOkAvailableNullTest() {
        Mockito.when(repository.getReferenceById(Mockito.anyLong())).thenReturn(new Item(1L, "GameBoy", "help me", true, 1L, 2L));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDto itemDto = itemService.update(new ItemDto(null, "GameGirl", "save us", null, 1L, 2L), 1L, 1L);
        Assertions.assertEquals("GameGirl", itemDto.getName());
        Assertions.assertEquals("save us", itemDto.getDescription());
        Assertions.assertEquals(true, itemDto.getAvailable());
        Assertions.assertEquals(1L, itemDto.getOwner());
        Assertions.assertEquals(2L, itemDto.getRequestId());
        Assertions.assertEquals(1L, itemDto.getId());
    }


    @Test
    void getItemOkTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item(1L, "GameBoy", "help me", true, 1L, 2L)));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDto itemDto = itemService.get(1L);

        Assertions.assertEquals("GameBoy", itemDto.getName());
        Assertions.assertEquals("help me", itemDto.getDescription());
        Assertions.assertEquals(true, itemDto.getAvailable());
        Assertions.assertEquals(1L, itemDto.getOwner());
        Assertions.assertEquals(2L, itemDto.getRequestId());
        Assertions.assertEquals(1L, itemDto.getId());
    }

    @Test
    void getItemThrowTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Предмет не найден"));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> itemService.get(555L));

        Assertions.assertEquals("Предмет не найден", exception.getMessage());
    }

    @Test
    void deleted() {
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        itemService.deleted(2L);
        Mockito.verify(repository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void getItemsTest() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "GameBoy", "help me", true, 1L, 2L));
        list.add(new Item(2L, "GameGirl", "help plz", true, 1L, 3L));
        Mockito.when(repository.findAllByOwner(Mockito.anyLong())).thenReturn(list);
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        List<ItemDtoForBooking> targetList = itemService.getItems(123L);
        Assertions.assertEquals("[ItemDtoForBooking(id=1, name=GameBoy, description=help me, available=true," +
                        " owner=1, lastBooking=null, nextBooking=null, comments=null), ItemDtoForBooking(id=2, name=GameGirl," +
                        " description=help plz, available=true, owner=1, lastBooking=null, nextBooking=null, comments=null)]",
                targetList.toString());
    }

    @Test
    void searchItemNullTextTest() {
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        List<ItemDto> list = itemService.searchItem(null);
        Assertions.assertEquals("[]", list.toString());
    }

    @Test
    void searchItemNullBlankTest() {
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        List<ItemDto> list = itemService.searchItem("");
        Assertions.assertEquals("[]", list.toString());
    }

    @Test
    void searchItemOkTest() {
        List<Item> itemDtos = new ArrayList<>();
        itemDtos.add(new Item(1L, "GameBoy", "help me", true, 1L, 2L));
        Mockito.when(repository.findAllByNameOrDescriptionContainingIgnoreCase(Mockito.anyString(), Mockito.anyString())).thenReturn(itemDtos);
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        List<ItemDto> list = itemService.searchItem("help");
        Assertions.assertEquals("[ItemDto(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=2)]", list.toString());
    }

    @Test
    void getItemDtoForBookingOkTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item(1L, "GameBoy", "help me", true, 1L, 2L)));
        Item item1 = new Item(1L, "GameBoy", "help me", true, 1L, 2L);
        User user = new User(1L, "Vasya", "Pupkin@yandex.ru");
        List<Booking> bookings = new ArrayList<>();
        bookings.add((new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(3L), item1, user, BookingStatusEnum.WAITING)));
        Mockito.when(bookingRepository.findAllByItemId(Mockito.anyLong())).thenReturn(bookings);
        bookingRepository.findAllByItemId(3L);
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDtoForBooking itemDtoForBooking = itemService.getItemDtoForBooking(1L, 2L);
        Assertions.assertEquals("ItemDtoForBooking(id=1, name=GameBoy, description=help me, available=true, owner=1, lastBooking=null, nextBooking=null, comments=[])", itemDtoForBooking.toString());
    }

    @Test
    void getItemDtoForBookingAddLastBookingTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item(1L, "GameBoy", "help me", true, 1L, 1L)));

        Item item1 = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        User user = new User(1L, "Vasya", "Pupkin@yandex.ru");
        List<Booking> bookings = new ArrayList<>();
        bookings.add((new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(3L), item1, user, BookingStatusEnum.APPROVED)));
        Mockito.when(bookingRepository.findAllByItemId(Mockito.anyLong())).thenReturn(bookings);
        bookingRepository.findAllByItemId(3L);
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDtoForBooking itemDtoForBooking = itemService.getItemDtoForBooking(1L, 1L);

        Assertions.assertEquals("ItemDtoForBooking(id=1, name=GameBoy, description=help me, available=true, owner=1, lastBooking=BookingDto(id=1, start=null, end=null, itemId=null, bookerId=1, status=null), nextBooking=null, comments=[])", itemDtoForBooking.toString());
    }

    @Test
    void getItemDtoForBookingAddLastAndNextBookingTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item(1L, "GameBoy", "help me", true, 1L, 1L)));

        Item item1 = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        User user = new User(1L, "Vasya", "Pupkin@yandex.ru");
        List<Booking> bookings = new ArrayList<>();
        bookings.add((new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(3L), item1, user, BookingStatusEnum.APPROVED)));
        bookings.add((new Booking(2L, LocalDateTime.now().plusDays(4L), LocalDateTime.now().plusDays(12L), item1, user, BookingStatusEnum.APPROVED)));
        Mockito.when(bookingRepository.findAllByItemId(Mockito.anyLong())).thenReturn(bookings);
        bookingRepository.findAllByItemId(3L);
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        ItemDtoForBooking itemDtoForBooking = itemService.getItemDtoForBooking(1L, 1L);

        Assertions.assertEquals("ItemDtoForBooking(id=1, name=GameBoy, description=help me, available=true, owner=1, lastBooking=BookingDto(id=1, start=null, end=null, itemId=null, bookerId=1, status=null), nextBooking=BookingDto(id=2, start=null, end=null, itemId=null, bookerId=1, status=null), comments=[])", itemDtoForBooking.toString());
    }

    @Test
    void getItemDtoForBookingFailTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Предмет не найден"));
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> itemService.getItemDtoForBooking(555L, 333L));

        Assertions.assertEquals("Предмет не найден", exception.getMessage());
    }

    @Test
    void addCommentОк() {
        List<Booking> bookings = new ArrayList<>();
        Item item1 = new Item(1L, "GameBoy", "help me", true, 1L, 2L);
        User user = new User(1L, "Vasya", "Pupkin@yandex.ru");
        LocalDateTime localDateTime = LocalDateTime.now();
        bookings.add((new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(3L), item1, user, BookingStatusEnum.WAITING)));
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item(1L, "GameBoy", "help me", true, 1L, 2L)));
        Mockito.when(bookingRepository.findByItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(bookings);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(1L, "name", "Op@yandex.ru"));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(new Comment(1L, "text", item1, user, localDateTime));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        CommentDto commentDto = itemService.addComment(new CommentDto(null, "Textxt", "vasya", localDateTime), 1L, 2L);

        Assertions.assertEquals("text", commentDto.getText());
        Assertions.assertEquals("Vasya", commentDto.getAuthorName());
    }

    @Test
    void addCommentNullText() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        final InvalidStatusException exception = Assertions.assertThrows(InvalidStatusException.class, () -> itemService.addComment(new CommentDto(null, "", "vasya", localDateTime), 1L, 2L));

        Assertions.assertEquals("Невозможно оставить пустой коммент", exception.getMessage());
    }

    @Test
    void addCommentItemNotFoundTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Предмет не найден"));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(1L, "name", "Op@yandex.ru"));

        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);
        final UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> itemService.addComment(new CommentDto(null, "asdasd", "vasya", localDateTime), 1L, 2L));

        Assertions.assertEquals("Предмет не найден", exception.getMessage());
    }

    @Test
    void addCommentErrorTest() {
        List<Booking> bookings = new ArrayList<>();
        Item item1 = new Item(1L, "GameBoy", "help me", true, 1L, 2L);
        User user = new User(1L, "Vasya", "Pupkin@yandex.ru");
        LocalDateTime localDateTime = LocalDateTime.now();

        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(2L, "name", "Op@yandex.ru"));
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item(1L, "GameBoy", "help me", true, 1L, 2L)));
        Mockito.when(bookingRepository.findByItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(bookings);
        ItemServiceImpl itemService = new ItemServiceImpl(repository, bookingRepository, userService, commentRepository);

        final InvalidStatusException exception = Assertions.assertThrows(InvalidStatusException.class, () -> itemService.addComment(new CommentDto(null, "asdasd", "vasya", localDateTime), 1L, 2L));

        Assertions.assertEquals("Ошибка", exception.getMessage());
    }
}