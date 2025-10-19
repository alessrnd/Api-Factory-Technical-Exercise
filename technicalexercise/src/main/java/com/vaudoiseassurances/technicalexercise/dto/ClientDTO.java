package com.vaudoiseassurances.technicalexercise.dto;

import com.vaudoiseassurances.technicalexercise.enums.ClientType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ClientDTO(
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
    String phone,

    @Past(message = "Birthdate must be in the past")
    LocalDate birthdate,

    @Pattern(regexp = "^[a-z]{3}-[0-9]{3}$", message = "Company identifier must follow format: aaa-123")
    String companyIdentifier
) {}