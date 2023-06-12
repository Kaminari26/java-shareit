package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;

public class ItemResponseDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    //private List<ResponseCommentDto> comments;
}
