package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;

    private static final String USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setup() {

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2022, 12, 30, 18, 20, 1));
        bookingDto.setEnd(LocalDateTime.of(2022, 12, 31, 18, 20, 1));
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItemId(1L);
        bookingDto.setItem(new BookingDto.Item(1L, "name"));
        bookingDto.setBooker(new BookingDto.User(1L));
    }

    @Test
    void createBookingTest() throws Exception {

        Mockito
                .when(bookingService.createBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), LocalDateTime.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name()), String.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("name"), String.class))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class));

        verify(bookingService, times(1)).createBooking(anyLong(), any(BookingDto.class));

    }

    @Test
    void setApprovalTest() throws Exception {

        bookingDto.setStatus(Status.APPROVED);

        Mockito
                .when(bookingService.setApproval(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(USER_ID, 1)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).setApproval(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void findBookingByIdTest() throws Exception {

        Mockito
                .when(bookingService.findBookingById(bookingDto.getId(), 1L))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).findBookingById(anyLong(), anyLong());
    }

    @Test
    void findBookingsOfOwnerTest() throws Exception {

        List<BookingDto> bookingDtoList = List.of(bookingDto);

        Mockito
                .when(bookingService.findBookingsOfOwner(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).findBookingsOfOwner(anyLong(), anyString(), any(Pageable.class));

    }

    @Test
    void findBookingsOfUserTest() throws Exception {

        List<BookingDto> bookingDtoList = List.of(bookingDto);

        Mockito
                .when(bookingService.findBookingsOfUser(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID, 1)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).findBookingsOfUser(anyLong(), anyString(), any(Pageable.class));
    }
}