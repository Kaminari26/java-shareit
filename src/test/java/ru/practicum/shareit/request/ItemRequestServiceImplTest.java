package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @Test
    void addRequestUserNotFoundTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Пользователь не найден"));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "discr", 1L, localDateTime, null);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemRequestService.add(itemRequestDto, 123L));

        assertEquals("Пользователь не найден", exception.getMessage());


    }

    @Test
    void addRequestOkTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "ne null", 1L, null, null);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        ItemRequestDto itemRequestDtoActual = itemRequestService.add(itemRequestDto, 123L);

        assertEquals("ItemRequestDto(id=1, description=ne null, requestorId=1, created=" + itemRequestDtoActual.getCreated() + ", items=[])", itemRequestDtoActual.toString());
    }

    @Test
    void addRequestNullTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, null, 1L, localDateTime, null);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> itemRequestService.add(itemRequestDto, 123L));

        assertEquals("Не может быть пустым", exception.getMessage());
    }

    @Test
    void getItemRequestByUserIdUserNotFoundTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Пользователь не найден"));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequestByUserId(123L));

        assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void getItemRequestByUserIdUserTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        User user = new User(1L, "vas", "asdqwe@yandex.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "asd", user, localDateTime);

        List<ItemRequest> itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequest);
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));
        Mockito.when(itemRequestRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(itemRequestDtoList);
        Mockito.when(itemRequestRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(itemRequestDtoList);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getItemRequestByUserId(1L);

        assertEquals("[ItemRequestDto(id=1, description=asd, requestorId=1, created=" + localDateTime + ", items=[])]", itemRequestDtos.toString());


    }

    @Test
    void getItemRequestNotFoundTest() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Реквест не найден"));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemRequestService.getItemRequest(123L));

        assertEquals("Реквест не найден", exception.getMessage());
    }

    @Test
    void getItemRequestOkTest() {
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);

        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new ItemRequest(1L, "iteeeem", new User(1L, "Vasya", "Pupkin@yandex.ru"), LocalDateTime.now())));

        Mockito.when(itemRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(items);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        ItemRequestDto itemRequestDto = itemRequestService.getItemRequest(1L);

        assertEquals("ItemRequestDto(id=1, description=iteeeem, requestorId=1, created=" + itemRequestDto.getCreated() + ", items=[Item(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=1)])", itemRequestDto.toString());
    }

    @Test
    void getAllRequestsUserNotFoundTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Пользователь не найден"));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemRequestService.getAllRequests(123L, 1, 3));

        assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void getAllRequests() {
        User user = new User(1L, "vas", "asdqwe@yandex.ru");
        LocalDateTime localDateTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "asd", user, localDateTime);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));
        Mockito.when(itemRequestRepository.findAllByRequestIdNot(Mockito.anyLong(), Mockito.any(Pageable.class))).thenReturn(requests);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        List<ItemRequestDto> itemRequestDtos = itemRequestService.getAllRequests(1L, 1, 1);

        assertEquals("[ItemRequestDto(id=1, description=asd, requestorId=1, created=" + localDateTime + ", items=[])]", itemRequestDtos.toString());
    }

    @Test
    void getRequestByIdUserNotFoundTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Пользователь не найден"));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequestById(123L, 1L));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getRequestByIdRequestNotFoundTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenThrow(new UserNotFoundException("Реквест не найден"));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> itemRequestService.getRequestById(123L, 1L));

        assertEquals("Реквест не найден", exception.getMessage());
    }

    @Test
    void getRequestByIdOkTest() {
        User user = new User(1L, "vas", "asdqwe@yandex.ru");
        LocalDateTime localDateTime = LocalDateTime.now();
        List<Item> items = new ArrayList<>();
        Item item = new Item(1L, "GameBoy", "help me", true, 1L, 1L);
        items.add(item);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(1L, "Vasya", "Pupkin@yandex.ru")));
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new ItemRequest(1L, "asd", user, localDateTime)));
        Mockito.when(itemRepository.findAllByRequestId(Mockito.anyLong())).thenReturn(items);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(1L, 1L);

        assertEquals("ItemRequestDto(id=1, description=asd, requestorId=1, created=" + localDateTime + ", items=[Item(id=1, name=GameBoy, description=help me, available=true, owner=1, requestId=1)])", itemRequestDto.toString());
    }
}