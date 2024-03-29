package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor

public class Item {

    private Long id;


    private String name;


    private String description;


    private Boolean available;

    private Long owner;

    private Long requestId;
}
