package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    @NotBlank
    @Email
    private String email;
}
