package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final IBookingService bookingService;

    @Autowired
    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        log.info("Пришел запрос Post в Booking");
        BookingDtoResponse booking = bookingService.add(bookingDto, owner);
        log.info("Отправлен ответ " + booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse changeBookingStatus(@PathVariable Long bookingId,
                                                  @RequestParam(name = "approved") Boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {

        log.info("Изменение статуса букинга");
        BookingDtoResponse booking = bookingService.changeStatus(bookingId, approved, userId);
        log.info("Отправлен ответ" + booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос букинга с ID " + bookingId);
        BookingDtoResponse booking = bookingService.getBooking(userId, bookingId);
        log.info("Отправлен ответ " + booking);
        return booking;
    }

    @GetMapping
    public List<BookingDtoResponse> getBookingsByBooker(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                        @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {


        return bookingService.getAllByBookers(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingsByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state", defaultValue = "ALL") String state, @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}
