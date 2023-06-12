package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.IItemService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.user.IUserService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RestController()
public class BookingServiceImpl implements IBookingService {
    private final BookingRepository repository;
     private final IItemService itemService;
    private final IUserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository repository,
                              IItemService itemService,
                              IUserService userService) {
        this.repository = repository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public BookingDtoResponse add(BookingDto bookingDto, Long owner) {
        ItemDto itemDto = itemService.get(bookingDto.getItemId());
        UserDto userDto = userService.get(owner);
        ItemDtoForBooking itemDtoForBooking = itemService.getItemDtoForBooking(itemDto.getId());
        if (itemDtoForBooking.getOwner().equals(owner)) {
            throw new UserNotFoundException("Нельзя забронировать свою же вещь");
        }
        if (itemDto.getAvailable() && bookingDto.getStart().isBefore(bookingDto.getEnd()) && bookingDto.getStart().isAfter(LocalDateTime.now())) {

            Booking booking = BookingMapper.toBookingDto(bookingDto);
            booking.setBooker(owner);
            booking.setStatus(BookingStatusEnum.WAITING);
            repository.save(booking);
            return BookingMapper.toBookingDto(booking, userDto, itemDto);
        }
        throw new ItemNotAvailableException("Предмет недоступен");
    }

    @Override
    public BookingDtoResponse changeStatus(Long id, Boolean approved, Long userId) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Букинг не найден, не удалось изменить статус"));
        ItemDto itemDto = itemService.get(booking.getItemId());
        UserDto userDto = userService.get(booking.getBooker());
        if (userDto.getId().equals(userId)) {
            throw new UserNotFoundException("Нет доступа к букингу.");

        }
        if (!booking.getStatus().equals(BookingStatusEnum.WAITING)) {
            throw new InvalidStatusException("Ошибка статуса");
        }
        if (approved) {
            booking.setStatus(BookingStatusEnum.APPROVED);
        } else {
            booking.setStatus(BookingStatusEnum.REJECTED);
        }
        repository.save(booking);
        return BookingMapper.toBookingDto(booking, userDto, itemDto);

    }

    @Override
    public BookingDtoResponse getBooking(Long userId, Long bookingId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() ->
                new UserNotFoundException("Букинг не найден"));
        ItemDto itemDto = itemService.get(booking.getItemId());

        UserDto userDto = userService.get(booking.getBooker());
        if (!booking.getBooker().equals(userId) && !itemService.getItemDtoForBooking(itemDto.getId()).getOwner().equals(userId)) {
            throw new UserNotFoundException("Не удалось получить доступ");
        }
        return BookingMapper.toBookingDto(booking, userDto, itemDto);
    }

    @Override
    public List<BookingDtoResponse> getAllByBookers(Long userId, String state) {
        UserDto userDto = userService.get(userId);
        LocalDateTime dateNow = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        State bookingState = State.stringToState(state)
                .orElseThrow(() -> new InvalidStatusException("Unknown state: " + state));
        List<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = repository.findAllByBooker(userId, sort);
                break;
            case CURRENT:
                bookings = repository.findByBookerAndStartIsBeforeAndEndIsAfter(userId, dateNow, dateNow, sort);
                break;
            case PAST:
                bookings = repository.findByBookerAndEndIsBefore(userId, dateNow, sort);
                break;
            case FUTURE:
                bookings = repository.findByBookerAndStartIsAfter(userId, dateNow, sort);
                break;
            case WAITING:
                bookings = repository.findByBookerAndStartIsAfterAndStatusIs(userId, dateNow, sort, BookingStatusEnum.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByBookerAndStartIsAfterAndStatusIs(userId, dateNow, sort, BookingStatusEnum.REJECTED);
                break;
            default:
                return List.of();
        }
        List<BookingDtoResponse> bookingDtoResponse = new ArrayList<>();
        for (Booking bok : bookings) {
            bookingDtoResponse.add(BookingMapper.toBookingDto(bok, userService.get(bok.getBooker()), itemService.get(bok.getItemId())));
        }
        return bookingDtoResponse;
    }

    @Override
    public List<BookingDtoResponse> getAllByOwner(Long ownerId, String state) {
        UserDto userDto = userService.get(ownerId);
        LocalDateTime dateNow = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        State bookingState = State.stringToState(state)
                .orElseThrow(() -> new InvalidStatusException("Unknown state: " + state));
        List<Long> itemIdList = itemService.getItems(userDto.getId())
                .stream()
                .map(ItemDto::getId)
                .collect(Collectors.toList());

        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = repository.findAllByItemIdIn(itemIdList, sort);
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartIsBeforeAndEndIsAfter(itemIdList, dateNow, dateNow, sort);
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndIsBefore(itemIdList, dateNow, sort);
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartIsAfter(itemIdList, dateNow, sort);
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, dateNow, sort, BookingStatusEnum.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, dateNow, sort, BookingStatusEnum.REJECTED);
                break;
            default:
                return List.of();
        }
        List<BookingDtoResponse> bookingDtoResponse = new ArrayList<>();
        for (Booking bok : bookings) {
            bookingDtoResponse.add(BookingMapper.toBookingDto(bok, userService.get(bok.getBooker()), itemService.get(bok.getItemId())));
        }
        return bookingDtoResponse;
    }

    @Override
    public List<Booking> getAllByItemId(Long id) {
        return repository.findAllByItemId(id);
    }

    @Override
    public List<Booking> getAllByItemIdIn(List<Long> itemIds) {
        return repository.findAllByItemIdIn(itemIds);
    }

    @Override
    public List<Booking> getAllByItemIdAndTime(Long itemId, LocalDateTime created) {
        return repository.findByItemIdAndEndIsBefore(itemId, created);
    }
}

