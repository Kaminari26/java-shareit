package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoForBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
   // private BookingDto lastBooking;
   // private BookingDto nextBooking;
    //private List<ResponseCommentDto> comments;
}
