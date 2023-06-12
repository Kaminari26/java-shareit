package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatusEnum;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.IUserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemServiceImpl implements IItemService {
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final IUserService userService;

    private final CommentRepository commentRepository;

//    @Autowired
//    public ItemServiceImpl(IUserService userService, ItemRepository repository) {
//        this.userService = userService;
//        this.repository = repository;
//    }

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
    public List<ItemDtoForBooking> getItems(Long userId) {
        List<ItemDtoForBooking> list = repository.findAllByOwner(userId).stream().map(ItemMapper::toDtoItemForBooking).collect(Collectors.toList());
        List<Long> itemsId = list.stream()
                .map(ItemDtoForBooking::getId)
                .collect(Collectors.toList());
        List<Booking> bookingList = bookingRepository.findAllByItemIdIn(itemsId);

        return list.stream().map(itemDtoForBooking -> setLastAndNext(bookingList, itemDtoForBooking)).collect(Collectors.toList());

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
    public ItemDtoForBooking getItemDtoForBooking(Long id, Long ownerId) {
        ItemDtoForBooking itemDtoForBooking = ItemMapper.toDtoItemForBooking(repository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Предмет не найден")));
        List<Booking> bookings = bookingRepository.findAllByItemId(id);
        LocalDateTime dateTime = LocalDateTime.now();
        bookings
                .stream()
                .sorted(Comparator.comparing(Booking::getEnd))
                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
                .filter(booking -> booking.getStart().isBefore(dateTime))
                .filter(ItemDtoForBooking -> itemDtoForBooking.getOwner().equals(ownerId))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemDtoForBooking.setLastBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker())
                        .build()));
        bookings
                .stream()
                .filter(booking -> Objects.equals(booking.getItemId(), itemDtoForBooking.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
                .filter(booking -> booking.getStart().isAfter(dateTime))
                .filter(ItemDtoForBooking -> itemDtoForBooking.getOwner().equals(ownerId))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemDtoForBooking.setNextBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker())
                        .build()));
        itemDtoForBooking.setComments(commentRepository.findAllByItemId(id).stream().map(CommentMapper::mapToDto)
                .collect(Collectors.toList()));
        return itemDtoForBooking;
    }

    public CommentDto addComment(CommentDto request, Long userId, Long itemId) {
        Comment comment = CommentMapper.mapToModel(request);

        User author = UserMapper.toDtoUser(userService.get(userId));
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("предмет не найден"));

        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBefore(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker(), userId))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new InvalidStatusException("ошибка");
        }

        comment.setAuthor(author);
        comment.setItem(item);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.mapToDto(savedComment);
    }

    private ItemDtoForBooking setLastAndNext(List<Booking> bookings, ItemDtoForBooking itemDtoForBooking) {
        LocalDateTime dateTime = LocalDateTime.now();

        bookings
                .stream()
                .sorted(Comparator.comparing(Booking::getEnd))
                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
                .filter(booking -> booking.getStart().isBefore(dateTime))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemDtoForBooking.setLastBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker())
                        .build()));
        bookings
                .stream()
                .filter(booking -> Objects.equals(booking.getItemId(), itemDtoForBooking.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
                .filter(booking -> booking.getStart().isAfter(dateTime))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemDtoForBooking.setNextBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker())
                        .build()));
        itemDtoForBooking.setComments(commentRepository.findAllByItemId(itemDtoForBooking.getId()).stream().map(CommentMapper::mapToDto)
                .collect(Collectors.toList()));
        return itemDtoForBooking;
    }
}
