package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {
    public static Booking toBookingDto (BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItemId(),
                bookingDto.getBookerId(),
                bookingDto.getStatus()
        );
    }

    public static BookingDto toBookingDto (Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItemId(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static BookingDtoResponse toBookingDto (Booking booking, UserDto user, ItemDto item) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                user,
                booking.getStatus()
        );
    }
}
