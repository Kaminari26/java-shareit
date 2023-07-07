package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.IUserService;
import ru.practicum.shareit.user.mapper.UserMapper;
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

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toDtoItem(itemDto, userId);
        if (userService.get(item.getOwner()) == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Item newItem = repository.save(item);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item itemUp = ItemMapper.toDtoItem(itemDto, userId);
        Item item = repository.getReferenceById(itemId);
        if (!Objects.equals(item.getOwner(), itemUp.getOwner())) {
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
    @Transactional(readOnly = true)
    public ItemDto get(Long itemId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new UserNotFoundException("Предмет не найден"));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void deleted(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    public List<ItemDtoForBooking> getItems(Long userId) {
        List<ItemDtoForBooking> list = repository.findAllByOwner(userId).stream().map(ItemMapper::toDtoItemForBooking).collect(Collectors.toList());
        List<Long> itemsId = list.stream()
                .map(ItemDtoForBooking::getId)
                .collect(Collectors.toList());
        List<Booking> bookingList = bookingRepository.findAllByItemIdIn(itemsId);

        return list.stream().map(itemDtoForBooking -> setLastAndNext(bookingList, itemDtoForBooking, userId)).collect(Collectors.toList());

    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return repository.findAllByNameOrDescriptionContainingIgnoreCase(text, text).stream().filter(Item::getAvailable).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoForBooking getItemDtoForBooking(Long id, Long ownerId) {
        ItemDtoForBooking itemDtoForBooking = ItemMapper.toDtoItemForBooking(repository.findById(id).orElseThrow(() -> new UserNotFoundException("Предмет не найден")));
        List<Booking> bookings = bookingRepository.findAllByItemId(id);
        itemDtoForBooking.setComments(commentRepository.findAllByItemId(id).stream().map(CommentMapper::mapToDto)
                .collect(Collectors.toList()));
        return setLastAndNext(bookings, itemDtoForBooking, ownerId);
    }

    public CommentDto addComment(CommentDto request, Long userId, Long itemId) {
        Comment comment = CommentMapper.mapToModel(request);
        User author = UserMapper.toDtoUser(userService.get(userId));
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("предмет не найден"));

        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBefore(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());

        if (bookings.isEmpty()) {
            throw new InvalidStatusException("Ошибка");
        }

        comment.setAuthor(author);
        comment.setItem(item);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.mapToDto(savedComment);
    }

    @Transactional(readOnly = true)
    private ItemDtoForBooking setLastAndNext(List<Booking> bookings, ItemDtoForBooking itemDtoForBooking, Long ownerId) {
        LocalDateTime dateTime = LocalDateTime.now();
        bookings
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemDtoForBooking.getId()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
                .filter(booking -> booking.getStart().isBefore(dateTime))
                .filter(ItemDtoForBooking -> itemDtoForBooking.getOwner().equals(ownerId)).filter(booking -> booking.getItem().getId().equals(itemDtoForBooking.getId()))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemDtoForBooking.setLastBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .build()));
        bookings
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemDtoForBooking.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStatus().equals(BookingStatusEnum.APPROVED))
                .filter(booking -> booking.getStart().isAfter(dateTime))
                .filter(ItemDtoForBooking -> itemDtoForBooking.getOwner().equals(ownerId)).filter(booking -> booking.getItem().getId().equals(itemDtoForBooking.getId()))
                .limit(1)
                .findAny()
                .ifPresent(booking -> itemDtoForBooking.setNextBooking(BookingDto.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .build()));
        return itemDtoForBooking;
    }
}
