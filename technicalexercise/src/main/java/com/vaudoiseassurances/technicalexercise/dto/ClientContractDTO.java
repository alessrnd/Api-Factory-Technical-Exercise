package com.vaudoiseassurances.technicalexercise.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientContractDTO(
    @NotNull(message = "Client ID is required")
    Long clientId,

    LocalDate startDate,

    LocalDate endDate,

    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.01", message = "Cost must be greater than 0")
    BigDecimal costAmount
) {}