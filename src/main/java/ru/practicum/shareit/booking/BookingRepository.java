package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(Long booker, Sort sort);

    List<Booking> findByBookerAndEndIsBefore(Long booker, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, Sort sort, BookingStatusEnum bookingStatus);

    List<Booking> findAllByItemId(Long id);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime date, LocalDateTime date1, Sort sort);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, Sort sort);

    List<Booking> findAllByItemIdIn(List<Long> itemId, Sort sort);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, Sort sort);

    List<Booking> findByItemIdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, Sort sort, BookingStatusEnum bookingStatus);

    List<Booking> findAllByItemIdIn(List<Long> itemId);

    List<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime date);
}
