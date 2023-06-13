package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    @NotNull
    private LocalDateTime end;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "booker_id")
    private Long booker;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatusEnum status;
}
