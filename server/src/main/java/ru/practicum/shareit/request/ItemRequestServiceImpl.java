package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements IItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;

    }


    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequestDto(itemRequestDto, user);
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findAllByRequestId(userId).stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId()));
        }

        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getItemRequest(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new UserNotFoundException("Реквест не найден"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> list = itemRepository.findAllByRequestId(itemRequestDto.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest, list);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        int page = 0;
        if (from != 0) {
            page = from / size;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());

        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequestIdNot(userId, pageable).stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIdIn(requests.stream().map(ItemRequestDto::getId).collect(Collectors.toList()));
        for (ItemRequestDto itemRequest : requests) {
            List<Item> itemRequests = items.stream().filter(item -> Objects.equals(itemRequest.getId(), item.getRequestId())).collect(Collectors.toList());
            itemRequest.setItems(itemRequests);
        }
        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(() -> new UserNotFoundException("Реквест не найден")));
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId()));
        return itemRequestDto;
    }
}
