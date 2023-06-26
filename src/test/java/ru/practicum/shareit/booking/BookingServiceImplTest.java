package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.IItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.IUserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository repository;
    @Mock
    IItemService itemService;
    @Mock
    IUserService userService;

    @Test
    void addBookingFailOwnerTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(1L, "Pupkin", "PupkinDestroyer@gmail.com"));
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(new ItemDto(1L, "GameBoy", "help me", true, 1L, 1L));
        Mockito.when(itemService.getItemDtoForBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDtoForBooking(1L, "game", "videogame", true, 1L, null, null, null));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> bookingService.add(new BookingDto(null, localDateTimeStart, localDateTimeFinish, 1L, 1L, BookingStatusEnum.WAITING), 1L));
        Assertions.assertEquals("Нельзя забронировать свою же вещь", exception.getMessage());
    }

    @Test
    void addBookingFailTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(3L, "Pupkin", "PupkinDestroyer@gmail.com"));
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(new ItemDto(1L, "GameBoy", "help me", true, 2L, 1L));
        Mockito.when(itemService.getItemDtoForBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDtoForBooking(1L, "game", "videogame", true, 1L, null, null, null));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class, () -> bookingService.add(new BookingDto(null, localDateTimeStart, localDateTimeFinish, 1L, 1L, BookingStatusEnum.WAITING), 3L));
        Assertions.assertEquals("Предмет недоступен", exception.getMessage());
    }

    @Test
    void addBookingFailStartTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now();
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(3L, "Pupkin", "PupkinDestroyer@gmail.com"));
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(new ItemDto(1L, "GameBoy", "help me", true, 2L, 1L));
        Mockito.when(itemService.getItemDtoForBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDtoForBooking(1L, "game", "videogame", true, 1L, null, null, null));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class, () -> bookingService.add(new BookingDto(null, localDateTimeFinish, localDateTimeStart, 1L, 1L, BookingStatusEnum.WAITING), 3L));
        Assertions.assertEquals("Предмет недоступен", exception.getMessage());
    }

    @Test
    void addBookingFailTimeTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().minusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(3L, "Pupkin", "PupkinDestroyer@gmail.com"));
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(new ItemDto(1L, "GameBoy", "help me", true, 2L, 1L));
        Mockito.when(itemService.getItemDtoForBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDtoForBooking(1L, "game", "videogame", true, 1L, null, null, null));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class, () -> bookingService.add(new BookingDto(null, localDateTimeStart, localDateTimeFinish, 1L, 1L, BookingStatusEnum.WAITING), 3L));
        Assertions.assertEquals("Предмет недоступен", exception.getMessage());
    }

    @Test
    void addBookingOkTimeTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(new UserDto(3L, "Pupkin", "PupkinDestroyer@gmail.com"));
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(new ItemDto(1L, "GameBoy", "help me", true, 2L, 1L));
        Mockito.when(itemService.getItemDtoForBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new ItemDtoForBooking(1L, "game", "videogame", true, 1L, null, null, null));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        BookingDtoResponse bookingDtoResponse = bookingService.add(new BookingDto(null, localDateTimeStart, localDateTimeFinish, 1L, 1L, BookingStatusEnum.WAITING), 3L);
        Assertions.assertEquals("UserDto(id=3, name=Pupkin, email=PupkinDestroyer@gmail.com)", bookingDtoResponse.getBooker().toString());
        Assertions.assertEquals("ItemDto(id=1, name=GameBoy, description=help me, available=true, owner=2, requestId=1)", bookingDtoResponse.getItem().toString());
        Assertions.assertEquals("WAITING", bookingDtoResponse.getStatus().toString());
    }

    @Test
    void changeStatusFailBookingNotFoundTest() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new EntityNotFoundException("Букинг не найден, не удалось изменить статус"));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookingService.changeStatus(1L, true, 1L));

        assertEquals("Букинг не найден, не удалось изменить статус", exception.getMessage());
    }

    @Test
    void changeStatusFailTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING)));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> bookingService.changeStatus(1L, true, 1L));

        assertEquals("Нет доступа к букингу.", exception.getMessage());
    }

    @Test
    void changeStatusFailStatusTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.REJECTED)));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        final InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> bookingService.changeStatus(1L, true, 2L));

        assertEquals("Ошибка статуса", exception.getMessage());
    }

    @Test
    void changeStatusOkApprovedStatusTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING)));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        BookingDtoResponse bookingDtoResponse = bookingService.changeStatus(1L, true, 2L);

        assertEquals("APPROVED", bookingDtoResponse.getStatus().toString());
    }

    @Test
    void changeStatusOkRejectedStatusTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING)));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        BookingDtoResponse bookingDtoResponse = bookingService.changeStatus(1L, false, 2L);

        assertEquals("REJECTED", bookingDtoResponse.getStatus().toString());
    }

    @Test
    void getBookingNotFound() {
        Mockito.when(repository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Букинг не найден"));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(11L, 12L));

        assertEquals("Букинг не найден", exception.getMessage());

    }

    @Test
    void getBookingNotAccessTest() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING)));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(new ItemDto(1L, "asd", "asds", true, 1L, 1L));

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> bookingService.getBooking(9L, 1L));

        assertEquals("Не удалось получить доступ", exception.getMessage());

    }

    @Test
    void getBookingOk() {
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING)));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        BookingDtoResponse bookingDtoResponse = bookingService.getBooking(1L, 1L);
        assertEquals("BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=ItemDto(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=1), booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)", bookingDtoResponse.toString());
    }

    @Test
    void getAllByBookersUnknownStatusTest() {
        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> bookingService.getAllByBookers(1L, "asd", 1, 1));

        assertEquals("Unknown state: asd", exception.getMessage());
    }

    @Test
    void getAllByBookersAllTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findAllByBookerId(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByBookers(1L, "All", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=null, status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByBookersCURRENTTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByBookers(1L, "CURRENT", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=null, status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByBookersPastTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findByBookerIdAndEndIsBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByBookers(1L, "PAST", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=null, status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByBookersFUTURETest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findByBookerIdAndStartIsAfter(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByBookers(1L, "FUTURE", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=null, status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByBookersWAITINGTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findByBookerIdAndStartIsAfterAndStatusIs(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class), Mockito.any())).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByBookers(1L, "WAITING", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=null, status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByBookersREJECTEDTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findByBookerIdAndStartIsAfterAndStatusIs(Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class), Mockito.any())).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByBookers(1L, "REJECTED", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=null, status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByOwnerUnknownStatusTest() {

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> bookingService.getAllByOwner(1L, "asd", 1, 1));

        assertEquals("Unknown state: asd", exception.getMessage());

    }

    @Test
    void getAllByOwnerALLTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(repository.findAllByItemIdIn(Mockito.anyList(), Mockito.any(Pageable.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByOwner(1L, "All", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByOwnerCURRENTTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(repository.findByItemIdInAndStartIsBeforeAndEndIsAfter(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByOwner(1L, "CURRENT", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByOwnerPASTTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(repository.findByItemIdInAndEndIsBefore(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByOwner(1L, "PAST", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByOwnerFUTURETest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(repository.findByItemIdInAndStartIsAfter(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByOwner(1L, "FUTURE", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByOwnerWAITINGTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(repository.findByItemIdInAndStartIsAfterAndStatusIs(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class), Mockito.any())).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByOwner(1L, "WAITING", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByOwnerREJECTEDTest() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(userService.get(Mockito.anyLong())).thenReturn(bookerDto);
        Mockito.when(repository.findByItemIdInAndStartIsAfterAndStatusIs(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Sort.class), Mockito.any())).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<BookingDtoResponse> bookingList = bookingService.getAllByOwner(1L, "REJECTED", 1, 1);

        assertEquals("[BookingDtoResponse(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=null, booker=UserDto(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());
    }

    @Test
    void getAllByItemId() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findAllByItemId(Mockito.anyLong())).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<Booking> bookingList = bookingService.getAllByItemId(23L);

        assertEquals("[Booking(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=Item(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=1), booker=User(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());

    }

    @Test
    void getAllByItemIdIn() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findAllByItemIdIn(Mockito.anyList())).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<Booking> bookingList = bookingService.getAllByItemIdIn(new ArrayList<>());

        assertEquals("[Booking(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=Item(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=1), booker=User(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());

    }

    @Test
    void getAllByItemIdAndTime() {
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1L);
        LocalDateTime localDateTimeFinish = LocalDateTime.now().plusDays(2L);
        User booker = new User(1L, "pepsik", "qwe@yandex.ru");
        UserDto bookerDto = new UserDto(1L, "pepsik", "qwe@yandex.ru");

        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        bookings.add(new Booking(1L, localDateTimeStart, localDateTimeFinish, item, booker, BookingStatusEnum.WAITING));
        Mockito.when(repository.findByItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class))).thenReturn(bookings);

        BookingServiceImpl bookingService = new BookingServiceImpl(repository, itemService, userService);

        List<Booking> bookingList = bookingService.getAllByItemIdAndTime(1L, LocalDateTime.now());

        assertEquals("[Booking(id=1, start=" + localDateTimeStart + ", end=" + localDateTimeFinish + ", item=Item(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=1), booker=User(id=1, name=pepsik, email=qwe@yandex.ru), status=WAITING)]", bookingList.toString());


    }
}