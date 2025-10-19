package com.vaudoiseassurances.technicalexercise.dto;

import jakarta.validation.constraints.Size;
import com.vaudoiseassurances.technicalexercise.enums.ClientType;
import jakarta.validation.constraints.*;

public record ClientUpdateDTO(

    @NotNull(message = "Client type is required")
    ClientType clientType,

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email,

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be valid (10-15 digits)")
    String phone
) {}
