package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_ID = "X-Sharer-User-Id";
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto.Item item;

    @BeforeEach
    void setup() {

        item = new ItemRequestDto.Item();
        item.setId(2L);
        item.setOwnerId(2L);
        item.setName("tool");
        item.setDescription("test tool");
        item.setAvailable(true);
        item.setRequestId(2L);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("test description");
        itemRequestDto.setCreated(LocalDateTime.of(2023, 1, 4, 12, 0, 1));
        itemRequestDto.setItems(List.of(item));
    }

    @Test
    void createRequestTest() throws Exception {

        Mockito
                .when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.items.[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemRequestDto.getItems().get(0).getName())));

    }

    @Test
    void getUserRequestsTest() throws Exception {

        Mockito
                .when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items.[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items.[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].items.[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items.[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items.[0].available", is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items.[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getAllRequestsTest() throws Exception {

        Mockito
                .when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items.[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items.[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].items.[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items.[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items.[0].available", is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items.[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }

    @Test
    void getRequestTest() throws Exception {

        Mockito
                .when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.items.[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items.[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items.[0].available", is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items.[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()), Long.class));
    }
}