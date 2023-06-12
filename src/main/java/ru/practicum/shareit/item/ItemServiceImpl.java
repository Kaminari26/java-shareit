package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusEnum;
import ru.practicum.shareit.booking.dto.IBookingService;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.IUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Primary
public class ItemServiceImpl implements IItemService {
    private final ItemRepository repository;
    private final IUserService userService;

   // private final IBookingService bookingService;

    @Autowired
    public ItemServiceImpl(IUserService userService, ItemRepository repository) {
        this.userService = userService;
        this.repository = repository;
     //   this.bookingService = bookingService;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toDtoItem(itemDto, userId);
        if (userService.get(item.getOwner()) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item itemUp = ItemMapper.toDtoItem(itemDto, userId);
        Item item = repository.getReferenceById(itemId);
        if (item.getOwner() != itemUp.getOwner()) {
            throw new NullPointerException();
        }
        if (itemUp.getAvailable() != null) {
            item.setAvailable(itemUp.getAvailable());
        }
        if (itemUp.getDescription() != null) {
            item.setDescription(itemUp.getDescription());
        }
        if (itemUp.getName() != null) {
            item.setName(itemUp.getName());
        }
        repository.save(item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto get(Long itemId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new UserNotFoundException("Предмет не найден"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleted(Long itemId) {

    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return repository.findAllByOwner(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.findAllByNameOrDescriptionContainingIgnoreCase(text, text).stream().filter(item ->
                item.getAvailable() == true).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDtoForBooking getItemDtoForBooking (Long id) {
        ItemDtoForBooking itemDtoForBooking = ItemMapper.toDtoItemForBooking(repository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Предмет не найден")));

//        List<Booking> bookings = bookingService.getAllByItemId(id);
//        LocalDateTime dateTime = LocalDateTime.now();
//        bookings
//                .stream()
//                .filter(booking -> Objects.equals(booking.getItemId(), itemDtoForBooking.getId()))
//                .sorted(Comparator.comparing(Booking::getEnd).reversed())
//                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
//                .filter(booking -> booking.getStart().isBefore(dateTime))
//                .limit(1)
//                .findAny()
//                .ifPresent(booking -> itemDtoForBooking.setLastBooking(BookingDto.builder()
//                        .id(booking.getId())
//                        .booker(booking.getBooker())
//                        .build()));
//        bookings
//                .stream()
//                .filter(booking -> Objects.equals(booking.getItemId(), itemDtoForBooking.getId()))
//                .sorted(Comparator.comparing(Booking::getStart))
//                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
//                .filter(booking -> booking.getStart().isAfter(dateTime))
//                .limit(1)
//                .findAny()
//                .ifPresent(booking -> itemDtoForBooking.setNextBooking(BookingDto.builder()
//                        .id(booking.getId())
//                        .booker(booking.getBooker())
//                        .build()));


        return itemDtoForBooking;
    }
}
