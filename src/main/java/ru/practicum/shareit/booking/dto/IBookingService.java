package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface IBookingService {
    BookingDtoResponse add(BookingDto bookingDto, Long owner);

    BookingDtoResponse changeStatus(Long id, Boolean approved, Long userId);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    List<BookingDtoResponse> getAllByBookers(Long userId, String state);

    List<BookingDtoResponse> getAllByOwner(Long ownerId, String state);

    List<Booking> getAllByItemId(Long id);

    List<Booking> getAllByItemIdIn(List<Long> itemIds);

    List<Booking> getAllByItemIdAndTime(Long itemId, LocalDateTime created);
}
