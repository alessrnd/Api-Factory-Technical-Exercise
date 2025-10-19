package com.vaudoiseassurances.technicalexercise.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateCostAmountDTO(
    @NotNull(message = "Cost amount is required")
    @DecimalMin(value = "0.01", message = "Cost must be greater than 0")
    BigDecimal costAmount
) {}