package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForBooking;

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
class ItemControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    IItemService itemService;

    @InjectMocks
    ItemController itemController;

    private MockMvc mockMvc;
    private ItemDto itemDto;
    private ItemDtoForBooking itemDtoForBooking;

    String userHeader = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.findAndRegisterModules();

        itemDto = new ItemDto(1L, "game", "best game ever", true, 1L, 1L);

        itemDtoForBooking = new ItemDtoForBooking(1L, "game", "best game ever", true, 1L, null, null, null);
    }

    @Test
    void createItem() throws Exception {
        when(itemService.add(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(itemDtoForBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForBooking.getId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDtoForBooking.getOwner()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoForBooking.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDtoForBooking.getName())));
    }

    @Test
    void updateItem() throws Exception {
        String newName = "Mario";
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        itemDto.setName(newName);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(itemDtoForBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForBooking.getId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDtoForBooking.getOwner()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoForBooking.getDescription())))
                .andExpect(jsonPath("$.name", is(newName)));

    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItemDtoForBooking(anyLong(), anyLong()))
                .thenReturn(itemDtoForBooking);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(itemDtoForBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForBooking.getId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDtoForBooking.getOwner()), Long.class))
                .andExpect(jsonPath("$.description", is(itemDtoForBooking.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDtoForBooking.getName())));

    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(anyLong()))
                .thenReturn(List.of(itemDtoForBooking));

        mockMvc.perform(get("/items")
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(itemDtoForBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].owner", is(itemDtoForBooking.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemDtoForBooking.getDescription())))
                .andExpect(jsonPath("$[0].name", is(itemDtoForBooking.getName())));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.searchItem(anyString()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "game")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].owner", is(itemDtoForBooking.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemDtoForBooking.getDescription())))
                .andExpect(jsonPath("$[0].name", is(itemDtoForBooking.getName())));
    }

    @Test
    void addComment() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "otstoy", "Vasya", localDateTime);
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(userHeader, 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}