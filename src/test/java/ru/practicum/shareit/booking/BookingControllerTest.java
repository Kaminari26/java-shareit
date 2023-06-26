package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    IBookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    String userHeader = "X-Sharer-User-Id";
    private MockMvc mockMvc;
    private BookingDto bookingDto;
    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(3L);
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        mapper.findAndRegisterModules();
        bookingDto = new BookingDto(1L,
                start,
                end,
                1L,
                1L,
                BookingStatusEnum.WAITING);
        ItemDto itemDto = new ItemDto(1L, "item", "TestItem", true, 1L, 1L);

        UserDto userDto = new UserDto(1L, "Vasya", "vasya@yandex.ru");

        bookingDtoResponse = new BookingDtoResponse(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(2L), itemDto, userDto, BookingStatusEnum.WAITING);

    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.add(any(), anyLong()))
                .thenReturn(bookingDtoResponse);

        mockMvc.perform(post("/bookings")
                        .header(userHeader, 1L)
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoResponse.getItem().getId()), Long.class));
    }

    @Test
    void changeBookingStatus() throws Exception {
        when(bookingService.changeStatus(anyLong(), any(Boolean.class), anyLong()))
                .thenReturn(bookingDtoResponse);

        bookingDto.setStatus(BookingStatusEnum.APPROVED);
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(userHeader, 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoResponse.getItem().getId()), Long.class));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDtoResponse);

        bookingDto.setStatus(BookingStatusEnum.APPROVED);
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(userHeader, 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoResponse.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDtoResponse.getItem().getId()), Long.class));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(get("/bookings/owner")
                        .header(userHeader, 1L)
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoResponse.getStatus().toString())));
    }

//  @Test
//    void getBookingsByOwnerFail() throws Exception{
//      //  doThrow(IllegalReceiveException.class).
//       // when(bookingService).getAllByOwner(anyLong(),anyString(), anyInt(),anyInt());
//
//        mockMvc.perform(get("/bookings/owner")
//                        .header(userHeader, 1)
//                        .param("from", "-1")
//                        .param("size", "-10")
//                        .param("state", "ALL")
//                .characterEncoding(StandardCharsets.UTF_8))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void getBookingsByBooker() throws Exception {
        when(bookingService.getAllByBookers(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mockMvc.perform(get("/bookings")
                        .header(userHeader, 1L)
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtoResponse.getStatus().toString())));
    }
}