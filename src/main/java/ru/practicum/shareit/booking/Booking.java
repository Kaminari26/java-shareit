package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private String status;
    //Я не уверен что я не должен был развивать эту часть кода, но наставник писал что если тз этого явно не требует, то не надо. Так что это не я дурак забыл)
}
