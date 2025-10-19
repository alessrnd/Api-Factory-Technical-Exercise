package com.vaudoiseassurances.technicalexercise.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientContractResponseDTO(
    Long id,
    Long clientId,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal costAmount
) {}