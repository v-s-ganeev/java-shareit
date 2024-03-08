package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationMarkers;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @Null(groups = {ValidationMarkers.Create.class})
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank(groups = {ValidationMarkers.Create.class})
    @Email(groups = {ValidationMarkers.Create.class, ValidationMarkers.Update.class})
    private String email;
}
